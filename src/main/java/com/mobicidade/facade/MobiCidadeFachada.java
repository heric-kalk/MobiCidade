package com.mobicidade.facade;

import com.mobicidade.model.Bicicleta;
import com.mobicidade.model.Corrida;
import com.mobicidade.model.Estacao;
import com.mobicidade.model.Manutencao;
import com.mobicidade.model.Patinete;
import com.mobicidade.model.PrevisaoClimatica;
import com.mobicidade.model.Usuario;
import com.mobicidade.model.UsuarioAssinante;
import com.mobicidade.model.UsuarioComum;
import com.mobicidade.model.Veiculo;
import com.mobicidade.repository.CorridaRepositorioMemoria;
import com.mobicidade.repository.DadosIniciais;
import com.mobicidade.repository.EstacaoRepositorioMemoria;
import com.mobicidade.repository.ManutencaoRepositorioMemoria;
import com.mobicidade.repository.UsuarioRepositorioMemoria;
import com.mobicidade.repository.VeiculoRepositorioMemoria;
import com.mobicidade.service.ClimaServico;
import com.mobicidade.service.CorridaServico;
import com.mobicidade.service.EstacaoServico;
import com.mobicidade.service.IClimaServico;
import com.mobicidade.service.ICorridaServico;
import com.mobicidade.service.IEstacaoServico;
import com.mobicidade.service.IManutencaoServico;
import com.mobicidade.service.IProvedorPrevisaoClimatica;
import com.mobicidade.service.IRelatorioServico;
import com.mobicidade.service.IUsuarioServico;
import com.mobicidade.service.IVeiculoServico;
import com.mobicidade.service.ManutencaoServico;
import com.mobicidade.service.OpenMeteoProvedor;
import com.mobicidade.service.RelatorioServico;
import com.mobicidade.service.UsuarioServico;
import com.mobicidade.service.VeiculoServico;
import com.mobicidade.model.Administrador;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class MobiCidadeFachada implements IMobiCidadeFachada {
    public static final double LATITUDE_CIDADE = -8.0476;
    public static final double LONGITUDE_CIDADE = -34.8770;

    private final IEstacaoServico estacaoServico;
    private final IVeiculoServico veiculoServico;
    private final IUsuarioServico usuarioServico;
    private final IClimaServico climaServico;
    private final ICorridaServico corridaServico;
    private final IManutencaoServico manutencaoServico;
    private final IRelatorioServico relatorioServico;

    public MobiCidadeFachada(IProvedorPrevisaoClimatica provedorClima) {
        this(provedorClima, false);
    }

    public static MobiCidadeFachada comDadosIniciais() {
        return new MobiCidadeFachada(new OpenMeteoProvedor(), true);
    }

    private MobiCidadeFachada(IProvedorPrevisaoClimatica provedorClima, boolean carregarDadosIniciais) {
        EstacaoRepositorioMemoria estacaoRepo = new EstacaoRepositorioMemoria();
        VeiculoRepositorioMemoria veiculoRepo = new VeiculoRepositorioMemoria();
        UsuarioRepositorioMemoria usuarioRepo = new UsuarioRepositorioMemoria();
        CorridaRepositorioMemoria corridaRepo = new CorridaRepositorioMemoria();
        ManutencaoRepositorioMemoria manutencaoRepo = new ManutencaoRepositorioMemoria();

        if (carregarDadosIniciais) {
            DadosIniciais.carregar(estacaoRepo, veiculoRepo, usuarioRepo, corridaRepo, manutencaoRepo);
        }

        this.estacaoServico = new EstacaoServico(estacaoRepo);
        this.veiculoServico = new VeiculoServico(veiculoRepo, estacaoServico);
        this.usuarioServico = new UsuarioServico(usuarioRepo, corridaRepo);
        this.climaServico = new ClimaServico(provedorClima);
        this.corridaServico = new CorridaServico(corridaRepo, usuarioServico, veiculoServico,
                estacaoServico, climaServico);
        this.manutencaoServico = new ManutencaoServico(manutencaoRepo, veiculoServico, estacaoServico);
        this.relatorioServico = new RelatorioServico(corridaServico);
    }

    @Override
    public Estacao cadastrarEstacao(String nome, double latitude, double longitude, int capacidade) {
        return estacaoServico.cadastrar(new Estacao(nome, latitude, longitude, capacidade));
    }

    @Override
    public Estacao buscarEstacao(int id) {
        return estacaoServico.buscarPorId(id);
    }

    @Override
    public List<Estacao> listarEstacoes() {
        return estacaoServico.listar();
    }

    @Override
    public Estacao atualizarEstacao(Estacao estacao) {
        return estacaoServico.atualizar(estacao);
    }

    @Override
    public void removerEstacao(int id) {
        estacaoServico.remover(id);
    }

    @Override
    public int veiculosDisponiveis(int idEstacao) {
        return estacaoServico.veiculosDisponiveis(idEstacao);
    }

    @Override
    public int vagasLivres(int idEstacao) {
        return estacaoServico.vagasLivres(idEstacao);
    }

    @Override
    public void bloquearEstacao(int idEstacao) {
        estacaoServico.bloquearEstacao(idEstacao);
    }

    @Override
    public void desbloquearEstacao(int idEstacao) {
        estacaoServico.desbloquearEstacao(idEstacao);
    }

    @Override
    public Bicicleta cadastrarBicicleta(int idEstacao) {
        return (Bicicleta) veiculoServico.cadastrar(new Bicicleta(estacaoServico.buscarPorId(idEstacao)));
    }

    @Override
    public Patinete cadastrarPatinete(int idEstacao, double bateria) {
        return (Patinete) veiculoServico.cadastrar(new Patinete(estacaoServico.buscarPorId(idEstacao), bateria));
    }

    @Override
    public Veiculo buscarVeiculo(int id) {
        return veiculoServico.buscarPorId(id);
    }

    @Override
    public List<Veiculo> listarVeiculos() {
        return veiculoServico.listar();
    }

    @Override
    public Veiculo atualizarVeiculo(Veiculo veiculo) {
        return veiculoServico.atualizar(veiculo);
    }

    @Override
    public void removerVeiculo(int id) {
        veiculoServico.remover(id);
    }

    @Override
    public Veiculo recolherVeiculoParaEstacao(int idVeiculo, int idEstacao) {
        return veiculoServico.recolherParaEstacao(idVeiculo, idEstacao);
    }

    @Override
    public UsuarioComum cadastrarUsuarioComum(
            String nome, String cpf, String senha, String formaDePagamento) {
        return (UsuarioComum) usuarioServico.cadastrar(
                new UsuarioComum(nome, cpf, senha, formaDePagamento));
    }

    @Override
    public UsuarioAssinante cadastrarUsuarioAssinante(
            String nome, String cpf, String senha,
            String formaDePagamento, int minutosInclusos) {
        return (UsuarioAssinante) usuarioServico.cadastrar(
                new UsuarioAssinante(nome, cpf, senha, formaDePagamento, minutosInclusos));
    }

    @Override
    public Administrador cadastrarAdministrador(String nome, String cpf, String senha) {
        return (Administrador) usuarioServico.cadastrar(
                new Administrador(nome, cpf, senha, "Não se aplica"));
    }

    @Override
    public Usuario buscarUsuario(String cpf) {
        return usuarioServico.buscarPorId(cpf);
    }

    @Override
    public Usuario autenticarUsuario(String cpf, String senha) {
        return usuarioServico.autenticar(cpf, senha);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioServico.listar();
    }

    @Override
    public Usuario atualizarUsuario(Usuario usuario) {
        return usuarioServico.atualizar(usuario);
    }

    @Override
    public void removerUsuario(String cpf) {
        usuarioServico.remover(cpf);
    }

    @Override
    public UsuarioAssinante renovarPlano(String cpf) {
        return usuarioServico.renovarPlano(cpf);
    }

    @Override
    public PrevisaoClimatica consultarPrevisaoDaCidade() {
        return climaServico.consultar(LATITUDE_CIDADE, LONGITUDE_CIDADE);
    }

    @Override
    public Corrida iniciarCorrida(String cpfUsuario, int idVeiculo, int idEstacaoOrigem) {
        return corridaServico.iniciar(cpfUsuario, idVeiculo, idEstacaoOrigem, null);
    }

    @Override
    public Corrida iniciarCorrida(String cpfUsuario, int idVeiculo, int idEstacaoOrigem, LocalDateTime inicio) {
        return corridaServico.iniciar(cpfUsuario, idVeiculo, idEstacaoOrigem, inicio);
    }

    @Override
    public Corrida finalizarCorrida(int idCorrida, Integer idEstacaoDestino) {
        return corridaServico.finalizar(idCorrida, idEstacaoDestino, null);
    }

    @Override
    public Corrida finalizarCorrida(int idCorrida, Integer idEstacaoDestino, LocalDateTime fim) {
        return corridaServico.finalizar(idCorrida, idEstacaoDestino, fim);
    }

    @Override
    public Corrida buscarCorrida(int id) {
        return corridaServico.buscarPorId(id);
    }

    @Override
    public List<Corrida> listarCorridas() {
        return corridaServico.listar();
    }

    @Override
    public Corrida atualizarCorrida(Corrida corrida) {
        return corridaServico.atualizar(corrida);
    }

    @Override
    public void removerCorrida(int id) {
        corridaServico.remover(id);
    }

    @Override
    public void definirLimiarBateriaMinima(double percentual) {
        corridaServico.definirLimiarBateriaMinima(percentual);
    }

    @Override
    public double getLimiarBateriaMinima() {
        return corridaServico.getLimiarBateriaMinima();
    }

    @Override
    public Manutencao registrarManutencao(int idVeiculo, String descricao) {
        return manutencaoServico.registrar(idVeiculo, descricao, null);
    }

    @Override
    public Manutencao registrarManutencao(int idVeiculo, String descricao, LocalDateTime inicio) {
        return manutencaoServico.registrar(idVeiculo, descricao, inicio);
    }

    @Override
    public Manutencao concluirManutencao(int idManutencao, int idEstacao) {
        return manutencaoServico.concluir(idManutencao, idEstacao, null);
    }

    @Override
    public Manutencao concluirManutencao(int idManutencao, int idEstacao, LocalDateTime fim) {
        return manutencaoServico.concluir(idManutencao, idEstacao, fim);
    }

    @Override
    public Manutencao buscarManutencao(int id) {
        return manutencaoServico.buscarPorId(id);
    }

    @Override
    public List<Manutencao> listarManutencoes() {
        return manutencaoServico.listar();
    }

    @Override
    public Manutencao atualizarManutencao(Manutencao manutencao) {
        return manutencaoServico.atualizar(manutencao);
    }

    @Override
    public void removerManutencao(int id) {
        manutencaoServico.remover(id);
    }

    @Override
    public List<Corrida> relatorioCorridasPorEstacao(Integer idEstacao, LocalDateTime de, LocalDateTime ate) {
        return relatorioServico.corridasPorEstacao(idEstacao, de, ate);
    }

    @Override
    public String relatorioCorridasPorEstacaoCsv(Integer idEstacao, LocalDateTime de, LocalDateTime ate) {
        return relatorioServico.corridasPorEstacaoCsv(idEstacao, de, ate);
    }

    @Override
    public Path exportarRelatorioCorridasCsv(Integer idEstacao, LocalDateTime de, LocalDateTime ate, Path arquivo)
            throws IOException {
        return relatorioServico.exportarCorridasPorEstacaoCsv(idEstacao, de, ate, arquivo);
    }

    @Override
    public Map<String, Double> relatorioFaturamentoPorTipoUsuario(LocalDateTime de, LocalDateTime ate) {
        return relatorioServico.faturamentoPorTipoUsuario(de, ate);
    }
}
