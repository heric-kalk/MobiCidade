package com.mobicidade.service;

import com.mobicidade.model.Estacao;

public interface IEstacaoServico extends IServico<Estacao, Integer> {

    // REQ03
    int veiculosDisponiveis(int idEstacao);

    // REQ03
    int vagasLivres(int idEstacao);
}
