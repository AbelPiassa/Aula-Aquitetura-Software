# Aula 6: comportamento do MVC

A pasta `mvc` usa a mesma organização da referência: controllers separados, entidades e serviços dentro de `model`, telas em `resources/view` e estilos em `static/css`. As regras de produtos, pedidos e movimentações foram preservadas do projeto anterior.

## Executar

Entre em `Aula6/mvc` e execute `bash mvnw spring-boot:run`. No Windows, use `.\mvnw.cmd spring-boot:run`. A página inicial abre em http://127.0.0.1:8080 e oferece acesso a Produtos, Estoque e Pedidos.

## Sequência: registrar pedido

O vendedor envia o formulário para `PedidoController`. `PedidoService` valida os campos e usa `ProdutoService` para localizar o produto com bloqueio de escrita. A venda reduz o saldo, salva o pedido e registra o movimento por `EstoqueService`, na mesma transação. Depois, o navegador é redirecionado para a lista de pedidos.

No diagrama, alguns participantes agrupam classes de persistência para facilitar a leitura. Eles não representam classes novas.

```mermaid
sequenceDiagram
    actor Vendedor
    participant Controller as PedidoController
    participant Service as PedidoService
    participant Produtos as ProdutoService e ProdutoRepository
    participant Registros as Repositórios de pedidos e movimentos

    Note over Vendedor,Service: Cenário: campos válidos e produto encontrado e ativo
    Vendedor->>Controller: POST /pedidos
    Controller->>Service: vender(produtoId, cliente, quantidade)
    Service->>Produtos: buscarAtivoBloqueado(produtoId)
    Produtos-->>Service: Produto gerenciado pelo JPA
    alt Estoque suficiente
        Note over Service: Reduz saldo e calcula total com o preço cadastrado
        Service->>Registros: pedidos.save(pedido)
        Service->>Registros: estoque.registrar(produto, VENDA, quantidade)
        Note over Service,Registros: Confirma pedido, movimento e saldo na mesma transação
        Service-->>Controller: Conclui
        Note over Controller: Prepara mensagem de sucesso
    else Estoque insuficiente
        Service-->>Controller: IllegalArgumentException após rollback
        Note over Controller: ErrorController prepara a mensagem de erro
    end
    Controller-->>Vendedor: Redireciona para /pedidos
    Vendedor->>Controller: GET /pedidos
    Controller->>Service: listar()
    Service->>Registros: Consulta pedidos por ID decrescente
    Registros-->>Service: Lista de pedidos
    Service-->>Controller: Lista de pedidos
    Controller->>Produtos: listarAtivos()
    Produtos-->>Controller: Produtos ativos
    Controller-->>Vendedor: View sales renderizada pelo Thymeleaf
```

## Atividade: permitir ou recusar a venda

O fluxo começa após a conversão dos campos HTTP. Os serviços conferem quantidade, cliente, produto ativo e saldo. Uma recusa de negócio causa rollback e `ErrorController` prepara a mensagem para a tela de pedidos.

```mermaid
flowchart TD
    Inicio((Início)) --> Receber["Receber POST /pedidos com campos convertidos"]
    Receber --> Quantidade{"Quantidade entre 1 e 1.000.000?"}
    Quantidade -->|Sim| Cliente{"Cliente tem de 1 a 120 caracteres após strip?"}
    Quantidade -->|Não| Erro["Lançar IllegalArgumentException"]
    Cliente -->|Não| Erro
    Cliente -->|Sim| Buscar["Buscar produto com bloqueio de escrita"]
    Buscar --> Encontrado{"Produto encontrado?"}
    Encontrado -->|Não| Erro
    Encontrado -->|Sim| Ativo{"Produto ativo?"}
    Ativo -->|Não| Erro
    Ativo -->|Sim| Saldo{"Estoque cobre a quantidade?"}
    Saldo -->|Não| Erro
    Saldo -->|Sim| Baixar["Reduzir saldo do produto"]
    Baixar --> Total["Calcular total: preço cadastrado vezes quantidade"]
    Total --> Pedido["Salvar pedido com cliente, produto, quantidade e total"]
    Pedido --> Movimento["Salvar movimento do tipo VENDA"]
    Movimento --> Confirmar["Confirmar transação e persistir o novo saldo"]
    Confirmar --> Sucesso["Controller prepara mensagem de sucesso"]
    Erro --> Reverter["Reverter transação sem confirmar alterações"]
    Reverter --> Avisar["ErrorController prepara mensagem de erro"]
    Sucesso --> Redirecionar["Redirecionar para GET /pedidos e exibir a página atualizada"]
    Avisar --> Redirecionar
    Redirecionar --> Fim((Fim))
```

## Estados do produto

Os estados representam a combinação dos campos `ativo` e `estoque`. Um produto nasce ativo sem saldo, recebe entradas, pode ter o saldo consumido e só pode ser inativado quando o saldo está zerado. O histórico permanece no banco.

```mermaid
stateDiagram-v2
    direction TB
    state "Ativo sem saldo" as SemSaldo
    state "Ativo com saldo" as ComSaldo
    state "Inativo" as Inativo

    [*] --> SemSaldo: cadastrar(nome, preco)
    SemSaldo --> ComSaldo: movimentar com ENTRADA válida
    ComSaldo --> ComSaldo: entrada válida, venda parcial ou saída manual parcial
    ComSaldo --> SemSaldo: vender ou movimentar com SAIDA zerando saldo
    SemSaldo --> Inativo: remover(id)

    note right of SemSaldo
        ativo = true
        estoque = 0
        Venda e saída são recusadas
    end note

    note right of ComSaldo
        ativo = true
        estoque maior que zero
        Remoção é recusada
    end note

    note right of Inativo
        ativo = false
        Histórico permanece no banco
        Não há reativação nesta versão
    end note
```

## Conferência

| Elemento | Arquivo |
|---|---|
| Requisições de pedidos | [PedidoController.java](mvc/src/main/java/br/com/mvc/controller/PedidoController.java) |
| Venda e transação | [PedidoService.java](mvc/src/main/java/br/com/mvc/model/service/PedidoService.java) |
| Produto ativo e bloqueio | [ProdutoService.java](mvc/src/main/java/br/com/mvc/model/service/ProdutoService.java) |
| Movimentações | [EstoqueService.java](mvc/src/main/java/br/com/mvc/model/service/EstoqueService.java) |
| Estado do produto | [Produto.java](mvc/src/main/java/br/com/mvc/model/entity/Produto.java) |
| Formulário de pedido | [sales.html](mvc/src/main/resources/view/sales.html) |

Cadastre um mouse por R$ 50,00, registre entrada de 10 e venda 3. O saldo deve ser 7 e o total, R$ 150,00. Tentar vender 8 deve ser recusado sem acrescentar pedido ou movimento.

`VendasEstoqueTest` e `TelasMvcTest` verificam esse comportamento. Veja [VALIDACAO.md](../VALIDACAO.md). Pagamento, envio e cancelamento não fazem parte desta versão.

Referências: aula 6 de Arquitetura de Software, do professor Pedro Henrique Nogueira Pizzutti, e organização do repositório Gapaivams/Arq.-Software fornecida para a continuação.
