package br.com.mvc.service;
import br.com.mvc.model.*;
import br.com.mvc.repository.*;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class VendasService {
 private final ProdutoRepository produtos;
 private final PedidoRepository pedidos;
 private final MovimentoRepository movimentos;
 public VendasService(ProdutoRepository produtos, PedidoRepository pedidos, MovimentoRepository movimentos) {
  this.produtos=produtos; this.pedidos=pedidos; this.movimentos=movimentos;
 }
 private String texto(String valor, String campo) {
  if(valor==null || valor.isBlank() || valor.strip().length()>120) throw new IllegalArgumentException(campo+": informe de 1 a 120 caracteres.");
  return valor.strip();
 }
 private Produto produto(Long id) {
  Produto p=produtos.buscarBloqueado(id).orElseThrow(()->new IllegalArgumentException("Produto não encontrado."));
  if(!p.isAtivo()) throw new IllegalArgumentException("Produto inativo.");
  return p;
 }
 private void quantidade(int n) {
  if(n<1 || n>1000000) throw new IllegalArgumentException("Quantidade deve estar entre 1 e 1.000.000.");
 }
 @Transactional
 public void cadastrar(String nome, BigDecimal preco) {
  if(preco==null || preco.signum()<=0 || preco.compareTo(new BigDecimal("999999.99"))>0 || preco.stripTrailingZeros().scale()>2)
   throw new IllegalArgumentException("Preço: use valor positivo até 999999,99 e no máximo duas casas decimais.");
  Produto p=new Produto();p.setNome(texto(nome,"Nome"));p.setPreco(preco);produtos.save(p);
 }
 @Transactional
 public void remover(Long id) {
  Produto p=produto(id);
  if(p.getEstoque()!=0) throw new IllegalArgumentException("Registre a saída do saldo antes de remover o produto.");
  p.setAtivo(false);
 }
 private void registrar(Produto p,String tipo,int n) {
  Movimento m=new Movimento();m.setProduto(p);m.setTipo(tipo);m.setQuantidade(n);movimentos.save(m);
 }
 @Transactional
 public void movimentar(Long id,String tipo,int n) {
  quantidade(n);Produto p=produto(id);
  if("ENTRADA".equals(tipo)) {
   if((long)p.getEstoque()+n>1000000) throw new IllegalArgumentException("Saldo máximo: 1.000.000.");
   p.setEstoque(p.getEstoque()+n);
  } else if("SAIDA".equals(tipo)) {
   if(p.getEstoque()<n) throw new IllegalArgumentException("Estoque insuficiente.");
   p.setEstoque(p.getEstoque()-n);
  } else throw new IllegalArgumentException("Tipo de movimento inválido.");
  registrar(p,tipo,n);
 }
 @Transactional
 public void vender(Long id,String cliente,int n) {
  quantidade(n);String nome=texto(cliente,"Cliente");Produto p=produto(id);
  if(p.getEstoque()<n) throw new IllegalArgumentException("Estoque insuficiente.");
  p.setEstoque(p.getEstoque()-n);
  Pedido pedido=new Pedido();pedido.setProduto(p);pedido.setCliente(nome);pedido.setQuantidade(n);
  pedido.setTotal(p.getPreco().multiply(BigDecimal.valueOf(n)));pedidos.save(pedido);
  registrar(p,"VENDA",n);
 }
}
