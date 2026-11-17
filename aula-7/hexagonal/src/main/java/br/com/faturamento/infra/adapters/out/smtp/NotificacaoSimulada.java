package br.com.faturamento.infra.adapters.out.smtp;

import br.com.faturamento.domain.entities.Fatura;
import br.com.faturamento.domain.ports.out.smtp.NotificarCliente;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class NotificacaoSimulada implements NotificarCliente {
    private String ultimaMensagem;

    @Override
    public synchronized String notificar(Fatura fatura) {
        String mensagem = "From: faturamento@exemplo.invalid\r\n"
                + "To: cliente-" + fatura.getPedidoId() + "@exemplo.invalid\r\n"
                + "Subject: Fatura do pedido " + fatura.getPedidoId() + "\r\n\r\n"
                + "Fatura: " + fatura.getId() + "\nValor: " + fatura.getValor().toPlainString()
                + "\nBoleto: " + fatura.getCodigoBarras() + "\nNota: " + fatura.getChaveAcesso();
        String resposta = servidorSmtpLocal(mensagem);
        if (!resposta.startsWith("250")) throw new IllegalStateException("O SMTP simulado recusou a mensagem.");
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(mensagem.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 indisponível.", ex);
        }
    }

    private String servidorSmtpLocal(String mensagem) {
        ultimaMensagem = mensagem;
        return "250 Mensagem aceita na simulação";
    }

    public synchronized String getUltimaMensagem() { return ultimaMensagem; }
}
