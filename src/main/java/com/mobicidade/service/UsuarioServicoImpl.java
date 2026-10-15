package com.mobicidade.service;

import com.mobicidade.model.Usuario;
import com.mobicidade.model.UsuarioAssinante;
import com.mobicidade.repository.UsuarioRepositorio;

public class UsuarioServicoImpl extends ServicoBase<Usuario, String> implements UsuarioServico {

    public UsuarioServicoImpl(UsuarioRepositorio repositorio) {
        super(repositorio);
    }

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
    protected String obterId(Usuario usuario) {
        return usuario.getCpf();
    }

    @Override
    protected String nomeEntidade() {
        return "Usuário";
    }
}
