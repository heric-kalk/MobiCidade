package com.mobicidade.service;

import com.mobicidade.model.PrevisaoClimatica;

import java.util.Optional;

public interface IClimaServico {

    // REQ06, REQ08
    PrevisaoClimatica consultar(double latitude, double longitude);

    // REQ08
    Optional<PrevisaoClimatica> ultimaConsulta();
}
