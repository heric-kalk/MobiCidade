package com.mobicidade.ui;

import com.mobicidade.exception.EntidadeNaoEncontradaException;
import com.mobicidade.exception.RegraDeNegocioException;
import com.mobicidade.facade.IMobiCidadeFachada;
import com.mobicidade.facade.MobiCidadeFachada;
import com.mobicidade.model.AlertaClimatico;
import com.mobicidade.model.Bicicleta;
import com.mobicidade.model.Corrida;
import com.mobicidade.model.Estacao;
import com.mobicidade.model.Manutencao;
import com.mobicidade.model.Patinete;
import com.mobicidade.model.PrevisaoClimatica;
import com.mobicidade.model.StatusVeiculo;
import com.mobicidade.model.UsuarioAssinante;
import com.mobicidade.model.UsuarioComum;
import com.mobicidade.service.ProvedorPrevisaoSimulado;
import com.mobicidade.service.ProvedorPrevisaoSimulado.Cenario;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;

// REQ01 a REQ22
public class Simulacao {

    private static final LocalDateTime T0 = LocalDateTime.of(2026, 10, 29, 8, 0);

    private final PrintStream out;
    private final ProvedorPrevisaoSimulado clima = new ProvedorPrevisaoSimulado();
    private final IMobiCidadeFachada mobi = new MobiCidadeFachada(clima);
    private int verificacoes;
    private int falhas;

    public Simulacao(PrintStream out) {
        this.out = out;
    }

    public boolean executar() {
        out.println("==== SIMULAÇÃO AUTOMÁTICA DO MOBICIDADE ====");

        titulo("REQ06/REQ07/REQ08 - Clima (provedor simulado)");
        clima.definirCenario(Cenario.CEU_LIMPO);
        PrevisaoClimatica previsao = mobi.consultarPrevisaoDaCidade();
        out.println("   " + previsao.consultarPrevisao());
        verificar("REQ06", "consulta de previsão atual + próximas horas", previsao.temDados());
        verificar("REQ07", "céu limpo: sem alerta", previsao.verificarAlerta() == AlertaClimatico.SEM_ALERTA);

        clima.definirCenario(Cenario.TEMPESTADE);
        previsao = mobi.consultarPrevisaoDaCidade();
        out.println("   " + previsao.consultarPrevisao());
        verificar("REQ07", "tempestade prevista: alerta de chuva forte",
                previsao.verificarAlerta() == AlertaClimatico.ALERTA_CHUVA_FORTE);

        clima.definirCenario(Cenario.API_FORA_DO_AR);
        previsao = mobi.consultarPrevisaoDaCidade();
        out.println("   " + previsao.consultarPrevisao());
        verificar("REQ08", "API fora do ar: usa cache sinalizado como desatualizado",
                previsao.isDesatualizada() && previsao.temDados());

        titulo("REQ01 - Estações");
        Estacao boaViagem = mobi.cadastrarEstacao("Boa Viagem", -8.1190, -34.9040, 4);
        Estacao derby = mobi.cadastrarEstacao("Derby", -8.0560, -34.8990, 2);
        Estacao cais = mobi.cadastrarEstacao("Cais do Apolo", -8.0630, -34.8710, 1);
        Estacao vazia = mobi.cadastrarEstacao("Estação sem veículos", -8.0500, -34.8800, 3);
        verificar("REQ01", "4 estações cadastradas com ids automáticos", mobi.listarEstacoes().size() == 4
                && boaViagem.getId() == 1 && vazia.getId() == 4);
        bloqueado("REQ01", "capacidade zero é rejeitada",
                () -> mobi.cadastrarEstacao("Inválida", 0, 0, 0), null);

        titulo("REQ02/REQ03 - Veículos e disponibilidade");
        Bicicleta bike1 = mobi.cadastrarBicicleta(boaViagem.getId());
        Patinete pat80 = mobi.cadastrarPatinete(boaViagem.getId(), 80);
        Patinete pat10 = mobi.cadastrarPatinete(boaViagem.getId(), 10);
        Bicicleta bike2 = mobi.cadastrarBicicleta(boaViagem.getId());
        Bicicleta bikeDerby = mobi.cadastrarBicicleta(derby.getId());
        Bicicleta bikeCais = mobi.cadastrarBicicleta(cais.getId());
        verificar("REQ02", "veículos ancorados na estação (relação Estação <-> Veículo)",
                bike1.getEstacao() == boaViagem && boaViagem.getVeiculos().contains(pat80));
        verificar("REQ03", "Boa Viagem: 4 disponíveis e 0 vagas livres",
                mobi.veiculosDisponiveis(boaViagem.getId()) == 4 && mobi.vagasLivres(boaViagem.getId()) == 0);
        verificar("REQ03", "Cais do Apolo: 1 disponível e 0 vagas livres",
                mobi.veiculosDisponiveis(cais.getId()) == 1 && mobi.vagasLivres(cais.getId()) == 0);
        bloqueado("REQ02", "estação lotada não aceita novo veículo",
                () -> mobi.cadastrarBicicleta(cais.getId()), null);
        bloqueado("REQ02", "bateria fora de 0..100% é rejeitada",
                () -> mobi.cadastrarPatinete(derby.getId(), 150), null);

        titulo("REQ04/REQ05 - Usuários Comum e Assinante");
        UsuarioComum ana = mobi.cadastrarUsuarioComum("Ana Souza", "11111111111", "Cartão");
        UsuarioAssinante bruno = mobi.cadastrarUsuarioAssinante("Bruno Lima", "22222222222", "Pix", 30);
        verificar("REQ04/05", "2 usuários de tipos diferentes", mobi.listarUsuarios().size() == 2
                && "Comum".equals(ana.getTipo()) && "Assinante".equals(bruno.getTipo()));
        bloqueado("REQ04", "CPF inválido é rejeitado",
                () -> mobi.cadastrarUsuarioComum("Fulano", "123", "Pix"), null);
        bloqueado("REQ04", "CPF duplicado é rejeitado",
                () -> mobi.cadastrarUsuarioComum("Outra Ana", "11111111111", "Pix"), null);

        titulo("REQ17 - Bloqueio por chuva forte/tempestade");
        clima.definirCenario(Cenario.TEMPESTADE);
        bloqueado("REQ17", "corrida bloqueada com tempestade prevista",
                () -> mobi.iniciarCorrida(ana.getCpf(), bike1.getId(), boaViagem.getId(), T0), "REQ17");
        clima.definirCenario(Cenario.API_FORA_DO_AR);
        bloqueado("REQ17", "continua bloqueada com a API fora do ar (cache de tempestade desatualizado)",
                () -> mobi.iniciarCorrida(ana.getCpf(), bike1.getId(), boaViagem.getId(), T0), "REQ17");
        clima.definirCenario(Cenario.CEU_LIMPO);

        titulo("REQ18 e REQ13/REQ14 - Manutenção");
        Manutencao manut = mobi.registrarManutencao(bike2.getId(), "Freio traseiro com folga", T0.minusHours(2));
        verificar("REQ13", "veículo sai de circulação e libera a vaga",
                bike2.getStatus() == StatusVeiculo.MANUTENCAO && bike2.getEstacao() == null
                        && mobi.vagasLivres(boaViagem.getId()) == 1);
        bloqueado("REQ18", "corrida com veículo em manutenção é impedida",
                () -> mobi.iniciarCorrida(ana.getCpf(), bike2.getId(), boaViagem.getId(), T0), "REQ18");

        titulo("REQ19 - Bateria mínima do patinete");
        bloqueado("REQ19", "patinete com 10% de bateria (limiar 15%) é impedido",
                () -> mobi.iniciarCorrida(ana.getCpf(), pat10.getId(), boaViagem.getId(), T0), "REQ19");
        mobi.definirLimiarBateriaMinima(5);
        verificar("REQ19", "limiar é configurável", mobi.getLimiarBateriaMinima() == 5);
        mobi.definirLimiarBateriaMinima(15);

        titulo("REQ20 - Estação sem veículo disponível");
        bloqueado("REQ20", "desbloqueio em estação sem veículos é bloqueado",
                () -> mobi.iniciarCorrida(ana.getCpf(), bike1.getId(), vazia.getId(), T0), "REQ20");

        titulo("REQ09/REQ10/REQ11 - Corrida de usuário Comum (bicicleta, Boa Viagem -> Derby)");
        Corrida c1 = mobi.iniciarCorrida(ana.getCpf(), bike1.getId(), boaViagem.getId(), T0);
        verificar("REQ09", "corrida iniciada: veículo EM_USO fora da estação, com data/hora de início",
                c1.emAndamento() && bike1.getStatus() == StatusVeiculo.EM_USO && bike1.getEstacao() == null
                        && c1.getDataHoraInicio().equals(T0));
        bloqueado("REQ09", "mesmo usuário não inicia segunda corrida simultânea",
                () -> mobi.iniciarCorrida(ana.getCpf(), pat80.getId(), boaViagem.getId(), T0), null);
        c1 = mobi.finalizarCorrida(c1.getId(), derby.getId(), T0.plusMinutes(25));
        verificar("REQ10", "corrida finalizada com destino, fim e duração de 25 min",
                c1.finalizada() && c1.getDuracao() == 25 && c1.getEstacaoDestino() == derby);
        verificar("REQ11", "tarifa Comum = 25 min x R$ 0,80 = R$ 20,00", Math.abs(c1.getTarifa() - 20.0) < 1e-9);
        verificar("REQ10", "veículo ancorado em Derby e disponível novamente",
                bike1.getEstacao() == derby && bike1.getStatus() == StatusVeiculo.DISPONIVEL
                        && mobi.vagasLivres(derby.getId()) == 0);

        titulo("REQ21 - Destino sem vaga");
        Corrida c2 = mobi.iniciarCorrida(bruno.getCpf(), pat80.getId(), boaViagem.getId(), T0.plusMinutes(30));
        bloqueado("REQ21", "finalização em estação lotada (Derby) é bloqueada",
                () -> mobi.finalizarCorrida(c2.getId(), derby.getId(), T0.plusMinutes(60)), "REQ21");
        verificar("REQ21", "corrida continua em andamento após o bloqueio", mobi.buscarCorrida(c2.getId()).emAndamento());

        titulo("REQ22 - Duração maior que zero");
        bloqueado("REQ22", "término igual ao início (0 min) é rejeitado",
                () -> mobi.finalizarCorrida(c2.getId(), boaViagem.getId(), T0.plusMinutes(30)), "REQ22");
        bloqueado("REQ22", "término antes do início é rejeitado",
                () -> mobi.finalizarCorrida(c2.getId(), boaViagem.getId(), T0), "REQ22");

        titulo("REQ11/REQ12 - Assinante (plano de 30 min) devolvendo em zona livre");
        Corrida c2f = mobi.finalizarCorrida(c2.getId(), null, T0.plusMinutes(70));
        verificar("REQ12", "destino null = zona livre; veículo FORA_DE_ESTACAO",
                c2f.zonaLivre() && pat80.getStatus() == StatusVeiculo.FORA_DE_ESTACAO && pat80.getEstacao() == null);
        verificar("REQ11", "40 min: 30 do plano + 10 x R$ 0,50 + taxa R$ 5,00 = R$ 10,00",
                c2f.getDuracao() == 40 && Math.abs(c2f.getTarifa() - 10.0) < 1e-9);
        verificar("REQ11", "minutos do plano foram consumidos", bruno.minutosRestantes() == 0);
        verificar("REQ19", "bateria do patinete caiu de 80% para 60% (40 min x 0,5%/min)",
                Math.abs(pat80.getBateria() - 60.0) < 1e-9);
        mobi.recolherVeiculoParaEstacao(pat80.getId(), boaViagem.getId());
        verificar("REQ12", "veículo recolhido volta a uma estação, disponível",
                pat80.getStatus() == StatusVeiculo.DISPONIVEL && pat80.getEstacao() == boaViagem);

        titulo("REQ14 - Reintegração após manutenção");
        final int idManut = manut.getId();
        Manutencao concluida = mobi.concluirManutencao(idManut, boaViagem.getId(), T0.plusHours(1));
        verificar("REQ14", "veículo reintegrado à frota na estação escolhida",
                !concluida.emAndamento() && bike2.getStatus() == StatusVeiculo.DISPONIVEL && bike2.getEstacao() == boaViagem);
        bloqueado("REQ14", "manutenção já concluída não pode ser concluída de novo",
                () -> mobi.concluirManutencao(idManut, boaViagem.getId()), null);

        titulo("REQ15/REQ16 - Relatórios");
        String csv = mobi.relatorioCorridasPorEstacaoCsv(null, T0.minusDays(1), T0.plusDays(1));
        out.println(indentar(csv));
        verificar("REQ15", "CSV traz cabeçalho + 2 corridas", csv.trim().split("\n").length == 3);
        verificar("REQ15", "filtro por estação Derby (destino da corrida 1) traz 1 corrida",
                mobi.relatorioCorridasPorEstacao(derby.getId(), null, null).size() == 1);
        try {
            Path arquivo = Files.createTempFile("mobicidade-corridas-", ".csv");
            mobi.exportarRelatorioCorridasCsv(null, null, null, arquivo);
            verificar("REQ15", "CSV exportado em arquivo (" + arquivo + ")", Files.size(arquivo) > 0);
            Files.deleteIfExists(arquivo);
        } catch (IOException e) {
            verificar("REQ15", "exportação de CSV: " + e.getMessage(), false);
        }
        Map<String, Double> faturamento = mobi.relatorioFaturamentoPorTipoUsuario(null, null);
        out.println("   Faturamento: " + faturamento);
        verificar("REQ16", "faturamento Comum R$ 20,00 / Assinante R$ 10,00",
                Math.abs(faturamento.get("Comum") - 20.0) < 1e-9 && Math.abs(faturamento.get("Assinante") - 10.0) < 1e-9);

        titulo("CRUD - atualizar e remover (consistência entre as classes)");
        Estacao dados = new Estacao("Derby (renomeada)", derby.getLatitude(), derby.getLongitude(), 3);
        dados.setId(derby.getId());
        mobi.atualizarEstacao(dados);
        verificar("CRUD", "atualização altera a mesma instância e preserva os veículos da estação",
                mobi.buscarEstacao(derby.getId()) == derby && derby.getCapacidade() == 3
                        && derby.getVeiculos().contains(bike1));
        bloqueado("CRUD", "estação com veículos não pode ser removida",
                () -> mobi.removerEstacao(derby.getId()), null);
        mobi.removerVeiculo(bikeDerby.getId());
        verificar("CRUD", "remover veículo libera a vaga na estação", mobi.vagasLivres(derby.getId()) == 2);
        bloqueado("CRUD", "buscar id inexistente lança EntidadeNaoEncontrada",
                () -> mobi.buscarVeiculo(999), null, EntidadeNaoEncontradaException.class);
        mobi.removerEstacao(vazia.getId());
        verificar("CRUD", "estação vazia removida", mobi.listarEstacoes().size() == 3);
        mobi.removerVeiculo(bikeCais.getId());

        out.println();
        out.println("==== RESULTADO: " + (verificacoes - falhas) + "/" + verificacoes + " verificações OK"
                + (falhas == 0 ? " ====" : " — " + falhas + " FALHA(S) ===="));
        return falhas == 0;
    }

    private void titulo(String texto) {
        out.println();
        out.println("-- " + texto);
    }

    private void verificar(String req, String descricao, boolean condicao) {
        verificacoes++;
        if (!condicao) {
            falhas++;
        }
        out.println("   [" + (condicao ? " OK  " : "FALHA") + "] " + req + ": " + descricao);
    }

    private void bloqueado(String req, String descricao, Runnable acao, String trechoEsperado) {
        bloqueado(req, descricao, acao, trechoEsperado, RegraDeNegocioException.class);
    }

    private void bloqueado(String req, String descricao, Runnable acao, String trechoEsperado,
                           Class<? extends RuntimeException> tipo) {
        verificacoes++;
        String resultado;
        boolean passou = false;
        try {
            acao.run();
            resultado = "NÃO foi bloqueado!";
        } catch (RuntimeException e) {
            passou = tipo.isInstance(e) && (trechoEsperado == null
                    || (e.getMessage() != null && e.getMessage().contains(trechoEsperado)));
            resultado = "bloqueado -> " + e.getMessage();
        }
        if (!passou) {
            falhas++;
        }
        out.println("   [" + (passou ? " OK  " : "FALHA") + "] " + req + ": " + descricao);
        out.println("            " + resultado);
    }

    private static String indentar(String texto) {
        StringBuilder sb = new StringBuilder();
        for (String linha : texto.trim().split("\n")) {
            sb.append("   | ").append(linha).append('\n');
        }
        return sb.toString().stripTrailing();
    }
}
