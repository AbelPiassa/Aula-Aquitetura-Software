package br.com.mvc.repository;
import br.com.mvc.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProdutoRepository extends JpaRepository<Produto,Long> { 
 @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
 @org.springframework.data.jpa.repository.Query("select p from Produto p where p.id = :id")
 java.util.Optional<Produto> buscarBloqueado(@org.springframework.data.repository.query.Param("id") Long id);
 java.util.List<Produto> findByAtivoTrueOrderByNomeAsc();
 }
