package br.com.mvc;

import br.com.mvc.model.repository.*;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(new CookieManager()).followRedirects(HttpClient.Redirect.ALWAYS).build();

    @BeforeEach void limpar() {
        movimentos.deleteAll();
        pedidos.deleteAll();
        produtos.deleteAll();
    }

    private HttpResponse<String> get(String caminho) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + porta + caminho)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String caminho, String formulario) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + porta + caminho))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formulario)).build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test void paginasUsamViewsSeparadas() throws Exception {
        for (String caminho : new String[]{"/", "/produtos", "/estoque", "/pedidos"}) {
            HttpResponse<String> resposta = get(caminho);
            assertEquals(200, resposta.statusCode(), caminho);
            assertTrue(resposta.body().contains("/css/style.css"), caminho);
        }
    }

    @Test void formularioDeVendaMantemSaldoEExibeValidacao() throws Exception {
        assertEquals(200, post("/produtos", "nome=Mouse&preco=50.00").statusCode());
        Long id = produtos.findByAtivoTrueOrderByNomeAsc().get(0).getId();
        assertEquals(200, post("/estoque", "produtoId=" + id + "&tipo=ENTRADA&quantidade=10").statusCode());
        HttpResponse<String> venda = post("/pedidos", "produtoId=" + id + "&cliente=Cliente+exemplo&quantidade=3");
        assertEquals(200, venda.statusCode());
        assertTrue(venda.body().contains("150,00"));
        HttpResponse<String> recusa = post("/pedidos", "produtoId=" + id + "&cliente=Cliente+exemplo&quantidade=8");
        assertEquals(200, recusa.statusCode());
        assertTrue(recusa.body().contains("Estoque insuficiente."));
        assertEquals(7, produtos.findById(id).orElseThrow().getEstoque());
        assertEquals(1, pedidos.count());
        assertEquals(2, movimentos.count());
    }
}
