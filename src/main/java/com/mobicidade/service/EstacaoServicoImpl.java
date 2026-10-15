package com.mobicidade.service;

import com.mobicidade.model.Estacao;
import com.mobicidade.repository.EstacaoRepositorio;

public class EstacaoServicoImpl extends ServicoBase<Estacao, Integer> implements EstacaoServico {

    public EstacaoServicoImpl(EstacaoRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    protected void validar(Estacao estacao) {
        exigir(estacao.getId() > 0, "O identificador da estação deve ser maior que zero.");
        exigir(estacao.getCapacidade() > 0, "A capacidade da estação deve ser maior que zero.");
        exigir(estacao.getLatitude() >= -90 && estacao.getLatitude() <= 90, "Latitude inválida.");
        exigir(estacao.getLongitude() >= -180 && estacao.getLongitude() <= 180, "Longitude inválida.");
        exigir(estacao.getVeiculos().size() <= estacao.getCapacidade(),
                "A capacidade não pode ser menor que a quantidade de veículos na estação.");
    }

    @Override
    protected Integer obterId(Estacao estacao) {
        return estacao.getId();
    }

    @Override
    protected String nomeEntidade() {
        return "Estação";
    }

    @Override
    public void remover(Integer id) {
        Estacao estacao = buscarPorId(id);
        exigir(estacao.getVeiculos().isEmpty(), "Não é possível remover uma estação que ainda possui veículos.");
        super.remover(id);
    }

    @Override
    public int veiculosDisponiveis(int idEstacao) {
        return buscarPorId(idEstacao).veiculosDisponiveis();
    }

    @Override
    public int vagasLivres(int idEstacao) {
        return buscarPorId(idEstacao).vagasLivres();
    }
}
