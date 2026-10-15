package com.mobicidade.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Estacao {
    private int id;
    private double latitude;
    private double longitude;
    private int capacidade;
    private List<Veiculo> veiculos = new ArrayList<>();

    public Estacao() {
    }

    public Estacao(int id, double latitude, double longitude, int capacidade) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacidade = capacidade;
    }

    public int veiculosDisponiveis() {
        int total = 0;
        for (Veiculo v : veiculos) {
            if (v.getStatus() == StatusVeiculo.DISPONIVEL) {
                total++;
            }
        }
        return total;
    }

    public int vagasLivres() {
        return capacidade - veiculos.size();
    }

    public void adicionarVeiculo(Veiculo veiculo) {
        if (veiculo == null) {
            throw new IllegalArgumentException("O veículo não pode ser nulo.");
        }

        boolean jaCadastrado = veiculos.stream()
                .anyMatch(atual -> atual.getId() == veiculo.getId());
        if (!jaCadastrado) {
            this.veiculos.add(veiculo);
        }
    }

    public void removerVeiculo(Veiculo veiculo) {
        if (veiculo != null) {
            this.veiculos.removeIf(atual -> atual.getId() == veiculo.getId());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public List<Veiculo> getVeiculos() {
        return Collections.unmodifiableList(veiculos);
    }
}
