# Sua vez de arquitetar

Sistema escolhido: vendas e estoque de uma empresa que recebe pedidos por telefone ou presencialmente. O vendedor digita o pedido; o cliente não acessa o sistema.

A aplicação é um monólito MVC com telas renderizadas no servidor. H2 é o armazenamento da versão didática, executado de forma embutida no mesmo processo; aparece separado como armazenamento no C4. Não há uma API ou aplicativo independente por perfil. Os diagramas usam Mermaid com os níveis do C4.

## c1.mermaid

```mermaid
flowchart TB
 V["Vendedor — pessoa"] -->|Registra pedidos| S["Sistema de vendas e estoque"]
 A["Administrador — pessoa"] -->|Cadastra e remove produtos| S
 E["Estoquista — pessoa"] -->|Registra entradas e saídas| S
```

## c2.mermaid

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

## c2-camadas.mermaid

```mermaid
flowchart TB
 U["Vendedor, administrador e estoquista"]
 subgraph SYS["Sistema de vendas e estoque"]
  APP["Aplicação web MVC — Java, Spring MVC e Thymeleaf; contêiner"]
  DB[("H2 — contêiner de dados")]
 end
 U -->|HTTP no ambiente local| APP
 APP -->|JPA e JDBC| DB
 N["Apresentação: controllers e templates dentro da aplicação"] -.-> APP
 D["Domínio: regras de vendas e estoque dentro da aplicação"] -.-> APP
 R["Dados: repositórios dentro da aplicação e persistência no H2"] -.-> APP
 R -.-> DB
```

## Decisões e limites

- Um pedido contém um produto e uma quantidade nesta versão inicial.
- Confirmar pedido registra a saída automaticamente; não lançar outra saída manual para a mesma venda.
- Remover produto significa inativar, preservando o histórico. Produtos com saldo devem ter a saída registrada antes da remoção.
- Apresentação, domínio e dados são responsabilidades internas, não três servidores. No desenho anotado, os contêineres permanecem os mesmos.
- Perfis representam responsabilidades de negócio; a aplicação didática não implementa login ou autorização.
- Sem pagamentos, emissão fiscal, integração externa ou cancelamento de pedidos neste escopo.

Referência: [Modelo C4](https://c4model.com/diagrams).
