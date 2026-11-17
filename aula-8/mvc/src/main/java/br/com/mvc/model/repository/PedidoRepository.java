package br.com.mvc.model.repository;
import br.com.mvc.model.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PedidoRepository extends JpaRepository<Pedido,Long> {
 @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
 @org.springframework.data.jpa.repository.Query("select p from Pedido p where p.id = :id")
 java.util.Optional<Pedido> buscarBloqueado(@org.springframework.data.repository.query.Param("id") Long id);
}
