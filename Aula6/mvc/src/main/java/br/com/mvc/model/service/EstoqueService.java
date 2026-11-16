package br.com.mvc.model.service;

import br.com.mvc.model.entity.Movimento;
import br.com.mvc.model.entity.Produto;
import br.com.mvc.model.repository.MovimentoRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstoqueService {
    private final ProdutoService produtos;
    private final MovimentoRepository movimentos;

    public EstoqueService(ProdutoService produtos, MovimentoRepository movimentos) {
        this.produtos = produtos;
        this.movimentos = movimentos;
    }

    public List<Movimento> listarMovimentos() {
        return movimentos.findAll(Sort.by("id").descending());
    }

    void registrar(Produto produto, String tipo, int quantidade) {
        Movimento movimento = new Movimento();
        movimento.setProduto(produto);
        movimento.setTipo(tipo);
        movimento.setQuantidade(quantidade);
        movimentos.save(movimento);
    }

    @Transactional
    public void movimentar(Long id, String tipo, int quantidade) {
        Validacao.quantidade(quantidade);
        Produto produto = produtos.buscarAtivoBloqueado(id);
        if ("ENTRADA".equals(tipo)) {
            if ((long) produto.getEstoque() + quantidade > 1000000) {
                throw new IllegalArgumentException("Saldo máximo: 1.000.000.");
            }
            produto.setEstoque(produto.getEstoque() + quantidade);
        } else if ("SAIDA".equals(tipo)) {
            if (produto.getEstoque() < quantidade) throw new IllegalArgumentException("Estoque insuficiente.");
            produto.setEstoque(produto.getEstoque() - quantidade);
        } else {
            throw new IllegalArgumentException("Tipo de movimento inválido.");
        }
        registrar(produto, tipo, quantidade);
    }
}
