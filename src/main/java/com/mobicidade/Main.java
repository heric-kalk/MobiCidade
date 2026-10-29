package com.mobicidade;

import com.mobicidade.facade.IMobiCidadeFachada;
import com.mobicidade.facade.MobiCidadeFachada;
import com.mobicidade.ui.DadosIniciais;
import com.mobicidade.ui.Painel;
import com.mobicidade.ui.Simulacao;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Main {

    public static void main(String[] args) throws UnsupportedEncodingException {
        PrintStream saida = new PrintStream(System.out, true, "UTF-8");
        if (args.length > 0 && args[0].equals("--simulacao")) {
            System.exit(new Simulacao(saida).executar() ? 0 : 1);
        }
        IMobiCidadeFachada mobi = new MobiCidadeFachada();
        DadosIniciais.carregar(mobi);
        Painel.exibir(mobi, saida);
    }
}
