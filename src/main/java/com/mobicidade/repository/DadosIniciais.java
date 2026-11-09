package com.mobicidade.repository;

import com.mobicidade.model.Administrador;
import com.mobicidade.model.Bicicleta;
import com.mobicidade.model.Corrida;
import com.mobicidade.model.Estacao;
import com.mobicidade.model.Manutencao;
import com.mobicidade.model.Patinete;
import com.mobicidade.model.Usuario;
import com.mobicidade.model.UsuarioAssinante;
import com.mobicidade.model.UsuarioComum;
import com.mobicidade.model.Veiculo;

import java.time.LocalDateTime;

public final class DadosIniciais {

    private int proximoIdVeiculo = 1;
    private int proximoIdCorrida = 1;

    private final IEstacaoRepositorio estacoes;
    private final IVeiculoRepositorio veiculos;
    private final IUsuarioRepositorio usuarios;
    private final ICorridaRepositorio corridas;
    private final IManutencaoRepositorio manutencoes;

    private DadosIniciais(IEstacaoRepositorio estacoes, IVeiculoRepositorio veiculos, IUsuarioRepositorio usuarios,
            ICorridaRepositorio corridas, IManutencaoRepositorio manutencoes) {
        this.estacoes = estacoes;
        this.veiculos = veiculos;
        this.usuarios = usuarios;
        this.corridas = corridas;
        this.manutencoes = manutencoes;
    }

    public static void carregar(IEstacaoRepositorio estacoes, IVeiculoRepositorio veiculos,
            IUsuarioRepositorio usuarios, ICorridaRepositorio corridas, IManutencaoRepositorio manutencoes) {
        new DadosIniciais(estacoes, veiculos, usuarios, corridas, manutencoes).popular();
    }

    private void popular() {
        Estacao boaViagem = estacao(1, "Boa Viagem", -8.1190, -34.9040, 6);
        Estacao derby = estacao(2, "Derby", -8.0560, -34.8990, 4);
        Estacao marcoZero = estacao(3, "Marco Zero", -8.0630, -34.8710, 5);
        estacao(4, "Universidade Federal Rural", -8.0150, -34.9490, 3);
        Estacao zonaLivre = estacao(5, Estacao.NOME_ZONA_LIVRE,
                -8.0476, -34.8770, 9999);

        Veiculo bike1 = veiculo(new Bicicleta(boaViagem));
        veiculo(new Bicicleta(boaViagem));
        Veiculo patineteBoaViagem = veiculo(new Patinete(boaViagem, 85));
        veiculo(new Patinete(boaViagem, 10));
        veiculo(new Bicicleta(derby));
        Veiculo patineteDerby = veiculo(new Patinete(derby, 70));
        veiculo(new Bicicleta(marcoZero));
        Veiculo bikeManutencao = veiculo(new Bicicleta(marcoZero));
        Veiculo bikeEmUso = veiculo(new Bicicleta(marcoZero));

        usuarios.salvar(new Administrador("Administrador", "99999999999", "abc", "Não se aplica"));
        UsuarioComum ana = new UsuarioComum("Ana Souza", "11111111111", "123456", "Cartão de crédito");
        UsuarioAssinante bruno = new UsuarioAssinante("Bruno Lima", "22222222222", "123456", "Pix", 60);
        UsuarioComum carla = new UsuarioComum("Carla Mendes", "33333333333", "123456", "Pix");
        usuarios.salvar(ana);
        usuarios.salvar(bruno);
        usuarios.salvar(carla);

        LocalDateTime agora = LocalDateTime.now();

        corridaFinalizada(ana, bike1, boaViagem, derby, agora.minusHours(5), 18);

        corridaFinalizada(bruno, patineteDerby, derby, zonaLivre, agora.minusHours(3), 35);
        patineteDerby.estacionarEm(marcoZero);

        corridaFinalizada(bruno, patineteBoaViagem, boaViagem, marcoZero, agora.minusHours(2), 52);

        Manutencao manutencao = new Manutencao("Pneu dianteiro furado", bikeManutencao);
        manutencao.setId(1);
        manutencao.registrar(agora.minusHours(1));
        manutencoes.salvar(manutencao);

        corridas.salvar(novaCorrida(carla, bikeEmUso, marcoZero, agora.minusMinutes(12)));
    }

    private Estacao estacao(int id, String nome, double latitude, double longitude, int capacidade) {
        Estacao estacao = new Estacao(nome, latitude, longitude, capacidade);
        estacao.setId(id);
        return estacoes.salvar(estacao);
    }

    private Veiculo veiculo(Veiculo veiculo) {
        veiculo.setId(proximoIdVeiculo++);
        veiculo.getEstacao().receberVeiculo(veiculo);
        return veiculos.salvar(veiculo);
    }

    private Corrida novaCorrida(Usuario usuario, Veiculo veiculo, Estacao origem,
            LocalDateTime inicio) {
        Corrida corrida = new Corrida();
        corrida.setId(proximoIdCorrida++);
        corrida.setUsuario(usuario);
        corrida.setVeiculo(veiculo);
        corrida.setEstacaoOrigem(origem);
        corrida.iniciar(inicio);
        return corrida;
    }

    private void corridaFinalizada(Usuario usuario, Veiculo veiculo, Estacao origem,
            Estacao destino, LocalDateTime inicio, int minutos) {
        Corrida corrida = novaCorrida(usuario, veiculo, origem, inicio);
        corrida.finalizar(destino, inicio.plusMinutes(minutos));
        corridas.salvar(corrida);
    }
}
