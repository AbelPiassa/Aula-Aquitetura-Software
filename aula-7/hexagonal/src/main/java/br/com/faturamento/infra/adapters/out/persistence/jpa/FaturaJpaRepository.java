package br.com.faturamento.infra.adapters.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaturaJpaRepository extends JpaRepository<FaturaRegistro, UUID> {
    Optional<FaturaRegistro> findByPedidoId(Long pedidoId);
}
