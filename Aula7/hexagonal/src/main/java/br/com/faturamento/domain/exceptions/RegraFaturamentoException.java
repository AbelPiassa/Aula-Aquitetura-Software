package br.com.faturamento.domain.exceptions;

public class RegraFaturamentoException extends IllegalArgumentException {
    public RegraFaturamentoException(String mensagem) {
        super(mensagem);
    }
}
