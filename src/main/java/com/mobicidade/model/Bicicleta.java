package com.mobicidade.model;

public class Bicicleta extends Veiculo {
    public Bicicleta(Estacao estacao) {
        super(estacao);
    }

    @Override
    public String getTipo() {
        return "Bicicleta";
    }
}
