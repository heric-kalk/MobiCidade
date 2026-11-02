package com.mobicidade.service;

import com.mobicidade.exception.ProvedorIndisponivelException;
import com.mobicidade.model.PrevisaoClimatica;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

public class ProvedorPrevisaoSimulado implements IProvedorPrevisaoClimatica {

    public enum Cenario { CEU_LIMPO, CHUVA_FORTE, TEMPESTADE, API_FORA_DO_AR }

    private Cenario cenario = Cenario.CEU_LIMPO;

    public void definirCenario(Cenario cenario) {
        this.cenario = cenario;
    }

    @Override
    public PrevisaoClimatica consultar(double latitude, double longitude) {
        LocalDateTime agora = LocalDateTime.now();
        switch (cenario) {
            case CHUVA_FORTE:
                return new PrevisaoClimatica(agora, latitude, longitude,
                        Arrays.asList(63, 65, 65, 63), Arrays.asList(2.0, 9.5, 11.0, 4.0));
            case TEMPESTADE:
                return new PrevisaoClimatica(agora, latitude, longitude,
                        Arrays.asList(3, 95, 95, 80), Arrays.asList(0.0, 6.0, 14.0, 5.0));
            case API_FORA_DO_AR:
                throw new ProvedorIndisponivelException("API simulada fora do ar.");
            case CEU_LIMPO:
            default:
                return new PrevisaoClimatica(agora, latitude, longitude,
                        Arrays.asList(0, 1, 1, 2), Collections.nCopies(4, 0.0));
        }
    }
}
