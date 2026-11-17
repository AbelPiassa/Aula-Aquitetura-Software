package br.com.faturamento.infra.adapters.out.soap;

import br.com.faturamento.domain.entities.Fatura;
import br.com.faturamento.domain.ports.out.soap.EmitirNota;
import java.io.StringReader;
import java.util.Locale;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class NotaSimulada implements EmitirNota {
    private static final String INICIO = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>";
    private static final String FIM = "</soap:Body></soap:Envelope>";

    @Override
    public String emitir(Fatura fatura) {
        String requisicao = INICIO + "<emitirNota><pedidoId>" + fatura.getPedidoId()
                + "</pedidoId><valor>" + fatura.getValor().toPlainString() + "</valor></emitirNota>" + FIM;
        String resposta = servicoSoapLocal(requisicao);
        return lerXml(resposta).getElementsByTagName("chaveAcesso").item(0).getTextContent();
    }

    private String servicoSoapLocal(String requisicao) {
        Document documento = lerXml(requisicao);
        long pedidoId = Long.parseLong(documento.getElementsByTagName("pedidoId").item(0).getTextContent());
        String chave = "2" + String.format(Locale.ROOT, "%043d", pedidoId);
        return INICIO + "<notaEmitida><chaveAcesso>" + chave + "</chaveAcesso></notaEmitida>" + FIM;
    }

    private Document lerXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        } catch (Exception ex) {
            throw new IllegalStateException("Falha na resposta SOAP simulada.", ex);
        }
    }
}
