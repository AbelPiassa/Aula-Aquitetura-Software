package br.com.mvc.model.service;

import br.com.mvc.model.client.FaturaResponse;
import br.com.mvc.model.client.FaturamentoClient;
import br.com.mvc.model.entity.Pedido;
import br.com.mvc.model.entity.Produto;
import br.com.mvc.model.entity.StatusPedido;
import br.com.mvc.model.repository.PedidoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {
    private final ProdutoService produtos;
    private final EstoqueService estoque;
    private final PedidoRepository pedidos;
    private final FaturamentoClient faturamento;

    public PedidoService(ProdutoService produtos, EstoqueService estoque, PedidoRepository pedidos,
            FaturamentoClient faturamento) {
        this.produtos = produtos;
        this.estoque = estoque;
        this.pedidos = pedidos;
        this.faturamento = faturamento;
    }

    public List<Pedido> listar() { return pedidos.findAll(Sort.by("id").descending()); }

    @Transactional
    public void criar(Long produtoId, String cliente, int quantidade) {
        Validacao.quantidade(quantidade);
        String nome = Validacao.texto(cliente, "Cliente");
        Produto produto = produtos.buscarAtivoBloqueado(produtoId);
        if (produto.getEstoque() < quantidade) throw new IllegalArgumentException("Estoque insuficiente.");
        Pedido pedido = new Pedido();
        pedido.setProduto(produto);
        pedido.setCliente(nome);
        pedido.setQuantidade(quantidade);
        pedido.setTotal(produto.getPreco().multiply(BigDecimal.valueOf(quantidade)));
        pedidos.save(pedido);
    }

    @Transactional
    public void fechar(Long pedidoId) {
        Pedido pedido = pedidos.buscarBloqueado(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
        if (pedido.getStatus() == StatusPedido.FECHADO) return;
        Produto produto = produtos.buscarAtivoBloqueado(pedido.getProduto().getId());
        if (produto.getEstoque() < pedido.getQuantidade()) throw new IllegalArgumentException("Estoque insuficiente.");
        FaturaResponse resposta = faturamento.faturar(pedido.getId(), pedido.getTotal());
        produto.alterarEstoque(produto.getEstoque() - pedido.getQuantidade());
        estoque.registrar(produto, "VENDA", pedido.getQuantidade());
        pedido.fechar(resposta.faturaId().toString());
    }
}
