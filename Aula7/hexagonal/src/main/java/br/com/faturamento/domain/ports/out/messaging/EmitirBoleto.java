package br.com.faturamento.domain.ports.out.messaging;

import br.com.faturamento.domain.entities.Fatura;

public interface EmitirBoleto {
    String emitir(Fatura fatura);
}
