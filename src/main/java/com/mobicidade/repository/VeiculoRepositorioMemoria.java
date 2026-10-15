package com.mobicidade.repository;

import com.mobicidade.model.Veiculo;

public class VeiculoRepositorioMemoria extends RepositorioMemoria<Veiculo, Integer> implements VeiculoRepositorio {

    @Override
    protected Integer obterId(Veiculo veiculo) {
        return veiculo.getId();
    }
}
