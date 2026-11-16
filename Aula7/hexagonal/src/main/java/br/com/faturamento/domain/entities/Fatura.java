package br.com.faturamento.domain.entities;

import br.com.faturamento.domain.exceptions.RegraFaturamentoException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Fatura {
    private final UUID id;
    private final Long pedidoId;
    private final BigDecimal valor;
    private final LocalDateTime criadaEm;
    private SituacaoFatura situacao;
    private String boleto;
    private String nota;
    private String notificacao;

    private Fatura(UUID id, Long pedidoId, BigDecimal valor, LocalDateTime criadaEm) {
        if (pedidoId == null || pedidoId <= 0) {
            throw new RegraFaturamentoException("Informe um pedido válido.");
        }
        if (valor == null || valor.signum() <= 0
                || valor.compareTo(new BigDecimal("999999999999.99")) > 0
                || valor.stripTrailingZeros().scale() > 2) {
            throw new RegraFaturamentoException("Informe um valor positivo com até duas casas decimais.");
        }
        this.id = id;
        this.pedidoId = pedidoId;
        this.valor = valor;
        this.criadaEm = criadaEm;
        this.situacao = SituacaoFatura.CRIADA;
    }

    public static Fatura criar(Long pedidoId, BigDecimal valor) {
        return new Fatura(UUID.randomUUID(), pedidoId, valor, LocalDateTime.now());
    }

    public static Fatura reconstituir(UUID id, Long pedidoId, BigDecimal valor,
            LocalDateTime criadaEm, String boleto, String nota, String notificacao) {
        Fatura fatura = new Fatura(id, pedidoId, valor, criadaEm);
        fatura.registrarBoleto(boleto);
        fatura.registrarNota(nota);
        fatura.registrarNotificacao(notificacao);
        return fatura;
    }

    private String comprovante(String valor, String campo) {
        if (valor == null || valor.isBlank() || valor.length() > 120) {
            throw new RegraFaturamentoException(campo + " inválido.");
        }
        return valor.strip();
    }

    public void registrarBoleto(String codigo) {
        if (situacao != SituacaoFatura.CRIADA) {
            throw new RegraFaturamentoException("O boleto só pode ser registrado uma vez.");
        }
        boleto = comprovante(codigo, "Comprovante do boleto");
        situacao = SituacaoFatura.COM_BOLETO;
    }

    public void registrarNota(String codigo) {
        if (situacao != SituacaoFatura.COM_BOLETO) {
            throw new RegraFaturamentoException("Registre o boleto antes da nota.");
        }
        nota = comprovante(codigo, "Comprovante da nota");
        situacao = SituacaoFatura.COM_NOTA;
    }

    public void registrarNotificacao(String codigo) {
        if (situacao != SituacaoFatura.COM_NOTA) {
            throw new RegraFaturamentoException("Registre a nota antes da notificação.");
        }
        notificacao = comprovante(codigo, "Comprovante da notificação");
        situacao = SituacaoFatura.CONCLUIDA;
    }

    public UUID getId() { return id; }
    public Long getPedidoId() { return pedidoId; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public SituacaoFatura getSituacao() { return situacao; }
    public String getBoleto() { return boleto; }
    public String getNota() { return nota; }
    public String getNotificacao() { return notificacao; }
}
