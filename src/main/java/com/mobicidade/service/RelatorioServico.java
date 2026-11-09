package com.mobicidade.service;

import com.mobicidade.model.Corrida;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RelatorioServico implements IRelatorioServico {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ICorridaServico corridaServico;

    public RelatorioServico(ICorridaServico corridaServico) {
        this.corridaServico = corridaServico;
    }

    @Override
    public List<Corrida> corridasPorEstacao(Integer idEstacao, LocalDateTime de, LocalDateTime ate) {
        List<Corrida> resultado = new ArrayList<>();
        for (Corrida c : corridaServico.listar()) {
            if (!c.finalizada() || !dentroDoPeriodo(c, de, ate)) {
                continue;
            }
            boolean origem = idEstacao != null && c.getEstacaoOrigem().getId() == idEstacao;
            boolean destino = idEstacao != null && c.getEstacaoDestino() != null
                    && c.getEstacaoDestino().getId() == idEstacao;
            if (idEstacao == null || origem || destino) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    private static boolean dentroDoPeriodo(Corrida c, LocalDateTime de, LocalDateTime ate) {
        LocalDateTime inicio = c.getDataHoraInicio();
        return (de == null || !inicio.isBefore(de)) && (ate == null || !inicio.isAfter(ate));
    }

    @Override
    public String corridasPorEstacaoCsv(Integer idEstacao, LocalDateTime de, LocalDateTime ate) {
        StringBuilder csv = new StringBuilder(
                "id,inicio,fim,estacao_origem,estacao_destino,usuario,tipo_usuario,veiculo,duracao_min,tarifa\n");
        for (Corrida c : corridasPorEstacao(idEstacao, de, ate)) {
            csv.append(c.getId()).append(',')
                    .append(c.getDataHoraInicio().format(FORMATO_DATA)).append(',')
                    .append(c.getDataHoraFim().format(FORMATO_DATA)).append(',')
                    .append(c.getEstacaoOrigem().getId()).append(',')
                    .append(c.getEstacaoDestino() != null ? String.valueOf(c.getEstacaoDestino().getId()) : "ZONA_LIVRE").append(',')
                    .append(campo(c.getUsuario().getNome())).append(',')
                    .append(c.getUsuario().getTipo()).append(',')
                    .append(c.getVeiculo().getTipo()).append(' ').append(c.getVeiculo().getId()).append(',')
                    .append(c.getDuracao()).append(',')
                    .append(String.format(Locale.US, "%.2f", c.getTarifa())).append('\n');
        }
        return csv.toString();
    }

    private static String campo(String valor) {
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    @Override
    public Path exportarCorridasPorEstacaoCsv(Integer idEstacao, LocalDateTime de, LocalDateTime ate, Path arquivo)
            throws IOException {
        return Files.write(arquivo, corridasPorEstacaoCsv(idEstacao, de, ate).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Map<String, Double> faturamentoPorTipoUsuario(LocalDateTime de, LocalDateTime ate) {
        Map<String, Double> faturamento = new LinkedHashMap<>();
        faturamento.put("Comum", 0.0);
        faturamento.put("Assinante", 0.0);
        for (Corrida c : corridaServico.listar()) {
            if (c.finalizada() && dentroDoPeriodo(c, de, ate)) {
                faturamento.merge(c.getUsuario().getTipo(), c.getTarifa(), Double::sum);
            }
        }
        return faturamento;
    }
}
