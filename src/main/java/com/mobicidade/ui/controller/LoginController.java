package com.mobicidade.ui.controller;

import com.mobicidade.facade.IMobiCidadeFachada;
import com.mobicidade.model.Administrador;
import com.mobicidade.model.Usuario;
import com.mobicidade.ui.Navegador;
import com.mobicidade.ui.EstiloMensagem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private final IMobiCidadeFachada fachada;

    @FXML
    private TextField campoCpf;

    @FXML
    private PasswordField campoSenha;

    @FXML
    private Label mensagem;

    public LoginController(IMobiCidadeFachada fachada) {
        this.fachada = fachada;
    }

    @FXML
    private void entrar() {
        String cpf = campoCpf.getText().trim();
        String senha = campoSenha.getText();

        if (cpf.isBlank() || senha.isBlank()) {
            EstiloMensagem.erro(mensagem, "Preencha todos os campos.");
            return;
        }

        try {
            Usuario usuario = fachada.autenticarUsuario(cpf, senha);

            if (usuario == null) {
                EstiloMensagem.erro(mensagem, "CPF ou senha inválidos.");
                return;
            }

            Navegador.exibir(campoCpf, usuario instanceof Administrador
                    ? Navegador.menuAdministrador(fachada)
                    : Navegador.menuUsuario(fachada, usuario));

        } catch (Exception e) {
            EstiloMensagem.erro(mensagem, "Erro ao abrir a tela: " + e.getMessage());
        }
    }

    @FXML
    private void abrirCadastro() {
        try {
            Navegador.exibir(campoCpf, Navegador.cadastro(fachada));
        } catch (Exception e) {
            EstiloMensagem.erro(mensagem, "Erro ao abrir a tela de cadastro.");
        }
    }
}
