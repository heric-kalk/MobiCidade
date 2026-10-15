package com.mobicidade.controller;

import com.mobicidade.service.Servico;

import java.util.List;

public abstract class ControllerBase<T, ID> {

    private final Servico<T, ID> servico;

    protected ControllerBase(Servico<T, ID> servico) {
        this.servico = servico;
    }

    public T cadastrar(T entidade) {
        return servico.cadastrar(entidade);
    }

    public T buscarPorId(ID id) {
        return servico.buscarPorId(id);
    }

    public List<T> listar() {
        return servico.listar();
    }

    public T atualizar(T entidade) {
        return servico.atualizar(entidade);
    }

    public void remover(ID id) {
        servico.remover(id);
    }
}
