package com.mobicidade.repository;

import com.mobicidade.model.Veiculo;

public class VeiculoRepositorioMemoria extends RepositorioMemoria<Veiculo, Integer> implements IVeiculoRepositorio {

    @Override
    protected Integer obterId(Veiculo veiculo) {
        return veiculo.getId();
    }
}
