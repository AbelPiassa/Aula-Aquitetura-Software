package br.com.faturamento.application;

import br.com.faturamento.domain.entities.Fatura;
import br.com.faturamento.domain.exceptions.RegraFaturamentoException;
import br.com.faturamento.domain.ports.in.FaturarPedido;
import br.com.faturamento.domain.ports.out.persistence.RepositorioFatura;
import br.com.faturamento.domain.ports.out.messaging.EmitirBoleto;
import br.com.faturamento.domain.ports.out.soap.EmitirNota;
import br.com.faturamento.domain.ports.out.smtp.NotificarCliente;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class FaturamentoService implements FaturarPedido {
    private final RepositorioFatura faturas;
    private final EmitirBoleto boletos;
    private final EmitirNota notas;
    private final NotificarCliente notificacoes;

    public FaturamentoService(RepositorioFatura faturas, EmitirBoleto boletos,
            EmitirNota notas, NotificarCliente notificacoes) {
        this.faturas = faturas;
        this.boletos = boletos;
        this.notas = notas;
        this.notificacoes = notificacoes;
    }

    // Um hub local processa um pedido por vez nesta versão didática.
    @Override
    public synchronized Fatura faturar(Long pedidoId, BigDecimal valor) {
        Fatura nova = Fatura.criar(pedidoId, valor);
        Optional<Fatura> existente = faturas.buscarPorPedido(pedidoId);
        if (existente.isPresent()) {
            Fatura fatura = existente.get();
            if (fatura.getValor().compareTo(valor) != 0) {
                throw new RegraFaturamentoException("O pedido já possui uma fatura com outro valor.");
            }
            return fatura;
        }

        nova.registrarBoleto(boletos.emitir(nova));
        nova.registrarNota(notas.emitir(nova));
        nova.registrarNotificacao(notificacoes.notificar(nova));
        return faturas.salvar(nova);
    }

    @Override
    public Optional<Fatura> consultar(UUID id) {
        return faturas.buscarPorId(id);
    }
}
