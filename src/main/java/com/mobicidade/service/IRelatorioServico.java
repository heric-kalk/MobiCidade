package com.mobicidade.service;

import com.mobicidade.model.Corrida;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IRelatorioServico {
    // REQ15
    List<Corrida> corridasPorEstacao(Integer idEstacao, LocalDateTime de, LocalDateTime ate);

    // REQ15
    String corridasPorEstacaoCsv(Integer idEstacao, LocalDateTime de, LocalDateTime ate);

    // REQ15
    Path exportarCorridasPorEstacaoCsv(Integer idEstacao, LocalDateTime de, LocalDateTime ate, Path arquivo)
            throws IOException;

    // REQ16
    Map<String, Double> faturamentoPorTipoUsuario(LocalDateTime de, LocalDateTime ate);
}
