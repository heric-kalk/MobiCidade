package com.mobicidade.model;

import java.time.LocalDateTime;

public class PrevisaoClimatica {

    private LocalDateTime ultimaConsulta;
    private String dados;

    public PrevisaoClimatica() {
    }

    public PrevisaoClimatica(LocalDateTime ultimaConsulta, String dados) {
        this.ultimaConsulta = ultimaConsulta;
        this.dados = dados;
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
}
