package com.mobicidade.service;

import com.mobicidade.exception.ProvedorIndisponivelException;
import com.mobicidade.model.PrevisaoClimatica;

import java.util.Optional;

public class ClimaServico implements IClimaServico {

    private final IProvedorPrevisaoClimatica provedor;
    private PrevisaoClimatica cache;

    public ClimaServico(IProvedorPrevisaoClimatica provedor) {
        this.provedor = provedor;
    }

    @Override
    public PrevisaoClimatica consultar(double latitude, double longitude) {
        try {
            PrevisaoClimatica atual = provedor.consultar(latitude, longitude);
            cache = atual;
            return atual;
        } catch (ProvedorIndisponivelException e) {
            // REQ08
            return cache != null ? cache.comoDesatualizada() : PrevisaoClimatica.semDados(latitude, longitude);
        }
    }

    @Override
    public Optional<PrevisaoClimatica> ultimaConsulta() {
        return Optional.ofNullable(cache);
    }
}
