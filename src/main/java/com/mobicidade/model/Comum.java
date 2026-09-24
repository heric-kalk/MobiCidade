package com.mobicidade.model;

public class Comum extends Usuario {

    private static final double TARIFA_POR_MINUTO = 0.80;

    public Comum() {
        super();
    }

    public Comum(String nome, String cpf, String formaDePagamento) {
        super(nome, cpf, formaDePagamento);
    }

    @Override
    public double calcularCusto(double duracao) {
        return duracao * TARIFA_POR_MINUTO;
    }
}
