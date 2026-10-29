package com.mobicidade.service;

import java.util.List;

public interface IServico<T, ID> {

    T cadastrar(T entidade);

    T buscarPorId(ID id);

    List<T> listar();

    T atualizar(T entidade);

    void remover(ID id);
}
