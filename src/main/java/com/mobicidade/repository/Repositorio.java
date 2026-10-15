package com.mobicidade.repository;

import java.util.List;
import java.util.Optional;

public interface Repositorio<T, ID> {

    T salvar(T entidade);

    Optional<T> buscarPorId(ID id);

    List<T> listarTodos();

    T atualizar(T entidade);

    void remover(ID id);

    boolean existe(ID id);
}
