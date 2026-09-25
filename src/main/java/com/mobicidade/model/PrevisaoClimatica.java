package com.mobicidade.model;

import java.time.LocalDateTime;

public class PrevisaoClimatica {

    private LocalDateTime ultimaConsulta;
    private String dados;
    private double latitude;
    private double longitude;

    public PrevisaoClimatica() {
    }

    public PrevisaoClimatica(LocalDateTime ultimaConsulta, String dados, double latitude, double longitude) {
        this.ultimaConsulta = ultimaConsulta;
        this.dados = dados;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String verificarAlerta() {
        if (dados == null) {
            return "SEM_DADOS";
        }
        boolean chuvaForte = dados.contains("chuva_forte") || dados.contains("tempestade");
        return chuvaForte ? "ALERTA_CHUVA_FORTE" : "SEM_ALERTA";
    }

    public String consultarPrevisao() {
        if (dados == null) {
            return "PREVISAO_INDISPONIVEL";
        }
        return dados;
    }

    public LocalDateTime getUltimaConsulta() {
        return ultimaConsulta;
    }

    public void setUltimaConsulta(LocalDateTime ultimaConsulta) {
        this.ultimaConsulta = ultimaConsulta;
    }

    public String getDados() {
        return dados;
    }

    public void setDados(String dados) {
        this.dados = dados;
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
}
