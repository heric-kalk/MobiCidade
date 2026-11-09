package com.mobicidade.service;

import com.mobicidade.model.Usuario;
import com.mobicidade.model.UsuarioAssinante;

public interface IUsuarioServico extends IServico<Usuario, String> {
    Usuario autenticar(String cpf, String senha);

    UsuarioAssinante renovarPlano(String cpf);
}
