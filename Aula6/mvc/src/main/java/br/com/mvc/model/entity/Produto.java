package br.com.mvc.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
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
    private int estoque;
    private boolean ativo = true;
    @Version
    private Long versao;
}
