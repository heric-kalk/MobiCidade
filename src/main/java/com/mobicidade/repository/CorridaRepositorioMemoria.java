package com.mobicidade.repository;

import com.mobicidade.model.Corrida;

public class CorridaRepositorioMemoria extends RepositorioMemoria<Corrida, Integer> implements CorridaRepositorio {

    @Override
    protected Integer obterId(Corrida corrida) {
        return corrida.getId();
    }
}
