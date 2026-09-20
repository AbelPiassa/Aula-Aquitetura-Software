package br.com.mvc.repository;
import br.com.mvc.model.Movimento;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MovimentoRepository extends JpaRepository<Movimento,Long> {  }
