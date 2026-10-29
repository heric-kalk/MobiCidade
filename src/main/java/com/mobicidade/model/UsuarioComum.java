package com.mobicidade.model;

public class UsuarioComum extends Usuario {

    public static final double TARIFA_POR_MINUTO = 0.80;

    public UsuarioComum() {
        super();
    }

    public UsuarioComum(String nome, String cpf, String formaDePagamento) {
        super(nome, cpf, formaDePagamento);
    }

    @Override
    public String getTipo() {
        return "Comum";
    }

    @Override
    public double calcularCusto(int duracaoMinutos) {
        return duracaoMinutos * TARIFA_POR_MINUTO;
    }
}
