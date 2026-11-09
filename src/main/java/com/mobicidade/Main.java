package com.mobicidade;

import com.mobicidade.facade.IMobiCidadeFachada;
import com.mobicidade.facade.MobiCidadeFachada;
import com.mobicidade.ui.MobiCidadeApp;
import com.mobicidade.ui.Painel;
import com.mobicidade.ui.Simulacao;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Main {
    public static void main(String[] args) throws UnsupportedEncodingException {
        if (args.length > 0 && args[0].equals("--simulacao")) {
            PrintStream saida = new PrintStream(System.out, true, "UTF-8");
            System.exit(new Simulacao(saida).executar() ? 0 : 1);
            return;
        }

        if (args.length > 0 && args[0].equals("--terminal")) {
            PrintStream saida = new PrintStream(System.out, true, "UTF-8");
            IMobiCidadeFachada mobi = MobiCidadeFachada.comDadosIniciais();
            Painel.exibir(mobi, saida);
            return;
        }

        MobiCidadeApp.main(args);
    }
}
