package com.mobicidade.service;

import com.mobicidade.model.Corrida;
import com.mobicidade.repository.CorridaRepositorio;

public class CorridaServicoImpl extends ServicoBase<Corrida, Integer> implements CorridaServico {

    public CorridaServicoImpl(CorridaRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    protected void validar(Corrida corrida) {
        exigir(corrida.getId() > 0, "O identificador da corrida deve ser maior que zero.");
        exigir(corrida.getUsuario() != null, "A corrida deve ter um usuário.");
        exigir(corrida.getVeiculo() != null, "A corrida deve ter um veículo.");
        exigir(corrida.getEstacaoOrigem() != null, "A corrida deve ter uma estação de origem.");
    }

    @Override
    protected Integer obterId(Corrida corrida) {
        return corrida.getId();
    }

    @Override
    protected String nomeEntidade() {
        return "Corrida";
    }
}
