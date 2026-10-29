package com.mobicidade.ui;

import com.mobicidade.exception.RegraDeNegocioException;
import com.mobicidade.facade.IMobiCidadeFachada;
import com.mobicidade.model.Bicicleta;
import com.mobicidade.model.Corrida;
import com.mobicidade.model.Estacao;
import com.mobicidade.model.Patinete;
import com.mobicidade.model.UsuarioAssinante;
import com.mobicidade.model.UsuarioComum;

import java.time.LocalDateTime;

public final class DadosIniciais {

    private DadosIniciais() {
    }

    public static void carregar(IMobiCidadeFachada mobi) {
        Estacao boaViagem = mobi.cadastrarEstacao("Boa Viagem", -8.1190, -34.9040, 6);
        Estacao derby = mobi.cadastrarEstacao("Derby", -8.0560, -34.8990, 4);
        Estacao marcoZero = mobi.cadastrarEstacao("Marco Zero", -8.0630, -34.8710, 5);
        mobi.cadastrarEstacao("Universidade Federal Rural", -8.0150, -34.9490, 3);

        Bicicleta bike1 = mobi.cadastrarBicicleta(boaViagem.getId());
        mobi.cadastrarBicicleta(boaViagem.getId());
        Patinete patineteBoaViagem = mobi.cadastrarPatinete(boaViagem.getId(), 85);
        mobi.cadastrarPatinete(boaViagem.getId(), 10);
        mobi.cadastrarBicicleta(derby.getId());
        Patinete patineteDerby = mobi.cadastrarPatinete(derby.getId(), 70);
        mobi.cadastrarBicicleta(marcoZero.getId());
        Bicicleta bikeManutencao = mobi.cadastrarBicicleta(marcoZero.getId());
        Bicicleta bikeEmUso = mobi.cadastrarBicicleta(marcoZero.getId());

        UsuarioComum ana = mobi.cadastrarUsuarioComum("Ana Souza", "11111111111", "Cartão de crédito");
        UsuarioAssinante bruno = mobi.cadastrarUsuarioAssinante("Bruno Lima", "22222222222", "Pix", 60);
        UsuarioComum carla = mobi.cadastrarUsuarioComum("Carla Mendes", "33333333333", "Pix");

        LocalDateTime agora = LocalDateTime.now();
        try {
            Corrida c1 = mobi.iniciarCorrida(ana.getCpf(), bike1.getId(), boaViagem.getId(), agora.minusHours(5));
            mobi.finalizarCorrida(c1.getId(), derby.getId(), agora.minusHours(5).plusMinutes(18));

            Corrida c2 = mobi.iniciarCorrida(bruno.getCpf(), patineteDerby.getId(), derby.getId(), agora.minusHours(3));
            mobi.finalizarCorrida(c2.getId(), null, agora.minusHours(3).plusMinutes(35));
            mobi.recolherVeiculoParaEstacao(patineteDerby.getId(), marcoZero.getId());

            Corrida c3 = mobi.iniciarCorrida(bruno.getCpf(), patineteBoaViagem.getId(), boaViagem.getId(), agora.minusHours(2));
            mobi.finalizarCorrida(c3.getId(), marcoZero.getId(), agora.minusHours(2).plusMinutes(52));

            mobi.registrarManutencao(bikeManutencao.getId(), "Pneu dianteiro furado", agora.minusHours(1));

            mobi.iniciarCorrida(carla.getCpf(), bikeEmUso.getId(), marcoZero.getId(), agora.minusMinutes(12));
        } catch (RegraDeNegocioException e) {
            System.out.println("Aviso: corridas e manutenção iniciais não carregadas -> " + e.getMessage());
        }
    }
}
