package com.mobicidade.service;

import com.mobicidade.model.Manutencao;

import java.time.LocalDateTime;

public interface IManutencaoServico extends IServico<Manutencao, Integer> {
    // REQ13
    Manutencao registrar(int idVeiculo, String descricao, LocalDateTime inicio);

    // REQ14
    Manutencao concluir(int idManutencao, int idEstacao, LocalDateTime fim);
}
