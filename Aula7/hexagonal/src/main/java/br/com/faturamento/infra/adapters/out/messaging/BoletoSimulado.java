package br.com.faturamento.infra.adapters.out.messaging;

import br.com.faturamento.domain.entities.Fatura;
import br.com.faturamento.domain.ports.out.messaging.EmitirBoleto;

public class BoletoSimulado implements EmitirBoleto {
    @Override
    public String emitir(Fatura fatura) {
        return "SIM-BOLETO-" + fatura.getPedidoId();
    }
}
