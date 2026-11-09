package com.mobicidade.ui;

import com.mobicidade.facade.IMobiCidadeFachada;
import com.mobicidade.model.AlertaClimatico;
import com.mobicidade.model.PrevisaoClimatica;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;

public final class AlertaClima {

    private static final String PERMITIDO = "alertaPermitido";
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private static final String ESTILO_BASE = "-fx-padding: 12; -fx-background-radius: 8; -fx-font-size: 14px;";
    private static final String ESTILO_ALERTA = ESTILO_BASE
            + "-fx-background-color: #fecaca; -fx-text-fill: #991b1b; -fx-font-weight: bold;";
    private static final String ESTILO_NORMAL = ESTILO_BASE
            + "-fx-background-color: #dcfce7; -fx-text-fill: #166534;";
    private static final String ESTILO_AVISO = ESTILO_BASE
            + "-fx-background-color: #fef9c3; -fx-text-fill: #854d0e;";

    private AlertaClima() {
    }

    // REQ06, REQ07, REQ08
    public static void atualizar(IMobiCidadeFachada fachada, Label rotulo) {
        rotulo.getProperties().put(PERMITIDO, true);
        Thread consulta = new Thread(() -> {
            PrevisaoClimatica previsao = fachada.consultarPrevisaoDaCidade();
            Platform.runLater(() -> {
                if (Boolean.TRUE.equals(rotulo.getProperties().get(PERMITIDO))) {
                    exibir(previsao, rotulo);
                }
            });
        });
        consulta.setDaemon(true);
        consulta.start();
    }

    private static void exibir(PrevisaoClimatica previsao, Label rotulo) {
        StringBuilder texto = new StringBuilder();
        String estilo;

        if (previsao.verificarAlerta() == AlertaClimatico.ALERTA_CHUVA_FORTE) {
            texto.append("ALERTA: chuva forte ou tempestade prevista. Novas corridas estão bloqueadas.\n");
            estilo = ESTILO_ALERTA;
        } else if (previsao.temDados()) {
            estilo = ESTILO_NORMAL;
        } else {
            estilo = ESTILO_ALERTA;
        }

        if (previsao.temDados()) {
            texto.append("Clima: ").append(previsao.consultarPrevisao());
            if (previsao.isDesatualizada()) {
                texto.append("\nAPI indisponível: exibindo a última consulta de ")
                        .append(previsao.getUltimaConsulta().format(HORA)).append('.');
                if (estilo.equals(ESTILO_NORMAL)) {
                    estilo = ESTILO_AVISO;
                }
            }
        } else {
            texto.append("Clima: previsão indisponível (sem conexão com a API e sem cache). Novas corridas estão bloqueadas.");
        }

        rotulo.setText(texto.toString());
        rotulo.setStyle(estilo);
        rotulo.setVisible(true);
        rotulo.setManaged(true);
    }

    public static void ocultar(Label rotulo) {
        rotulo.getProperties().put(PERMITIDO, false);
        rotulo.setVisible(false);
        rotulo.setManaged(false);
    }
}
