package br.com.mvc.controller;

import br.com.mvc.model.service.ProdutoService;
import jakarta.servlet.http.HttpServletRequest;
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
    public String listar(Model model, HttpServletRequest request) {
        model.addAttribute("produtos", produtos.listarTodos());
        model.addAttribute("administrador", request.isUserInRole("ADM"));
        return "products";
    }

    @PostMapping
    public String cadastrar(@RequestParam String nome, @RequestParam BigDecimal preco, RedirectAttributes flash) {
        produtos.cadastrar(nome, preco);
        flash.addFlashAttribute("sucesso", "Produto cadastrado como indisponível. Registre uma entrada no estoque.");
        return "redirect:/produtos";
    }

    @PostMapping("/{id}/descontinuar")
    public String descontinuar(@PathVariable Long id, RedirectAttributes flash) {
        produtos.descontinuar(id);
        flash.addFlashAttribute("sucesso", "Produto descontinuado. O histórico e o saldo foram preservados.");
        return "redirect:/produtos";
    }
}
