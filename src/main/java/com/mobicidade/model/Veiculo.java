package com.mobicidade.model;

public abstract class Veiculo {

    private int id;
    private StatusVeiculo status;
    private Estacao estacao;

    protected Veiculo() {
        this.status = StatusVeiculo.DISPONIVEL;
    }

    protected Veiculo(int id, Estacao estacao) {
        this.id = id;
        this.estacao = estacao;
        this.status = StatusVeiculo.DISPONIVEL;
    }

    public void alterarDisponibilidade(StatusVeiculo status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StatusVeiculo getStatus() {
        return status;
    }

    public void setStatus(StatusVeiculo status) {
        this.status = status;
    }

    public Estacao getEstacao() {
        return estacao;
    }

    public void setEstacao(Estacao estacao) {
        this.estacao = estacao;
    }
}
