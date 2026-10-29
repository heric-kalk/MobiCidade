package com.mobicidade.ui;

import com.mobicidade.facade.IMobiCidadeFachada;
import com.mobicidade.model.AlertaClimatico;
import com.mobicidade.model.Corrida;
import com.mobicidade.model.Estacao;
import com.mobicidade.model.Manutencao;
import com.mobicidade.model.PrevisaoClimatica;
import com.mobicidade.model.Usuario;
import com.mobicidade.model.Veiculo;

import java.io.PrintStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public final class Painel {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private Painel() {
    }

    public static void exibir(IMobiCidadeFachada mobi, PrintStream out) {
        out.println("================ MOBICIDADE ================");

        // REQ07
        PrevisaoClimatica previsao = mobi.consultarPrevisaoDaCidade();
        if (previsao.verificarAlerta() == AlertaClimatico.ALERTA_CHUVA_FORTE) {
            out.println("*** ALERTA: chuva forte/tempestade prevista! Novas corridas bloqueadas. ***");
        }
        if (previsao.temDados()) {
            out.println("Clima: " + previsao.consultarPrevisao());
            if (previsao.isDesatualizada()) {
                out.println("(!) API indisponível - última consulta em " + previsao.getUltimaConsulta().format(HORA));
            }
        } else {
            out.println("Clima: previsão indisponível (sem conexão com a API e sem cache).");
        }

        out.println();
        out.println("-- Estações");
        for (Estacao e : mobi.listarEstacoes()) {
            out.println("  " + e);
        }

        out.println();
        out.println("-- Veículos");
        for (Veiculo v : mobi.listarVeiculos()) {
            out.println("  " + v);
        }

        out.println();
        out.println("-- Usuários");
        for (Usuario u : mobi.listarUsuarios()) {
            out.println("  " + u);
        }

        out.println();
        out.println("-- Corridas");
        for (Corrida c : mobi.listarCorridas()) {
            out.println("  " + c + " início " + c.getDataHoraInicio().format(HORA));
        }

        out.println();
        out.println("-- Manutenções");
        for (Manutencao m : mobi.listarManutencoes()) {
            out.println("  " + m);
        }

        // REQ16
        out.println();
        out.println("-- Faturamento por tipo de usuário");
        for (Map.Entry<String, Double> f : mobi.relatorioFaturamentoPorTipoUsuario(null, null).entrySet()) {
            out.println(String.format("  %-10s R$ %.2f", f.getKey(), f.getValue()));
        }
        out.println();
        out.println("(Execute com --simulacao para validar os requisitos REQ01 a REQ22.)");
    }
}
