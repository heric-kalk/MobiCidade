package com.mobicidade.model;

import com.mobicidade.exception.RegraDeNegocioException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Estacao {
    private int id;
    private String nome;
    private double latitude;
    private double longitude;
    private int capacidade;
    private final List<Veiculo> veiculos = new ArrayList<>();

    public Estacao() {
    }

    public Estacao(String nome, double latitude, double longitude, int capacidade) {
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacidade = capacidade;
    }

    // REQ03
    public int veiculosDisponiveis() {
        int total = 0;
        for (Veiculo v : veiculos) {
            if (v.getStatus() == StatusVeiculo.DISPONIVEL) {
                total++;
            }
        }
        return total;
    }

    // REQ03
    public int vagasLivres() {
        return capacidade - veiculos.size();
    }

    public boolean temVaga() {
        return vagasLivres() > 0;
    }

    public void receberVeiculo(Veiculo veiculo) {
        if (veiculo == null) {
            throw new IllegalArgumentException("O veículo não pode ser nulo.");
        }
        if (veiculos.contains(veiculo)) {
            return;
        }
        if (!temVaga()) {
            throw new RegraDeNegocioException("A estação " + id + " não possui vagas livres.");
        }
        veiculos.add(veiculo);
        veiculo.setEstacao(this);
    }

    public void liberarVeiculo(Veiculo veiculo) {
        if (veiculo != null && veiculos.remove(veiculo)) {
            veiculo.setEstacao(null);
        }
    }

    public boolean possuiVeiculo(Veiculo veiculo) {
        return veiculos.contains(veiculo);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    @Override
    public String toString() {
        return "Estação " + id + " (" + nome + ") [" + veiculosDisponiveis() + " disponíveis, "
                + vagasLivres() + " vagas livres de " + capacidade + "]";
    }
}
