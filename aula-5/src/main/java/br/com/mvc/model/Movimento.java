package br.com.mvc.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
@Entity @Getter @Setter
public class Movimento {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private Produto produto;
 private String tipo;
 private int quantidade;
 private LocalDateTime data=LocalDateTime.now();
}
