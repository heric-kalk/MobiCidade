package com.mobicidade.service;

import com.mobicidade.exception.ProvedorIndisponivelException;
import com.mobicidade.model.PrevisaoClimatica;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// REQ06
public class OpenMeteoProvedor implements IProvedorPrevisaoClimatica {
    private static final String URL_BASE = "https://api.open-meteo.com/v1/forecast";
    private static final int HORAS_A_FRENTE = 6;

    private static final Pattern BLOCO_CURRENT = Pattern.compile("\"current\"\\s*:\\s*\\{([^}]*)\\}");
    private static final Pattern BLOCO_HOURLY = Pattern.compile("\"hourly\"\\s*:\\s*\\{(.*?)\\}\\s*[,}]", Pattern.DOTALL);

    private final HttpClient cliente;

    public OpenMeteoProvedor() {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
    }

    public OpenMeteoProvedor(HttpClient cliente) {
        this.cliente = cliente;
    }

    @Override
    public PrevisaoClimatica consultar(double latitude, double longitude) {
        String url = String.format(Locale.US,
                "%s?latitude=%.4f&longitude=%.4f&current=weather_code,precipitation"
                        + "&hourly=weather_code,precipitation&forecast_hours=%d&timezone=auto",
                URL_BASE, latitude, longitude, HORAS_A_FRENTE);
        try {
            HttpRequest requisicao = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(8)).GET().build();
            HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
            if (resposta.statusCode() != 200) {
                throw new ProvedorIndisponivelException("Open-Meteo respondeu HTTP " + resposta.statusCode() + ".");
            }
            return interpretar(resposta.body());
        } catch (IOException e) {
            throw new ProvedorIndisponivelException("Falha ao consultar a Open-Meteo: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProvedorIndisponivelException("Consulta à Open-Meteo interrompida.", e);
        }
    }

    private PrevisaoClimatica interpretar(String json) {
        List<Integer> codigos = new ArrayList<>();
        List<Double> precipitacoes = new ArrayList<>();

        Matcher atual = BLOCO_CURRENT.matcher(json);
        if (atual.find()) {
            Integer codigo = primeiroNumero(atual.group(1), "weather_code");
            Double mm = primeiroDecimal(atual.group(1), "precipitation");
            if (codigo != null) {
                codigos.add(codigo);
            }
            if (mm != null) {
                precipitacoes.add(mm);
            }
        }
        Matcher hora = BLOCO_HOURLY.matcher(json);
        if (hora.find()) {
            for (String valor : lista(hora.group(1), "weather_code")) {
                codigos.add((int) Math.round(Double.parseDouble(valor)));
            }
            for (String valor : lista(hora.group(1), "precipitation")) {
                precipitacoes.add(Double.parseDouble(valor));
            }
        }
        if (codigos.isEmpty() && precipitacoes.isEmpty()) {
            throw new ProvedorIndisponivelException("Resposta da Open-Meteo sem dados de previsão.");
        }
        return new PrevisaoClimatica(LocalDateTime.now(), codigos, precipitacoes);
    }

    private static Integer primeiroNumero(String bloco, String campo) {
        Double valor = primeiroDecimal(bloco, campo);
        return valor == null ? null : (int) Math.round(valor);
    }

    private static Double primeiroDecimal(String bloco, String campo) {
        Matcher m = Pattern.compile("\"" + campo + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").matcher(bloco);
        return m.find() ? Double.valueOf(m.group(1)) : null;
    }

    private static List<String> lista(String bloco, String campo) {
        List<String> valores = new ArrayList<>();
        Matcher m = Pattern.compile("\"" + campo + "\"\\s*:\\s*\\[([^\\]]*)\\]").matcher(bloco);
        if (m.find()) {
            for (String item : m.group(1).split(",")) {
                String limpo = item.trim();
                if (!limpo.isEmpty() && !limpo.equals("null")) {
                    valores.add(limpo);
                }
            }
        }
        return valores;
    }
}
