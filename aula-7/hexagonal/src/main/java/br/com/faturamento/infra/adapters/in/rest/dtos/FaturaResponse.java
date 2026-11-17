package br.com.faturamento.infra.adapters.in.rest.dtos;

import br.com.faturamento.domain.entities.Fatura;
import java.math.BigDecimal;
import java.util.UUID;

public record FaturaResponse(UUID faturaId, Long pedidoId, BigDecimal valor,
        String situacao, String codigoBarras, String chaveAcesso, String hashEmail) {
    public static FaturaResponse de(Fatura fatura) {
        return new FaturaResponse(fatura.getId(), fatura.getPedidoId(), fatura.getValor(),
                fatura.getSituacao().name(), fatura.getCodigoBarras(), fatura.getChaveAcesso(), fatura.getHashEmail());
    }
}
