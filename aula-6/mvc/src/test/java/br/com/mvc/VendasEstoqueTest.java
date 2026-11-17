package br.com.mvc;

import br.com.mvc.model.entity.StatusProduto;
import br.com.mvc.model.repository.*;
import br.com.mvc.model.service.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:teste-estoque;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"})
@WithMockUser(username = "admin", roles = "ADM")
class VendasEstoqueTest {
    @Autowired PedidoService pedidosService;
    @Autowired ProdutoService catalogo;
    @Autowired EstoqueService estoque;
    @Autowired ProdutoRepository produtos;
    @Autowired PedidoRepository pedidos;
    @Autowired MovimentoRepository movimentos;
    private Long produtoId;

    @BeforeEach void preparar() {
        movimentos.deleteAll(); pedidos.deleteAll(); produtos.deleteAll();
        catalogo.cadastrar("Mouse", new BigDecimal("50.00"));
        produtoId = produtos.findAll().get(0).getId();
    }

    private StatusProduto status() { return produtos.findById(produtoId).orElseThrow().getStatus(); }

    @Test void saldoControlaDisponibilidadeDoProduto() {
        assertEquals(StatusProduto.INDISPONIVEL, status());
        estoque.movimentar(produtoId, "ENTRADA", 10);
        assertEquals(StatusProduto.DISPONIVEL, status());
        estoque.movimentar(produtoId, "SAIDA", 10);
        assertEquals(StatusProduto.INDISPONIVEL, status());
        estoque.movimentar(produtoId, "ENTRADA", 3);
        assertEquals(StatusProduto.DISPONIVEL, status());
    }

    @Test void descontinuadoMantemHistoricoESaldoMasBloqueiaMovimentacoes() {
        estoque.movimentar(produtoId, "ENTRADA", 5);
        catalogo.descontinuar(produtoId);
        assertEquals(StatusProduto.DESCONTINUADO, status());
        assertThrows(IllegalArgumentException.class, () -> estoque.movimentar(produtoId, "ENTRADA", 1));
        assertThrows(IllegalArgumentException.class, () -> estoque.movimentar(produtoId, "SAIDA", 1));
        assertThrows(IllegalArgumentException.class, () -> pedidosService.vender(produtoId, "Cliente", 1));
        assertEquals(5, produtos.findById(produtoId).orElseThrow().getEstoque());
        assertEquals(1, movimentos.count());
        assertEquals(0, pedidos.count());
    }

    @Test
    @WithMockUser(username = "estoquista", roles = "ESTOQUISTA")
    void somenteAdmPodeDescontinuarMesmoChamandoOServico() {
        assertThrows(AccessDeniedException.class, () -> catalogo.descontinuar(produtoId));
        assertEquals(StatusProduto.INDISPONIVEL, status());
    }

    @Test void saidaSemSaldoOuQuantidadeInvalidaNaoGravaMovimento() {
        assertThrows(IllegalArgumentException.class, () -> estoque.movimentar(produtoId, "SAIDA", 1));
        assertThrows(IllegalArgumentException.class, () -> estoque.movimentar(produtoId, "ENTRADA", 0));
        assertThrows(IllegalArgumentException.class, () -> estoque.movimentar(produtoId, "ENTRADA", -1));
        estoque.movimentar(produtoId, "ENTRADA", 2);
        assertThrows(IllegalArgumentException.class, () -> estoque.movimentar(produtoId, "SAIDA", 3));
        assertEquals(2, produtos.findById(produtoId).orElseThrow().getEstoque());
        assertEquals(1, movimentos.count());
    }
}
