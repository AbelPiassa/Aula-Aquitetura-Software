package br.com.faturamento.infra.adapters.in.rest.dtos;

import java.math.BigDecimal;

public record FaturaRequest(Long pedidoId, BigDecimal valor) { }
