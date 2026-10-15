package com.mobicidade.service;

import com.mobicidade.exception.EntidadeNaoEncontradaException;
import com.mobicidade.exception.RegraDeNegocioException;
import com.mobicidade.repository.Repositorio;

import java.util.List;

public abstract class ServicoBase<T, ID> implements Servico<T, ID> {

    private final Repositorio<T, ID> repositorio;

    protected ServicoBase(Repositorio<T, ID> repositorio) {
        this.repositorio = repositorio;
    }

    protected abstract void validar(T entidade);

    protected abstract ID obterId(T entidade);

    protected abstract String nomeEntidade();

    protected void aposCadastrar(T entidade) {
    }

    protected void aposRemover(T entidade) {
    }

    protected void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new RegraDeNegocioException(mensagem);
        }
    }

    @Override
    public T cadastrar(T entidade) {
        exigir(entidade != null, nomeEntidade() + " não pode ser nulo(a).");
        validar(entidade);
        T salvo = repositorio.salvar(entidade);
        aposCadastrar(salvo);
        return salvo;
    }

    @Override
    public T buscarPorId(ID id) {
        return repositorio.buscarPorId(id).orElseThrow(() ->
                new EntidadeNaoEncontradaException(nomeEntidade() + " " + id + " não encontrado(a)."));
    }

    @Override
    public List<T> listar() {
        return repositorio.listarTodos();
    }

    @Override
    public T atualizar(T entidade) {
        exigir(entidade != null, nomeEntidade() + " não pode ser nulo(a).");
        validar(entidade);
        return repositorio.atualizar(entidade);
    }

    @Override
    public void remover(ID id) {
        T existente = buscarPorId(id);
        repositorio.remover(id);
        aposRemover(existente);
    }
}
