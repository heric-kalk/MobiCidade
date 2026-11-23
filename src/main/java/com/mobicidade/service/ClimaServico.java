package com.mobicidade.service;

import com.mobicidade.exception.ProvedorIndisponivelException;
import com.mobicidade.model.PrevisaoClimatica;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Mantém cache separado por coordenada para nunca reutilizar previsão de outra região. */
public class ClimaServico implements IClimaServico {
    private final IProvedorPrevisaoClimatica provedor;
    private final Map<String, PrevisaoClimatica> cachePorLocalizacao = new ConcurrentHashMap<>();

    public ClimaServico(IProvedorPrevisaoClimatica provedor) {
        this.provedor = provedor;
    }

    @Override
    public PrevisaoClimatica consultar(double latitude, double longitude) {
        String chave = chaveLocalizacao(latitude, longitude);
        try {
            PrevisaoClimatica atual = provedor.consultar(latitude, longitude);
            if (atual != null && atual.temDados()) {
                cachePorLocalizacao.put(chave, atual);
                return atual;
            }
            PrevisaoClimatica cache = cachePorLocalizacao.get(chave);
            return cache != null ? cache.comoDesatualizada() : PrevisaoClimatica.semDados();
        } catch (ProvedorIndisponivelException e) {
            PrevisaoClimatica cache = cachePorLocalizacao.get(chave);
            return cache != null ? cache.comoDesatualizada() : PrevisaoClimatica.semDados();
        }
    }

    private String chaveLocalizacao(double latitude, double longitude) {
        return String.format(Locale.ROOT, "%.4f|%.4f", latitude, longitude);
    }
}
