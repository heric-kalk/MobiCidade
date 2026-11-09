package com.mobicidade.model;

public class Patinete extends Veiculo {
    public static final double CONSUMO_BATERIA_POR_MINUTO = 0.5;

    private double bateria;

    public Patinete(Estacao estacao, double bateria) {
        super(estacao);
        this.bateria = bateria;
    }

    // REQ19
    public boolean bateriaSuficiente(double limiarMinimo) {
        return bateria >= limiarMinimo;
    }

    public void consumirBateria(int minutosDeUso) {
        bateria = Math.max(0.0, bateria - minutosDeUso * CONSUMO_BATERIA_POR_MINUTO);
    }

    public void recarregar() {
        bateria = 100.0;
    }

    public double getBateria() {
        return bateria;
    }

    public void setBateria(double bateria) {
        this.bateria = bateria;
    }

    @Override
    public String getTipo() {
        return "Patinete";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" bateria %.1f%%", bateria);
    }
}
