package com.mobicidade.service;

import com.mobicidade.model.Manutencao;
import com.mobicidade.repository.ManutencaoRepositorio;

public class ManutencaoServicoImpl extends ServicoBase<Manutencao, Integer> implements ManutencaoServico {

    public ManutencaoServicoImpl(ManutencaoRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    protected void validar(Manutencao manutencao) {
        exigir(manutencao.getId() > 0, "O identificador da manutenção deve ser maior que zero.");
        exigir(manutencao.getDescricao() != null && !manutencao.getDescricao().isBlank(),
                "A descrição da manutenção é obrigatória.");
        exigir(manutencao.getVeiculo() != null, "A manutenção deve estar vinculada a um veículo.");
    }

    @Override
    protected Integer obterId(Manutencao manutencao) {
        return manutencao.getId();
    }

    @Override
    protected String nomeEntidade() {
        return "Manutenção";
    }
}
