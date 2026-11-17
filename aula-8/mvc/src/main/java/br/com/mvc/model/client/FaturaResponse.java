package br.com.mvc.model.client;

import java.math.BigDecimal;
import java.util.UUID;

public record FaturaResponse(UUID faturaId, Long pedidoId, BigDecimal valor,
        String situacao, String codigoBarras, String chaveAcesso, String hashEmail) { }
