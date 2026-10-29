package com.mobicidade.service;

import com.mobicidade.model.Patinete;
import com.mobicidade.model.Veiculo;

public interface IVeiculoServico extends IServico<Veiculo, Integer> {

    Patinete recarregarPatinete(int idVeiculo);

    // REQ12
    Veiculo recolherParaEstacao(int idVeiculo, int idEstacao);
}
