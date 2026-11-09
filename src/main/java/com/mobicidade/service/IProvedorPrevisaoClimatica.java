package com.mobicidade.service;

import com.mobicidade.model.PrevisaoClimatica;

@FunctionalInterface
public interface IProvedorPrevisaoClimatica {
    PrevisaoClimatica consultar(double latitude, double longitude);
}
