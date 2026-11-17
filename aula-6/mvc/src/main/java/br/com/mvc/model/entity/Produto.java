package br.com.mvc.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String nome;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;
    @Setter(AccessLevel.NONE)
    private int estoque;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Setter(AccessLevel.NONE)
    private StatusProduto status = StatusProduto.INDISPONIVEL;
    @Version
    private Long versao;

    public boolean isAtivo() { return status != StatusProduto.DESCONTINUADO; }

    public void alterarEstoque(int saldo) {
        if (status == StatusProduto.DESCONTINUADO) {
            throw new IllegalArgumentException("Produto descontinuado. O estoque não pode ser alterado.");
        }
        if (saldo < 0 || saldo > 1000000) {
            throw new IllegalArgumentException("O saldo deve estar entre zero e 1.000.000.");
        }
        estoque = saldo;
        status = saldo == 0 ? StatusProduto.INDISPONIVEL : StatusProduto.DISPONIVEL;
    }

    public void descontinuar() { status = StatusProduto.DESCONTINUADO; }
}
