package com.mobicidade.ui.controller;

import com.mobicidade.facade.IMobiCidadeFachada;
import com.mobicidade.model.Corrida;
import com.mobicidade.model.Estacao;
import com.mobicidade.model.Usuario;
import com.mobicidade.model.UsuarioAssinante;
import com.mobicidade.model.UsuarioComum;
import com.mobicidade.model.Veiculo;
import com.mobicidade.ui.AlertaClima;
import com.mobicidade.ui.Navegador;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

public class MenuUsuarioController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String SEM_CORRIDA = "Você não possui nenhuma corrida em andamento.";

    private final IMobiCidadeFachada fachada;
    private Usuario usuario;
    private Corrida corridaAtual;
    private Timeline atualizacaoEstacoes;

    @FXML
    private Label tituloConteudo;

    @FXML
    private Label descricaoConteudo;

    @FXML
    private Label alertaClima;

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
        configurarTabelaEstacoes();
        configurarTabelaHistorico();
        mostrarInicio();

        atualizacaoEstacoes = new Timeline(new KeyFrame(Duration.seconds(1), evento -> {
            if (painelEstacoes.isVisible()) {
                carregarEstacoes();
            }
        }));
        atualizacaoEstacoes.setCycleCount(Timeline.INDEFINITE);
        atualizacaoEstacoes.play();
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
        for (Node p : List.of(painelEstacoes, formularioCorrida, painelMinhaCorrida, painelHistorico, painelPerfil)) {
            boolean exibir = p == painel;
            p.setVisible(exibir);
            p.setManaged(exibir);
        }
        tituloConteudo.setText(titulo);
        descricaoConteudo.setText(descricao);
        AlertaClima.ocultar(alertaClima);
    }

    @FXML
    private void mostrarInicio() {
        exibirApenas(null, "Bem-vindo ao MobiCidade",
                "Gerencie suas viagens e acompanhe os serviços disponíveis.");
        AlertaClima.atualizar(fachada, alertaClima);
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
            mensagemEstacoes.setText(estacoes.isEmpty()
                    ? "Não existem estações cadastradas no momento."
                    : "Total de estações cadastradas: " + estacoes.size());
        } catch (Exception e) {
            mensagemEstacoes.setText("Erro ao carregar as estações: " + e.getMessage());
        }
    }

    private void carregarEstacoesParaCorrida() {
        List<Estacao> estacoes = fachada.listarEstacoes().stream()
                .filter(estacao -> !estacao.estaBloqueada())
                .filter(estacao -> !estacao.ehZonaLivre())
                .toList();

        selecaoEstacaoOrigem.setItems(FXCollections.observableArrayList(estacoes));
        selecaoVeiculo.getItems().clear();
        mensagemCorrida.setText(estacoes.isEmpty()
                ? "Não existem estações ativas disponíveis."
                : "Selecione uma estação para visualizar os veículos.");
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
        mensagemCorrida.setText(veiculos.isEmpty()
                ? "Esta estação não possui veículos disponíveis."
                : "Veículos disponíveis: " + veiculos.size());
    }

    // REQ09, REQ17 a REQ20
    @FXML
    private void iniciarCorrida() {
        Estacao estacao = selecaoEstacaoOrigem.getValue();
        Veiculo veiculo = selecaoVeiculo.getValue();

        if (estacao == null) {
            mensagemCorrida.setText("Selecione uma estação de origem.");
            return;
        }

        if (veiculo == null) {
            mensagemCorrida.setText("Selecione um veículo.");
            return;
        }

        try {
            Corrida corrida = fachada.iniciarCorrida(usuario.getCpf(), veiculo.getId(), estacao.getId());
            atualizarVeiculosDisponiveis();
            mensagemCorrida.setText("Corrida #" + corrida.getId() + " iniciada com sucesso!");
        } catch (Exception e) {
            mensagemCorrida.setText("Não foi possível iniciar a corrida: " + e.getMessage());
        }
    }

    private void carregarMinhaCorrida() {
        corridaAtual = fachada.listarCorridas().stream()
                .filter(Corrida::emAndamento)
                .filter(c -> c.getUsuario().getCpf().equals(usuario.getCpf()))
                .findFirst()
                .orElse(null);

        zonaLivre.setSelected(false);
        mensagemMinhaCorrida.setText("");

        if (corridaAtual == null) {
            detalhesMinhaCorrida.setText(SEM_CORRIDA);
            selecaoEstacaoDestino.getItems().clear();
            return;
        }

        detalhesMinhaCorrida.setText(
                "Corrida: #" + corridaAtual.getId() + "\n"
                        + "Veículo: " + descreverVeiculo(corridaAtual.getVeiculo()) + "\n"
                        + "Origem: " + corridaAtual.getEstacaoOrigem().getNome() + "\n"
                        + "Início: " + corridaAtual.getDataHoraInicio().format(FORMATO_DATA));

        List<Estacao> estacoes = fachada.listarEstacoes().stream()
                .filter(Estacao::temVaga)
                .filter(estacao -> !estacao.ehZonaLivre())
                .toList();

        selecaoEstacaoDestino.setItems(FXCollections.observableArrayList(estacoes));
        selecaoEstacaoDestino.setDisable(false);

        if (estacoes.isEmpty()) {
            mensagemMinhaCorrida.setText("Não existem estações com vagas disponíveis no momento.");
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
            mensagemMinhaCorrida.setText("Você não possui uma corrida em andamento.");
            return;
        }

        Estacao destino = selecaoEstacaoDestino.getValue();

        if (destino == null && !zonaLivre.isSelected()) {
            mensagemMinhaCorrida.setText("Selecione uma estação de destino ou marque a devolução na Zona Livre.");
            return;
        }

        try {
            Corrida finalizada = fachada.finalizarCorrida(corridaAtual.getId(),
                    destino == null ? null : destino.getId());

            mensagemMinhaCorrida.setText("Corrida finalizada com sucesso!\n"
                    + "Duração: " + finalizada.getDuracao() + " minutos\n"
                    + "Tarifa: " + moeda(finalizada.getTarifa()));

            corridaAtual = null;
            detalhesMinhaCorrida.setText(SEM_CORRIDA);
            selecaoEstacaoDestino.getItems().clear();
            zonaLivre.setSelected(false);
            selecaoEstacaoDestino.setDisable(false);

        } catch (Exception e) {
            mensagemMinhaCorrida.setText("Não foi possível finalizar a corrida: " + e.getMessage());
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
        } else {
            perfilPlano.setText(String.format("Plano comum\nTarifa: R$ %.2f por minuto utilizado.",
                    UsuarioComum.TARIFA_POR_MINUTO));
        }

        List<Corrida> corridas = corridasFinalizadasDoUsuario();
        estatisticaCorridas.setText(String.valueOf(corridas.size()));
        estatisticaMinutos.setText(corridas.stream().mapToInt(Corrida::getDuracao).sum() + " min");
        estatisticaGastos.setText(moeda(corridas.stream().mapToDouble(Corrida::getTarifa).sum()));

        mensagemPerfil.setText("");
    }

    @FXML
    private void salvarPerfil() {
        String nomeAntigo = usuario.getNome();
        String pagamentoAntigo = usuario.getFormaDePagamento();

        String novoNome = campoNome.getText().trim();
        String novaFormaPagamento = campoFormaPagamento.getText().trim();

        if (novoNome.isEmpty()) {
            mensagemPerfil.setText("O nome não pode ficar vazio.");
            return;
        }

        if (novaFormaPagamento.isEmpty()) {
            mensagemPerfil.setText("Informe uma forma de pagamento.");
            return;
        }

        try {
            usuario.setNome(novoNome);
            usuario.setFormaDePagamento(novaFormaPagamento);
            usuario = fachada.atualizarUsuario(usuario);

            carregarPerfil();
            mensagemPerfil.setText("Perfil atualizado com sucesso!");

        } catch (Exception e) {
            usuario.setNome(nomeAntigo);
            usuario.setFormaDePagamento(pagamentoAntigo);
            campoNome.setText(nomeAntigo);
            campoFormaPagamento.setText(pagamentoAntigo);
            mensagemPerfil.setText("Não foi possível atualizar o perfil: " + e.getMessage());
        }
    }

    @FXML
    private void voltarLogin() {
        try {
            atualizacaoEstacoes.stop();
            Navegador.exibir(tituloConteudo, Navegador.login(fachada));
        } catch (Exception e) {
            atualizacaoEstacoes.play();
            descricaoConteudo.setText("Não foi possível voltar ao login: " + e.getMessage());
        }
    }
}
