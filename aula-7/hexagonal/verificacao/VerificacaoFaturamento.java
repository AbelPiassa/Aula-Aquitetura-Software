import br.com.faturamento.application.FaturamentoService;
import br.com.faturamento.domain.entities.*;
import br.com.faturamento.domain.exceptions.RegraFaturamentoException;
import br.com.faturamento.domain.ports.out.persistence.RepositorioFatura;
import br.com.faturamento.infra.adapters.out.messaging.BoletoSimulado;
import br.com.faturamento.infra.adapters.out.soap.NotaSimulada;
import br.com.faturamento.infra.adapters.out.smtp.NotificacaoSimulada;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VerificacaoFaturamento {
    private static int verificacoes;

    private static void conferir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
        verificacoes++;
    }

    private static void recusar(Runnable acao) {
        try {
            acao.run();
        } catch (RegraFaturamentoException esperada) {
            verificacoes++;
            return;
        }
        throw new AssertionError("A operação deveria ter sido recusada.");
    }

    private static class Memoria implements RepositorioFatura {
        private final Map<Long, Fatura> dados = new HashMap<>();
        public Optional<Fatura> buscarPorPedido(Long id) { return Optional.ofNullable(dados.get(id)); }
        public Optional<Fatura> buscarPorId(UUID id) {
            return dados.values().stream().filter(f -> f.getId().equals(id)).findFirst();
        }
        public Fatura salvar(Fatura fatura) { dados.put(fatura.getPedidoId(), fatura); return fatura; }
    }

    public static void main(String[] args) throws Exception {
        BigDecimal valor = new BigDecimal("150.00");
        recusar(() -> Fatura.criar(null, valor));
        recusar(() -> Fatura.criar(0L, valor));
        recusar(() -> Fatura.criar(-1L, valor));
        recusar(() -> Fatura.criar(1L, null));
        recusar(() -> Fatura.criar(1L, BigDecimal.ZERO));
        recusar(() -> Fatura.criar(1L, new BigDecimal("-1")));
        recusar(() -> Fatura.criar(1L, new BigDecimal("1.999")));
        recusar(() -> Fatura.criar(1L, new BigDecimal("1000000000000")));

        Fatura fatura = Fatura.criar(1L, valor);
        recusar(() -> fatura.registrarNota("2".repeat(44)));
        recusar(() -> fatura.registrarNotificacao("a".repeat(64)));
        recusar(() -> fatura.registrarBoleto(" "));
        conferir(fatura.getSituacao() == SituacaoFatura.CRIADA, "A recusa não deve avançar o estado.");
        fatura.registrarBoleto("0".repeat(44));
        recusar(() -> fatura.registrarBoleto("OUTRO"));
        recusar(() -> fatura.registrarNota("2".repeat(43)));
        recusar(() -> fatura.registrarNota("2".repeat(45)));
        fatura.registrarNota("2".repeat(44));
        fatura.registrarNotificacao("a".repeat(64));
        conferir(fatura.getSituacao() == SituacaoFatura.CONCLUIDA, "Fatura deve concluir o fluxo.");
        recusar(() -> fatura.registrarNotificacao("OUTRO"));
        Fatura restaurada = Fatura.reconstituir(fatura.getId(), 1L, valor,
                fatura.getCriadaEm(), "0".repeat(44), "2".repeat(44), "a".repeat(64));
        conferir(restaurada.getId().equals(fatura.getId()), "O ID deve ser preservado na leitura.");
        conferir(restaurada.getSituacao() == SituacaoFatura.CONCLUIDA, "A leitura deve remontar a situação.");

        Memoria banco = new Memoria();
        AtomicInteger boletos = new AtomicInteger();
        AtomicInteger notas = new AtomicInteger();
        AtomicInteger avisos = new AtomicInteger();
        FaturamentoService service = new FaturamentoService(banco,
                f -> { boletos.incrementAndGet(); return new BoletoSimulado().emitir(f); },
                f -> { notas.incrementAndGet(); return new NotaSimulada().emitir(f); },
                f -> { avisos.incrementAndGet(); return new NotificacaoSimulada().notificar(f); });
        Fatura primeira = service.faturar(10L, valor);
        conferir(primeira.getCodigoBarras().matches("[0-9]{44}"), "Código de barras deve ser numérico.");
        conferir(primeira.getChaveAcesso().matches("[0-9]{44}"), "Chave SOAP deve ter 44 dígitos.");
        conferir(primeira.getHashEmail().matches("[a-f0-9]{64}"), "E-mail deve gerar hash SHA-256.");
        NotificacaoSimulada smtp = new NotificacaoSimulada();
        String hash = smtp.notificar(primeira);
        String esperado = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
            .digest(smtp.getUltimaMensagem().getBytes(StandardCharsets.UTF_8)));
        conferir(hash.equals(esperado), "Hash deve corresponder ao conteúdo enviado ao SMTP simulado.");
        conferir(hash.equals(primeira.getHashEmail()), "Hash persistido deve corresponder ao mesmo e-mail.");
        Fatura repetida = service.faturar(10L, new BigDecimal("150.0"));
        conferir(primeira.getId().equals(repetida.getId()), "Repetir o pedido deve devolver a mesma fatura.");
        conferir(banco.dados.size() == 1, "Só pode haver uma fatura para o pedido.");
        conferir(boletos.get() == 1 && notas.get() == 1 && avisos.get() == 1,
                "Uma repetição não deve acionar novamente os adaptadores.");
        recusar(() -> service.faturar(10L, new BigDecimal("149.00")));
        conferir(banco.dados.size() == 1 && boletos.get() == 1,
                "O valor incompatível não deve substituir a fatura.");
        conferir(service.consultar(primeira.getId()).isPresent(), "A consulta deve encontrar o ID.");
        conferir(service.consultar(UUID.randomUUID()).isEmpty(), "ID desconhecido deve retornar vazio.");

        Memoria outroBanco = new Memoria();
        FaturamentoService falha = new FaturamentoService(outroBanco, new BoletoSimulado(),
                f -> { throw new RegraFaturamentoException("Falha simulada na nota."); },
                new NotificacaoSimulada());
        recusar(() -> falha.faturar(20L, valor));
        conferir(outroBanco.dados.isEmpty(), "Uma falha não deve salvar uma fatura concluída.");
        FaturamentoService novaTentativa = new FaturamentoService(outroBanco, new BoletoSimulado(),
                new NotaSimulada(), new NotificacaoSimulada());
        conferir(novaTentativa.faturar(20L, valor).getSituacao() == SituacaoFatura.CONCLUIDA,
                "A nova tentativa deve conseguir concluir o pedido.");
        System.out.println(verificacoes + " verificações passaram no domínio, aplicação e adaptadores simulados.");
    }
}
