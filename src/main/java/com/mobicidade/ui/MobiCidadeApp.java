package com.mobicidade.ui;

import com.mobicidade.facade.IMobiCidadeFachada;
import com.mobicidade.facade.MobiCidadeFachada;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MobiCidadeApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        IMobiCidadeFachada fachada = MobiCidadeFachada.comDadosIniciais();

        stage.setTitle("MobiCidade");
        stage.setScene(new Scene(Navegador.login(fachada)));
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
