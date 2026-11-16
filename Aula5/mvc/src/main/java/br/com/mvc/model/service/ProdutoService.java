package br.com.mvc.model.service;

import br.com.mvc.model.entity.Produto;
import br.com.mvc.model.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {
    private final ProdutoRepository produtos;

    public ProdutoService(ProdutoRepository produtos) {
        this.produtos = produtos;
    }

    public List<Produto> listarAtivos() {
        return produtos.findByAtivoTrueOrderByNomeAsc();
    }

    Produto buscarAtivoBloqueado(Long id) {
        Produto produto = produtos.buscarBloqueado(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        if (!produto.isAtivo()) throw new IllegalArgumentException("Produto inativo.");
        return produto;
    }

    @Transactional
    public void cadastrar(String nome, BigDecimal preco) {
        if (preco == null || preco.signum() <= 0
                || preco.compareTo(new BigDecimal("999999.99")) > 0
                || preco.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Preço: use valor positivo até 999999,99 e no máximo duas casas decimais.");
        }
        Produto produto = new Produto();
        produto.setNome(Validacao.texto(nome, "Nome"));
        produto.setPreco(preco);
        produtos.save(produto);
    }

    @Transactional
    public void remover(Long id) {
        Produto produto = buscarAtivoBloqueado(id);
        if (produto.getEstoque() != 0) {
            throw new IllegalArgumentException("Registre a saída do saldo antes de remover o produto.");
        }
        produto.setAtivo(false);
    }
}
