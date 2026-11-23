package com.mobicidade.ui.controller;

import com.mobicidade.facade.IMobiCidadeFachada;
import com.mobicidade.model.Corrida;
import com.mobicidade.model.Estacao;
import com.mobicidade.model.Usuario;
import com.mobicidade.model.UsuarioAssinante;
import com.mobicidade.model.UsuarioComum;
import com.mobicidade.model.StatusVeiculo;
import com.mobicidade.model.Veiculo;
import com.mobicidade.ui.AlertaClima;
import com.mobicidade.ui.EstiloMensagem;
import com.mobicidade.ui.Navegador;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Comparator;

public class MenuUsuarioController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int INTERVALO_ATUALIZACAO_SEGUNDOS = 2;
    private static final String SEM_CORRIDA = "Você não possui nenhuma corrida em andamento.";

    private final IMobiCidadeFachada fachada;
    private Usuario usuario;
    private Corrida corridaAtual;
    private Timeline atualizacaoEstacoes;
    private List<Corrida> corridasDoSistema = List.of();

    @FXML
    private Label tituloConteudo;

    @FXML
    private Label descricaoConteudo;

    @FXML
    private Label alertaClima;

    @FXML
    private VBox painelInicio;

    @FXML
    private Label resumoCorridasAtivas;

    @FXML
    private Label resumoEstacoesAtivas;

    @FXML
    private Label resumoVeiculosDisponiveis;

    @FXML
    private TableView<Corrida> tabelaInicioCorridas;

    @FXML
    private TableColumn<Corrida, String> colunaInicioCorridaId;

    @FXML
    private TableColumn<Corrida, String> colunaInicioCorridaVeiculo;

    @FXML
    private TableColumn<Corrida, String> colunaInicioCorridaOrigem;

    @FXML
    private TableColumn<Corrida, String> colunaInicioCorridaDestino;

    @FXML
    private TableColumn<Corrida, String> colunaInicioCorridaStatus;

    @FXML
    private TableColumn<Corrida, String> colunaInicioCorridaData;

    @FXML
    private TableView<Estacao> tabelaInicioEstacoes;

    @FXML
    private TableColumn<Estacao, String> colunaInicioEstacaoId;

    @FXML
    private TableColumn<Estacao, String> colunaInicioEstacaoNome;

    @FXML
    private TableColumn<Estacao, String> colunaInicioEstacaoStatus;

    @FXML
    private TableColumn<Estacao, String> colunaInicioEstacaoDisponiveis;

    @FXML
    private TableColumn<Estacao, String> colunaInicioEstacaoVagas;

    @FXML
    private TableView<Veiculo> tabelaInicioVeiculos;

    @FXML
    private TableColumn<Veiculo, String> colunaInicioVeiculoEstacao;

    @FXML
    private TableColumn<Veiculo, String> colunaInicioVeiculoIdentificacao;

    @FXML
    private TableColumn<Veiculo, String> colunaInicioVeiculoStatus;

    @FXML
    private TableColumn<Veiculo, String> colunaInicioVeiculoBateria;

    @FXML
    private Label mensagemInicio;

    @FXML
    private VBox painelEstacoes;

    @FXML
    private Label mensagemEstacoes;

    @FXML
    private TableView<Estacao> tabelaEstacoes;

    @FXML
    private TableColumn<Estacao, String> colunaId;

    @FXML
    private TableColumn<Estacao, String> colunaNome;

    @FXML
    private TableColumn<Estacao, String> colunaLatitude;

    @FXML
    private TableColumn<Estacao, String> colunaLongitude;

    @FXML
    private TableColumn<Estacao, String> colunaCapacidade;

    @FXML
    private TableColumn<Estacao, String> colunaDisponiveis;

    @FXML
    private TableColumn<Estacao, String> colunaVagas;

    @FXML
    private TableColumn<Estacao, String> colunaStatus;

    @FXML
    private VBox formularioCorrida;

    @FXML
    private ComboBox<Estacao> selecaoEstacaoOrigem;

    @FXML
    private ComboBox<Veiculo> selecaoVeiculo;

    @FXML
    private Label mensagemCorrida;

    @FXML
    private VBox painelMinhaCorrida;

    @FXML
    private Label detalhesMinhaCorrida;

    @FXML
    private Label mensagemMinhaCorrida;

    @FXML
    private ComboBox<Estacao> selecaoEstacaoDestino;

    @FXML
    private CheckBox zonaLivre;

    @FXML
    private VBox painelHistorico;

    @FXML
    private TableView<Corrida> tabelaHistorico;

    @FXML
    private TableColumn<Corrida, String> colunaHistoricoId;

    @FXML
    private TableColumn<Corrida, String> colunaHistoricoVeiculo;

    @FXML
    private TableColumn<Corrida, String> colunaHistoricoOrigem;

    @FXML
    private TableColumn<Corrida, String> colunaHistoricoDestino;

    @FXML
    private TableColumn<Corrida, String> colunaHistoricoDuracao;

    @FXML
    private TableColumn<Corrida, String> colunaHistoricoTarifa;

    @FXML
    private TableColumn<Corrida, String> colunaHistoricoData;

    @FXML
    private VBox painelPerfil;

    @FXML
    private Label perfilCpf;

    @FXML
    private Label perfilTipo;

    @FXML
    private Label perfilPlano;

    @FXML
    private Button botaoRenovarPlano;

    @FXML
    private Label estatisticaCorridas;

    @FXML
    private Label estatisticaMinutos;

    @FXML
    private Label estatisticaGastos;

    @FXML
    private TextField campoNome;

    @FXML
    private TextField campoFormaPagamento;

    @FXML
    private Label mensagemPerfil;

    public MenuUsuarioController(IMobiCidadeFachada fachada, Usuario usuario) {
        this.fachada = fachada;
        this.usuario = usuario;
    }

    @FXML
    private void initialize() {
        configurarTabelaInicio();
        configurarTabelaEstacoes();
        configurarTabelaHistorico();
        mostrarInicio();

        atualizacaoEstacoes = new Timeline(new KeyFrame(
                Duration.seconds(INTERVALO_ATUALIZACAO_SEGUNDOS), evento -> {
                    if (painelInicio.isVisible()) {
                        carregarPainelInicio();
                    }
                    if (painelEstacoes.isVisible()) {
                        carregarEstacoes();
                    }
                }));
        atualizacaoEstacoes.setCycleCount(Timeline.INDEFINITE);
        atualizacaoEstacoes.playFromStart();
    }

    private void configurarTabelaInicio() {
        tabelaInicioCorridas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colunaInicioCorridaId.setCellValueFactory(d -> texto("#" + d.getValue().getId()));
        colunaInicioCorridaVeiculo.setCellValueFactory(d -> texto(descreverVeiculo(d.getValue().getVeiculo())));
        colunaInicioCorridaOrigem.setCellValueFactory(d -> texto(d.getValue().getEstacaoOrigem().getNome()));
        colunaInicioCorridaDestino.setCellValueFactory(d -> texto(descreverDestino(d.getValue())));
        colunaInicioCorridaStatus.setCellValueFactory(d -> texto(descreverStatusCorrida(d.getValue())));
        EstiloMensagem.destacarEstadosNegativos(colunaInicioCorridaStatus,
                status -> "Não iniciada".equalsIgnoreCase(status));
        colunaInicioCorridaData.setCellValueFactory(d -> texto(
                d.getValue().getDataHoraInicio() == null
                        ? "—"
                        : d.getValue().getDataHoraInicio().format(FORMATO_DATA)));

        tabelaInicioEstacoes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colunaInicioEstacaoId.setCellValueFactory(d -> texto(d.getValue().getId()));
        colunaInicioEstacaoNome.setCellValueFactory(d -> texto(d.getValue().getNome()));
        colunaInicioEstacaoStatus.setCellValueFactory(d -> texto(
                d.getValue().estaBloqueada() ? "Bloqueada" : "Ativa"));
        EstiloMensagem.destacarEstadosNegativos(colunaInicioEstacaoStatus,
                status -> "Bloqueada".equalsIgnoreCase(status));
        colunaInicioEstacaoDisponiveis.setCellValueFactory(d -> texto(d.getValue().veiculosDisponiveis()));
        colunaInicioEstacaoVagas.setCellValueFactory(d -> texto(d.getValue().vagasLivres()));

        tabelaInicioVeiculos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colunaInicioVeiculoEstacao.setCellValueFactory(
                d -> texto(descreverLocalizacaoVeiculo(d.getValue(), corridasDoSistema)));
        EstiloMensagem.destacarEstadosNegativos(colunaInicioVeiculoEstacao,
                local -> local != null && (local.startsWith("Em manutenção")
                        || local.startsWith("Fora de estação")
                        || local.startsWith("Sem estação vinculada")));
        colunaInicioVeiculoIdentificacao.setCellValueFactory(d -> texto(descreverVeiculo(d.getValue())));
        colunaInicioVeiculoStatus.setCellValueFactory(d -> texto(descreverStatusVeiculo(d.getValue())));
        EstiloMensagem.destacarEstadosNegativos(colunaInicioVeiculoStatus,
                status -> !"Disponível".equalsIgnoreCase(status));
        colunaInicioVeiculoBateria.setCellValueFactory(d -> texto(descreverBateria(d.getValue())));
    }

    private void configurarTabelaEstacoes() {
        tabelaEstacoes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colunaId.setCellValueFactory(d -> texto(d.getValue().getId()));
        colunaNome.setCellValueFactory(d -> texto(d.getValue().getNome()));
        colunaLatitude.setCellValueFactory(d -> texto(d.getValue().getLatitude()));
        colunaLongitude.setCellValueFactory(d -> texto(d.getValue().getLongitude()));
        colunaCapacidade.setCellValueFactory(d -> texto(d.getValue().getCapacidade()));
        // REQ03
        colunaDisponiveis.setCellValueFactory(d -> texto(d.getValue().veiculosDisponiveis()));
        colunaVagas.setCellValueFactory(d -> texto(d.getValue().vagasLivres()));
        colunaStatus.setCellValueFactory(d -> texto(d.getValue().getStatus()));
        EstiloMensagem.destacarEstadosNegativos(colunaStatus,
                status -> status != null && status.toUpperCase(java.util.Locale.ROOT).contains("BLOQUEAD"));
    }

    private void configurarTabelaHistorico() {
        tabelaHistorico.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colunaHistoricoId.setCellValueFactory(d -> texto("#" + d.getValue().getId()));
        colunaHistoricoVeiculo.setCellValueFactory(d -> texto(descreverVeiculo(d.getValue().getVeiculo())));
        colunaHistoricoOrigem.setCellValueFactory(d -> texto(d.getValue().getEstacaoOrigem().getNome()));
        colunaHistoricoDestino.setCellValueFactory(d -> texto(d.getValue().getEstacaoDestino() == null
                ? "Zona livre"
                : d.getValue().getEstacaoDestino().getNome()));
        colunaHistoricoDuracao.setCellValueFactory(d -> texto(d.getValue().getDuracao() + " min"));
        colunaHistoricoTarifa.setCellValueFactory(d -> texto(moeda(d.getValue().getTarifa())));
        colunaHistoricoData.setCellValueFactory(d -> texto(d.getValue().getDataHoraInicio().format(FORMATO_DATA)));
    }

    private static SimpleStringProperty texto(Object valor) {
        return new SimpleStringProperty(String.valueOf(valor));
    }

    private static String moeda(double valor) {
        return String.format("R$ %.2f", valor);
    }

    private static String descreverVeiculo(Veiculo veiculo) {
        return veiculo.getTipo() + " #" + veiculo.getId();
    }

    private void exibirApenas(Node painel, String titulo, String descricao) {
        for (Node p : List.of(painelInicio, painelEstacoes, formularioCorrida,
                painelMinhaCorrida, painelHistorico, painelPerfil)) {
            boolean exibir = p == painel;
            p.setVisible(exibir);
            p.setManaged(exibir);
        }
        tituloConteudo.setText(titulo);
        EstiloMensagem.informacao(descricaoConteudo, descricao);
        AlertaClima.ocultar(alertaClima);
    }

    @FXML
    private void mostrarInicio() {
        exibirApenas(painelInicio, "Bem-vindo ao MobiCidade",
                "Acompanhe suas corridas, consulte a situação das estações e veja cada veículo e seu status.");
        carregarPainelInicio();
        AlertaClima.atualizar(fachada, alertaClima);
    }

    private void carregarPainelInicio() {
        try {
            // Consulta cada fonte uma única vez por ciclo, mantendo os dados desta
            // atualização consistentes entre as três tabelas.
            List<Corrida> todasCorridas = fachada.listarCorridas();
            corridasDoSistema = todasCorridas;
            List<Estacao> estacoes = fachada.listarEstacoes().stream()
                    .sorted(Comparator.comparingInt(Estacao::getId))
                    .toList();
            List<Veiculo> todosVeiculos = fachada.listarVeiculos();

            List<Corrida> corridasUsuario = todasCorridas.stream()
                    .filter(corrida -> corrida.getUsuario() != null
                            && corrida.getUsuario().getCpf().equals(usuario.getCpf()))
                    .sorted(Comparator.comparing(Corrida::getDataHoraInicio,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .toList();

            // A tela inicial apresenta apenas uma corrida: a que estiver ativa.
            // Sem corrida ativa, mostra a mais recente para preservar o resultado
            // da última finalização (inclusive o destino Zona Livre).
            List<Corrida> corridaStatus = corridasUsuario.stream()
                    .filter(Corrida::emAndamento)
                    .findFirst()
                    .or(() -> corridasUsuario.stream().findFirst())
                    .map(List::of)
                    .orElseGet(List::of);

            List<Veiculo> veiculos = todosVeiculos.stream()
                    .sorted(Comparator
                            .comparing((Veiculo veiculo) -> descreverLocalizacaoVeiculo(veiculo, todasCorridas))
                            .thenComparingInt(Veiculo::getId))
                    .toList();

            // setAll substitui a coleção e refresh força a reavaliação das células.
            // Isso é importante porque as entidades são os mesmos objetos em memória,
            // mas seus status, vínculos de estação e quantidades podem ter mudado.
            tabelaInicioCorridas.getItems().setAll(corridaStatus);
            tabelaInicioEstacoes.getItems().setAll(estacoes);
            tabelaInicioVeiculos.getItems().setAll(veiculos);
            tabelaInicioCorridas.refresh();
            tabelaInicioEstacoes.refresh();
            tabelaInicioVeiculos.refresh();

            long corridasAtivas = corridaStatus.stream().filter(Corrida::emAndamento).count();
            long estacoesAtivas = estacoes.stream().filter(estacao -> !estacao.estaBloqueada()).count();
            long veiculosDisponiveis = veiculos.stream().filter(Veiculo::estaDisponivel).count();

            resumoCorridasAtivas.setText(String.valueOf(corridasAtivas));
            resumoEstacoesAtivas.setText(estacoesAtivas + " / " + estacoes.size());
            resumoVeiculosDisponiveis.setText(String.valueOf(veiculosDisponiveis));

            String detalheVeiculos = veiculos.isEmpty()
                    ? "Ainda não existem veículos cadastrados no sistema."
                    : "Veículos listados individualmente: " + veiculos.size() + ".";
            if (veiculos.isEmpty()) {
                EstiloMensagem.erro(mensagemInicio, detalheVeiculos);
            } else {
                EstiloMensagem.informacao(mensagemInicio, detalheVeiculos);
            }
        } catch (Exception e) {
            EstiloMensagem.erro(mensagemInicio,
                    "Não foi possível consultar os dados do painel: " + e.getMessage());
        }
    }

    private static String descreverDestino(Corrida corrida) {
        if (corrida.emAndamento()) {
            return "—";
        }
        if (corrida.getEstacaoDestino() != null) {
            return corrida.getEstacaoDestino().getNome();
        }
        return corrida.zonaLivre() ? "Zona Livre" : "Fora de estação";
    }

    private static String descreverStatusCorrida(Corrida corrida) {
        if (corrida.emAndamento()) {
            return "Em andamento";
        }
        if (corrida.finalizada()) {
            return "Finalizada";
        }
        return "Não iniciada";
    }

    private static String descreverStatusVeiculo(Veiculo veiculo) {
        StatusVeiculo status = veiculo.getStatus();
        if (status == null) {
            return "Indefinido";
        }
        return switch (status) {
            case DISPONIVEL -> "Disponível";
            case EM_USO -> "Em uso";
            case MANUTENCAO -> "Em manutenção";
            case FORA_DE_ESTACAO -> "Fora de estação";
        };
    }

    private String descreverLocalizacaoVeiculo(Veiculo veiculo, List<Corrida> corridas) {
        if (veiculo.getEstacao() != null) {
            return veiculo.getEstacao().getNome();
        }

        if (veiculo.getStatus() == StatusVeiculo.EM_USO) {
            return corridas.stream()
                    .filter(Corrida::emAndamento)
                    .filter(corrida -> corrida.getVeiculo() != null
                            && corrida.getVeiculo().getId() == veiculo.getId())
                    .findFirst()
                    .map(corrida -> "Em uso — corrida #" + corrida.getId())
                    .orElse("Em uso");
        }
        if (veiculo.getStatus() == StatusVeiculo.MANUTENCAO) {
            return "Em manutenção";
        }
        if (veiculo.getStatus() == StatusVeiculo.FORA_DE_ESTACAO) {
            return "Fora de estação";
        }
        return "Sem estação vinculada";
    }

    private static String descreverBateria(Veiculo veiculo) {
        if (veiculo instanceof com.mobicidade.model.Patinete patinete) {
            return String.format(java.util.Locale.forLanguageTag("pt-BR"), "%.1f%%", patinete.getBateria());
        }
        return "—";
    }

    @FXML
    private void mostrarEstacoes() {
        exibirApenas(painelEstacoes, "Estações",
                "Consulte as estações, veículos disponíveis e vagas livres.");
        carregarEstacoes();
    }

    @FXML
    private void mostrarIniciarCorrida() {
        exibirApenas(formularioCorrida, "Iniciar corrida",
                "Selecione uma estação de origem e um veículo disponível.");
        carregarEstacoesParaCorrida();
    }

    @FXML
    private void mostrarMinhaCorrida() {
        exibirApenas(painelMinhaCorrida, "Minha corrida",
                "Acompanhe sua viagem e finalize-a ao chegar ao destino.");
        carregarMinhaCorrida();
    }

    @FXML
    private void mostrarHistorico() {
        exibirApenas(painelHistorico, "Histórico de corridas",
                "Consulte suas viagens anteriores, duração, destino e tarifas.");
        carregarHistorico();
    }

    @FXML
    private void mostrarPerfil() {
        exibirApenas(painelPerfil, "Meu perfil",
                "Consulte suas informações pessoais, seu plano e suas estatísticas.");
        carregarPerfil();
    }

    private void carregarEstacoes() {
        try {
            List<Estacao> estacoes = fachada.listarEstacoes();
            tabelaEstacoes.getItems().setAll(estacoes);
            tabelaEstacoes.refresh();
            if (estacoes.isEmpty()) {
                EstiloMensagem.erro(mensagemEstacoes, "Não existem estações cadastradas no momento.");
            } else {
                EstiloMensagem.informacao(mensagemEstacoes,
                        "Total de estações cadastradas: " + estacoes.size());
            }
        } catch (Exception e) {
            EstiloMensagem.erro(mensagemEstacoes, "Erro ao carregar as estações: " + e.getMessage());
        }
    }

    private void carregarEstacoesParaCorrida() {
        List<Estacao> estacoes = fachada.listarEstacoes().stream()
                .filter(estacao -> !estacao.estaBloqueada())
                .toList();

        selecaoEstacaoOrigem.setItems(FXCollections.observableArrayList(estacoes));
        selecaoVeiculo.getItems().clear();
        if (estacoes.isEmpty()) {
            EstiloMensagem.erro(mensagemCorrida, "Não existem estações ativas disponíveis.");
        } else {
            EstiloMensagem.informacao(mensagemCorrida,
                    "Selecione uma estação para visualizar os veículos.");
        }
    }

    @FXML
    private void atualizarVeiculosDisponiveis() {
        Estacao estacao = selecaoEstacaoOrigem.getValue();
        selecaoVeiculo.getItems().clear();

        if (estacao == null) {
            return;
        }

        List<Veiculo> veiculos = estacao.getVeiculos().stream()
                .filter(Veiculo::estaDisponivel)
                .toList();

        selecaoVeiculo.setItems(FXCollections.observableArrayList(veiculos));
        if (veiculos.isEmpty()) {
            EstiloMensagem.erro(mensagemCorrida, "Esta estação não possui veículos disponíveis.");
        } else {
            EstiloMensagem.informacao(mensagemCorrida, "Veículos disponíveis: " + veiculos.size());
        }
    }

    // REQ09, REQ17 a REQ20
    @FXML
    private void iniciarCorrida() {
        Estacao estacao = selecaoEstacaoOrigem.getValue();
        Veiculo veiculo = selecaoVeiculo.getValue();

        if (estacao == null) {
            EstiloMensagem.erro(mensagemCorrida, "Selecione uma estação de origem.");
            return;
        }

        if (veiculo == null) {
            EstiloMensagem.erro(mensagemCorrida, "Selecione um veículo.");
            return;
        }

        try {
            Corrida corrida = fachada.iniciarCorrida(usuario.getCpf(), veiculo.getId(), estacao.getId());
            atualizarVeiculosDisponiveis();
            EstiloMensagem.sucesso(mensagemCorrida, "Corrida #" + corrida.getId() + " iniciada com sucesso!");
        } catch (Exception e) {
            EstiloMensagem.erro(mensagemCorrida, "Não foi possível iniciar a corrida: " + e.getMessage());
        }
    }

    private void carregarMinhaCorrida() {
        corridaAtual = fachada.listarCorridas().stream()
                .filter(Corrida::emAndamento)
                .filter(c -> c.getUsuario().getCpf().equals(usuario.getCpf()))
                .findFirst()
                .orElse(null);

        zonaLivre.setSelected(false);
        EstiloMensagem.limpar(mensagemMinhaCorrida);

        if (corridaAtual == null) {
            EstiloMensagem.erro(detalhesMinhaCorrida, SEM_CORRIDA);
            selecaoEstacaoDestino.getItems().clear();
            return;
        }

        EstiloMensagem.informacao(detalhesMinhaCorrida,
                "Corrida: #" + corridaAtual.getId() + "\n"
                        + "Veículo: " + descreverVeiculo(corridaAtual.getVeiculo()) + "\n"
                        + "Origem: " + corridaAtual.getEstacaoOrigem().getNome() + "\n"
                        + "Início: " + corridaAtual.getDataHoraInicio().format(FORMATO_DATA));

        List<Estacao> estacoes = fachada.listarEstacoes().stream()
                .filter(estacao -> !estacao.ehZonaLivre())
                .filter(Estacao::temVaga)
                .toList();

        selecaoEstacaoDestino.setItems(FXCollections.observableArrayList(estacoes));
        selecaoEstacaoDestino.setDisable(false);

        if (estacoes.isEmpty()) {
            EstiloMensagem.erro(mensagemMinhaCorrida, "Não existem estações com vagas disponíveis no momento.");
        }
    }

    // REQ12
    @FXML
    private void alternarZonaLivre() {
        selecaoEstacaoDestino.setDisable(zonaLivre.isSelected());
        if (zonaLivre.isSelected()) {
            selecaoEstacaoDestino.setValue(null);
        }
    }

    // REQ10 a REQ12, REQ21, REQ22
    @FXML
    private void finalizarCorrida() {
        if (corridaAtual == null) {
            EstiloMensagem.erro(mensagemMinhaCorrida, "Você não possui uma corrida em andamento.");
            return;
        }

        Estacao destino = selecaoEstacaoDestino.getValue();

        if (destino == null && !zonaLivre.isSelected()) {
            EstiloMensagem.erro(mensagemMinhaCorrida, "Selecione uma estação de destino ou marque a devolução em zona livre.");
            return;
        }

        try {
            Corrida finalizada = fachada.finalizarCorrida(corridaAtual.getId(),
                    zonaLivre.isSelected() || destino == null ? null : destino.getId());

            EstiloMensagem.sucesso(mensagemMinhaCorrida, "Corrida finalizada com sucesso!\n"
                    + "Duração: " + finalizada.getDuracao() + " minutos\n"
                    + "Tarifa: " + moeda(finalizada.getTarifa()));

            corridaAtual = null;
            EstiloMensagem.erro(detalhesMinhaCorrida, SEM_CORRIDA);
            selecaoEstacaoDestino.getItems().clear();
            zonaLivre.setSelected(false);
            selecaoEstacaoDestino.setDisable(false);

        } catch (Exception e) {
            EstiloMensagem.erro(mensagemMinhaCorrida, "Não foi possível finalizar a corrida: " + e.getMessage());
        }
    }

    private List<Corrida> corridasFinalizadasDoUsuario() {
        return fachada.listarCorridas().stream()
                .filter(Corrida::finalizada)
                .filter(c -> c.getUsuario().getCpf().equals(usuario.getCpf()))
                .toList();
    }

    private void carregarHistorico() {
        List<Corrida> corridas = corridasFinalizadasDoUsuario().stream()
                .sorted((a, b) -> b.getDataHoraInicio().compareTo(a.getDataHoraInicio()))
                .toList();

        tabelaHistorico.setItems(FXCollections.observableArrayList(corridas));
    }

    private void carregarPerfil() {
        perfilCpf.setText(usuario.getCpf());
        perfilTipo.setText(usuario.getTipo());
        campoNome.setText(usuario.getNome());
        campoFormaPagamento.setText(usuario.getFormaDePagamento());

        if (usuario instanceof UsuarioAssinante assinante) {
            perfilPlano.setText(
                    "Minutos inclusos: " + assinante.getMinutosInclusos() + "\n"
                            + "Minutos consumidos: " + assinante.getMinutosConsumidos() + "\n"
                            + "Minutos restantes: " + assinante.minutosRestantes() + "\n"
                            + String.format("Tarifa excedente: R$ %.2f/min",
                                    UsuarioAssinante.TARIFA_EXCEDENTE_POR_MINUTO));
            botaoRenovarPlano.setVisible(true);
            botaoRenovarPlano.setManaged(true);
        } else {
            perfilPlano.setText(String.format("Plano comum\nTarifa: R$ %.2f por minuto utilizado.",
                    UsuarioComum.TARIFA_POR_MINUTO));
            botaoRenovarPlano.setVisible(false);
            botaoRenovarPlano.setManaged(false);
        }

        List<Corrida> corridas = corridasFinalizadasDoUsuario();
        estatisticaCorridas.setText(String.valueOf(corridas.size()));
        estatisticaMinutos.setText(corridas.stream().mapToInt(Corrida::getDuracao).sum() + " min");
        estatisticaGastos.setText(moeda(corridas.stream().mapToDouble(Corrida::getTarifa).sum()));

        EstiloMensagem.limpar(mensagemPerfil);
    }

    @FXML
    private void renovarPlano() {
        try {
            usuario = fachada.renovarPlano(usuario.getCpf());
            carregarPerfil();
            EstiloMensagem.sucesso(mensagemPerfil, "Plano renovado com sucesso!");
        } catch (Exception e) {
            EstiloMensagem.erro(mensagemPerfil, "Não foi possível renovar o plano: " + e.getMessage());
        }
    }

    @FXML
    private void salvarPerfil() {
        String nomeAntigo = usuario.getNome();
        String pagamentoAntigo = usuario.getFormaDePagamento();

        String novoNome = campoNome.getText().trim();
        String novaFormaPagamento = campoFormaPagamento.getText().trim();

        if (novoNome.isEmpty()) {
            EstiloMensagem.erro(mensagemPerfil, "O nome não pode ficar vazio.");
            return;
        }

        if (novaFormaPagamento.isEmpty()) {
            EstiloMensagem.erro(mensagemPerfil, "Informe uma forma de pagamento.");
            return;
        }

        try {
            usuario.setNome(novoNome);
            usuario.setFormaDePagamento(novaFormaPagamento);
            usuario = fachada.atualizarUsuario(usuario);

            carregarPerfil();
            EstiloMensagem.sucesso(mensagemPerfil, "Perfil atualizado com sucesso!");

        } catch (Exception e) {
            usuario.setNome(nomeAntigo);
            usuario.setFormaDePagamento(pagamentoAntigo);
            campoNome.setText(nomeAntigo);
            campoFormaPagamento.setText(pagamentoAntigo);
            EstiloMensagem.erro(mensagemPerfil, "Não foi possível atualizar o perfil: " + e.getMessage());
        }
    }

    @FXML
    private void voltarLogin() {
        try {
            atualizacaoEstacoes.stop();
            Navegador.exibir(tituloConteudo, Navegador.login(fachada));
        } catch (Exception e) {
            atualizacaoEstacoes.play();
            EstiloMensagem.erro(descricaoConteudo, "Não foi possível voltar ao login: " + e.getMessage());
        }
    }
}
