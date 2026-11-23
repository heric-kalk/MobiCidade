package com.mobicidade.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Utilitários para armazenar e verificar senhas usando PBKDF2 com salt individual. */
public final class SenhaUtil {
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIXO = "pbkdf2_sha256$";
    private static final int ITERACOES = 120_000;
    private static final int TAMANHO_SALT_BYTES = 16;
    private static final int TAMANHO_CHAVE_BITS = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private SenhaUtil() {
    }

    public static String gerarHash(String senha) {
        if (senha == null) {
            throw new IllegalArgumentException("A senha não pode ser nula.");
        }
        if (ehHash(senha)) {
            return senha;
        }

        byte[] salt = new byte[TAMANHO_SALT_BYTES];
        ALEATORIO.nextBytes(salt);
        byte[] hash = derivar(senha.toCharArray(), salt, ITERACOES);
        return PREFIXO + ITERACOES + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(salt) + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(hash);
    }

    public static boolean verificar(String senhaInformada, String hashArmazenado) {
        if (senhaInformada == null || !ehHash(hashArmazenado)) {
            return false;
        }

        try {
            String[] partes = hashArmazenado.split("\\$", -1);
            int iteracoes = Integer.parseInt(partes[1]);
            if (iteracoes < 10_000 || iteracoes > 2_000_000) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[3]);
            byte[] hashInformado = derivar(senhaInformada.toCharArray(), salt, iteracoes);
            return MessageDigest.isEqual(hashEsperado, hashInformado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean ehHash(String valor) {
        return valor != null && valor.startsWith(PREFIXO);
    }

    private static byte[] derivar(char[] senha, byte[] salt, int iteracoes) {
        PBEKeySpec especificacao = new PBEKeySpec(senha, salt, iteracoes, TAMANHO_CHAVE_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(especificacao).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Não foi possível proteger a senha.", e);
        } finally {
            especificacao.clearPassword();
            java.util.Arrays.fill(senha, '\0');
        }
    }
}
