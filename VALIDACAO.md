# Validação conforme os enunciados

## Requisitos conferidos

| Atividade | Conferência |
|---|---|
| Aula 6 | Pasta `aula-6/mvc`, enum persistido com os três status, mudança automática com o saldo e descontinuação exclusiva de ADM |
| Diagramas da aula 6 | Sequência de entrada/saída, atividade de saída e estados em três arquivos `.mermaid` |
| Aula 7 | Fatura persistida com UUID devolvido pela REST, código de barras, chave de 44 dígitos e hash do e-mail |
| Adaptadores da aula 7 | Fila em memória, envelope/resposta SOAP XML e aceitação SMTP simulada |
| Aula 8 | Pedido `ABERTO` e fechamento REST que guarda a fatura e muda para `FECHADO` |
| Análise da aula 8 | Dois problemas e duas soluções justificadas em `aula-8/analise-integracao.md` |

## Verificações locais

As 34 verificações independentes do núcleo e dos adaptadores do faturamento passaram. Elas incluem a forma dos códigos, a chave de 44 dígitos, o hash calculado sobre a mensagem enviada, ordem das etapas, repetição de pedido e falha antes de salvar.

Os 88 fontes Java passaram pela análise de sintaxe. Esse procedimento não resolve as dependências externas e não substitui a compilação Maven. Os POMs, links locais, blocos de diagramas e scripts também são conferidos antes da publicação.

## Testes Maven

- `VendasEstoqueTest`, nas aulas 6 e 8: criação indisponível, entrada, saída até zero, bloqueio de descontinuados, preservação de saldo/histórico e recusa de usuário sem ADM no serviço.
- `TelasMvcTest`: formulários autenticados com CSRF, status e tentativa HTTP de descontinuação com um perfil de usuário não autorizado.
- `FaturaRestTest`: retorno de ID, persistência e leitura dos comprovantes, repetição do mesmo pedido e valor incompatível.
- `FaturamentoIntegracaoTest`: abertura sem baixa, fechamento com UUID e saída única, erro do hub mantendo pedido aberto, valor incompatível, produto descontinuado e fechamento que zera o saldo.

O workflow `.github/workflows/maven.yml` compila e testa os quatro projetos com JDK 21. O resultado da versão atual deve ser consultado nas verificações do pull request e na aba Actions; este documento não antecipa o resultado remoto. O ambiente local não dispõe de acesso às dependências Maven.

## Demonstração manual

1. Inicie `aula-7/hexagonal` e `aula-8/mvc` em terminais separados com `bash mvnw spring-boot:run`.
2. No navegador, use `admin/admin123`, cadastre um produto e confira o status indisponível.
3. Registre entrada e saída até zero, conferindo as mudanças de status.
4. Descontinue um produto e confirme o bloqueio de movimentações. A tentativa com `estoquista/estoque123` deve ser recusada.
5. Com outro produto disponível, abra um pedido e feche-o. O pedido deve guardar o UUID e o estoque deve baixar uma única vez.
6. Repita o cenário de hub indisponível descrito na análise da aula 8.

As contas e os protocolos externos são simulados para o laboratório. Códigos de boleto e nota não têm validade bancária ou fiscal. O hash é calculado sobre os bytes da mensagem do e-mail simulado.
