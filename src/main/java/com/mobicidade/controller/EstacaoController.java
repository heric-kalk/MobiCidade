package com.mobicidade.controller;

import com.mobicidade.model.Estacao;
import com.mobicidade.service.EstacaoServico;

public class EstacaoController extends ControllerBase<Estacao, Integer> {

    private final EstacaoServico servico;

    public EstacaoController(EstacaoServico servico) {
        super(servico);
        this.servico = servico;
    }

    public int veiculosDisponiveis(int idEstacao) {
        return servico.veiculosDisponiveis(idEstacao);
    }

    public int vagasLivres(int idEstacao) {
        return servico.vagasLivres(idEstacao);
    }
}
