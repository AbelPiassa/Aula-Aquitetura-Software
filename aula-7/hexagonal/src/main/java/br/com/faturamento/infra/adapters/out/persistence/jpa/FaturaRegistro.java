package br.com.faturamento.infra.adapters.out.persistence.jpa;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "faturas")
@Getter
@Setter
public class FaturaRegistro {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true)
    private Long pedidoId;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;
    @Column(nullable = false)
    private LocalDateTime criadaEm;
    @Column(nullable = false, length = 44)
    private String codigoBarras;
    @Column(nullable = false, length = 44)
    private String chaveAcesso;
    @Column(nullable = false, length = 64)
    private String hashEmail;
}
