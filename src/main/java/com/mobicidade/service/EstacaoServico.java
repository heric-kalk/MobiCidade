package com.mobicidade.service;

import com.mobicidade.model.Estacao;

public interface EstacaoServico extends Servico<Estacao, Integer> {

    int veiculosDisponiveis(int idEstacao);

    int vagasLivres(int idEstacao);
}
