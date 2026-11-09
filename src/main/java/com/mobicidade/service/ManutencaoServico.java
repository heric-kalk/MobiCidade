package com.mobicidade.service;

import com.mobicidade.model.Estacao;
import com.mobicidade.model.Manutencao;
import com.mobicidade.model.StatusVeiculo;
import com.mobicidade.repository.IManutencaoRepositorio;

import java.time.LocalDateTime;

public class ManutencaoServico extends ServicoInteiroBase<Manutencao> implements IManutencaoServico {
    private final IVeiculoServico veiculoServico;
    private final IEstacaoServico estacaoServico;

    public ManutencaoServico(IManutencaoRepositorio repositorio, IVeiculoServico veiculoServico,
            IEstacaoServico estacaoServico) {
        super(repositorio);
        this.veiculoServico = veiculoServico;
        this.estacaoServico = estacaoServico;
    }

    @Override
    protected Integer obterId(Manutencao manutencao) {
        return manutencao.getId();
    }

    @Override
    protected void definirId(Manutencao manutencao, int id) {
        manutencao.setId(id);
    }

    @Override
    protected String nomeEntidade() {
        return "Manutenção";
    }

    // REQ13

    @Override
    public Manutencao registrar(int idVeiculo, String descricao, LocalDateTime inicio) {
        Manutencao manutencao = new Manutencao(descricao, veiculoServico.buscarPorId(idVeiculo));
        manutencao.setDataInicio(inicio);
        return cadastrar(manutencao);
    }

    @Override
    protected void prepararCadastro(Manutencao manutencao) {
        super.prepararCadastro(manutencao);
        exigir(manutencao.getVeiculo() != null, "A manutenção deve estar vinculada a um veículo.");
        manutencao.setVeiculo(veiculoServico.buscarPorId(manutencao.getVeiculo().getId()));
        if (manutencao.getDataInicio() == null) {
            manutencao.setDataInicio(LocalDateTime.now());
        }
    }

    @Override
    protected void validar(Manutencao manutencao) {
        exigir(!manutencao.getDataInicio().isAfter(LocalDateTime.now()),
                "A data de início da manutenção não pode estar no futuro.");
        exigir(manutencao.getDescricao() != null && !manutencao.getDescricao().isBlank(),
                "A descrição da manutenção é obrigatória.");
        exigir(manutencao.getVeiculo() != null, "A manutenção deve estar vinculada a um veículo.");
    }

    @Override
    protected void regrasDeCadastro(Manutencao manutencao) {
        StatusVeiculo status = manutencao.getVeiculo().getStatus();
        exigir(status != StatusVeiculo.EM_USO, "Não é possível enviar para manutenção um veículo em uso.");
        exigir(status != StatusVeiculo.MANUTENCAO, "O veículo já está em manutenção.");
    }

    @Override
    protected void aposCadastrar(Manutencao manutencao) {
        manutencao.registrar(manutencao.getDataInicio());
    }

    // REQ14

    @Override
    public Manutencao concluir(int idManutencao, int idEstacao, LocalDateTime fim) {
        Manutencao manutencao = buscarPorId(idManutencao);
        exigir(manutencao.emAndamento(), "A manutenção " + idManutencao + " já foi concluída.");
        LocalDateTime termino = fim != null ? fim : LocalDateTime.now();
        exigir(!termino.isBefore(manutencao.getDataInicio()),
                "O término não pode ser anterior ao início da manutenção.");
        exigir(!termino.isAfter(LocalDateTime.now()),
                "A data de conclusão da manutenção não pode estar no futuro.");

        Estacao estacao = estacaoServico.buscarPorId(idEstacao);

        exigir(!estacao.estaBloqueada(),
                "Não é possível reintegrar o veículo em uma estação bloqueada.");

        exigir(estacao.temVaga(),
                "A estação " + idEstacao + " não possui vagas livres para reintegrar o veículo.");

        manutencao.finalizar(estacao, termino);
        return repositorio.atualizar(manutencao);
    }

    @Override
    protected void copiarCampos(Manutencao existente, Manutencao novo) {
        existente.setDescricao(novo.getDescricao());
    }

    @Override
    protected void regrasDeRemocao(Manutencao manutencao) {
        exigir(!manutencao.emAndamento(), "Conclua a manutenção antes de removê-la.");
    }
}
