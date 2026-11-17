package br.com.mvc;

import br.com.mvc.model.entity.StatusPedido;
import br.com.mvc.model.entity.StatusProduto;
import br.com.mvc.model.repository.*;
import br.com.mvc.model.service.*;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:teste-faturamento;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@WithMockUser(username = "admin", roles = "ADM")
class FaturamentoIntegracaoTest {
    private static final AtomicInteger chamadas = new AtomicInteger();
    private static final UUID faturaId = UUID.randomUUID();
    private static volatile Long pedidoEsperado;
    private static volatile int statusHttp = 200;
    private static volatile String valorResposta = "150.00";
    private static final HttpServer servidor = iniciarServidor();
    @Autowired PedidoService service;
    @Autowired ProdutoService catalogo;
    @Autowired EstoqueService estoque;
    @Autowired ProdutoRepository produtos;
    @Autowired PedidoRepository pedidos;
    @Autowired MovimentoRepository movimentos;
    private Long produtoId;

    private static HttpServer iniciarServidor() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/faturas", exchange -> {
                exchange.getRequestBody().readAllBytes();
                chamadas.incrementAndGet();
                String json = statusHttp == 200
                    ? "{\"faturaId\":\"" + faturaId + "\",\"pedidoId\":" + pedidoEsperado
                        + ",\"valor\":" + valorResposta
                        + ",\"situacao\":\"CONCLUIDA\",\"codigoBarras\":\"" + "0".repeat(44)
                        + "\",\"chaveAcesso\":\"" + "2".repeat(44)
                        + "\",\"hashEmail\":\"" + "a".repeat(64) + "\"}"
                    : "{\"erro\":\"Falha simulada\"}";
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(statusHttp, bytes.length);
                try (var output = exchange.getResponseBody()) { output.write(bytes); }
            });
            server.start();
            return server;
        } catch (IOException ex) { throw new UncheckedIOException(ex); }
    }

    @DynamicPropertySource
    static void configurarHub(DynamicPropertyRegistry registry) {
        registry.add("faturamento.url", () -> "http://127.0.0.1:" + servidor.getAddress().getPort());
    }
    @AfterAll static void encerrarServidor() { servidor.stop(0); }

    @BeforeEach void prepararPedido() {
        movimentos.deleteAll(); pedidos.deleteAll(); produtos.deleteAll();
        chamadas.set(0); statusHttp = 200; valorResposta = "150.00";
        catalogo.cadastrar("Mouse", new BigDecimal("50.00"));
        produtoId = produtos.findAll().get(0).getId();
        estoque.movimentar(produtoId, "ENTRADA", 10);
        service.criar(produtoId, "Cliente exemplo", 3);
        pedidoEsperado = pedidos.findAll().get(0).getId();
    }

    @Test void abrirNaoReservaEstoqueEFecharDuasVezesNaoDuplicaSaida() {
        assertEquals(StatusPedido.ABERTO, pedidos.findById(pedidoEsperado).orElseThrow().getStatus());
        assertEquals(10, produtos.findById(produtoId).orElseThrow().getEstoque());
        assertEquals(0, chamadas.get());
        service.fechar(pedidoEsperado);
        service.fechar(pedidoEsperado);
        var pedido = pedidos.findById(pedidoEsperado).orElseThrow();
        assertEquals(StatusPedido.FECHADO, pedido.getStatus());
        assertEquals(faturaId.toString(), pedido.getFaturaId());
        assertEquals(1, chamadas.get());
        assertEquals(7, produtos.findById(produtoId).orElseThrow().getEstoque());
        assertEquals(2, movimentos.count());
    }

    @Test void falhaDoHubMantemPedidoAbertoESaldoIntacto() {
        statusHttp = 503;
        assertThrows(IllegalArgumentException.class, () -> service.fechar(pedidoEsperado));
        var pedido = pedidos.findById(pedidoEsperado).orElseThrow();
        assertEquals(StatusPedido.ABERTO, pedido.getStatus());
        assertNull(pedido.getFaturaId());
        assertEquals(10, produtos.findById(produtoId).orElseThrow().getEstoque());
        assertEquals(1, movimentos.count());
        statusHttp = 200;
        service.fechar(pedidoEsperado);
        assertEquals(StatusPedido.FECHADO, pedidos.findById(pedidoEsperado).orElseThrow().getStatus());
        assertEquals(7, produtos.findById(produtoId).orElseThrow().getEstoque());
    }

    @Test void respostaComOutroValorNaoFechaPedido() {
        valorResposta = "149.99";
        assertThrows(IllegalArgumentException.class, () -> service.fechar(pedidoEsperado));
        assertEquals(StatusPedido.ABERTO, pedidos.findById(pedidoEsperado).orElseThrow().getStatus());
        assertEquals(10, produtos.findById(produtoId).orElseThrow().getEstoque());
        assertEquals(1, movimentos.count());
    }

    @Test void descontinuarProdutoBloqueiaFechamentoAntesDeChamarHub() {
        catalogo.descontinuar(produtoId);
        assertThrows(IllegalArgumentException.class, () -> service.fechar(pedidoEsperado));
        assertEquals(0, chamadas.get());
        assertEquals(StatusPedido.ABERTO, pedidos.findById(pedidoEsperado).orElseThrow().getStatus());
        assertEquals(10, produtos.findById(produtoId).orElseThrow().getEstoque());
    }

    @Test void fecharComUltimasUnidadesTornaProdutoIndisponivel() {
        estoque.movimentar(produtoId, "SAIDA", 7);
        service.fechar(pedidoEsperado);
        assertEquals(0, produtos.findById(produtoId).orElseThrow().getEstoque());
        assertEquals(StatusProduto.INDISPONIVEL, produtos.findById(produtoId).orElseThrow().getStatus());
        assertEquals(3, movimentos.count());
    }
}
