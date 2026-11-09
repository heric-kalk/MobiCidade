package com.mobicidade.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Corrida {
    // REQ12
    public static final double TAXA_ZONA_LIVRE = 5.0;

    private int id;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private int duracao;
    private double tarifa;

    private Usuario usuario;
    private Veiculo veiculo;
    private Estacao estacaoOrigem;
    private Estacao estacaoDestino;

    public Corrida() {
    }

    // REQ09
    public void iniciar(LocalDateTime inicio) {
        this.dataHoraInicio = inicio;
        estacaoOrigem.liberarVeiculo(veiculo);
        veiculo.alterarDisponibilidade(StatusVeiculo.EM_USO);
    }

    // REQ10 a REQ12
    public void finalizar(Estacao destino, LocalDateTime fim) {
        this.dataHoraFim = fim;
        this.estacaoDestino = destino;
        this.duracao = calcularDuracao();
        this.tarifa = calcularTarifa();

        usuario.registrarUso(duracao);
        if (veiculo instanceof Patinete) {
            ((Patinete) veiculo).consumirBateria(duracao);
        }
        if (destino != null) {
            veiculo.estacionarEm(destino);
        } else {
            veiculo.alterarDisponibilidade(StatusVeiculo.FORA_DE_ESTACAO);
        }
    }

    public boolean emAndamento() {
        return dataHoraInicio != null && dataHoraFim == null;
    }

    public boolean finalizada() {
        return dataHoraFim != null;
    }

    public boolean zonaLivre() {
        return finalizada() && estacaoDestino != null && estacaoDestino.ehZonaLivre();
    }

    public int calcularDuracao() {
        return calcularDuracao(dataHoraFim);
    }

    public int calcularDuracao(LocalDateTime fim) {
        if (dataHoraInicio == null || fim == null || !fim.isAfter(dataHoraInicio)) {
            return 0;
        }
        long nanos = Duration.between(dataHoraInicio, fim).toNanos();
        return (int) ((nanos + 60_000_000_000L - 1) / 60_000_000_000L);
    }

    private double calcularTarifa() {
        double custoBase = usuario != null ? usuario.calcularCusto(calcularDuracao()) : 0.0;
        double taxaExtra = estacaoDestino != null && estacaoDestino.ehZonaLivre() ? TAXA_ZONA_LIVRE : 0.0;
        return custoBase + taxaExtra;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public int getDuracao() {
        return duracao;
    }

    public double getTarifa() {
        return tarifa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public Estacao getEstacaoOrigem() {
        return estacaoOrigem;
    }

    public void setEstacaoOrigem(Estacao estacaoOrigem) {
        this.estacaoOrigem = estacaoOrigem;
    }

    public Estacao getEstacaoDestino() {
        return estacaoDestino;
    }

    @Override
    public String toString() {
        String situacao = emAndamento() ? "em andamento"
                : String.format("%d min, R$ %.2f%s", duracao, tarifa, zonaLivre() ? " (zona livre)" : "");
        return "Corrida " + id + " [" + (usuario != null ? usuario.getNome() : "?") + " / "
                + (veiculo != null ? veiculo.getTipo() + " " + veiculo.getId() : "?") + " / " + situacao + "]";
    }
}
