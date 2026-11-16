package br.com.mvc.model.client;

import java.math.BigDecimal;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class FaturamentoClient {
    private final RestClient client;

    public FaturamentoClient(@Value("${faturamento.url}") String url,
            @Value("${faturamento.timeout-ms:2000}") int timeoutMs) {
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("O timeout de faturamento deve ser positivo.");
        }
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(timeoutMs));
        factory.setReadTimeout(Duration.ofMillis(timeoutMs));
        client = RestClient.builder().baseUrl(url).requestFactory(factory).build();
    }

    public FaturaResponse faturar(Long pedidoId, BigDecimal valor) {
        try {
            FaturaResponse resposta = client.post().uri("/faturas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new FaturaRequest(pedidoId, valor))
                    .retrieve().body(FaturaResponse.class);
            if (resposta == null || resposta.faturaId() == null
                    || !pedidoId.equals(resposta.pedidoId()) || resposta.valor() == null
                    || valor.compareTo(resposta.valor()) != 0
                    || !"CONCLUIDA".equals(resposta.situacao())) {
                throw new FaturamentoException("O faturamento devolveu uma resposta incompatível com o pedido.");
            }
            return resposta;
        } catch (RestClientException ex) {
            throw new FaturamentoException("O faturamento não respondeu corretamente. O pedido continua aguardando faturamento.", ex);
        }
    }
}
