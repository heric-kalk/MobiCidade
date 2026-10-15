package com.mobicidade.controller;

import com.mobicidade.model.Usuario;
import com.mobicidade.service.UsuarioServico;

public class UsuarioController extends ControllerBase<Usuario, String> {

    public UsuarioController(UsuarioServico servico) {
        super(servico);
    }
}
