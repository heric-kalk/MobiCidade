package com.mobicidade.ui;

import java.util.function.Predicate;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;

/**
 * Padroniza a apresentação de mensagens e estados de atenção na interface.
 */
public final class EstiloMensagem {

    private static final String VERMELHO = "-fx-text-fill: #dc2626; -fx-font-weight: bold;";
    private static final String VERDE = "-fx-text-fill: #16a34a; -fx-font-weight: bold;";
    private static final String NEUTRO = "-fx-text-fill: #475569;";

    private EstiloMensagem() {
    }

    public static void erro(Label rotulo, String texto) {
        definir(rotulo, texto, VERMELHO);
    }

    public static void sucesso(Label rotulo, String texto) {
        definir(rotulo, texto, VERDE);
    }

    public static void informacao(Label rotulo, String texto) {
        definir(rotulo, texto, NEUTRO);
    }

    public static void limpar(Label rotulo) {
        definir(rotulo, "", NEUTRO);
    }

    private static void definir(Label rotulo, String texto, String estilo) {
        rotulo.setText(texto == null ? "" : texto);
        rotulo.setStyle(estilo);
    }

    /** Destaca em vermelho o conteúdo de alertas, avisos e confirmações de ações. */
    public static void destacarAlerta(Alert alerta) {
        if (alerta.getAlertType() == Alert.AlertType.ERROR
                || alerta.getAlertType() == Alert.AlertType.WARNING
                || alerta.getAlertType() == Alert.AlertType.CONFIRMATION) {
            aplicarEstiloAlerta(alerta, VERMELHO);
        }
    }

    public static void destacarSucesso(Alert alerta) {
        aplicarEstiloAlerta(alerta, VERDE);
    }

    public static void destacarAviso(Alert alerta) {
        aplicarEstiloAlerta(alerta, VERMELHO);
    }

    private static void aplicarEstiloAlerta(Alert alerta, String estilo) {
        alerta.setOnShown(evento -> {
            destacarNo(alerta.getDialogPane().lookup(".header-panel .label"), estilo);
            destacarNo(alerta.getDialogPane().lookup(".content.label"), estilo);
        });
    }

    private static void destacarNo(Node no, String estilo) {
        if (no != null) {
            no.setStyle(estilo);
        }
    }

    /** Destaca estados indisponíveis ou impeditivos nas tabelas. */
    public static <S> void destacarEstadosNegativos(
            TableColumn<S, String> coluna, Predicate<String> estadoNegativo) {
        coluna.setCellFactory(colunaAtual -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean vazio) {
                super.updateItem(item, vazio);
                if (vazio || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item);
                boolean negativo = estadoNegativo.test(item);
                setTextFill(Color.web(negativo ? "#dc2626" : "#334155"));
                setStyle(negativo ? "-fx-font-weight: bold;" : "");
            }
        });
    }
}
