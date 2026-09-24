package com.mobicidade.model;

public class Patinete extends Veiculo {

    private double bateria;

    public Patinete() {
        super();
    }

    public Patinete(int id, Estacao estacao, double bateria) {
        super(id, estacao);
        this.bateria = bateria;
    }

    public double getBateria() {
        return bateria;
    }

    public void setBateria(double bateria) {
        this.bateria = bateria;
    }
}
