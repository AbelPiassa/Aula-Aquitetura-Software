package br.com.mvc.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
@Entity @Getter @Setter
public class Pedido {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private Produto produto;
 @Column(nullable=false,length=120) private String cliente;
 private int quantidade;
 @Column(precision=14,scale=2) private BigDecimal total;
 private LocalDateTime data=LocalDateTime.now();
}
