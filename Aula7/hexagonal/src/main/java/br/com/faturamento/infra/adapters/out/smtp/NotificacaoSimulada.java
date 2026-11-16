package br.com.faturamento.infra.adapters.out.smtp;

import br.com.faturamento.domain.entities.Fatura;
import br.com.faturamento.domain.ports.out.smtp.NotificarCliente;

public class NotificacaoSimulada implements NotificarCliente {
    @Override
    public String notificar(Fatura fatura) {
        return "SIM-AVISO-" + fatura.getPedidoId();
    }
}
