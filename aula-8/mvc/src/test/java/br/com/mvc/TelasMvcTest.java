package br.com.mvc;

import br.com.mvc.model.entity.StatusProduto;
import br.com.mvc.model.repository.*;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=jdbc:h2:mem:teste-telas;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TelasMvcTest {
    @Value("${local.server.port}") int porta;
    @Autowired ProdutoRepository produtos;
    @Autowired PedidoRepository pedidos;
    @Autowired MovimentoRepository movimentos;
    private NavegadorTeste admin;

    @BeforeEach void preparar() {
        movimentos.deleteAll(); pedidos.deleteAll(); produtos.deleteAll();
        admin = new NavegadorTeste(porta, "admin", "admin123");
    }

    @Test void paginasUsamViewsSeparadas() throws Exception {
        for (String caminho : new String[]{"/", "/produtos", "/estoque", "/pedidos"}) {
            HttpResponse<String> resposta = admin.get(caminho);
            assertEquals(200, resposta.statusCode(), caminho);
            assertTrue(resposta.body().contains("/css/style.css"), caminho);
        }
    }

    @Test void formulariosValidamSaldoEExibemTotal() throws Exception {
        assertEquals(200, admin.post("/produtos", "nome=Mouse&preco=50.00").statusCode());
        assertTrue(admin.get("/produtos").body().contains(">Indisponível<"));
        Long id = produtos.findAll().get(0).getId();
        assertEquals(200, admin.post("/estoque", "produtoId=" + id + "&tipo=ENTRADA&quantidade=10").statusCode());
        HttpResponse<String> venda = admin.post("/pedidos", "produtoId=" + id + "&cliente=Cliente+exemplo&quantidade=3");
        assertEquals(200, venda.statusCode());
        assertTrue(venda.body().contains("150,00"));
        HttpResponse<String> recusa = admin.post("/pedidos", "produtoId=" + id + "&cliente=Cliente+exemplo&quantidade=11");
        assertEquals(200, recusa.statusCode());
        assertTrue(recusa.body().contains("Estoque insuficiente."));
        assertEquals(10, produtos.findById(id).orElseThrow().getEstoque());
        assertEquals(1, pedidos.count());
        assertEquals(1, movimentos.count());
    }

    @Test void perfilDoClienteNaoConsegueSePassarPorAdm() throws Exception {
        admin.post("/produtos", "nome=Mouse&preco=50.00");
        Long id = produtos.findAll().get(0).getId();
        NavegadorTeste estoquista = new NavegadorTeste(porta, "estoquista", "estoque123");
        assertFalse(estoquista.get("/produtos").body().contains(">Descontinuar<"));
        assertEquals(403, estoquista.post("/produtos/" + id + "/descontinuar", "perfil=ADM").statusCode());
        assertEquals(StatusProduto.INDISPONIVEL, produtos.findById(id).orElseThrow().getStatus());
        assertEquals(200, admin.post("/produtos/" + id + "/descontinuar", "").statusCode());
        assertEquals(StatusProduto.DESCONTINUADO, produtos.findById(id).orElseThrow().getStatus());
    }
}
