package com.mobicidade.service;

import com.mobicidade.model.Administrador;
import com.mobicidade.model.Corrida;
import com.mobicidade.model.Usuario;
import com.mobicidade.model.UsuarioAssinante;
import com.mobicidade.repository.ICorridaRepositorio;
import com.mobicidade.repository.IUsuarioRepositorio;
import com.mobicidade.security.SenhaUtil;
import com.mobicidade.exception.EntidadeNaoEncontradaException;

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

    @Override
    protected void prepararCadastro(Usuario usuario) {
        super.prepararCadastro(usuario);
        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
            usuario.setSenha(SenhaUtil.gerarHash(usuario.getSenha()));
        }
    }

    @Override
    protected void validar(Usuario usuario) {
        exigir(usuario.getNome() != null && !usuario.getNome().isBlank(),
                "O nome do usuário é obrigatório.");

        exigir(usuario.getCpf() != null && usuario.getCpf().matches("\\d{11}"),
                "O CPF deve conter exatamente 11 dígitos numéricos.");

        // Dados iniciais antigos podem conter CPFs fictícios; novos cadastros exigem dígitos verificadores válidos.
        if (!repositorio.existe(usuario.getCpf())) {
            exigir(cpfValido(usuario.getCpf()),
                    "O CPF informado é inválido: confira os dígitos verificadores.");
        }

        exigir(usuario.getSenha() != null && !usuario.getSenha().isBlank(),
                "A senha é obrigatória.");

        exigir(usuario.getFormaDePagamento() != null && !usuario.getFormaDePagamento().isBlank(),
                "A forma de pagamento é obrigatória.");

        if (usuario instanceof UsuarioAssinante) {
            exigir(((UsuarioAssinante) usuario).getMinutosInclusos() >= 0,
                    "Os minutos inclusos do plano não podem ser negativos.");
        }
    }

    @Override
    protected void regrasDeCadastro(Usuario usuario) {
        exigir(!repositorio.existe(usuario.getCpf()),
                "Já existe uma conta cadastrada com esse CPF.");
    }

    @Override
    protected void regrasDeAtualizacao(Usuario existente, Usuario novo) {
        exigir(existente.getClass() == novo.getClass(),
                "Não é possível trocar o tipo do usuário por atualização.");
    }

    @Override
    protected void copiarCampos(Usuario existente, Usuario novo) {
        existente.setNome(novo.getNome());
        existente.setSenha(SenhaUtil.gerarHash(novo.getSenha()));
        existente.setFormaDePagamento(novo.getFormaDePagamento());

        if (existente instanceof UsuarioAssinante) {
            ((UsuarioAssinante) existente).setMinutosInclusos(
                    ((UsuarioAssinante) novo).getMinutosInclusos());
        }
    }

    @Override
    protected void regrasDeRemocao(Usuario usuario) {
        for (Corrida corrida : corridaRepositorio.listarTodos()) {
            exigir(!(corrida.emAndamento() && corrida.getUsuario() != null
                            && corrida.getUsuario().getCpf().equals(usuario.getCpf())),
                    "Não é possível remover um usuário com corrida em andamento.");
        }

        if (usuario instanceof Administrador) {
            long administradores = repositorio.listarTodos().stream()
                    .filter(Administrador.class::isInstance)
                    .count();
            exigir(administradores > 1,
                    "Não é possível remover o único administrador cadastrado.");
        }
    }

    @Override
    public Usuario autenticar(String cpf, String senha) {
        if (cpf == null || senha == null) {
            return null;
        }

        try {
            Usuario usuario = buscarPorId(cpf);

            if (!SenhaUtil.verificar(senha, usuario.getSenha())) {
                return null;
            }

            return usuario;
        } catch (EntidadeNaoEncontradaException e) {
            return null;
        }
    }

    private boolean cpfValido(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) {
            return false;
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * (10 - i);
        }
        int primeiro = (soma * 10) % 11;
        if (primeiro == 10) primeiro = 0;
        if (primeiro != cpf.charAt(9) - '0') return false;

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * (11 - i);
        }
        int segundo = (soma * 10) % 11;
        if (segundo == 10) segundo = 0;
        return segundo == cpf.charAt(10) - '0';
    }

    @Override
    public UsuarioAssinante renovarPlano(String cpf) {
        Usuario usuario = buscarPorId(cpf);

        exigir(usuario instanceof UsuarioAssinante,
                "Apenas usuários assinantes possuem plano para renovar.");

        UsuarioAssinante assinante = (UsuarioAssinante) usuario;
        assinante.renovarPlano();
        return assinante;
    }
}