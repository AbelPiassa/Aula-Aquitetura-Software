package br.com.mvc.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ErrorController {
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public String erro(Exception ex, HttpServletRequest request, RedirectAttributes flash) {
        String mensagem = ex instanceof IllegalArgumentException && !(ex instanceof MethodArgumentTypeMismatchException)
                ? ex.getMessage() : "Confira os campos obrigatórios e os valores numéricos.";
        flash.addFlashAttribute("erro", mensagem);
        String caminho = request.getRequestURI();
        if (caminho.startsWith("/produtos")) return "redirect:/produtos";
        if (caminho.startsWith("/estoque")) return "redirect:/estoque";
        if (caminho.startsWith("/pedidos")) return "redirect:/pedidos";
        return "redirect:/";
    }
}
