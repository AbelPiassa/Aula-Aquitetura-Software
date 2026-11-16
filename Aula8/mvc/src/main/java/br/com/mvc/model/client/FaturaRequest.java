package br.com.mvc.model.client;

import java.math.BigDecimal;

public record FaturaRequest(Long pedidoId, BigDecimal valor) { }
