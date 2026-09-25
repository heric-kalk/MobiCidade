package com.mobicidade.model;

public class UsuarioComum extends Usuario {

    private static final double TARIFA_POR_MINUTO = 0.80;

    public UsuarioComum() {
        super();
    }

    public UsuarioComum(String nome, String cpf, String formaDePagamento) {
        super(nome, cpf, formaDePagamento);
    }

    @Override
    public double calcularCusto(double duracao) {
        return duracao * TARIFA_POR_MINUTO;
    }
}
