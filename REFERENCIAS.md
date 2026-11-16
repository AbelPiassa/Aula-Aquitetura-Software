# Referências da continuação

## Fontes

- Base anterior do aluno: [AbelPiassa/Aula-Aquitetura-Software](https://github.com/AbelPiassa/Aula-Aquitetura-Software), consultada no commit `ef01428915695d99c3ab2fcf1592a45f21f4211e`.
- Referência de organização indicada pelo aluno: [Gapaivams/Arq.-Software](https://github.com/Gapaivams/Arq.-Software), consultada no commit `195b925ddf0681bd3c55e8d1efa5c374d11f39ce`.
- Exemplos enviados em `Arq.-Software-main.zip`, `Arq.-Software-main(1).zip` e `hexagonal.zip`.
- Slides das aulas 6, 7 e 8 de Arquitetura de Software, do professor Pedro Henrique Nogueira Pizzutti.

## Organização aplicada

| Referência | Neste projeto |
|---|---|
| Pastas `Aula2` a `Aula8` | Mesma organização por aula |
| Diagramas da aula 2 em `Exemplo` | Diagramas próprios do sistema de vendas nessa pasta |
| MVC em `Aula5/mvc`, `Aula6/mvc` e `Aula8/mvc` | Mesmos locais para os projetos |
| Entidades, repositórios e serviços em `model` | Pacotes `model/entity`, `model/repository` e `model/service` |
| Controllers e telas separados | Produtos, estoque e pedidos com suas próprias classes e views |
| HTML em `resources/view` | Mesma pasta, com prefixo Thymeleaf configurado |
| Portas e adaptadores do faturamento | `domain/entities`, `domain/exceptions`, `domain/ports/in`, `domain/ports/out` e `infra/adapters` |

## Regras preservadas

As entidades `Produto`, `Pedido` e `Movimento` mantêm o vocabulário da base do aluno. O saldo continua no produto e o histórico fica nos movimentos. A confirmação da venda calcula o total e reduz o estoque uma única vez. A remoção continua lógica e depende de saldo zero.

A referência utiliza `Product`, `Stock` e `Sale`. Aqui, a organização foi aplicada às classes anteriores sem trocar o modelo de dados por essas entidades. Os serviços foram separados por responsabilidade e os testes foram adaptados às novas classes.

Na integração, o endpoint `/faturas` é usado pelo hub e pelo cliente. O faturamento tem banco separado, reutiliza a fatura do mesmo pedido e retorna comprovantes simulados. O MVC continua responsável pela venda e pelo estoque.

Os scripts Maven Wrapper preservam os avisos de licença existentes. A configuração do wrapper foi completada com o arquivo fornecido nos materiais.
