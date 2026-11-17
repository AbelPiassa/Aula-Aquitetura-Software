package br.com.faturamento.domain.ports.out.smtp;

import br.com.faturamento.domain.entities.Fatura;

public interface NotificarCliente {
    String notificar(Fatura fatura);
}
