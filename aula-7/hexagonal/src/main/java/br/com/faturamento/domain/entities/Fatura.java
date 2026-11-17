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
    private SituacaoFatura situacao = SituacaoFatura.CRIADA;
    private String codigoBarras;
    private String chaveAcesso;
    private String hashEmail;

    private Fatura(UUID id, Long pedidoId, BigDecimal valor, LocalDateTime criadaEm) {
        if (pedidoId == null || pedidoId <= 0) throw new RegraFaturamentoException("Informe um pedido válido.");
        if (valor == null || valor.signum() <= 0
                || valor.compareTo(new BigDecimal("999999999999.99")) > 0
                || valor.stripTrailingZeros().scale() > 2) {
            throw new RegraFaturamentoException("Informe um valor positivo com até duas casas decimais.");
        }
        this.id = id;
        this.pedidoId = pedidoId;
        this.valor = valor;
        this.criadaEm = criadaEm;
    }

    public static Fatura criar(Long pedidoId, BigDecimal valor) {
        return new Fatura(UUID.randomUUID(), pedidoId, valor, LocalDateTime.now());
    }

    public static Fatura reconstituir(UUID id, Long pedidoId, BigDecimal valor,
            LocalDateTime criadaEm, String codigoBarras, String chaveAcesso, String hashEmail) {
        Fatura fatura = new Fatura(id, pedidoId, valor, criadaEm);
        fatura.registrarBoleto(codigoBarras);
        fatura.registrarNota(chaveAcesso);
        fatura.registrarNotificacao(hashEmail);
        return fatura;
    }

    public void registrarBoleto(String codigo) {
        if (situacao != SituacaoFatura.CRIADA) throw new RegraFaturamentoException("O boleto só pode ser registrado uma vez.");
        if (codigo == null || !codigo.matches("[0-9]{44}")) throw new RegraFaturamentoException("Código de barras inválido.");
        codigoBarras = codigo;
        situacao = SituacaoFatura.COM_BOLETO;
    }

    public void registrarNota(String chave) {
        if (situacao != SituacaoFatura.COM_BOLETO) throw new RegraFaturamentoException("Registre o boleto antes da nota.");
        if (chave == null || !chave.matches("[0-9]{44}")) throw new RegraFaturamentoException("A chave de acesso deve ter 44 dígitos.");
        chaveAcesso = chave;
        situacao = SituacaoFatura.COM_NOTA;
    }

    public void registrarNotificacao(String hash) {
        if (situacao != SituacaoFatura.COM_NOTA) throw new RegraFaturamentoException("Registre a nota antes da notificação.");
        if (hash == null || !hash.matches("[a-f0-9]{64}")) throw new RegraFaturamentoException("Hash do e-mail inválido.");
        hashEmail = hash;
        situacao = SituacaoFatura.CONCLUIDA;
    }

    public UUID getId() { return id; }
    public Long getPedidoId() { return pedidoId; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public SituacaoFatura getSituacao() { return situacao; }
    public String getCodigoBarras() { return codigoBarras; }
    public String getChaveAcesso() { return chaveAcesso; }
    public String getHashEmail() { return hashEmail; }
}
