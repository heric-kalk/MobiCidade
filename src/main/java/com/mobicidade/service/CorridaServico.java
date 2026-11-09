package com.mobicidade.service;

import com.mobicidade.model.AlertaClimatico;
import com.mobicidade.model.Corrida;
import com.mobicidade.model.Estacao;
import com.mobicidade.model.Patinete;
import com.mobicidade.model.StatusVeiculo;
import com.mobicidade.model.StatusEstacao;
import com.mobicidade.model.Usuario;
import com.mobicidade.model.Veiculo;
import com.mobicidade.repository.ICorridaRepositorio;

import java.time.LocalDateTime;

public class CorridaServico extends ServicoInteiroBase<Corrida> implements ICorridaServico {
    public static final double LIMIAR_BATERIA_PADRAO = 15.0;

    private final IUsuarioServico usuarioServico;
    private final IVeiculoServico veiculoServico;
    private final IEstacaoServico estacaoServico;
    private final IClimaServico climaServico;

    private double limiarBateriaMinima = LIMIAR_BATERIA_PADRAO;

    public CorridaServico(
            ICorridaRepositorio repositorio,
            IUsuarioServico usuarioServico,
            IVeiculoServico veiculoServico,
            IEstacaoServico estacaoServico,
            IClimaServico climaServico) {
        super(repositorio);
        this.usuarioServico = usuarioServico;
        this.veiculoServico = veiculoServico;
        this.estacaoServico = estacaoServico;
        this.climaServico = climaServico;
    }

    @Override
    protected Integer obterId(Corrida corrida) {
        return corrida.getId();
    }

    @Override
    protected void definirId(Corrida corrida, int id) {
        corrida.setId(id);
    }

    @Override
    protected String nomeEntidade() {
        return "Corrida";
    }

    // REQ09, REQ17 a REQ20

    @Override
    public Corrida iniciar(
            String cpfUsuario,
            int idVeiculo,
            int idEstacaoOrigem,
            LocalDateTime inicio) {
        Corrida corrida = new Corrida();

        corrida.setUsuario(usuarioServico.buscarPorId(cpfUsuario));
        corrida.setVeiculo(veiculoServico.buscarPorId(idVeiculo));
        corrida.setEstacaoOrigem(estacaoServico.buscarPorId(idEstacaoOrigem));
        corrida.setDataHoraInicio(inicio);

        return cadastrar(corrida);
    }

    @Override
    protected void prepararCadastro(Corrida corrida) {
        super.prepararCadastro(corrida);

        exigir(corrida.getUsuario() != null,
                "A corrida deve ter um usuário.");

        exigir(corrida.getVeiculo() != null,
                "A corrida deve ter um veículo.");

        exigir(corrida.getEstacaoOrigem() != null,
                "A corrida deve ter uma estação de origem.");

        exigir(corrida.getDataHoraFim() == null,
                "Uma nova corrida não pode nascer finalizada.");

        corrida.setUsuario(
                usuarioServico.buscarPorId(corrida.getUsuario().getCpf()));

        corrida.setVeiculo(
                veiculoServico.buscarPorId(corrida.getVeiculo().getId()));

        corrida.setEstacaoOrigem(
                estacaoServico.buscarPorId(corrida.getEstacaoOrigem().getId()));

        if (corrida.getDataHoraInicio() == null) {
            corrida.setDataHoraInicio(LocalDateTime.now());
        }
    }

    @Override
    protected void validar(Corrida corrida) {
        exigir(
                corrida.getUsuario() != null
                        && corrida.getVeiculo() != null
                        && corrida.getEstacaoOrigem() != null,
                "A corrida deve ter usuário, veículo e estação de origem.");

        exigir(
                corrida.getDataHoraInicio() != null,
                "A corrida deve ter data/hora de início.");
    }

    @Override
    protected void regrasDeCadastro(Corrida corrida) {
        Usuario usuario = corrida.getUsuario();
        Veiculo veiculo = corrida.getVeiculo();
        Estacao origem = corrida.getEstacaoOrigem();

        exigir(origem.getStatus() != StatusEstacao.BLOQUEADA,
                "Não é possível iniciar uma corrida: a estação "
                        + origem.getId() + " está bloqueada.");

        exigir(!corrida.getDataHoraInicio().isAfter(LocalDateTime.now()),
                "A data/hora de início da corrida não pode estar no futuro.");

        // REQ17 - Verificação climática.
        AlertaClimatico alerta = climaServico.consultar(
                origem.getLatitude(),
                origem.getLongitude()).verificarAlerta();

        exigir(alerta != AlertaClimatico.SEM_DADOS,
                "[REQ17] Corrida bloqueada: previsão climática indisponível (sem conexão com a API e sem cache).");

        exigir(alerta != AlertaClimatico.ALERTA_CHUVA_FORTE,
                "[REQ17] Corrida bloqueada: há previsão de chuva forte ou tempestade na região da estação de origem.");

        // REQ18 - Veículo não pode estar em manutenção.
        exigir(veiculo.getStatus() != StatusVeiculo.MANUTENCAO,
                "[REQ18] O veículo " + veiculo.getId() + " está em manutenção.");

        // REQ20 - Estação precisa possuir veículos disponíveis.
        exigir(origem.veiculosDisponiveis() > 0,
                "[REQ20] A estação " + origem.getId()
                        + " não possui nenhum veículo disponível.");

        exigir(veiculo.estaDisponivel(),
                "O veículo " + veiculo.getId()
                        + " não está disponível (" + veiculo.getStatus() + ").");

        exigir(origem.possuiVeiculo(veiculo),
                "O veículo " + veiculo.getId()
                        + " não está na estação de origem " + origem.getId() + ".");

        // REQ19 - Verificação de bateria do patinete.
        if (veiculo instanceof Patinete) {
            Patinete patinete = (Patinete) veiculo;
            double bateria = patinete.getBateria();

            exigir(patinete.bateriaSuficiente(limiarBateriaMinima),
                    String.format(
                            "[REQ19] Bateria do patinete %d (%.1f%%) abaixo do mínimo de %.1f%%.",
                            veiculo.getId(),
                            bateria,
                            limiarBateriaMinima));
        }

        for (Corrida outra : repositorio.listarTodos()) {
            exigir(
                    !(outra.emAndamento()
                            && outra.getUsuario().getCpf().equals(usuario.getCpf())),
                    "O usuário " + usuario.getNome()
                            + " já possui uma corrida em andamento.");
        }
    }

    @Override
    protected void aposCadastrar(Corrida corrida) {
        corrida.iniciar(corrida.getDataHoraInicio());
    }

    // REQ10 a REQ12, REQ21 e REQ22

    @Override
    public Corrida finalizar(
            int idCorrida,
            Integer idEstacaoDestino,
            LocalDateTime fim) {
        Corrida corrida = buscarPorId(idCorrida);

        exigir(corrida.emAndamento(),
                "A corrida " + idCorrida + " não está em andamento.");

        LocalDateTime termino = fim != null
                ? fim
                : LocalDateTime.now();

        // REQ22 - Duração precisa ser maior que zero.
        exigir(corrida.calcularDuracao(termino) > 0,
                "[REQ22] A duração da corrida deve ser maior que zero minutos (término deve ser posterior ao início).");

        Estacao destino = idEstacaoDestino != null
                ? estacaoServico.buscarPorId(idEstacaoDestino)
                : estacaoServico.obterZonaLivre();

        // REQ21 - Estação precisa possuir vaga.
        exigir(destino.temVaga(),
                "[REQ21] A estação de destino "
                        + destino.getId()
                        + " não possui vaga disponível.");

        corrida.finalizar(destino, termino);

        return repositorio.atualizar(corrida);
    }

    @Override
    protected void regrasDeAtualizacao(Corrida existente, Corrida novo) {
        exigir(existente.emAndamento(),
                "Corridas finalizadas são histórico e não podem ser alteradas.");

        exigir(
                novo.getDataHoraInicio() != null
                        && !novo.getDataHoraInicio().isAfter(LocalDateTime.now()),
                "A data/hora de início deve existir e não pode estar no futuro.");
    }

    @Override
    protected void copiarCampos(Corrida existente, Corrida novo) {
        existente.setDataHoraInicio(novo.getDataHoraInicio());
    }

    @Override
    protected void regrasDeRemocao(Corrida corrida) {
        exigir(!corrida.emAndamento(),
                "Finalize a corrida antes de removê-la.");
    }

    @Override
    public void definirLimiarBateriaMinima(double percentual) {
        exigir(percentual >= 0 && percentual <= 100,
                "O limiar de bateria deve estar entre 0 e 100%.");

        this.limiarBateriaMinima = percentual;
    }

    @Override
    public double getLimiarBateriaMinima() {
        return limiarBateriaMinima;
    }
}
