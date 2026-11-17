package br.com.mvc;

import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

class NavegadorTeste {
    private final int porta;
    private final String autorizacao;
    private final HttpClient client = HttpClient.newBuilder().cookieHandler(new CookieManager())
            .followRedirects(HttpClient.Redirect.ALWAYS).build();

    NavegadorTeste(int porta, String usuario, String senha) {
        this.porta = porta;
        autorizacao = "Basic " + Base64.getEncoder().encodeToString((usuario + ":" + senha).getBytes(StandardCharsets.UTF_8));
    }

    HttpResponse<String> get(String caminho) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + porta + caminho))
                .header("Authorization", autorizacao).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> post(String caminho, String formulario) throws Exception {
        String tela = "/" + caminho.split("/")[1];
        String html = get(tela).body();
        String token = null;
        var inputs = Pattern.compile("<input[^>]*>").matcher(html);
        while (inputs.find()) {
            String tag = inputs.group();
            if (tag.contains("name=\"_csrf\"")) {
                var valor = Pattern.compile("value=\"([^\"]+)\"").matcher(tag);
                if (valor.find()) token = valor.group(1);
            }
        }
        if (token == null) throw new AssertionError("Token CSRF não encontrado em " + tela);
        return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + porta + caminho))
                .header("Authorization", autorizacao)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formulario + "&_csrf="
                    + URLEncoder.encode(token, StandardCharsets.UTF_8))).build(), HttpResponse.BodyHandlers.ofString());
    }
}
