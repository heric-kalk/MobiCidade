package com.mobicidade.service;

import com.mobicidade.repository.IRepositorio;

public abstract class ServicoInteiroBase<T> extends ServicoBase<T, Integer> {

    private int sequencia = 0;

    protected ServicoInteiroBase(IRepositorio<T, Integer> repositorio) {
        super(repositorio);
    }

    protected abstract void definirId(T entidade, int id);

    @Override
    protected void atribuirId(T entidade) {
        if (obterId(entidade) <= 0) {
            do {
                sequencia++;
            } while (repositorio.existe(sequencia));
            definirId(entidade, sequencia);
        }
    }
}
