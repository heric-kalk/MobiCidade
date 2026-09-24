package com.mobicidade.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Corrida {

    private static final double TAXA_ZONA_LIVRE = 5.0;

    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private double duracao;
    private double tarifa;

    private Usuario usuario;
    private Veiculo veiculo;
    private Estacao estacaoOrigem;
    private Estacao estacaoDestino;

    public Corrida() {
    }
    
    public Corrida(Usuario usuario, Veiculo veiculo, Estacao estacaoOrigem) {
        this.usuario = usuario;
        this.veiculo = veiculo;
        this.estacaoOrigem = estacaoOrigem;
        this.dataHoraInicio = LocalDateTime.now();
        if (veiculo != null) {
            veiculo.alterarDisponibilidade(StatusVeiculo.EM_USO);
        }
    }

    public void finalizarCorrida() {
        this.dataHoraFim = LocalDateTime.now();
        this.duracao = calcularDuracao();
        this.tarifa = calcularTarifa();
        if (veiculo != null) {
            veiculo.alterarDisponibilidade(StatusVeiculo.DISPONIVEL);
        }
    }

    public int calcularDuracao() {
        if (dataHoraInicio == null || dataHoraFim == null) {
            return 0;
        }
        return (int) Duration.between(dataHoraInicio, dataHoraFim).toMinutes();
    }

    public double calcularTarifa() {
        double custoBase = (usuario != null) ? usuario.calcularCusto(calcularDuracao()) : 0.0;
        double taxaExtra = (estacaoDestino == null) ? TAXA_ZONA_LIVRE : 0.0;
        return custoBase + taxaExtra;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public double getDuracao() {
        return duracao;
    }

    public void setDuracao(double duracao) {
        this.duracao = duracao;
    }

    public double getTarifa() {
        return tarifa;
    }

    public void setTarifa(double tarifa) {
        this.tarifa = tarifa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public Estacao getEstacaoOrigem() {
        return estacaoOrigem;
    }

    public void setEstacaoOrigem(Estacao estacaoOrigem) {
        this.estacaoOrigem = estacaoOrigem;
    }

    public Estacao getEstacaoDestino() {
        return estacaoDestino;
    }

    public void setEstacaoDestino(Estacao estacaoDestino) {
        this.estacaoDestino = estacaoDestino;
    }
}
