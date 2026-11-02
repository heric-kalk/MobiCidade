package com.mobicidade.model;

import java.time.LocalDateTime;

public class Manutencao {

    private int id;
    private String descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Veiculo veiculo;

    public Manutencao() {
    }

    public Manutencao(String descricao, Veiculo veiculo) {
        this.descricao = descricao;
        this.veiculo = veiculo;
    }

    // REQ13
    public void registrar(LocalDateTime inicio) {
        this.dataInicio = inicio;
        if (veiculo.getEstacao() != null) {
            veiculo.getEstacao().liberarVeiculo(veiculo);
        }
        veiculo.alterarDisponibilidade(StatusVeiculo.MANUTENCAO);
    }

    // REQ14
    public void finalizar(Estacao destino, LocalDateTime fim) {
        this.dataFim = fim;
        veiculo.estacionarEm(destino);
        if (veiculo instanceof Patinete) {
            ((Patinete) veiculo).recarregar();
        }
    }

    public boolean emAndamento() {
        return dataInicio != null && dataFim == null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    @Override
    public String toString() {
        return "Manutenção " + id + " [" + descricao + " / " + (veiculo != null ? veiculo.getTipo() + " " + veiculo.getId() : "?")
                + " / " + (emAndamento() ? "em andamento" : "concluída") + "]";
    }
}
