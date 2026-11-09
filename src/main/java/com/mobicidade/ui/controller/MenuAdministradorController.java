package com.mobicidade.ui.controller;

import com.mobicidade.facade.IMobiCidadeFachada;
import com.mobicidade.model.Corrida;
import com.mobicidade.model.Estacao;
import com.mobicidade.model.Manutencao;
import com.mobicidade.model.Patinete;
import com.mobicidade.model.StatusVeiculo;
import com.mobicidade.model.Veiculo;
import com.mobicidade.ui.AlertaClima;
import com.mobicidade.ui.Navegador;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.File;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MenuAdministradorController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
    private static final String TIPO_BICICLETA = "Bicicleta";
    private static final String TIPO_PATINETE = "Patinete";

    private final IMobiCidadeFachada fachada;
    private Timeline atualizacaoRelatorios;

    @FXML
    private Label tituloConteudo;

    @FXML
    private Label descricaoConteudo;

    @FXML
    private Label alertaClima;

    @FXML
    private VBox formularioEstacao;

    @FXML
    private VBox acoesBloqueioEstacao;

    @FXML
    private TableView<Estacao> tabelaEstacoes;

    @FXML
    private TableColumn<Estacao, Integer> colunaId;

    @FXML
    private TableColumn<Estacao, String> colunaNome;

    @FXML
    private TableColumn<Estacao, Double> colunaLatitude;

    @FXML
    private TableColumn<Estacao, Double> colunaLongitude;

    @FXML
    private TableColumn<Estacao, Integer> colunaCapacidade;

    @FXML
    private TableColumn<Estacao, Integer> colunaVeiculos;

    @FXML
    private TableColumn<Estacao, Integer> colunaVagas;

    @FXML
    private TableColumn<Estacao, String> colunaStatus;

    @FXML
    private Button botaoRemoverEstacao;

    @FXML
    private TextField campoNomeEstacao;

    @FXML
    private TextField campoLatitude;

    @FXML
    private TextField campoLongitude;

    @FXML
    private TextField campoCapacidade;

    @FXML
    private Label mensagemEstacao;

    @FXML
    private TableView<Veiculo> tabelaVeiculos;

    @FXML
    private TableColumn<Veiculo, Integer> colunaIdVeiculo;

    @FXML
    private TableColumn<Veiculo, String> colunaTipoVeiculo;

    @FXML
    private TableColumn<Veiculo, String> colunaStatusVeiculo;

    @FXML
    private TableColumn<Veiculo, String> colunaEstacaoVeiculo;

    @FXML
    private TableColumn<Veiculo, String> colunaBateriaVeiculo;

    @FXML
    private VBox acoesRecolherVeiculo;

    @FXML
    private ComboBox<Estacao> selecaoEstacaoRecolhimento;

    @FXML
    private Button botaoRemoverVeiculo;

    @FXML
    private VBox formularioVeiculo;

    @FXML
    private ComboBox<String> selecaoTipoVeiculo;

    @FXML
    private ComboBox<Estacao> selecaoEstacaoVeiculo;

    @FXML
    private VBox campoBateriaContainer;

    @FXML
    private TextField campoBateriaVeiculo;

    @FXML
    private Label mensagemVeiculo;

    @FXML
    private TableView<Manutencao> tabelaManutencoes;

    @FXML
    private TableColumn<Manutencao, Integer> colunaIdManutencao;

    @FXML
    private TableColumn<Manutencao, String> colunaVeiculoManutencao;

    @FXML
    private TableColumn<Manutencao, String> colunaDescricaoManutencao;

    @FXML
    private TableColumn<Manutencao, String> colunaInicioManutencao;

    @FXML
    private TableColumn<Manutencao, String> colunaFimManutencao;

    @FXML
    private TableColumn<Manutencao, String> colunaStatusManutencao;

    @FXML
    private VBox formularioManutencao;

    @FXML
    private ComboBox<Veiculo> selecaoVeiculoManutencao;

    @FXML
    private TextField campoDescricaoManutencao;

    @FXML
    private Label mensagemManutencao;

    @FXML
    private VBox formularioFinalizacaoManutencao;

    @FXML
    private ComboBox<Manutencao> selecaoManutencao;

    @FXML
    private ComboBox<Estacao> selecaoEstacaoDestino;

    @FXML
    private Label mensagemFinalizacaoManutencao;

    @FXML
    private VBox painelRelatorios;

    @FXML
    private ComboBox<Estacao> filtroEstacaoRelatorio;

    @FXML
    private DatePicker filtroDeRelatorio;

    @FXML
    private DatePicker filtroAteRelatorio;

    @FXML
    private Label totalCorridasRelatorio;

    @FXML
    private Label faturamentoComumRelatorio;

    @FXML
    private Label faturamentoAssinanteRelatorio;

    @FXML
    private Label totalArrecadadoRelatorio;

    @FXML
    private VBox listaCorridasPorUsuario;

    @FXML
    private Label mensagemRelatorios;

    public MenuAdministradorController(IMobiCidadeFachada fachada) {
        this.fachada = fachada;
    }

    @FXML
    private void initialize() {
        configurarColunasEstacoes();
        configurarColunasVeiculos();
        configurarColunasManutencoes();
        configurarFormularioVeiculo();

        StringConverter<Estacao> conversor = conversorEstacao();
        selecaoEstacaoRecolhimento.setConverter(conversor);
        filtroEstacaoRelatorio.setConverter(conversor);

        mostrarInicio();

        atualizacaoRelatorios = new Timeline(new KeyFrame(Duration.seconds(2), evento -> {
            if (painelRelatorios.isVisible()) {
                carregarRelatorios();
            }
        }));
        atualizacaoRelatorios.setCycleCount(Timeline.INDEFINITE);
        atualizacaoRelatorios.play();
    }

    private static StringConverter<Estacao> conversorEstacao() {
        return new StringConverter<>() {
            @Override
            public String toString(Estacao estacao) {
                return estacao == null ? "" : estacao.getNome() + " (ID: " + estacao.getId() + ")";
            }

            @Override
            public Estacao fromString(String texto) {
                return null;
            }
        };
    }

    private static void exibir(Node no, boolean exibir) {
        no.setVisible(exibir);
        no.setManaged(exibir);
    }

    private void exibirApenas(String titulo, String descricao, Node... nos) {
        for (Node no : List.of(formularioEstacao, formularioVeiculo, formularioManutencao,
                formularioFinalizacaoManutencao, tabelaEstacoes, tabelaManutencoes, tabelaVeiculos,
                botaoRemoverEstacao, botaoRemoverVeiculo, acoesBloqueioEstacao, acoesRecolherVeiculo,
                painelRelatorios)) {
            exibir(no, false);
        }
        for (Node no : nos) {
            exibir(no, true);
        }
        AlertaClima.ocultar(alertaClima);
        tituloConteudo.setText(titulo);
        descricaoConteudo.setText(descricao);
    }

    private boolean confirmar(String titulo, String cabecalho, String texto) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle(titulo);
        confirmacao.setHeaderText(cabecalho);
        confirmacao.setContentText(texto);
        return confirmacao.showAndWait().filter(botao -> botao == ButtonType.OK).isPresent();
    }

    private void mostrarMensagem(Alert.AlertType tipo, String titulo, String cabecalho, String conteudo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(conteudo);
        alerta.showAndWait();
    }

    private void mostrarErro(String cabecalho, Exception e) {
        mostrarMensagem(Alert.AlertType.ERROR, "Erro", cabecalho, e.getMessage());
    }

    private void mostrarSucesso(String cabecalho, String conteudo) {
        mostrarMensagem(Alert.AlertType.INFORMATION, "Sucesso", cabecalho, conteudo);
    }

    private <T> T obterSelecionado(TableView<T> tabela, String cabecalho, String conteudo) {
        T item = tabela.getSelectionModel().getSelectedItem();
        if (item == null) {
            mostrarMensagem(Alert.AlertType.WARNING, "Atenção", cabecalho, conteudo);
        }
        return item;
    }

    @FXML
    private void voltarLogin() {
        try {
            atualizacaoRelatorios.stop();
            Navegador.exibir(tituloConteudo, Navegador.login(fachada));
        } catch (Exception e) {
            atualizacaoRelatorios.play();
            mostrarErro("Não foi possível voltar ao login", e);
        }
    }

    private void configurarColunasEstacoes() {
        colunaId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colunaNome.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNome()));
        colunaLatitude.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getLatitude()));
        colunaLongitude.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getLongitude()));
        colunaCapacidade.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCapacidade()));
        // REQ03
        colunaVeiculos.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().veiculosDisponiveis()));
        colunaVagas.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().vagasLivres()));
        colunaStatus.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStatus().name()));
    }

    private void configurarColunasVeiculos() {
        colunaIdVeiculo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colunaTipoVeiculo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTipo()));
        colunaStatusVeiculo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStatus().name()));
        colunaEstacaoVeiculo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().getEstacao() == null ? "-" : c.getValue().getEstacao().getNome()));
        colunaBateriaVeiculo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue() instanceof Patinete patinete ? String.format("%.1f%%", patinete.getBateria()) : "-"));
    }

    private void configurarColunasManutencoes() {
        colunaIdManutencao.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colunaVeiculoManutencao.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().getVeiculo().getTipo() + " " + c.getValue().getVeiculo().getId()));
        colunaDescricaoManutencao.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDescricao()));
        colunaInicioManutencao.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                formatarData(c.getValue().getDataInicio())));
        colunaFimManutencao.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                formatarData(c.getValue().getDataFim())));
        colunaStatusManutencao.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().emAndamento() ? "Em andamento" : "Concluída"));
    }

    private void configurarFormularioVeiculo() {
        selecaoTipoVeiculo.getItems().setAll(TIPO_BICICLETA, TIPO_PATINETE);
        selecaoTipoVeiculo.setOnAction(event ->
                exibir(campoBateriaContainer, TIPO_PATINETE.equals(selecaoTipoVeiculo.getValue())));
        selecaoEstacaoVeiculo.setConverter(conversorEstacao());
    }

    private static String formatarData(LocalDateTime data) {
        return data == null ? "-" : data.format(FORMATO_DATA);
    }

    @FXML
    private void mostrarInicio() {
        exibirApenas("Visão Geral", "Confira as estações cadastradas no sistema.",
                tabelaEstacoes, botaoRemoverEstacao);
        carregarEstacoes();
        AlertaClima.atualizar(fachada, alertaClima);
    }

    @FXML
    private void mostrarRegistroEstacao() {
        exibirApenas("Registrar estação", "Preencha os dados abaixo para cadastrar uma nova estação.",
                formularioEstacao);
        mensagemEstacao.setText("");
    }

    @FXML
    private void mostrarBloqueioEstacao() {
        exibirApenas("Bloquear estação", "Selecione uma estação para bloquear ou desbloquear.",
                tabelaEstacoes, acoesBloqueioEstacao);
        carregarEstacoes();
    }

    private void carregarEstacoes() {
        try {
            tabelaEstacoes.getItems().setAll(fachada.listarEstacoes());
            tabelaEstacoes.refresh();
        } catch (Exception e) {
            descricaoConteudo.setText("Erro ao carregar as estações: " + e.getMessage());
        }
    }

    private Estacao obterEstacaoSelecionada() {
        return obterSelecionado(tabelaEstacoes, "Nenhuma estação selecionada", "Selecione uma estação na tabela.");
    }

    @FXML
    private void bloquearEstacao() {
        Estacao estacao = obterEstacaoSelecionada();
        if (estacao == null) {
            return;
        }

        if (estacao.ehZonaLivre()) {
            mostrarMensagem(Alert.AlertType.WARNING, "Atenção", "Operação não permitida",
                    "A estação Zona Livre não pode ser bloqueada.");
            return;
        }

        if (!confirmar("Confirmar bloqueio", "Bloquear estação?",
                "Deseja realmente bloquear a estação " + estacao.getNome() + "?")) {
            return;
        }

        try {
            fachada.bloquearEstacao(estacao.getId());
            carregarEstacoes();
            mostrarSucesso("Estação bloqueada", "A estação foi bloqueada com sucesso.");
        } catch (Exception e) {
            mostrarErro("Não foi possível bloquear a estação", e);
        }
    }

    @FXML
    private void desbloquearEstacao() {
        Estacao estacao = obterEstacaoSelecionada();
        if (estacao == null) {
            return;
        }

        if (estacao.ehZonaLivre()) {
            mostrarMensagem(Alert.AlertType.WARNING, "Atenção", "Operação não permitida",
                    "A estação Zona Livre não pode ser bloqueada ou desbloqueada.");
            return;
        }

        if (!confirmar("Confirmar desbloqueio", "Desbloquear estação?",
                "Deseja realmente desbloquear a estação " + estacao.getNome() + "?")) {
            return;
        }

        try {
            fachada.desbloquearEstacao(estacao.getId());
            carregarEstacoes();
            mostrarSucesso("Estação desbloqueada", "A estação foi desbloqueada com sucesso.");
        } catch (Exception e) {
            mostrarErro("Não foi possível desbloquear a estação", e);
        }
    }

    // REQ01
    @FXML
    private void cadastrarEstacao() {
        try {
            Estacao estacao = fachada.cadastrarEstacao(
                    campoNomeEstacao.getText().trim(),
                    Double.parseDouble(campoLatitude.getText().trim().replace(',', '.')),
                    Double.parseDouble(campoLongitude.getText().trim().replace(',', '.')),
                    Integer.parseInt(campoCapacidade.getText().trim()));

            mensagemEstacao.setText("Estação cadastrada com sucesso! ID: " + estacao.getId());

            campoNomeEstacao.clear();
            campoLatitude.clear();
            campoLongitude.clear();
            campoCapacidade.clear();
            carregarEstacoes();

        } catch (NumberFormatException e) {
            mensagemEstacao.setText("Latitude, longitude e capacidade devem conter valores numéricos válidos.");
        } catch (Exception e) {
            mensagemEstacao.setText(e.getMessage());
        }
    }

    @FXML
    private void removerEstacao() {
        Estacao estacao = obterEstacaoSelecionada();
        if (estacao == null) {
            return;
        }

        if (estacao.ehZonaLivre()) {
            mostrarMensagem(Alert.AlertType.WARNING, "Atenção", "Operação não permitida",
                    "A estação Zona Livre não pode ser removida.");
            return;
        }

        if (!confirmar("Confirmar remoção", "Remover estação?",
                "Deseja realmente remover a estação " + estacao.getNome() + "?")) {
            return;
        }

        try {
            fachada.removerEstacao(estacao.getId());
            carregarEstacoes();
            mostrarSucesso("Estação removida", "A estação foi removida com sucesso.");
        } catch (Exception e) {
            mostrarErro("Não foi possível remover a estação", e);
        }
    }

    @FXML
    private void mostrarCadastroVeiculo() {
        exibirApenas("Cadastrar veículo",
                "Selecione o tipo de veículo e a estação onde ele será disponibilizado.", formularioVeiculo);
        mensagemVeiculo.setText("");
        carregarEstacoesDisponiveis();
    }

    private List<Estacao> estacoesAtivasComVaga() {
        return fachada.listarEstacoes().stream()
                .filter(estacao -> !estacao.ehZonaLivre() && estacao.temVaga() && !estacao.estaBloqueada())
                .toList();
    }

    private void carregarEstacoesDisponiveis() {
        try {
            selecaoEstacaoVeiculo.getItems().setAll(estacoesAtivasComVaga());
        } catch (Exception e) {
            mensagemVeiculo.setText("Erro ao carregar estações: " + e.getMessage());
        }
    }

    // REQ02
    @FXML
    private void cadastrarVeiculo() {
        String tipo = selecaoTipoVeiculo.getValue();
        Estacao estacao = selecaoEstacaoVeiculo.getValue();

        if (tipo == null) {
            mensagemVeiculo.setText("Selecione o tipo de veículo.");
            return;
        }

        if (estacao == null) {
            mensagemVeiculo.setText("Selecione uma estação.");
            return;
        }

        try {
            Veiculo veiculo;

            if (tipo.equals(TIPO_BICICLETA)) {
                veiculo = fachada.cadastrarBicicleta(estacao.getId());
            } else {
                double bateria;
                try {
                    bateria = Double.parseDouble(campoBateriaVeiculo.getText().trim().replace(',', '.'));
                } catch (NumberFormatException e) {
                    mensagemVeiculo.setText("Informe um valor numérico válido para a bateria.");
                    return;
                }
                veiculo = fachada.cadastrarPatinete(estacao.getId(), bateria);
            }

            mensagemVeiculo.setText(tipo + " cadastrado(a) com sucesso! ID: " + veiculo.getId());

            campoBateriaVeiculo.clear();
            selecaoTipoVeiculo.getSelectionModel().clearSelection();
            selecaoEstacaoVeiculo.getSelectionModel().clearSelection();
            exibir(campoBateriaContainer, false);

            carregarEstacoesDisponiveis();
            carregarEstacoes();

        } catch (Exception e) {
            mensagemVeiculo.setText(e.getMessage());
        }
    }

    @FXML
    private void mostrarVeiculos() {
        exibirApenas("Gerenciar veículos",
                "Confira os veículos, recolha os deixados em zona livre ou remova os que não são mais necessários.",
                tabelaVeiculos, acoesRecolherVeiculo, botaoRemoverVeiculo);
        carregarVeiculos();
        selecaoEstacaoRecolhimento.getItems().setAll(estacoesAtivasComVaga());
    }

    private void carregarVeiculos() {
        try {
            tabelaVeiculos.getItems().setAll(fachada.listarVeiculos());
            tabelaVeiculos.refresh();
        } catch (Exception e) {
            descricaoConteudo.setText("Erro ao carregar os veículos: " + e.getMessage());
        }
    }

    // REQ12
    @FXML
    private void recolherVeiculo() {
        Veiculo veiculo = obterSelecionado(tabelaVeiculos, "Nenhum veículo selecionado",
                "Selecione na tabela um veículo deixado em zona livre.");
        if (veiculo == null) {
            return;
        }

        Estacao estacao = selecaoEstacaoRecolhimento.getValue();
        if (estacao == null) {
            mostrarMensagem(Alert.AlertType.WARNING, "Atenção", "Nenhuma estação selecionada",
                    "Escolha a estação que receberá o veículo.");
            return;
        }

        try {
            fachada.recolherVeiculoParaEstacao(veiculo.getId(), estacao.getId());
            mostrarVeiculos();
            mostrarSucesso("Veículo recolhido", "O veículo voltou à frota na estação " + estacao.getNome() + ".");
        } catch (Exception e) {
            mostrarErro("Não foi possível recolher o veículo", e);
        }
    }

    @FXML
    private void removerVeiculo() {
        Veiculo veiculo = obterSelecionado(tabelaVeiculos, "Nenhum veículo selecionado",
                "Selecione um veículo na tabela para removê-lo.");
        if (veiculo == null
                || !confirmar("Confirmar remoção", "Remover veículo?",
                        "Deseja realmente remover o veículo " + veiculo.getTipo() + " ID: " + veiculo.getId() + "?")) {
            return;
        }

        try {
            fachada.removerVeiculo(veiculo.getId());
            carregarVeiculos();
            mostrarSucesso("Veículo removido", "O veículo foi removido com sucesso.");
        } catch (Exception e) {
            mostrarErro("Não foi possível remover o veículo", e);
        }
    }

    @FXML
    private void mostrarManutencoes() {
        exibirApenas("Manutenções", "Confira o histórico de manutenções dos veículos.", tabelaManutencoes);
        try {
            tabelaManutencoes.getItems().setAll(fachada.listarManutencoes());
            tabelaManutencoes.refresh();
        } catch (Exception e) {
            descricaoConteudo.setText("Erro ao carregar as manutenções: " + e.getMessage());
        }
    }

    @FXML
    private void mostrarRegistroManutencao() {
        exibirApenas("Registrar manutenção",
                "Selecione um veículo disponível e informe o motivo da manutenção.", formularioManutencao);
        mensagemManutencao.setText("");
        carregarVeiculosDisponiveis();
    }

    private void carregarVeiculosDisponiveis() {
        try {
            selecaoVeiculoManutencao.getItems().setAll(fachada.listarVeiculos().stream()
                    .filter(veiculo -> veiculo.getStatus() == StatusVeiculo.DISPONIVEL)
                    .toList());
        } catch (Exception e) {
            mensagemManutencao.setText("Erro ao carregar veículos: " + e.getMessage());
        }
    }

    // REQ13
    @FXML
    private void registrarManutencao() {
        Veiculo veiculo = selecaoVeiculoManutencao.getValue();
        String descricao = campoDescricaoManutencao.getText().trim();

        if (veiculo == null) {
            mensagemManutencao.setText("Selecione um veículo para continuar.");
            return;
        }

        if (descricao.isBlank()) {
            mensagemManutencao.setText("Informe a descrição da manutenção.");
            return;
        }

        try {
            Manutencao manutencao = fachada.registrarManutencao(veiculo.getId(), descricao);
            mensagemManutencao.setText("Manutenção registrada com sucesso! ID: " + manutencao.getId());

            campoDescricaoManutencao.clear();
            selecaoVeiculoManutencao.getSelectionModel().clearSelection();
            carregarVeiculosDisponiveis();

        } catch (Exception e) {
            mensagemManutencao.setText(e.getMessage());
        }
    }

    @FXML
    private void mostrarFinalizacaoManutencao() {
        exibirApenas("Finalizar manutenção",
                "Selecione uma manutenção em andamento e uma estação com vagas livres.",
                formularioFinalizacaoManutencao);
        mensagemFinalizacaoManutencao.setText("");
        carregarOpcoesFinalizacao();
    }

    private void carregarOpcoesFinalizacao() {
        try {
            selecaoManutencao.getItems().setAll(fachada.listarManutencoes().stream()
                    .filter(Manutencao::emAndamento)
                    .toList());
            selecaoEstacaoDestino.getItems().setAll(estacoesAtivasComVaga());
        } catch (Exception e) {
            mensagemFinalizacaoManutencao.setText("Erro ao carregar dados: " + e.getMessage());
        }
    }

    // REQ14
    @FXML
    private void finalizarManutencao() {
        Manutencao manutencao = selecaoManutencao.getValue();
        Estacao estacao = selecaoEstacaoDestino.getValue();

        if (manutencao == null) {
            mensagemFinalizacaoManutencao.setText("Selecione uma manutenção.");
            return;
        }

        if (estacao == null) {
            mensagemFinalizacaoManutencao.setText("Selecione uma estação de destino.");
            return;
        }

        if (!confirmar("Confirmar finalização", "Finalizar manutenção?",
                "Deseja finalizar a manutenção " + manutencao.getId()
                        + " e enviar o veículo para a estação " + estacao.getNome() + "?")) {
            return;
        }

        try {
            fachada.concluirManutencao(manutencao.getId(), estacao.getId());
            mensagemFinalizacaoManutencao.setText("Manutenção finalizada com sucesso!");

            selecaoManutencao.getSelectionModel().clearSelection();
            selecaoEstacaoDestino.getSelectionModel().clearSelection();
            carregarOpcoesFinalizacao();

        } catch (Exception e) {
            mensagemFinalizacaoManutencao.setText(e.getMessage());
        }
    }

    @FXML
    private void mostrarRelatorios() {
        exibirApenas("Relatórios", "Acompanhe as corridas finalizadas, o faturamento e exporte em CSV.",
                painelRelatorios);
        filtroEstacaoRelatorio.getItems().setAll(fachada.listarEstacoes());
        atualizarRelatorios();
    }

    @FXML
    private void limparFiltrosRelatorio() {
        filtroEstacaoRelatorio.setValue(null);
        filtroDeRelatorio.setValue(null);
        filtroAteRelatorio.setValue(null);
        atualizarRelatorios();
    }

    private Integer idEstacaoFiltrada() {
        Estacao estacao = filtroEstacaoRelatorio.getValue();
        return estacao == null ? null : estacao.getId();
    }

    private LocalDateTime periodoDe() {
        return filtroDeRelatorio.getValue() == null ? null : filtroDeRelatorio.getValue().atStartOfDay();
    }

    private LocalDateTime periodoAte() {
        return filtroAteRelatorio.getValue() == null ? null : filtroAteRelatorio.getValue().atTime(23, 59, 59);
    }

    // REQ15, REQ16
    @FXML
    private void atualizarRelatorios() {
        int total = carregarRelatorios();
        if (total == 0) {
            mensagemRelatorios.setText("Nenhuma corrida finalizada para os filtros informados.");
        } else if (total > 0) {
            mensagemRelatorios.setText("Relatório atualizado com sucesso.");
        }
    }

    private int carregarRelatorios() {
        try {
            LocalDateTime de = periodoDe();
            LocalDateTime ate = periodoAte();

            if (de != null && ate != null && de.isAfter(ate)) {
                mensagemRelatorios.setText("A data inicial não pode ser posterior à data final.");
                return -1;
            }

            List<Corrida> corridas = fachada.relatorioCorridasPorEstacao(idEstacaoFiltrada(), de, ate);
            Map<String, Double> faturamento = fachada.relatorioFaturamentoPorTipoUsuario(de, ate);

            totalCorridasRelatorio.setText("Total de corridas: " + corridas.size());
            faturamentoComumRelatorio.setText("Faturamento Comum: " + MOEDA.format(faturamento.get("Comum")));
            faturamentoAssinanteRelatorio.setText(
                    "Faturamento Assinante: " + MOEDA.format(faturamento.get("Assinante")));
            totalArrecadadoRelatorio.setText("Total arrecadado: "
                    + MOEDA.format(faturamento.values().stream().mapToDouble(Double::doubleValue).sum()));

            listaCorridasPorUsuario.getChildren().clear();
            agruparPorUsuario(corridas).forEach((nome, lista) ->
                    listaCorridasPorUsuario.getChildren().add(criarSecaoUsuario(nome, lista)));

            return corridas.size();

        } catch (Exception e) {
            mensagemRelatorios.setText("Erro ao consultar relatórios: " + e.getMessage());
            return -1;
        }
    }

    private static Map<String, List<Corrida>> agruparPorUsuario(List<Corrida> corridas) {
        Map<String, List<Corrida>> agrupadas = new LinkedHashMap<>();
        corridas.stream()
                .sorted(Comparator.comparing((Corrida c) -> c.getUsuario().getNome(), String.CASE_INSENSITIVE_ORDER))
                .forEach(c -> agrupadas.computeIfAbsent(c.getUsuario().getNome(), nome -> new ArrayList<>()).add(c));
        agrupadas.values().forEach(lista ->
                lista.sort(Comparator.comparing(Corrida::getDataHoraInicio).reversed()));
        return agrupadas
                ;
    }

    private VBox criarSecaoUsuario(String nome, List<Corrida> corridas) {
        VBox secao = new VBox(10);
        secao.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8;"
                + "-fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-padding: 15;");

        Label titulo = new Label(nome);
        titulo.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        secao.getChildren().add(titulo);

        for (Corrida corrida : corridas) {
            Label detalhes = new Label(
                    "Corrida #" + corrida.getId()
                            + "\nInício: " + corrida.getDataHoraInicio().format(FORMATO_DATA)
                            + "\nTérmino: " + corrida.getDataHoraFim().format(FORMATO_DATA)
                            + "\nOrigem: Estação " + corrida.getEstacaoOrigem().getId()
                            + "\nDestino: " + (corrida.getEstacaoDestino() == null
                                    ? "Zona livre"
                                    : "Estação " + corrida.getEstacaoDestino().getId())
                            + "\nVeículo: " + corrida.getVeiculo().getTipo() + " #" + corrida.getVeiculo().getId()
                            + "\nDuração: " + corrida.getDuracao() + " minutos"
                            + "\nTarifa: " + MOEDA.format(corrida.getTarifa()));
            detalhes.setWrapText(true);
            detalhes.setStyle("-fx-font-size: 13px; -fx-text-fill: #334155; -fx-padding: 8;"
                    + "-fx-background-color: #f1f5f9; -fx-background-radius: 5;");
            secao.getChildren().add(detalhes);
        }

        return secao;
    }

    // REQ15
    @FXML
    private void exportarRelatorioCsv() {
        FileChooser seletor = new FileChooser();
        seletor.setTitle("Exportar relatório de corridas");
        seletor.setInitialFileName("corridas.csv");
        seletor.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        File arquivo = seletor.showSaveDialog(tituloConteudo.getScene().getWindow());
        if (arquivo == null) {
            return;
        }

        try {
            fachada.exportarRelatorioCorridasCsv(idEstacaoFiltrada(), periodoDe(), periodoAte(), arquivo.toPath());
            mensagemRelatorios.setText("CSV exportado em " + arquivo.getAbsolutePath());
        } catch (Exception e) {
            mensagemRelatorios.setText("Erro ao exportar o CSV: " + e.getMessage());
        }
    }
}
