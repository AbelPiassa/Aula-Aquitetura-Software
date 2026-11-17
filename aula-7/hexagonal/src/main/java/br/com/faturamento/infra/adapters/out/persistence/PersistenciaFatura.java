package br.com.faturamento.infra.adapters.out.persistence;

import br.com.faturamento.domain.entities.Fatura;
import br.com.faturamento.infra.adapters.out.persistence.jpa.FaturaRegistro;
import br.com.faturamento.infra.adapters.out.persistence.jpa.FaturaJpaRepository;
import br.com.faturamento.domain.ports.out.persistence.RepositorioFatura;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PersistenciaFatura implements RepositorioFatura {
    private final FaturaJpaRepository repository;

    public PersistenciaFatura(FaturaJpaRepository repository) {
        this.repository = repository;
    }

    private Fatura paraDominio(FaturaRegistro registro) {
        return Fatura.reconstituir(registro.getId(), registro.getPedidoId(), registro.getValor(),
                registro.getCriadaEm(), registro.getCodigoBarras(), registro.getChaveAcesso(), registro.getHashEmail());
    }

    @Override
    public Optional<Fatura> buscarPorPedido(Long pedidoId) {
        return repository.findByPedidoId(pedidoId).map(this::paraDominio);
    }

    @Override
    public Optional<Fatura> buscarPorId(UUID id) {
        return repository.findById(id).map(this::paraDominio);
    }

    @Override
    public Fatura salvar(Fatura fatura) {
        FaturaRegistro registro = new FaturaRegistro();
        registro.setId(fatura.getId());
        registro.setPedidoId(fatura.getPedidoId());
        registro.setValor(fatura.getValor());
        registro.setCriadaEm(fatura.getCriadaEm());
        registro.setCodigoBarras(fatura.getCodigoBarras());
        registro.setChaveAcesso(fatura.getChaveAcesso());
        registro.setHashEmail(fatura.getHashEmail());
        return paraDominio(repository.saveAndFlush(registro));
    }
}
