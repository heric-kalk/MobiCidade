package com.mobicidade.service;

import com.mobicidade.model.Estacao;
import com.mobicidade.repository.IEstacaoRepositorio;

public class EstacaoServico extends ServicoInteiroBase<Estacao> implements IEstacaoServico {
    public EstacaoServico(IEstacaoRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    protected Integer obterId(Estacao estacao) {
        return estacao.getId();
    }

    @Override
    protected void definirId(Estacao estacao, int id) {
        estacao.setId(id);
    }

    @Override
    protected String nomeEntidade() {
        return "Estação";
    }

    // REQ01
    @Override
    protected void validar(Estacao estacao) {
        exigir(estacao.getNome() != null && !estacao.getNome().isBlank(),
                "O nome da estação é obrigatório.");

        exigir(!Estacao.NOME_ZONA_LIVRE.equalsIgnoreCase(estacao.getNome().trim()),
                "O nome Zona Livre é reservado para a estação especial do sistema.");

        exigir(estacao.getCapacidade() > 0,
                "A capacidade da estação deve ser maior que zero.");

        exigir(estacao.getLatitude() >= -90 && estacao.getLatitude() <= 90,
                "Latitude inválida.");

        exigir(estacao.getLongitude() >= -180 && estacao.getLongitude() <= 180,
                "Longitude inválida.");
    }

    @Override
    protected void regrasDeAtualizacao(Estacao existente, Estacao novo) {
        exigir(!existente.ehZonaLivre(),
                "A estação Zona Livre é especial e não pode ser alterada.");

        exigir(novo.getCapacidade() >= existente.getVeiculos().size(),
                "A capacidade não pode ser menor que a quantidade de veículos na estação.");
    }

    @Override
    protected void copiarCampos(Estacao existente, Estacao novo) {
        existente.setNome(novo.getNome());
        existente.setLatitude(novo.getLatitude());
        existente.setLongitude(novo.getLongitude());
        existente.setCapacidade(novo.getCapacidade());
    }

    @Override
    protected void regrasDeRemocao(Estacao estacao) {
        exigir(!estacao.ehZonaLivre(),
                "A estação Zona Livre é obrigatória e não pode ser removida.");

        exigir(estacao.getVeiculos().isEmpty(),
                "Não é possível remover uma estação que ainda possui veículos.");
    }

    @Override
    public int veiculosDisponiveis(int idEstacao) {
        return buscarPorId(idEstacao).veiculosDisponiveis();
    }

    @Override
    public int vagasLivres(int idEstacao) {
        return buscarPorId(idEstacao).vagasLivres();
    }

    @Override
    public void bloquearEstacao(int idEstacao) {
        Estacao estacao = buscarPorId(idEstacao);

        exigir(!estacao.ehZonaLivre(),
                "A estação Zona Livre não pode ser bloqueada.");

        exigir(!estacao.estaBloqueada(),
                "A estação " + idEstacao + " já está bloqueada.");

        estacao.bloquear();
    }

    @Override
    public void desbloquearEstacao(int idEstacao) {
        Estacao estacao = buscarPorId(idEstacao);

        exigir(estacao.estaBloqueada(),
                "A estação " + idEstacao + " não está bloqueada.");

        estacao.desbloquear();
    }
}
