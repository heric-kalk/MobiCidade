package com.mobicidade.controller;

import com.mobicidade.model.Corrida;
import com.mobicidade.service.CorridaServico;

public class CorridaController extends ControllerBase<Corrida, Integer> {

    public CorridaController(CorridaServico servico) {
        super(servico);
    }
}
