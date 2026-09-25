package com.mobicidade.model;

public class UsuarioAssinante extends Usuario {

    private static final double TARIFA_EXCEDENTE_POR_MINUTO = 0.50;
    private int minutosInclusos;

    public UsuarioAssinante() {
        super();
    }

    public UsuarioAssinante(String nome, String cpf, String formaDePagamento, int minutosInclusos) {
        super(nome, cpf, formaDePagamento);
        this.minutosInclusos = minutosInclusos;
    }

    @Override
    public double calcularCusto(double duracao) {
        double minutosExcedentes = duracao - minutosInclusos;
        if (minutosExcedentes <= 0) {
            return 0.0;
        }
        return minutosExcedentes * TARIFA_EXCEDENTE_POR_MINUTO;
    }

    public int getMinutosInclusos() {
        return minutosInclusos;
    }

    public void setMinutosInclusos(int minutosInclusos) {
        this.minutosInclusos = minutosInclusos;
    }
}
