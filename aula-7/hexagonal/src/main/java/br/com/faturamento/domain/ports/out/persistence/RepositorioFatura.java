package br.com.faturamento.domain.ports.out.persistence;

import br.com.faturamento.domain.entities.Fatura;
import java.util.Optional;
import java.util.UUID;

public interface RepositorioFatura {
    Optional<Fatura> buscarPorPedido(Long pedidoId);
    Optional<Fatura> buscarPorId(UUID id);
    Fatura salvar(Fatura fatura);
}
