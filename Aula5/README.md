# Aula 5: nosso MVC de vendas e estoque

O projeto está em `mvc`, organizado como a referência da disciplina. Os controllers recebem HTTP, chamam os serviços e escolhem uma tela de `resources/view`.

| Operação | Controller | Serviço | View |
|---|---|---|---|
| Início | `HomeController` | Não precisa de consulta | `home` |
| Cadastro e remoção de produtos | `ProdutoController` | `ProdutoService` | `products` |
| Entradas e saídas | `EstoqueController` | `EstoqueService` | `stock` |
| Registro e consulta de pedidos | `PedidoController` | `PedidoService` | `sales` |

O cadastro valida nome e preço. O produto começa ativo com saldo zero. A remoção é lógica e depende de saldo zero. Uma venda valida o saldo, calcula o total no servidor e registra a saída automaticamente. Pedido, movimento e saldo participam da mesma transação, com bloqueio de escrita no produto.

## Executar

Com JDK 21, entre em `Aula5/mvc` e execute `bash mvnw spring-boot:run`. No Windows, use `.\mvnw.cmd spring-boot:run`. Abra http://127.0.0.1:8080.

Cadastre um mouse por R$ 50,00 em Produtos, registre entrada de 10 unidades em Estoque e confirme um pedido de 3 em Pedidos. O saldo deve ficar 7 e o total, R$ 150,00. Uma tentativa de vender 8 deve ser recusada.

Os dados ficam em `mvc/data`. As rotas de produtos, estoque e pedidos preservam as regras da versão anterior, agora em telas separadas. A aplicação é didática, local e sem autenticação.

## Testes

Execute `bash mvnw test` em `mvc`. `VendasEstoqueTest` verifica as regras de saldo e histórico. `TelasMvcTest` utiliza HTTP para conferir as páginas e os formulários. Consulte [VALIDACAO.md](../VALIDACAO.md) para os resultados.
