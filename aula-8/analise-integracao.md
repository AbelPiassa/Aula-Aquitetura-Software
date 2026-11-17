# Análise da integração entre a venda e o hub

A venda é MVC e o faturamento usa arquitetura hexagonal. Ao fechar um pedido, `PedidoService` chama `FaturamentoClient`, que envia `POST /faturas`. O hub persiste a fatura e devolve seu UUID. O MVC guarda esse ID e altera o pedido para `FECHADO`.

A baixa de estoque e o movimento de venda são confirmados na mesma transação local do fechamento. Criar um pedido apenas o deixa `ABERTO`, sem reservar ou diminuir saldo. Não existe uma transação única envolvendo os dois bancos.

## Problema 1: hub demora ou deixa de responder

Imagine o vendedor fechando um pedido de R$ 150,00 enquanto o serviço de faturamento fica indisponível. Sem limite de espera, a requisição pode ocupar o atendimento por muito tempo e o vendedor fica sem saber se a venda foi concluída.

Nesta implementação, uma falha HTTP ou timeout causa uma exceção e o rollback local. O pedido continua `ABERTO`, sem `faturaId`, e não há saída de estoque nem movimento de venda. Para o negócio, o fechamento está pendente: o vendedor vê a mensagem de erro e pode tentar novamente quando o hub responder.

**Solução estudada: timeout.** O limite atua no cliente HTTP do MVC, dentro de `FaturamentoClient`, evitando que o atendimento espere indefinidamente pela conexão ou leitura. Os dois limites são configurados por `faturamento.timeout-ms`, com valor padrão de 2000 ms para cada etapa; não são um prazo único de dois segundos para toda a operação.

O timeout já está implementado. Ele libera a operação local com uma resposta de falha, mas não comprova que o hub deixou de executar. Se o hub concluiu antes da perda da resposta, temos o segundo problema.

## Problema 2: fatura criada, mas resposta perdida

O hub pode salvar a fatura e emitir os comprovantes simulados, mas a resposta HTTP se perder antes de chegar ao MVC. O hub tem a fatura; a venda continua `ABERTO`, sem o UUID. O vendedor percebe uma falha e repete o fechamento.

Sem proteção, essa repetição poderia criar outra fatura para a mesma venda, com outro boleto e outro e-mail. O negócio teria documentos duplicados e dificuldade para identificar qual deles corresponde ao pedido.

**Solução estudada: idempotência.** Ela atua principalmente no caso de uso do hub. O número do pedido é a chave de repetição: `FaturamentoService` consulta uma fatura existente e, para o mesmo pedido e valor, devolve o mesmo UUID sem emitir tudo outra vez. Um valor diferente é recusado. A persistência também tem uma restrição de unicidade por pedido.

O MVC aplica uma segunda verificação: se o pedido já está `FECHADO`, retorna sem outra chamada HTTP e sem nova baixa de estoque. Quando a primeira resposta se perdeu, a nova tentativa recupera o UUID do hub e consegue concluir a transação local uma única vez.

A idempotência está implementada para o laboratório, com uma instância do hub. Ela trata a repetição, mas não cria uma transação distribuída. Caso uma recuperação posterior encontre falta de estoque ou produto descontinuado, o fechamento continua bloqueado e a fatura já existente precisa ser conciliada; o sistema não cancela documentos automaticamente.

## Onde conferir

| Situação | Código | Verificação |
|---|---|---|
| Limites de conexão e leitura | `mvc/src/main/java/br/com/mvc/model/client/FaturamentoClient.java` | Falha HTTP no teste de integração mantém o pedido aberto |
| Reutilização da fatura | `../aula-7/hexagonal/src/main/java/br/com/faturamento/application/FaturamentoService.java` | Repetição mantém o mesmo UUID e não repete emissões |
| Fechamento local único | `mvc/src/main/java/br/com/mvc/model/service/PedidoService.java` | Fechar duas vezes faz uma chamada e uma saída de estoque |
| Persistência por pedido | `../aula-7/hexagonal/src/main/java/br/com/faturamento/infra/adapters/out/persistence/jpa/FaturaRegistro.java` | Pedido possui unicidade no H2 |

Referência conceitual: aula 8 de Arquitetura de Software, nos temas de atraso, timeout, repetição e idempotência.
