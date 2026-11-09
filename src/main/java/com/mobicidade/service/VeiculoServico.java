package com.mobicidade.service;

import com.mobicidade.model.Estacao;
import com.mobicidade.model.Patinete;
import com.mobicidade.model.StatusVeiculo;
import com.mobicidade.model.Veiculo;
import com.mobicidade.repository.IVeiculoRepositorio;

public class VeiculoServico extends ServicoInteiroBase<Veiculo> implements IVeiculoServico {
    private final IEstacaoServico estacaoServico;

    public VeiculoServico(IVeiculoRepositorio repositorio, IEstacaoServico estacaoServico) {
        super(repositorio);
        this.estacaoServico = estacaoServico;
    }

    @Override
    protected Integer obterId(Veiculo veiculo) {
        return veiculo.getId();
    }

    @Override
    protected void definirId(Veiculo veiculo, int id) {
        veiculo.setId(id);
    }

    @Override
    protected String nomeEntidade() {
        return "Veículo";
    }

    @Override
    protected void prepararCadastro(Veiculo veiculo) {
        super.prepararCadastro(veiculo);
        exigir(veiculo.getEstacao() != null, "O veículo deve estar vinculado a uma estação.");
        veiculo.setEstacao(estacaoServico.buscarPorId(veiculo.getEstacao().getId()));
        veiculo.alterarDisponibilidade(StatusVeiculo.DISPONIVEL);
    }

    // REQ02
    @Override
    protected void validar(Veiculo veiculo) {
        exigir(veiculo.getEstacao() != null, "O veículo deve estar vinculado a uma estação.");
        if (veiculo instanceof Patinete) {
            double bateria = ((Patinete) veiculo).getBateria();
            exigir(bateria >= 0 && bateria <= 100, "A bateria do patinete deve estar entre 0 e 100%.");
        }
    }

    @Override
    protected void regrasDeCadastro(Veiculo veiculo) {
        exigir(veiculo.getEstacao().temVaga(), "A estação não possui vagas livres.");
    }

    @Override
    protected void aposCadastrar(Veiculo veiculo) {
        veiculo.getEstacao().receberVeiculo(veiculo);
    }

    @Override
    protected void regrasDeAtualizacao(Veiculo existente, Veiculo novo) {
        exigir(existente.getClass() == novo.getClass(), "Não é possível trocar o tipo do veículo.");
        exigir(existente.getStatus() != StatusVeiculo.EM_USO, "Veículo em uso não pode ser alterado.");
        if (novo.getEstacao() != null && existente.getEstacao() != null
                && novo.getEstacao().getId() != existente.getEstacao().getId()) {
            Estacao destino = estacaoServico.buscarPorId(novo.getEstacao().getId());
            exigir(destino.temVaga(), "A nova estação não possui vagas livres.");
        }
    }

    @Override
    protected void copiarCampos(Veiculo existente, Veiculo novo) {
        if (novo.getEstacao() != null && existente.getEstacao() != null
                && novo.getEstacao().getId() != existente.getEstacao().getId()) {
            Estacao destino = estacaoServico.buscarPorId(novo.getEstacao().getId());
            existente.getEstacao().liberarVeiculo(existente);
            destino.receberVeiculo(existente);
        }
        if (existente instanceof Patinete) {
            ((Patinete) existente).setBateria(((Patinete) novo).getBateria());
        }
    }

    @Override
    protected void regrasDeRemocao(Veiculo veiculo) {
        exigir(veiculo.getStatus() != StatusVeiculo.EM_USO,
                "Não é possível remover um veículo em uso.");

        exigir(veiculo.getStatus() != StatusVeiculo.MANUTENCAO,
                "Não é possível remover um veículo em manutenção.");
    }

    @Override
    protected void aposRemover(Veiculo veiculo) {
        if (veiculo.getEstacao() != null) {
            veiculo.getEstacao().liberarVeiculo(veiculo);
        }
    }

    @Override
    public Veiculo recolherParaEstacao(int idVeiculo, int idEstacao) {
        Veiculo veiculo = buscarPorId(idVeiculo);
        exigir(veiculo.getStatus() == StatusVeiculo.FORA_DE_ESTACAO
                || (veiculo.getEstacao() != null && veiculo.getEstacao().ehZonaLivre()),
                "Apenas veículos deixados em zona livre podem ser recolhidos.");
        Estacao estacao = estacaoServico.buscarPorId(idEstacao);
        exigir(!estacao.ehZonaLivre(), "Escolha uma estação válida para recolher o veículo.");
        exigir(estacao.temVaga(), "A estação não possui vagas livres.");
        veiculo.estacionarEm(estacao);
        return veiculo;
    }
}
