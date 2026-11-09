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
import com.mobicidade.model.Administrador;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IMobiCidadeFachada {
        // REQ01, REQ03
        Estacao cadastrarEstacao(String nome, double latitude, double longitude, int capacidade);

        Estacao buscarEstacao(int id);

        List<Estacao> listarEstacoes();

        Estacao atualizarEstacao(Estacao estacao);

        void removerEstacao(int id);

        int veiculosDisponiveis(int idEstacao);

        int vagasLivres(int idEstacao);

        void bloquearEstacao(int idEstacao);

        void desbloquearEstacao(int idEstacao);

        // REQ02
        Bicicleta cadastrarBicicleta(int idEstacao);

        Patinete cadastrarPatinete(int idEstacao, double bateria);

        Veiculo buscarVeiculo(int id);

        List<Veiculo> listarVeiculos();

        Veiculo atualizarVeiculo(Veiculo veiculo);

        void removerVeiculo(int id);

        Veiculo recolherVeiculoParaEstacao(int idVeiculo, int idEstacao);

        // REQ04, REQ05
        UsuarioComum cadastrarUsuarioComum(String nome, String cpf, String senha, String formaDePagamento);

        UsuarioAssinante cadastrarUsuarioAssinante(String nome, String cpf, String senha, String formaDePagamento,
                        int minutosInclusos);

        Administrador cadastrarAdministrador(String nome, String cpf, String senha);

        Usuario buscarUsuario(String cpf);

        Usuario autenticarUsuario(String cpf, String senha);

        List<Usuario> listarUsuarios();

        Usuario atualizarUsuario(Usuario usuario);

        void removerUsuario(String cpf);

        UsuarioAssinante renovarPlano(String cpf);

        // REQ06 a REQ08
        PrevisaoClimatica consultarPrevisaoDaCidade();

        // REQ09 a REQ12, REQ17 a REQ22
        Corrida iniciarCorrida(String cpfUsuario, int idVeiculo, int idEstacaoOrigem);

        Corrida iniciarCorrida(String cpfUsuario, int idVeiculo, int idEstacaoOrigem, LocalDateTime inicio);

        Corrida finalizarCorrida(int idCorrida, Integer idEstacaoDestino);

        Corrida finalizarCorrida(int idCorrida, Integer idEstacaoDestino, LocalDateTime fim);

        Corrida buscarCorrida(int id);

        List<Corrida> listarCorridas();

        Corrida atualizarCorrida(Corrida corrida);

        void removerCorrida(int id);

        void definirLimiarBateriaMinima(double percentual);

        double getLimiarBateriaMinima();

        // REQ13, REQ14
        Manutencao registrarManutencao(int idVeiculo, String descricao);

        Manutencao registrarManutencao(int idVeiculo, String descricao, LocalDateTime inicio);

        Manutencao concluirManutencao(int idManutencao, int idEstacao);

        Manutencao concluirManutencao(int idManutencao, int idEstacao, LocalDateTime fim);

        Manutencao buscarManutencao(int id);

        List<Manutencao> listarManutencoes();

        Manutencao atualizarManutencao(Manutencao manutencao);

        void removerManutencao(int id);

        // REQ15, REQ16
        List<Corrida> relatorioCorridasPorEstacao(Integer idEstacao, LocalDateTime de, LocalDateTime ate);

        String relatorioCorridasPorEstacaoCsv(Integer idEstacao, LocalDateTime de, LocalDateTime ate);

        Path exportarRelatorioCorridasCsv(Integer idEstacao, LocalDateTime de, LocalDateTime ate, Path arquivo)
                        throws IOException;

        Map<String, Double> relatorioFaturamentoPorTipoUsuario(LocalDateTime de, LocalDateTime ate);
}
