package br.com.mvc.model.service;

import br.com.mvc.model.entity.Pedido;
import br.com.mvc.model.entity.Produto;
import br.com.mvc.model.repository.PedidoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.mvc.model.client.FaturaResponse;
import br.com.mvc.model.client.FaturamentoClient;

@Service
public class PedidoService {
    private final ProdutoService produtos;
    private final EstoqueService estoque;
    private final PedidoRepository pedidos;
    private final FaturamentoClient faturamento;

    public PedidoService(ProdutoService produtos, EstoqueService estoque, PedidoRepository pedidos, FaturamentoClient faturamento) {
        this.produtos = produtos;
        this.estoque = estoque;
        this.pedidos = pedidos;
        this.faturamento = faturamento;
    }

    public List<Pedido> listar() {
        return pedidos.findAll(Sort.by("id").descending());
    }

    @Transactional
    public void vender(Long produtoId, String cliente, int quantidade) {
        Validacao.quantidade(quantidade);
        String nome = Validacao.texto(cliente, "Cliente");
        Produto produto = produtos.buscarAtivoBloqueado(produtoId);
        if (produto.getEstoque() < quantidade) throw new IllegalArgumentException("Estoque insuficiente.");
        produto.setEstoque(produto.getEstoque() - quantidade);
        Pedido pedido = new Pedido();
        pedido.setProduto(produto);
        pedido.setCliente(nome);
        pedido.setQuantidade(quantidade);
        pedido.setTotal(produto.getPreco().multiply(BigDecimal.valueOf(quantidade)));
        pedidos.save(pedido);
        estoque.registrar(produto, "VENDA", quantidade);
    }

    @Transactional
    public void faturar(Long pedidoId) {
        Pedido pedido = pedidos.buscarBloqueado(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
        if (pedido.isFaturado()) return;
        FaturaResponse resposta = faturamento.faturar(pedido.getId(), pedido.getTotal());
        pedido.setFaturaId(resposta.faturaId().toString());
    }

}
