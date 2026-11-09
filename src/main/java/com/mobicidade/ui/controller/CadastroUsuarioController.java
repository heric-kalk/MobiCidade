package com.mobicidade.ui.controller;

import com.mobicidade.facade.IMobiCidadeFachada;
import com.mobicidade.model.UsuarioAssinante;
import com.mobicidade.ui.Navegador;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CadastroUsuarioController {

    private static final String TIPO_COMUM = "Comum";
    private static final String TIPO_ASSINANTE = "Assinante";

    private final IMobiCidadeFachada fachada;

    @FXML
    private TextField campoNome;

    @FXML
    private TextField campoCpf;

    @FXML
    private PasswordField campoSenha;

    @FXML
    private TextField campoFormaPagamento;

    @FXML
    private ComboBox<String> campoTipo;

    @FXML
    private Label mensagem;

    public CadastroUsuarioController(IMobiCidadeFachada fachada) {
        this.fachada = fachada;
    }

    @FXML
    private void initialize() {
        campoTipo.getItems().addAll(TIPO_COMUM, TIPO_ASSINANTE);
        campoTipo.setValue(TIPO_COMUM);
    }

    // REQ04, REQ05
    @FXML
    private void cadastrar() {
        String nome = campoNome.getText().trim();
        String cpf = campoCpf.getText().trim();
        String senha = campoSenha.getText();
        String formaPagamento = campoFormaPagamento.getText().trim();
        String tipo = campoTipo.getValue();

        if (nome.isBlank() || cpf.isBlank() || senha.isBlank() || formaPagamento.isBlank() || tipo == null) {
            erro("Preencha todos os campos.");
            return;
        }

        try {
            if (tipo.equals(TIPO_ASSINANTE)) {
                fachada.cadastrarUsuarioAssinante(nome, cpf, senha, formaPagamento,
                        UsuarioAssinante.MINUTOS_PLANO_PADRAO);
            } else {
                fachada.cadastrarUsuarioComum(nome, cpf, senha, formaPagamento);
            }

            mensagem.setStyle("-fx-text-fill: #16a34a;");
            mensagem.setText("Conta criada com sucesso! Você já pode entrar.");

            campoNome.clear();
            campoCpf.clear();
            campoSenha.clear();
            campoFormaPagamento.clear();
            campoTipo.setValue(TIPO_COMUM);

        } catch (Exception e) {
            erro(e.getMessage() == null || e.getMessage().isBlank()
                    ? "Não foi possível realizar o cadastro."
                    : e.getMessage());
        }
    }

    @FXML
    private void voltarLogin() {
        try {
            Navegador.exibir(campoNome, Navegador.login(fachada));
        } catch (Exception e) {
            erro("Erro ao voltar para o login.");
        }
    }

    private void erro(String texto) {
        mensagem.setStyle("-fx-text-fill: #dc2626;");
        mensagem.setText(texto);
    }
}
