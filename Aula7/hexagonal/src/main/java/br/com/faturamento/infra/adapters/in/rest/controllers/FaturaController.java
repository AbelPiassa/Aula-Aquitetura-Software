package br.com.faturamento.infra.adapters.in.rest.controllers;

import br.com.faturamento.domain.ports.in.FaturarPedido;
import br.com.faturamento.infra.adapters.in.rest.dtos.FaturaRequest;
import br.com.faturamento.infra.adapters.in.rest.dtos.FaturaResponse;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/faturas")
public class FaturaController {
    private final FaturarPedido faturamento;

    public FaturaController(FaturarPedido faturamento) {
        this.faturamento = faturamento;
    }

    @PostMapping
    public FaturaResponse faturar(@RequestBody FaturaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Informe o pedido e o valor.");
        }
        return FaturaResponse.de(faturamento.faturar(request.pedidoId(), request.valor()));
    }

    @GetMapping("/{id}")
    public FaturaResponse consultar(@PathVariable UUID id) {
        return faturamento.consultar(id).map(FaturaResponse::de)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fatura não encontrada."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> erro(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }
}
