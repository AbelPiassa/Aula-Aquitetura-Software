package br.com.mvc.controller;

import br.com.mvc.model.service.PedidoService;
import br.com.mvc.model.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {
    private final ProdutoService produtos;
    private final PedidoService pedidos;

    public PedidoController(ProdutoService produtos, PedidoService pedidos) {
        this.produtos = produtos;
        this.pedidos = pedidos;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", produtos.listarAtivos());
        model.addAttribute("pedidos", pedidos.listar());
        return "sales";
    }

    @PostMapping
    public String vender(@RequestParam Long produtoId, @RequestParam String cliente,
            @RequestParam int quantidade, RedirectAttributes flash) {
        pedidos.vender(produtoId, cliente, quantidade);
        flash.addFlashAttribute("sucesso", "Pedido registrado e estoque atualizado.");
        return "redirect:/pedidos";
    }

}
