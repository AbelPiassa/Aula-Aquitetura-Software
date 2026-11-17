package br.com.faturamento;

import br.com.faturamento.infra.adapters.out.persistence.jpa.FaturaJpaRepository;
import java.net.URI;
import java.net.http.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=jdbc:h2:mem:teste-faturas;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FaturaRestTest {
    @Value("${local.server.port}") int porta;
    @Autowired FaturaJpaRepository faturas;
    private final HttpClient client = HttpClient.newHttpClient();
    @BeforeEach void limpar() { faturas.deleteAll(); }

    private HttpResponse<String> faturar(long pedidoId, String valor) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + porta + "/faturas"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"pedidoId\":" + pedidoId + ",\"valor\":" + valor + "}"))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    private String texto(String json, String campo) {
        var m = Pattern.compile("\"" + campo + "\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        assertTrue(m.find(), campo + " ausente no JSON");
        return m.group(1);
    }

    @Test void restDevolveIdEComprovantesPersistidos() throws Exception {
        var resposta = faturar(11, "150.00");
        assertEquals(200, resposta.statusCode());
        String id = texto(resposta.body(), "faturaId");
        var registro = faturas.findByPedidoId(11L).orElseThrow();
        assertEquals(id, registro.getId().toString());
        assertTrue(registro.getCodigoBarras().matches("[0-9]{44}"));
        assertTrue(registro.getChaveAcesso().matches("[0-9]{44}"));
        assertTrue(registro.getHashEmail().matches("[a-f0-9]{64}"));
        var consulta = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + porta + "/faturas/" + id)).GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, consulta.statusCode());
        assertEquals(registro.getChaveAcesso(), texto(consulta.body(), "chaveAcesso"));
        assertEquals(registro.getHashEmail(), texto(consulta.body(), "hashEmail"));
    }

    @Test void pedidoRepetidoReutilizaFaturaEValorDiferenteERecusado() throws Exception {
        String id = texto(faturar(12, "150.00").body(), "faturaId");
        var repetida = faturar(12, "150.0");
        assertEquals(200, repetida.statusCode());
        assertEquals(id, texto(repetida.body(), "faturaId"));
        assertEquals(400, faturar(12, "149.00").statusCode());
        assertEquals(1, faturas.count());
    }
}
