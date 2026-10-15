package com.mobicidade;

import com.mobicidade.controller.CorridaController;
import com.mobicidade.controller.EstacaoController;
import com.mobicidade.controller.ManutencaoController;
import com.mobicidade.controller.UsuarioController;
import com.mobicidade.controller.VeiculoController;
import com.mobicidade.repository.CorridaRepositorioMemoria;
import com.mobicidade.repository.EstacaoRepositorioMemoria;
import com.mobicidade.repository.ManutencaoRepositorioMemoria;
import com.mobicidade.repository.UsuarioRepositorioMemoria;
import com.mobicidade.repository.VeiculoRepositorioMemoria;
import com.mobicidade.service.CorridaServicoImpl;
import com.mobicidade.service.EstacaoServicoImpl;
import com.mobicidade.service.ManutencaoServicoImpl;
import com.mobicidade.service.ProvedorPrevisaoClimatica;
import com.mobicidade.service.UsuarioServicoImpl;
import com.mobicidade.service.VeiculoServicoImpl;

public final class AplicacaoMobiCidade {

    private final EstacaoController estacoes;
    private final VeiculoController veiculos;
    private final UsuarioController usuarios;
    private final CorridaController corridas;
    private final ManutencaoController manutencoes;

    public AplicacaoMobiCidade() {
        this(ProvedorPrevisaoClimatica.semDados());
    }

    public AplicacaoMobiCidade(ProvedorPrevisaoClimatica previsaoClimatica) {
        EstacaoRepositorioMemoria estacaoRepositorio = new EstacaoRepositorioMemoria();
        VeiculoRepositorioMemoria veiculoRepositorio = new VeiculoRepositorioMemoria();
        UsuarioRepositorioMemoria usuarioRepositorio = new UsuarioRepositorioMemoria();
        CorridaRepositorioMemoria corridaRepositorio = new CorridaRepositorioMemoria();
        ManutencaoRepositorioMemoria manutencaoRepositorio = new ManutencaoRepositorioMemoria();

        this.estacoes = new EstacaoController(new EstacaoServicoImpl(estacaoRepositorio));
        this.veiculos = new VeiculoController(new VeiculoServicoImpl(veiculoRepositorio, estacaoRepositorio));
        this.usuarios = new UsuarioController(new UsuarioServicoImpl(usuarioRepositorio));
        this.corridas = new CorridaController(new CorridaServicoImpl(corridaRepositorio, previsaoClimatica));
        this.manutencoes = new ManutencaoController(new ManutencaoServicoImpl(manutencaoRepositorio));
    }

    public EstacaoController getEstacoes() {
        return estacoes;
    }

    public VeiculoController getVeiculos() {
        return veiculos;
    }

    public UsuarioController getUsuarios() {
        return usuarios;
    }

    public CorridaController getCorridas() {
        return corridas;
    }

    public ManutencaoController getManutencoes() {
        return manutencoes;
    }
}
