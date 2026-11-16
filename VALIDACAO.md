# Validação

Data: 16/11/2026.

## Organização

A continuação foi reorganizada conforme a referência Gapaivams/Arq.-Software: pastas `Aula2` a `Aula8`, MVC dentro de `mvc`, pacotes de entidade, repositório e serviço dentro de `model` e telas separadas em `resources/view`.

O faturamento usa entidades e exceções de domínio, portas de entrada e saída e adaptadores organizados por tecnologia. Os serviços de vendas foram separados em `ProdutoService`, `EstoqueService` e `PedidoService`, preservando validações, transações, bloqueio de produto e histórico.

## Verificações locais

- As 27 verificações de domínio, aplicação e adaptadores simulados do faturamento passaram novamente após a reorganização.
- Os fontes Java foram analisados pelo parser do compilador, sem resolução das dependências externas.
- Os arquivos POM foram lidos como XML e os links locais foram conferidos.
- Os blocos Mermaid dos READMEs foram sincronizados com os arquivos dos diagramas.
- Os scripts Bash e o diff foram conferidos.

A análise de sintaxe não substitui a compilação completa. O ambiente local não conseguiu acessar o Maven Central para resolver o parent Spring Boot 4.1.1 mantido da base. A aplicação completa e os testes Maven não foram executados localmente.

## Testes do projeto

`VendasEstoqueTest` confere saldo, total, histórico, recusa de venda sem estoque e inativação. `TelasMvcTest` verifica as páginas e os formulários de produtos, estoque e pedidos por HTTP. A aula 8 também contém `FaturamentoIntegracaoTest`, com um servidor HTTP de teste para simular sucesso, indisponibilidade e resposta incompatível do hub.

O workflow `.github/workflows/maven.yml` executa `bash mvnw -B verify` com JDK 21 nos quatro projetos e roda a verificação independente do núcleo no faturamento. O resultado remoto fica disponível na aba Actions e nas verificações do pull request; este documento não antecipa esse resultado.

## Conferência manual

1. Inicie o hub em `Aula7/hexagonal` e o MVC em `Aula8/mvc` usando `bash mvnw spring-boot:run` em dois terminais.
2. Em Produtos, cadastre um mouse por R$ 50,00.
3. Em Estoque, registre entrada de 10 unidades.
4. Em Pedidos, confirme venda de 3: total R$ 150,00 e saldo 7.
5. Tente vender 8 e confira a recusa sem novos registros.
6. Fature o pedido e confira o UUID recebido. Repetir a operação não deve reduzir o saldo novamente.
7. Confira o cenário de indisponibilidade do hub e a nova tentativa descritos no README da aula 8.

No Windows, use `.\mvnw.cmd` no lugar de `bash mvnw`. Boletos, notas e notificações são simulados. A renderização dos diagramas Mermaid ainda depende da conferência no GitHub ou em um renderizador compatível.

## GitHub

Depois da atualização de acesso mostrada pelo aluno, a operação de gravação de árvore Git funcionou. A publicação usa uma branch de trabalho e um pull request para revisar a reorganização.
