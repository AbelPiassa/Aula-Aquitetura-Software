package br.com.faturamento.infra.adapters.out.soap;

import br.com.faturamento.domain.entities.Fatura;
import br.com.faturamento.domain.ports.out.soap.EmitirNota;

public class NotaSimulada implements EmitirNota {
    @Override
    public String emitir(Fatura fatura) {
        return "SIM-NOTA-" + fatura.getPedidoId();
    }
}
