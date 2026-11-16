package br.com.mvc;

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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:teste-faturamento;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FaturamentoIntegracaoTest {
    private static final AtomicInteger chamadas = new AtomicInteger();
    private static final UUID faturaId = UUID.randomUUID();
    private static volatile Long pedidoEsperado;
    private static volatile int status = 200;
    private static volatile String valorResposta = "150.00";
    private static final HttpServer servidor = iniciarServidor();

    @Autowired PedidoService service;
 @Autowired ProdutoService catalogo;
 @Autowired EstoqueService estoque;
    @Autowired ProdutoRepository produtos;
    @Autowired PedidoRepository pedidos;
    @Autowired MovimentoRepository movimentos;

    private static HttpServer iniciarServidor() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/faturas", exchange -> {
                exchange.getRequestBody().readAllBytes();
                chamadas.incrementAndGet();
                String json = status == 200
                    ? "{\"faturaId\":\"" + faturaId + "\",\"pedidoId\":" + pedidoEsperado
                        + ",\"valor\":" + valorResposta
                        + ",\"situacao\":\"CONCLUIDA\",\"boleto\":\"SIM-BOLETO\","
                        + "\"nota\":\"SIM-NOTA\",\"notificacao\":\"SIM-AVISO\"}"
                    : "{\"erro\":\"Falha simulada\"}";
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(status, bytes.length);
                try (var output = exchange.getResponseBody()) { output.write(bytes); }
            });
            server.start();
            return server;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @DynamicPropertySource
    static void configurarHub(DynamicPropertyRegistry registry) {
        registry.add("faturamento.url", () -> "http://127.0.0.1:" + servidor.getAddress().getPort());
    }

    @AfterAll static void encerrarServidor() { servidor.stop(0); }

    @BeforeEach void prepararPedido() {
        movimentos.deleteAll(); pedidos.deleteAll(); produtos.deleteAll();
        chamadas.set(0); status = 200; valorResposta = "150.00";
        catalogo.cadastrar("Mouse", new BigDecimal("50.00"));
        Long produtoId = produtos.findAll().get(0).getId();
        estoque.movimentar(produtoId, "ENTRADA", 10);
        service.vender(produtoId, "Cliente exemplo", 3);
        pedidoEsperado = pedidos.findAll().get(0).getId();
    }

    @Test void repetirFaturamentoNaoRepeteChamadaNemBaixaEstoque() {
        service.faturar(pedidoEsperado);
        service.faturar(pedidoEsperado);
        assertEquals(faturaId.toString(), pedidos.findById(pedidoEsperado).orElseThrow().getFaturaId());
        assertEquals(1, chamadas.get());
        assertEquals(7, produtos.findAll().get(0).getEstoque());
        assertEquals(2, movimentos.count());
    }

    @Test void falhaDoHubPermiteNovaTentativaSemAlterarAVenda() {
        status = 503;
        assertThrows(IllegalArgumentException.class, () -> service.faturar(pedidoEsperado));
        assertFalse(pedidos.findById(pedidoEsperado).orElseThrow().isFaturado());
        assertEquals(7, produtos.findAll().get(0).getEstoque());
        assertEquals(2, movimentos.count());
        status = 200;
        service.faturar(pedidoEsperado);
        assertTrue(pedidos.findById(pedidoEsperado).orElseThrow().isFaturado());
    }

    @Test void respostaComOutroValorNaoFechaFaturamento() {
        valorResposta = "149.99";
        assertThrows(IllegalArgumentException.class, () -> service.faturar(pedidoEsperado));
        assertFalse(pedidos.findById(pedidoEsperado).orElseThrow().isFaturado());
        assertEquals(1, pedidos.count());
    }
}
