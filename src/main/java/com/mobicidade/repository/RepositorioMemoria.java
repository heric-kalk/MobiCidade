package com.mobicidade.repository;

import com.mobicidade.exception.EntidadeNaoEncontradaException;
import com.mobicidade.exception.RegraDeNegocioException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class RepositorioMemoria<T, ID> implements Repositorio<T, ID> {

    private final Map<ID, T> armazenamento = new LinkedHashMap<>();

    protected abstract ID obterId(T entidade);

    @Override
    public T salvar(T entidade) {
        ID id = obterId(entidade);
        if (armazenamento.containsKey(id)) {
            throw new RegraDeNegocioException("Já existe um registro com o identificador " + id + ".");
        }
        armazenamento.put(id, entidade);
        return entidade;
    }

    @Override
    public Optional<T> buscarPorId(ID id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(armazenamento.values());
    }

    @Override
    public T atualizar(T entidade) {
        ID id = obterId(entidade);
        if (!armazenamento.containsKey(id)) {
            throw new EntidadeNaoEncontradaException("Registro com identificador " + id + " não encontrado.");
        }
        armazenamento.put(id, entidade);
        return entidade;
    }

    @Override
    public void remover(ID id) {
        if (armazenamento.remove(id) == null) {
            throw new EntidadeNaoEncontradaException("Registro com identificador " + id + " não encontrado.");
        }
    }

    @Override
    public boolean existe(ID id) {
        return armazenamento.containsKey(id);
    }
}
