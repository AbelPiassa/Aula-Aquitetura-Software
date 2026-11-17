package br.com.mvc.model.service;

import br.com.mvc.model.entity.Produto;
import br.com.mvc.model.entity.StatusProduto;
import br.com.mvc.model.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {
    private final ProdutoRepository produtos;

    public ProdutoService(ProdutoRepository produtos) { this.produtos = produtos; }

    public List<Produto> listarAtivos() {
        return produtos.findByStatusNotOrderByNomeAsc(StatusProduto.DESCONTINUADO);
    }

    public List<Produto> listarTodos() { return produtos.findAll(Sort.by("nome")); }

    Produto buscarAtivoBloqueado(Long id) {
        Produto produto = produtos.buscarBloqueado(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        if (produto.getStatus() == StatusProduto.DESCONTINUADO) {
            throw new IllegalArgumentException("Produto descontinuado. O estoque não pode ser alterado.");
        }
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

    @PreAuthorize("hasRole('ADM')")
    @Transactional
    public void descontinuar(Long id) {
        Produto produto = produtos.buscarBloqueado(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        produto.descontinuar();
    }
}
