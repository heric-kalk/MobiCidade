package com.mobicidade.model;

public abstract class Usuario {

    private String nome;
    private String cpf;
    private String formaDePagamento;

    protected Usuario() {
    }

    protected Usuario(String nome, String cpf, String formaDePagamento) {
        this.nome = nome;
        this.cpf = cpf;
        this.formaDePagamento = formaDePagamento;
    }

    // REQ16
    public abstract String getTipo();

    // REQ11
    public abstract double calcularCusto(int duracaoMinutos);

    public void registrarUso(int duracaoMinutos) {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getFormaDePagamento() {
        return formaDePagamento;
    }

    public void setFormaDePagamento(String formaDePagamento) {
        this.formaDePagamento = formaDePagamento;
    }

    @Override
    public String toString() {
        return getTipo() + " " + nome + " (CPF " + cpf + ", " + formaDePagamento + ")";
    }
}
