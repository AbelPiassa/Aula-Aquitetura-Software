# Referências da continuação

## Fontes

- Base anterior do aluno: [AbelPiassa/Aula-Aquitetura-Software](https://github.com/AbelPiassa/Aula-Aquitetura-Software), consultada no commit `ef01428915695d99c3ab2fcf1592a45f21f4211e`.
- Referência de organização indicada pelo aluno: [Gapaivams/Arq.-Software](https://github.com/Gapaivams/Arq.-Software), consultada no commit `195b925ddf0681bd3c55e8d1efa5c374d11f39ce`.
- Exemplos enviados em `Arq.-Software-main.zip`, `Arq.-Software-main(1).zip` e `hexagonal.zip`.
- Slides das aulas 6, 7 e 8 de Arquitetura de Software, do professor Pedro Henrique Nogueira Pizzutti.

## Organização aplicada

| Material de referência | Entrega atual |
|---|---|
| Pastas por aula | Aulas anteriores preservadas e entregas em `aula-6`, `aula-7` e `aula-8`, conforme os enunciados |
| MVC com controllers, entidades, repositórios e serviços | Mesma separação de responsabilidades dentro de `mvc` |
| HTML em `resources/view` | Mesma pasta, com prefixo Thymeleaf configurado |
| Hub com portas e adaptadores | Domínio, aplicação, REST, persistência e protocolos simulados em `hexagonal` |

## Adequação aos enunciados

Os enunciados enviados em imagem definem a entrega atual. As pastas das atividades são `aula-6`, `aula-7` e `aula-8`. Os arquivos das aulas anteriores permanecem em suas pastas históricas.

A aula 6 acrescenta o enum persistido com os status disponível, indisponível e descontinuado. A permissão de descontinuar é verificada no servidor com Spring Security. Os diagramas `.mermaid` representam entrada/saída, saída sem saldo ou com produto descontinuado e as transições de status.

A aula 7 mantém portas e adaptadores e simula os três protocolos exigidos. A fatura guarda `codigoBarras`, `chaveAcesso` de 44 dígitos e `hashEmail` do conteúdo. Os scripts Maven Wrapper preservam os avisos de licença existentes.

A aula 8 abre pedidos sem reservar saldo e conclui a venda no fechamento, após receber o ID da fatura pela REST. A análise em Markdown explica timeout e repetição de solicitação, com as soluções aplicadas em cada lado.

## Documentação técnica consultada

- [Spring Security: usuários em memória](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/in-memory.html).
- [Spring Security: autorização em métodos](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html).
- [Spring Security: CSRF e formulários](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html).
