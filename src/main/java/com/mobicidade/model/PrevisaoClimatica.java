package com.mobicidade.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// REQ06
public class PrevisaoClimatica {

    private static final int[] CODIGOS_CHUVA_FORTE = {65, 67, 82, 95, 96, 99};
    public static final double LIMIAR_CHUVA_FORTE_MM_H = 7.6;

    private final LocalDateTime ultimaConsulta;
    private final double latitude;
    private final double longitude;
    private final List<Integer> codigosTempo;
    private final List<Double> precipitacaoMmPorHora;
    private boolean desatualizada;

    public PrevisaoClimatica(LocalDateTime ultimaConsulta, double latitude, double longitude,
                             List<Integer> codigosTempo, List<Double> precipitacaoMmPorHora) {
        this.ultimaConsulta = ultimaConsulta;
        this.latitude = latitude;
        this.longitude = longitude;
        this.codigosTempo = new ArrayList<>(codigosTempo);
        this.precipitacaoMmPorHora = new ArrayList<>(precipitacaoMmPorHora);
    }

    public static PrevisaoClimatica semDados(double latitude, double longitude) {
        return new PrevisaoClimatica(null, latitude, longitude, Collections.<Integer>emptyList(),
                Collections.<Double>emptyList());
    }

    public boolean temDados() {
        return ultimaConsulta != null && !(codigosTempo.isEmpty() && precipitacaoMmPorHora.isEmpty());
    }

    // REQ07, REQ17
    public AlertaClimatico verificarAlerta() {
        if (!temDados()) {
            return AlertaClimatico.SEM_DADOS;
        }
        for (int codigo : codigosTempo) {
            for (int forte : CODIGOS_CHUVA_FORTE) {
                if (codigo == forte) {
                    return AlertaClimatico.ALERTA_CHUVA_FORTE;
                }
            }
        }
        for (double mm : precipitacaoMmPorHora) {
            if (mm >= LIMIAR_CHUVA_FORTE_MM_H) {
                return AlertaClimatico.ALERTA_CHUVA_FORTE;
            }
        }
        return AlertaClimatico.SEM_ALERTA;
    }

    public String consultarPrevisao() {
        if (!temDados()) {
            return "Previsão indisponível";
        }
        double maxMm = 0;
        for (double mm : precipitacaoMmPorHora) {
            maxMm = Math.max(maxMm, mm);
        }
        String agora = codigosTempo.isEmpty() ? "?" : descreverCodigo(codigosTempo.get(0));
        return String.format("Agora: %s | próximas %d h: precipitação máxima %.1f mm/h%s",
                agora, Math.max(0, codigosTempo.size() - 1), maxMm, desatualizada ? " (dados desatualizados)" : "");
    }

    static String descreverCodigo(int codigo) {
        if (codigo == 0) return "céu limpo";
        if (codigo <= 3) return "parcialmente nublado";
        if (codigo <= 48) return "nevoeiro/nublado";
        if (codigo <= 57) return "garoa";
        if (codigo == 65 || codigo == 67) return "chuva forte";
        if (codigo <= 67) return "chuva";
        if (codigo <= 77) return "neve";
        if (codigo == 82) return "pancadas violentas de chuva";
        if (codigo <= 86) return "pancadas de chuva";
        if (codigo >= 95) return "tempestade";
        return "condição " + codigo;
    }

    // REQ08
    public PrevisaoClimatica comoDesatualizada() {
        PrevisaoClimatica copia = new PrevisaoClimatica(ultimaConsulta, latitude, longitude,
                codigosTempo, precipitacaoMmPorHora);
        copia.desatualizada = true;
        return copia;
    }

    public boolean isDesatualizada() {
        return desatualizada;
    }

    public LocalDateTime getUltimaConsulta() {
        return ultimaConsulta;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public List<Integer> getCodigosTempo() {
        return Collections.unmodifiableList(codigosTempo);
    }

    public List<Double> getPrecipitacaoMmPorHora() {
        return Collections.unmodifiableList(precipitacaoMmPorHora);
    }
}
