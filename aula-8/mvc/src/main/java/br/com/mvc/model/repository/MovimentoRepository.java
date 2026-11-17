package br.com.mvc.model.repository;

import br.com.mvc.model.entity.Movimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentoRepository extends JpaRepository<Movimento, Long> { }
