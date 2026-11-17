package br.com.faturamento.domain.ports.in;

import br.com.faturamento.domain.entities.Fatura;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface FaturarPedido {
    Fatura faturar(Long pedidoId, BigDecimal valor);
    Optional<Fatura> consultar(UUID id);
}
