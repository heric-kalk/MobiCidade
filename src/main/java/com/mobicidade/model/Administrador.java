package com.mobicidade.model;

public class Administrador extends Usuario {
    public Administrador(String nome, String cpf, String senha, String formaDePagamento) {
        super(nome, cpf, senha, formaDePagamento);
    }

    @Override
    public String getTipo() {
        return "Administrador";
    }

    @Override
    public double calcularCusto(int duracaoMinutos) {
        return 0.0;
    }
}
