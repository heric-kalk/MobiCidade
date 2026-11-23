package com.mobicidade.service;

import com.mobicidade.model.Estacao;
import com.mobicidade.repository.ICorridaRepositorio;
import com.mobicidade.repository.IEstacaoRepositorio;

public class EstacaoServico extends ServicoInteiroBase<Estacao> implements IEstacaoServico {
    private final ICorridaRepositorio corridaRepositorio;

    public EstacaoServico(IEstacaoRepositorio repositorio) {
        this(repositorio, null);
    }

    public EstacaoServico(IEstacaoRepositorio repositorio, ICorridaRepositorio corridaRepositorio) {
        super(repositorio);
        this.corridaRepositorio = corridaRepositorio;
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
        estacao.setNome(estacao.getNome().trim());

        exigir(!Estacao.NOME_ZONA_LIVRE.equalsIgnoreCase(estacao.getNome()),
                "O nome Zona Livre é reservado para a estação especial do sistema.");

        exigir(estacao.getCapacidade() > 0,
                "A capacidade da estação deve ser maior que zero.");

        exigir(estacao.getLatitude() >= -90 && estacao.getLatitude() <= 90,
                "Latitude inválida.");

        exigir(estacao.getLongitude() >= -180 && estacao.getLongitude() <= 180,
                "Longitude inválida.");
    }

    @Override
    protected void regrasDeCadastro(Estacao estacao) {
        exigir(repositorio.listarTodos().stream()
                        .noneMatch(existente -> nomesIguais(existente.getNome(), estacao.getNome())),
                "Já existe uma estação cadastrada com esse nome.");
    }

    @Override
    protected void regrasDeAtualizacao(Estacao existente, Estacao novo) {
        exigir(!existente.ehZonaLivre(),
                "A estação Zona Livre é especial e não pode ser alterada.");

        exigir(repositorio.listarTodos().stream()
                        .filter(outra -> outra.getId() != existente.getId())
                        .noneMatch(outra -> nomesIguais(outra.getNome(), novo.getNome())),
                "Já existe outra estação cadastrada com esse nome.");

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
                "A estação Zona Livre não pode ser removida.");

        exigir(estacao.getVeiculos().isEmpty(),
                "Não é possível remover uma estação que ainda possui veículos.");

        boolean possuiHistorico = corridaRepositorio != null
                && corridaRepositorio.listarTodos().stream().anyMatch(corrida ->
                        mesmaEstacao(corrida.getEstacaoOrigem(), estacao)
                                || mesmaEstacao(corrida.getEstacaoDestino(), estacao));
        exigir(!possuiHistorico,
                "Não é possível remover uma estação vinculada ao histórico de corridas.");
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

    @Override
    public Estacao obterZonaLivre() {
        for (Estacao estacao : repositorio.listarTodos()) {
            if (estacao.ehZonaLivre()) {
                return estacao;
            }
        }

        // A estação especial é criada internamente; o cadastro público reserva esse nome.
        Estacao zonaLivre = new Estacao(Estacao.NOME_ZONA_LIVRE, -8.0476, -34.8770, 100);
        zonaLivre.setId(proximoIdDisponivel());
        return repositorio.salvar(zonaLivre);
    }

    private int proximoIdDisponivel() {
        int id = 1;
        while (repositorio.existe(id)) {
            id++;
        }
        return id;
    }

    private boolean nomesIguais(String a, String b) {
        return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
    }

    private boolean mesmaEstacao(Estacao a, Estacao b) {
        return a != null && b != null && a.getId() == b.getId();
    }
}
