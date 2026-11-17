package br.com.faturamento.infra.config;

import br.com.faturamento.application.FaturamentoService;
import br.com.faturamento.domain.ports.in.FaturarPedido;
import br.com.faturamento.domain.ports.out.persistence.RepositorioFatura;
import br.com.faturamento.infra.adapters.out.messaging.BoletoSimulado;
import br.com.faturamento.infra.adapters.out.soap.NotaSimulada;
import br.com.faturamento.infra.adapters.out.smtp.NotificacaoSimulada;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FaturamentoConfig {
    @Bean
    public FaturarPedido faturarPedido(RepositorioFatura faturas) {
        return new FaturamentoService(faturas, new BoletoSimulado(),
                new NotaSimulada(), new NotificacaoSimulada());
    }
}
