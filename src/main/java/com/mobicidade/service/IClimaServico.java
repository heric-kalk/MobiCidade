package com.mobicidade.service;

import com.mobicidade.model.PrevisaoClimatica;

public interface IClimaServico {
    // REQ06, REQ08
    PrevisaoClimatica consultar(double latitude, double longitude);
}
