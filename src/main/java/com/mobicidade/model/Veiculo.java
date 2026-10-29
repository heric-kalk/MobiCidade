package com.mobicidade.model;

public abstract class Veiculo {

    private int id;
    private StatusVeiculo status;
    private Estacao estacao;

    protected Veiculo() {
        this.status = StatusVeiculo.DISPONIVEL;
    }

    protected Veiculo(Estacao estacao) {
        this.estacao = estacao;
        this.status = StatusVeiculo.DISPONIVEL;
    }

    public abstract String getTipo();

    public void alterarDisponibilidade(StatusVeiculo status) {
        this.status = status;
    }

    public boolean estaDisponivel() {
        return status == StatusVeiculo.DISPONIVEL;
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

    @Override
    public String toString() {
        return getTipo() + " " + id + " [" + status + (estacao != null ? ", estação " + estacao.getId() : "") + "]";
    }
}
