package com.mobicidade.ui;

import com.mobicidade.facade.IMobiCidadeFachada;
import com.mobicidade.model.Usuario;
import com.mobicidade.ui.controller.CadastroUsuarioController;
import com.mobicidade.ui.controller.LoginController;
import com.mobicidade.ui.controller.MenuAdministradorController;
import com.mobicidade.ui.controller.MenuUsuarioController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Callback;

import java.io.IOException;

public final class Navegador {

    private static final String VIEW = "/com/mobicidade/view/";

    private Navegador() {
    }

    public static Parent login(IMobiCidadeFachada fachada) throws IOException {
        return carregar("login.fxml", tipo -> new LoginController(fachada));
    }

    public static Parent cadastro(IMobiCidadeFachada fachada) throws IOException {
        return carregar("cadastro-usuario.fxml", tipo -> new CadastroUsuarioController(fachada));
    }

    public static Parent menuUsuario(IMobiCidadeFachada fachada, Usuario usuario) throws IOException {
        return carregar("menu-usuario.fxml", tipo -> new MenuUsuarioController(fachada, usuario));
    }

    public static Parent menuAdministrador(IMobiCidadeFachada fachada) throws IOException {
        return carregar("menu-administrador.fxml", tipo -> new MenuAdministradorController(fachada));
    }

    public static void exibir(Node origem, Parent raiz) {
        origem.getScene().setRoot(raiz);
    }

    private static Parent carregar(String fxml, Callback<Class<?>, Object> fabrica) throws IOException {
        FXMLLoader loader = new FXMLLoader(Navegador.class.getResource(VIEW + fxml));
        loader.setControllerFactory(fabrica);
        return loader.load();
    }
}
