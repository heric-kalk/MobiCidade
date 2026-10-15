package com.mobicidade.repository;

import com.mobicidade.model.Manutencao;

public class ManutencaoRepositorioMemoria extends RepositorioMemoria<Manutencao, Integer> implements ManutencaoRepositorio {

    @Override
    protected Integer obterId(Manutencao manutencao) {
        return manutencao.getId();
    }
}
