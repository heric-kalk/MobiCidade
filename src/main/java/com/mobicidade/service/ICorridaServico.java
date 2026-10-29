package com.mobicidade.service;

import com.mobicidade.model.Corrida;

import java.time.LocalDateTime;

public interface ICorridaServico extends IServico<Corrida, Integer> {

    // REQ09, REQ17 a REQ20
    Corrida iniciar(String cpfUsuario, int idVeiculo, int idEstacaoOrigem);

    // REQ09
    Corrida iniciar(String cpfUsuario, int idVeiculo, int idEstacaoOrigem, LocalDateTime inicio);

    // REQ10 a REQ12, REQ21, REQ22
    Corrida finalizar(int idCorrida, Integer idEstacaoDestino, LocalDateTime fim);

    // REQ19
    void definirLimiarBateriaMinima(double percentual);

    double getLimiarBateriaMinima();
}
