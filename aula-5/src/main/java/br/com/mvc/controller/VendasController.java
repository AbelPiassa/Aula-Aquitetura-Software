package br.com.mvc.controller;
import br.com.mvc.repository.*;
import br.com.mvc.service.VendasService;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class VendasController {
 private final VendasService service;
 private final ProdutoRepository produtos;
 private final PedidoRepository pedidos;
 private final MovimentoRepository movimentos;
 public VendasController(VendasService service,ProdutoRepository produtos,PedidoRepository pedidos,MovimentoRepository movimentos) {
  this.service=service;this.produtos=produtos;this.pedidos=pedidos;this.movimentos=movimentos;
 }
 @GetMapping("/") public String inicio(Model model) {
  model.addAttribute("produtos",produtos.findByAtivoTrueOrderByNomeAsc());
  model.addAttribute("pedidos",pedidos.findAll(org.springframework.data.domain.Sort.by("id").descending()));
  model.addAttribute("movimentos",movimentos.findAll(org.springframework.data.domain.Sort.by("id").descending()));
  return "index";
 }
 @PostMapping("/produtos") public String cadastrar(@RequestParam String nome,@RequestParam BigDecimal preco,RedirectAttributes flash) {
  service.cadastrar(nome,preco);flash.addFlashAttribute("sucesso","Produto cadastrado. Registre uma entrada para disponibilizar estoque.");return "redirect:/";
 }
 @PostMapping("/produtos/{id}/remover") public String remover(@PathVariable Long id,RedirectAttributes flash) {
  service.remover(id);flash.addFlashAttribute("sucesso","Produto removido do catálogo ativo.");return "redirect:/";
 }
 @PostMapping("/estoque") public String estoque(@RequestParam Long produtoId,@RequestParam String tipo,@RequestParam int quantidade,RedirectAttributes flash) {
  service.movimentar(produtoId,tipo,quantidade);flash.addFlashAttribute("sucesso","Movimentação registrada.");return "redirect:/";
 }
 @PostMapping("/pedidos") public String vender(@RequestParam Long produtoId,@RequestParam String cliente,@RequestParam int quantidade,RedirectAttributes flash) {
  service.vender(produtoId,cliente,quantidade);flash.addFlashAttribute("sucesso","Pedido registrado e estoque atualizado.");return "redirect:/";
 }
 @ExceptionHandler({IllegalArgumentException.class,org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,org.springframework.web.bind.MissingServletRequestParameterException.class})
 public String erro(Exception ex,RedirectAttributes flash) {
  flash.addFlashAttribute("erro",ex instanceof IllegalArgumentException && !(ex instanceof org.springframework.web.method.annotation.MethodArgumentTypeMismatchException) ? ex.getMessage() : "Confira os campos obrigatórios e os valores numéricos.");return "redirect:/";
 }
}
