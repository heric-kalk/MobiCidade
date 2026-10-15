package com.mobicidade.service;

import com.mobicidade.exception.EntidadeNaoEncontradaException;
import com.mobicidade.model.Estacao;
import com.mobicidade.model.Patinete;
import com.mobicidade.model.Veiculo;
import com.mobicidade.repository.EstacaoRepositorio;
import com.mobicidade.repository.VeiculoRepositorio;

public class VeiculoServicoImpl extends ServicoBase<Veiculo, Integer> implements VeiculoServico {

    private final EstacaoRepositorio estacaoRepositorio;

    public VeiculoServicoImpl(VeiculoRepositorio repositorio, EstacaoRepositorio estacaoRepositorio) {
        super(repositorio);
        this.estacaoRepositorio = estacaoRepositorio;
    }

    @Override
    protected void validar(Veiculo veiculo) {
        exigir(veiculo.getId() > 0, "O identificador do veículo deve ser maior que zero.");
        exigir(veiculo.getStatus() != null, "O status do veículo é obrigatório.");
        exigir(veiculo.getEstacao() != null, "O veículo deve estar vinculado a uma estação.");
        exigir(estacaoRepositorio.existe(veiculo.getEstacao().getId()),
                "A estação do veículo não está cadastrada.");

        if (veiculo instanceof Patinete) {
            double bateria = ((Patinete) veiculo).getBateria();
            exigir(bateria >= 0 && bateria <= 100,
                    "A bateria do patinete deve estar entre 0 e 100%.");
        }
    }

    @Override
    protected Integer obterId(Veiculo veiculo) {
        return veiculo.getId();
    }

    @Override
    protected String nomeEntidade() {
        return "Veículo";
    }

    @Override
    public Veiculo cadastrar(Veiculo veiculo) {
        exigir(veiculo != null, "Veículo não pode ser nulo.");
        validar(veiculo);

        // Sempre usa a estação que está efetivamente cadastrada no repositório.
        Estacao estacaoCadastrada = obterEstacaoCadastrada(veiculo.getEstacao().getId());
        exigir(estacaoCadastrada.vagasLivres() > 0, "A estação não possui vagas livres.");
        veiculo.setEstacao(estacaoCadastrada);

        return super.cadastrar(veiculo);
    }

    @Override
    public Veiculo atualizar(Veiculo veiculo) {
        exigir(veiculo != null, "Veículo não pode ser nulo.");
        Veiculo anterior = buscarPorId(veiculo.getId());
        validar(veiculo);

        Estacao estacaoAnterior = anterior.getEstacao();
        Estacao novaEstacao = obterEstacaoCadastrada(veiculo.getEstacao().getId());
        boolean mudouDeEstacao = estacaoAnterior == null
                || estacaoAnterior.getId() != novaEstacao.getId();

        if (mudouDeEstacao) {
            exigir(novaEstacao.vagasLivres() > 0, "A nova estação não possui vagas livres.");
        }

        // Mantém a relação bidirecional Estação <-> Veículo sincronizada no update.
        veiculo.setEstacao(novaEstacao);
        Veiculo atualizado = super.atualizar(veiculo);

        if (estacaoAnterior != null) {
            estacaoAnterior.removerVeiculo(anterior);
        }
        novaEstacao.adicionarVeiculo(atualizado);

        return atualizado;
    }

    @Override
    protected void aposCadastrar(Veiculo veiculo) {
        veiculo.getEstacao().adicionarVeiculo(veiculo);
    }

    @Override
    protected void aposRemover(Veiculo veiculo) {
        if (veiculo.getEstacao() != null) {
            veiculo.getEstacao().removerVeiculo(veiculo);
        }
    }

    private Estacao obterEstacaoCadastrada(int idEstacao) {
        return estacaoRepositorio.buscarPorId(idEstacao)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Estação " + idEstacao + " não encontrada."));
    }
}
