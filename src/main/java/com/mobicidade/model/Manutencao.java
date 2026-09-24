package com.mobicidade.model;

import java.time.LocalDateTime;

public class Manutencao {

    private String descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Veiculo veiculo;

    public Manutencao() {
    }

    public Manutencao(String descricao, Veiculo veiculo) {
        this.descricao = descricao;
        this.veiculo = veiculo;
    }

    public void registrar() {
        this.dataInicio = LocalDateTime.now();
        if (veiculo != null) {
            veiculo.alterarDisponibilidade(StatusVeiculo.MANUTENCAO);
        }
    }

    public void finalizar() {
        this.dataFim = LocalDateTime.now();
        if (veiculo != null) {
            veiculo.alterarDisponibilidade(StatusVeiculo.DISPONIVEL);
        }
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }
}
