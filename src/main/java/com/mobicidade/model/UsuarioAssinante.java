package com.mobicidade.model;

public class UsuarioAssinante extends Usuario {
    public static final double TARIFA_EXCEDENTE_POR_MINUTO = 0.50;
    public static final int MINUTOS_PLANO_PADRAO = 500;

    private int minutosInclusos;
    private int minutosConsumidos;

    public UsuarioAssinante(String nome, String cpf, String senha,
                            String formaDePagamento, int minutosInclusos) {
        super(nome, cpf, senha, formaDePagamento);
        this.minutosInclusos = minutosInclusos;
    }

    @Override
    public String getTipo() {
        return "Assinante";
    }

    public int minutosRestantes() {
        return Math.max(0, minutosInclusos - minutosConsumidos);
    }

    @Override
    public double calcularCusto(int duracaoMinutos) {
        int excedente = Math.max(0, duracaoMinutos - minutosRestantes());
        return excedente * TARIFA_EXCEDENTE_POR_MINUTO;
    }

    @Override
    public void registrarUso(int duracaoMinutos) {
        minutosConsumidos += duracaoMinutos;
    }

    public int getMinutosInclusos() {
        return minutosInclusos;
    }

    public void setMinutosInclusos(int minutosInclusos) {
        this.minutosInclusos = minutosInclusos;
    }

    public int getMinutosConsumidos() {
        return minutosConsumidos;
    }

    @Override
    public String toString() {
        return super.toString() + " [plano: " + minutosRestantes()
                + "/" + minutosInclusos + " min restantes]";
    }
}
