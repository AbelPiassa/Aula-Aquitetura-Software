package br.com.mvc.repository;
import br.com.mvc.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PedidoRepository extends JpaRepository<Pedido,Long> {  }
