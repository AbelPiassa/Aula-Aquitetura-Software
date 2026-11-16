# Cliente-servidor

## Conceito e funcionamento
O cliente solicita uma operação; o servidor recebe a requisição, processa as regras e devolve uma resposta. Em uma aplicação web, o navegador pode enviar um formulário e o servidor retornar uma página com o resultado. O banco armazena os dados utilizados pelo servidor. Centralizar regras no servidor evita depender apenas das validações da interface.

## Casos de uso — exemplos práticos
1. **Sistema de vendas e estoque:** o vendedor envia um pedido pelo navegador. O servidor verifica o saldo, registra a venda e responde com a confirmação. Vários funcionários consultam a mesma base.
2. **Sistema acadêmico:** alunos consultam notas e professores lançam avaliações em clientes web. O servidor centraliza regras e persistência.

É adequado quando diversos usuários precisam compartilhar informações e as regras devem ser administradas centralmente.

## Principais vantagens
- Regras e dados centralizados facilitam atualização e consistência.
- Clientes distintos podem utilizar o mesmo serviço.
- O servidor pode controlar autenticação, permissões e validações.
- A separação entre interface e processamento facilita a manutenção.

## Principais desvantagens
- Indisponibilidade do servidor pode impedir o uso por todos os clientes.
- A comunicação depende da rede e sofre com latência.
- Sob alta carga, o servidor ou banco pode virar gargalo.
- Escalar exige planejamento de capacidade, sessões e persistência; o estilo sozinho não garante desempenho.
