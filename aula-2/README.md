# Nosso primeiro C4

Sistema escolhido: vendas e estoque de uma empresa que recebe pedidos por telefone ou presencialmente. O vendedor digita o pedido; o cliente não acessa o sistema.

A aplicação é um monólito MVC com telas renderizadas no servidor. H2 é o armazenamento da versão didática, executado de forma embutida no mesmo processo; aparece separado como armazenamento no C4. Não há uma API ou aplicativo independente por perfil. Os diagramas usam Mermaid com os níveis do C4.

## c1-context.mmd

```mermaid
flowchart TB
 V["Vendedor — pessoa"] -->|Registra pedidos| S["Sistema de vendas e estoque"]
 A["Administrador — pessoa"] -->|Cadastra e remove produtos| S
 E["Estoquista — pessoa"] -->|Registra entradas e saídas| S
```

## c2-container.mmd

```mermaid
flowchart TB
 U["Vendedor, administrador e estoquista"]
 subgraph S["Sistema de vendas e estoque"]
  WEB["Aplicação web MVC — Java, Spring MVC e Thymeleaf; telas e regras"]
  DB[("Banco de dados H2 — produtos, pedidos e movimentos")]
 end
 U -->|Usa navegador; HTTP no ambiente local| WEB
 WEB -->|Lê e grava via JPA e JDBC| DB
```

## c3-component.mmd

```mermaid
flowchart TB
 U["Navegador"]
 subgraph APP["Aplicação web MVC"]
  C["VendasController — recebe ações e prepara telas"]
  T["Templates Thymeleaf — apresenta formulários e listas"]
  S["VendasService — valida pedidos e movimenta estoque"]
  R["Repositórios JPA — acesso aos dados"]
 end
 D[("H2")]
 U -->|Requisição HTTP| C
 C -->|Consulta e executa operações| S
 C -->|Fornece modelo| T
 T -->|Resposta HTML| U
 S -->|Persiste e consulta| R
 R -->|JDBC| D
```

## c4-code.mmd

```mermaid
classDiagram
 class Produto {
  Long id
  String nome
  BigDecimal preco
  int estoque
  boolean ativo
 }
 class Pedido {
  Long id
  String cliente
  int quantidade
  BigDecimal total
 }
 class Movimento {
  Long id
  String tipo
  int quantidade
 }
 class VendasService {
  +cadastrar(nome, preco)
  +remover(id)
  +movimentar(id, tipo, quantidade)
  +vender(id, cliente, quantidade)
 }
 class ProdutoRepository
 class PedidoRepository
 class MovimentoRepository
 Pedido "*" --> "1" Produto : produto
 Movimento "*" --> "1" Produto : produto
 VendasService --> ProdutoRepository : usa
 VendasService --> PedidoRepository : usa
 VendasService --> MovimentoRepository : usa
 ProdutoRepository --> Produto : persiste
 PedidoRepository --> Pedido : persiste
 MovimentoRepository --> Movimento : persiste
```

## Decisões e limites

- Um pedido contém um produto e uma quantidade nesta versão inicial.
- Confirmar pedido registra a saída automaticamente; não lançar outra saída manual para a mesma venda.
- Remover produto significa inativar, preservando o histórico. Produtos com saldo devem ter a saída registrada antes da remoção.
- Apresentação, domínio e dados são responsabilidades internas, não três servidores. No desenho anotado, os contêineres permanecem os mesmos.
- Perfis representam responsabilidades de negócio; a aplicação didática não implementa login ou autorização.
- Sem pagamentos, emissão fiscal, integração externa ou cancelamento de pedidos neste escopo.

Referência: [Modelo C4](https://c4model.com/diagrams).
