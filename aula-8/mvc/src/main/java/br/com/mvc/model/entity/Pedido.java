package br.com.mvc.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Produto produto;
    @Column(nullable = false, length = 120)
    private String cliente;
    private int quantidade;
    @Column(precision = 14, scale = 2)
    private BigDecimal total;
    private LocalDateTime data = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    @Setter(AccessLevel.NONE)
    private StatusPedido status = StatusPedido.ABERTO;
    @Column(length = 36)
    @Setter(AccessLevel.NONE)
    private String faturaId;

    public boolean isFaturado() { return status == StatusPedido.FECHADO && faturaId != null; }

    public void fechar(String idFatura) {
        if (idFatura == null || idFatura.isBlank()) throw new IllegalArgumentException("Informe o ID da fatura.");
        if (status == StatusPedido.FECHADO) throw new IllegalArgumentException("O pedido já foi fechado.");
        faturaId = idFatura;
        status = StatusPedido.FECHADO;
    }
}
