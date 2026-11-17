package br.com.mvc.model.client;

public class FaturamentoException extends IllegalArgumentException {
    public FaturamentoException(String mensagem) {
        super(mensagem);
    }

    public FaturamentoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
