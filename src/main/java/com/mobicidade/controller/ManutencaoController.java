package com.mobicidade.controller;

import com.mobicidade.model.Manutencao;
import com.mobicidade.service.ManutencaoServico;

public class ManutencaoController extends ControllerBase<Manutencao, Integer> {

    public ManutencaoController(ManutencaoServico servico) {
        super(servico);
    }
}
