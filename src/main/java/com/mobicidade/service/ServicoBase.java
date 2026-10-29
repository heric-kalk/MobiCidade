package com.mobicidade.service;

import com.mobicidade.exception.EntidadeNaoEncontradaException;
import com.mobicidade.exception.RegraDeNegocioException;
import com.mobicidade.repository.IRepositorio;

import java.util.List;

public abstract class ServicoBase<T, ID> implements IServico<T, ID> {

    protected final IRepositorio<T, ID> repositorio;

    protected ServicoBase(IRepositorio<T, ID> repositorio) {
        this.repositorio = repositorio;
    }

    protected abstract ID obterId(T entidade);

    protected abstract String nomeEntidade();

    protected abstract void validar(T entidade);

    protected void prepararCadastro(T entidade) {
    }

    protected void regrasDeCadastro(T entidade) {
    }

    protected void atribuirId(T entidade) {
    }

    protected void aposCadastrar(T entidade) {
    }

    protected void regrasDeAtualizacao(T existente, T novo) {
    }

    protected void copiarCampos(T existente, T novo) {
    }

    protected void regrasDeRemocao(T entidade) {
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
        prepararCadastro(entidade);
        validar(entidade);
        regrasDeCadastro(entidade);
        atribuirId(entidade);
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
        T existente = buscarPorId(obterId(entidade));
        regrasDeAtualizacao(existente, entidade);
        if (existente != entidade) {
            copiarCampos(existente, entidade);
        }
        return repositorio.atualizar(existente);
    }

    @Override
    public void remover(ID id) {
        T existente = buscarPorId(id);
        regrasDeRemocao(existente);
        repositorio.remover(id);
        aposRemover(existente);
    }
}
