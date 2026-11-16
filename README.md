# Arquitetura de Software

Trabalho aplicado a um sistema de vendas e estoque. O vendedor registra pedidos recebidos por telefone ou presencialmente. O administrador cuida dos produtos e o estoquista registra as movimentações.

A organização segue a referência [Gapaivams/Arq.-Software](https://github.com/Gapaivams/Arq.-Software). As classes e regras do projeto anterior foram distribuídas nas pastas e responsabilidades correspondentes.

## Aulas

| Pasta | Conteúdo |
|---|---|
| [Aula2](Aula2/README.md) | Diagramas C4 em `Exemplo` |
| [Aula3](Aula3/README.md) | Cliente-servidor e pipes e filtros em arquivos separados |
| [Aula4](Aula4/README.md) | Organização da aplicação e das camadas |
| [Aula5](Aula5/README.md) | Projeto MVC em `mvc` |
| [Aula6](Aula6/README.md) | MVC e diagramas de comportamento |
| [Aula7](Aula7/README.md) | Projeto hexagonal de faturamento |
| [Aula8](Aula8/README.md) | MVC integrado ao faturamento por REST |

## Organização do MVC

| Pasta em `src/main` | Responsabilidade |
|---|---|
| `java/br/com/mvc/controller` | Controllers de início, produtos, estoque, pedidos e tratamento de erros |
| `java/br/com/mvc/model/entity` | Entidades `Produto`, `Pedido` e `Movimento` |
| `java/br/com/mvc/model/repository` | Acesso ao H2 com Spring Data JPA |
| `java/br/com/mvc/model/service` | Serviços de produtos, estoque e pedidos |
| `java/br/com/mvc/model/client` | Cliente REST de faturamento, na aula 8 |
| `resources/view` | Telas `home`, `products`, `stock`, `sales`, `error` e fragmentos de `index` |
| `resources/static/css` | Estilos das telas |

A configuração `spring.thymeleaf.prefix=classpath:/view/` faz o Thymeleaf localizar as telas nessa pasta. O faturamento segue `domain/entities`, `domain/exceptions`, `domain/ports` e `infra/adapters`, com o núcleo independente do Spring.

## Executar

Use JDK 21. Para a versão integrada, abra um terminal em `Aula7/hexagonal` e outro em `Aula8/mvc`. Em cada um:

```bash
bash mvnw spring-boot:run
```

No Windows: `.\mvnw.cmd spring-boot:run`.

Abra http://127.0.0.1:8080. O faturamento atende na porta 8081. Execute somente uma versão MVC por vez. As versões das aulas 5 e 6 também podem ser iniciadas individualmente dentro de suas pastas `mvc`.

## Verificar

Em cada projeto MVC, execute `bash mvnw test`. Em `Aula7/hexagonal`, execute `bash verificar.sh` para verificar o núcleo sem baixar dependências. O pull request também executa os quatro projetos no GitHub Actions.

Os boletos, notas e notificações são simulados. Veja [REFERENCIAS.md](REFERENCIAS.md) e [VALIDACAO.md](VALIDACAO.md) para as decisões e os limites da verificação.
