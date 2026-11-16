package br.com.faturamento.domain.ports.out.soap;

import br.com.faturamento.domain.entities.Fatura;

public interface EmitirNota {
    String emitir(Fatura fatura);
}
