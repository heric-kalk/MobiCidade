package com.mobicidade.controller;

import com.mobicidade.model.Veiculo;
import com.mobicidade.service.VeiculoServico;

public class VeiculoController extends ControllerBase<Veiculo, Integer> {

    public VeiculoController(VeiculoServico servico) {
        super(servico);
    }
}
