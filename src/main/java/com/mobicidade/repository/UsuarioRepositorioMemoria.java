package com.mobicidade.repository;

import com.mobicidade.model.Usuario;

public class UsuarioRepositorioMemoria extends RepositorioMemoria<Usuario, String> implements IUsuarioRepositorio {
    @Override
    protected String obterId(Usuario usuario) {
        return usuario.getCpf();
    }
}
