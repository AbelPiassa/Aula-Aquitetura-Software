package br.com.mvc.controller;

import br.com.mvc.model.service.EstoqueService;
import br.com.mvc.model.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {
    private final ProdutoService produtos;
    private final EstoqueService estoque;

    public EstoqueController(ProdutoService produtos, EstoqueService estoque) {
        this.produtos = produtos;
        this.estoque = estoque;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", produtos.listarAtivos());
        model.addAttribute("movimentos", estoque.listarMovimentos());
        return "stock";
    }

    @PostMapping
    public String movimentar(@RequestParam Long produtoId, @RequestParam String tipo,
            @RequestParam int quantidade, RedirectAttributes flash) {
        estoque.movimentar(produtoId, tipo, quantidade);
        flash.addFlashAttribute("sucesso", "Movimentação registrada.");
        return "redirect:/estoque";
    }
}
