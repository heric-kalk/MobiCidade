package com.mobicidade.service;

import com.mobicidade.model.Usuario;

public interface IUsuarioServico extends IServico<Usuario, String> {
    Usuario autenticar(String cpf, String senha);
}
