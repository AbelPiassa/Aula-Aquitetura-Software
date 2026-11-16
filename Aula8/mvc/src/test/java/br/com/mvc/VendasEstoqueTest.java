package br.com.mvc;
import br.com.mvc.model.service.*;
import br.com.mvc.model.repository.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:teste;DB_CLOSE_DELAY=-1", "spring.jpa.hibernate.ddl-auto=create-drop"})
class VendasEstoqueTest {
 @Autowired PedidoService service;
 @Autowired ProdutoService catalogo;
 @Autowired EstoqueService estoque;
 @Autowired ProdutoRepository produtos;
 @Autowired PedidoRepository pedidos;
 @Autowired MovimentoRepository movimentos;
 @Test void fluxoDeVendaPreservaSaldoEHistorico() {
  catalogo.cadastrar("Mouse",new BigDecimal("50.00"));
  Long id=produtos.findByAtivoTrueOrderByNomeAsc().get(0).getId();
  estoque.movimentar(id,"ENTRADA",10);
  service.vender(id,"Cliente exemplo",3);
  assertEquals(7,produtos.findById(id).orElseThrow().getEstoque());
  assertEquals(0,new BigDecimal("150.00").compareTo(pedidos.findAll().get(0).getTotal()));
  assertEquals(2,movimentos.count());
  assertThrows(IllegalArgumentException.class,()->service.vender(id,"Cliente",8));
  assertEquals(7,produtos.findById(id).orElseThrow().getEstoque());
  assertEquals(1,pedidos.count());
  assertEquals(2,movimentos.count());
  assertThrows(IllegalArgumentException.class,()->catalogo.remover(id));
  assertThrows(IllegalArgumentException.class,()->estoque.movimentar(id,"ENTRADA",-1));
  estoque.movimentar(id,"SAIDA",7);
  catalogo.remover(id);
  assertFalse(produtos.findById(id).orElseThrow().isAtivo());
  assertEquals(1,pedidos.count());
  assertEquals(3,movimentos.count());
  assertThrows(IllegalArgumentException.class,()->service.vender(id,"Cliente",1));
 }
}
