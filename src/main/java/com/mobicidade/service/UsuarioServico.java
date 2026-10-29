package com.mobicidade.service;

import com.mobicidade.model.Corrida;
import com.mobicidade.model.Usuario;
import com.mobicidade.model.UsuarioAssinante;
import com.mobicidade.repository.ICorridaRepositorio;
import com.mobicidade.repository.IUsuarioRepositorio;

public class UsuarioServico extends ServicoBase<Usuario, String> implements IUsuarioServico {

    private final ICorridaRepositorio corridaRepositorio;

    public UsuarioServico(IUsuarioRepositorio repositorio, ICorridaRepositorio corridaRepositorio) {
        super(repositorio);
        this.corridaRepositorio = corridaRepositorio;
    }

    @Override
    protected String obterId(Usuario usuario) {
        return usuario.getCpf();
    }

    @Override
    protected String nomeEntidade() {
        return "Usuário";
    }

    // REQ04, REQ05
    @Override
    protected void validar(Usuario usuario) {
        exigir(usuario.getNome() != null && !usuario.getNome().isBlank(), "O nome do usuário é obrigatório.");
        exigir(usuario.getCpf() != null && usuario.getCpf().matches("\\d{11}"),
                "O CPF deve conter exatamente 11 dígitos numéricos.");
        exigir(usuario.getFormaDePagamento() != null && !usuario.getFormaDePagamento().isBlank(),
                "A forma de pagamento é obrigatória.");
        if (usuario instanceof UsuarioAssinante) {
            exigir(((UsuarioAssinante) usuario).getMinutosInclusos() >= 0,
                    "Os minutos inclusos do plano não podem ser negativos.");
        }
    }

    @Override
    protected void regrasDeAtualizacao(Usuario existente, Usuario novo) {
        exigir(existente.getClass() == novo.getClass(),
                "Não é possível trocar o tipo do usuário (Comum/Assinante) por atualização.");
    }

    @Override
    protected void copiarCampos(Usuario existente, Usuario novo) {
        existente.setNome(novo.getNome());
        existente.setFormaDePagamento(novo.getFormaDePagamento());
        if (existente instanceof UsuarioAssinante) {
            ((UsuarioAssinante) existente).setMinutosInclusos(((UsuarioAssinante) novo).getMinutosInclusos());
        }
    }

    @Override
    protected void regrasDeRemocao(Usuario usuario) {
        for (Corrida corrida : corridaRepositorio.listarTodos()) {
            exigir(!(corrida.emAndamento() && corrida.getUsuario() == usuario),
                    "Não é possível remover um usuário com corrida em andamento.");
        }
    }
}
