package br.com.faturamento.infra.adapters.out.messaging;

import br.com.faturamento.domain.entities.Fatura;
import br.com.faturamento.domain.ports.out.messaging.EmitirBoleto;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BoletoSimulado implements EmitirBoleto {
    private record Solicitacao(UUID faturaId, Long pedidoId, BigDecimal valor) { }
    private final BlockingQueue<Solicitacao> fila = new LinkedBlockingQueue<>();

    @Override
    public synchronized String emitir(Fatura fatura) {
        fila.add(new Solicitacao(fatura.getId(), fatura.getPedidoId(), fatura.getValor()));
        // O consumidor local processa a mensagem durante a chamada do laboratório.
        Solicitacao recebida = fila.remove();
        return String.format(Locale.ROOT, "%044d", recebida.pedidoId());
    }
}
