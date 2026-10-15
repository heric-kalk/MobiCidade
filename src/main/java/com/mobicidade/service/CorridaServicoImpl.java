package com.mobicidade.service;

import com.mobicidade.model.Corrida;
import com.mobicidade.model.Estacao;
import com.mobicidade.model.PrevisaoClimatica;
import com.mobicidade.model.StatusVeiculo;
import com.mobicidade.repository.CorridaRepositorio;

public class CorridaServicoImpl extends ServicoBase<Corrida, Integer> implements CorridaServico {

    private static final String ALERTA_CHUVA_FORTE = "ALERTA_CHUVA_FORTE";

    private final ProvedorPrevisaoClimatica previsaoClimatica;

    public CorridaServicoImpl(CorridaRepositorio repositorio) {
        this(repositorio, ProvedorPrevisaoClimatica.semDados());
    }

    public CorridaServicoImpl(CorridaRepositorio repositorio, ProvedorPrevisaoClimatica previsaoClimatica) {
        super(repositorio);
        this.previsaoClimatica = previsaoClimatica;
    }

    @Override
    public Corrida cadastrar(Corrida corrida) {
        exigir(corrida != null, nomeEntidade() + " não pode ser nulo(a).");
        validar(corrida);
        exigirSemChuvaForte(corrida.getEstacaoOrigem());
        return super.cadastrar(corrida);
    }

    @Override
    protected void aposCadastrar(Corrida corrida) {
        corrida.getVeiculo().alterarDisponibilidade(StatusVeiculo.EM_USO);
    }

    private void exigirSemChuvaForte(Estacao origem) {
        PrevisaoClimatica previsao = previsaoClimatica.consultar(origem.getLatitude(), origem.getLongitude());
        boolean chuvaForte = previsao != null && ALERTA_CHUVA_FORTE.equals(previsao.verificarAlerta());
        exigir(!chuvaForte,
                "Corrida bloqueada: há previsão de chuva forte ou tempestade na região da estação de origem.");
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
