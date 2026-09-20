# Nosso primeiro MVC — Vendas e estoque

Aplicação didática criada para exercitar Spring MVC. É uma implementação proposta a partir do enunciado, não uma reprodução de código não fornecido do professor.

## Requisitos e execução no Windows

1. Instale o JDK 21 e confira `java -version`. O Java deve estar no PATH; se definir JAVA_HOME, use a pasta do JDK.
2. Abra o terminal dentro de `aula-5`.
3. No PowerShell, execute `./mvnw.cmd spring-boot:run` (ou `mvnw.cmd spring-boot:run` no CMD).
4. Abra http://localhost:8080 no navegador.
5. Para encerrar, pressione Ctrl+C.

Linux/macOS: `chmod +x mvnw` e `./mvnw spring-boot:run`.
A primeira execução precisa de internet para baixar Maven e dependências. Não é necessário instalar Maven separadamente. O projeto mantém Java 21 e Spring Boot 4.1.1 do pom.xml enviado, com Lombok para gerar getters e setters durante a compilação.

## Funcionalidades

- Cadastro e listagem de produtos ativos.
- Remoção lógica, permitida somente com saldo zero.
- Entradas e saídas manuais com quantidades positivas.
- Pedido de um produto com quantidade e cliente, total calculado no servidor.
- Baixa automática e histórico de movimentações.
- Rejeição de vendas sem saldo; bloqueio pessimista e transação para evitar concorrência sobre o mesmo saldo.

## Onde está o MVC?

| Parte | Arquivos | Responsabilidade |
|---|---|---|
| Model | `model`, `service`, `repository` | Dados, regras de negócio e persistência |
| View | `templates/index.html`, `static/style.css` | Formulários e exibição |
| Controller | `controller/VendasController.java` | Recebe HTTP, chama serviço e escolhe a tela |

Fluxo: formulário → controller → serviço → repositório → banco. Após uma alteração, há redirecionamento para a listagem, evitando repetição do POST ao atualizar a página.

## Roteiro para demonstrar

1. Cadastre Mouse por R$ 50,00. Saldo inicial: zero.
2. Registre entrada de 10 unidades.
3. Registre pedido de 3 unidades para Cliente exemplo. Total: R$ 150,00; saldo: 7.
4. Tente vender 8 unidades. Deve aparecer erro e o saldo deve continuar 7.
5. Tente remover o produto com saldo. A operação deve ser recusada.
6. Registre saída manual de 7 unidades e remova o produto.
7. Confira que pedidos e movimentos continuam visíveis.
8. Reinicie a aplicação: os dados permanecem na pasta `data`.

## Testes

Execute `./mvnw.cmd test` no Windows ou `./mvnw test` no Linux/macOS. Consulte `../VALIDACAO.md` para saber o que foi efetivamente verificado na preparação.

## Limites didáticos

Servidor restrito a 127.0.0.1, sem login, autorização por perfil ou proteção CSRF. Use apenas localmente para a atividade. Sem edição de produto, cancelamento, múltiplos itens por pedido ou emissão fiscal. O banco H2 é persistido em arquivo; não envie a pasta data ao GitHub. Para começar do zero, encerre o programa e remova essa pasta conscientemente.

## Referência

[Guia oficial de Spring MVC e Thymeleaf](https://spring.io/guides/gs/serving-web-content/).
