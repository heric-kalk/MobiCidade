package com.mobicidade.repository;

import com.mobicidade.model.Estacao;

public class EstacaoRepositorioMemoria extends RepositorioMemoria<Estacao, Integer> implements IEstacaoRepositorio {
    @Override
    protected Integer obterId(Estacao estacao) {
        return estacao.getId();
    }
}
