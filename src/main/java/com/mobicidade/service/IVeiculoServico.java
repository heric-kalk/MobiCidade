package com.mobicidade.service;

import com.mobicidade.model.Veiculo;

public interface IVeiculoServico extends IServico<Veiculo, Integer> {
    // REQ12
    Veiculo recolherParaEstacao(int idVeiculo, int idEstacao);
}
