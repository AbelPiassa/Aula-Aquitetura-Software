package br.com.mvc.controller;

import br.com.mvc.model.service.ProdutoService;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoService produtos;

    public ProdutoController(ProdutoService produtos) { this.produtos = produtos; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", produtos.listarAtivos());
        return "products";
    }

    @PostMapping
    public String cadastrar(@RequestParam String nome, @RequestParam BigDecimal preco, RedirectAttributes flash) {
        produtos.cadastrar(nome, preco);
        flash.addFlashAttribute("sucesso", "Produto cadastrado. Registre uma entrada para disponibilizar estoque.");
        return "redirect:/produtos";
    }

    @PostMapping("/{id}/remover")
    public String remover(@PathVariable Long id, RedirectAttributes flash) {
        produtos.remover(id);
        flash.addFlashAttribute("sucesso", "Produto removido do catálogo ativo.");
        return "redirect:/produtos";
    }
}
