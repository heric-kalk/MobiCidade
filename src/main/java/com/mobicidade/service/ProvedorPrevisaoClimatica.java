package com.mobicidade.service;

import com.mobicidade.model.PrevisaoClimatica;

@FunctionalInterface
public interface ProvedorPrevisaoClimatica {

    PrevisaoClimatica consultar(double latitude, double longitude);

    static ProvedorPrevisaoClimatica semDados() {
        return (latitude, longitude) -> new PrevisaoClimatica(null, null, latitude, longitude);
    }
}
