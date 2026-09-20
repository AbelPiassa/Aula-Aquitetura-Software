# Destrinchando os estilos arquiteturais

Estilos escolhidos: **cliente-servidor** e **pipes e filtros**.

## 1. Cliente-servidor

### Conceito e funcionamento
O cliente solicita uma operação; o servidor recebe a requisição, processa as regras e devolve uma resposta. Em uma aplicação web, o navegador pode enviar um formulário e o servidor retornar uma página com o resultado. O banco armazena os dados utilizados pelo servidor. Centralizar regras no servidor evita depender apenas das validações da interface.

### Casos de uso — exemplos práticos
1. **Sistema de vendas e estoque:** o vendedor envia um pedido pelo navegador. O servidor verifica o saldo, registra a venda e responde com a confirmação. Vários funcionários consultam a mesma base.
2. **Sistema acadêmico:** alunos consultam notas e professores lançam avaliações em clientes web. O servidor centraliza regras e persistência.

É adequado quando diversos usuários precisam compartilhar informações e as regras devem ser administradas centralmente.

### Principais vantagens
- Regras e dados centralizados facilitam atualização e consistência.
- Clientes distintos podem utilizar o mesmo serviço.
- O servidor pode controlar autenticação, permissões e validações.
- A separação entre interface e processamento facilita a manutenção.

### Principais desvantagens
- Indisponibilidade do servidor pode impedir o uso por todos os clientes.
- A comunicação depende da rede e sofre com latência.
- Sob alta carga, o servidor ou banco pode virar gargalo.
- Escalar exige planejamento de capacidade, sessões e persistência; o estilo sozinho não garante desempenho.

## 2. Pipes e filtros

### Conceito e funcionamento
O processamento é dividido em etapas chamadas filtros. Cada filtro recebe dados, realiza uma transformação e passa o resultado por uma conexão, o pipe, para a próxima etapa. Os filtros devem ter interfaces claras e responsabilidades delimitadas. A comunicação pode ocorrer por fluxos, arquivos ou filas, dependendo da implementação.

### Casos de uso — exemplos práticos
1. **Importação de produtos por CSV:** leitura do arquivo → validação dos campos → normalização dos nomes e preços → identificação de duplicidades → gravação dos registros aceitos. Linhas inválidas seguem para um relatório de erros.
2. **Tratamento de logs:** leitura de eventos → remoção de informações desnecessárias → conversão para um formato comum → agrupamento por categoria → geração de relatório. Cada etapa pode ser testada com uma entrada conhecida.

É adequado para transformações sequenciais de dados e etapas reutilizáveis. Nem todo processo de negócio deve ser forçado a virar um pipeline.

### Principais vantagens
- Etapas pequenas podem ser testadas e reutilizadas separadamente.
- Uma transformação pode ser substituída sem reescrever todo o fluxo, respeitando o contrato.
- Etapas independentes podem permitir paralelismo, dependendo dos dados e da implementação.
- A divisão deixa explícito o caminho de processamento.

### Principais desvantagens
- Conversões e transferências entre etapas acrescentam custo.
- Uma etapa lenta pode acumular dados e limitar o fluxo inteiro.
- Tratamento de falhas, reprocessamento e duplicidades exige cuidado.
- Transações globais e estado compartilhado tornam o desenho mais complexo.

## Comparação e aplicação no projeto

| Critério | Cliente-servidor | Pipes e filtros |
|---|---|---|
| Foco | Interação entre quem solicita e quem atende | Transformação em etapas |
| Uso no sistema de vendas | Navegador acessando a aplicação | Possível importação de produtos |
| Risco principal | Dependência do servidor e da rede | Gargalos e falhas entre etapas |
| Quando escolher | Operações interativas com dados compartilhados | Processamento sequencial e reutilizável |

Os estilos podem coexistir. O sistema proposto usa cliente-servidor e sua aplicação é implantada como um monólito. Uma futura importação pode usar pipes e filtros internamente. Monólito descreve a unidade de implantação; cliente-servidor descreve a relação entre participantes.

## Referências

- Materiais da disciplina: `Cliente - Servidor.md` e `Pipes e Filtros.md`.
- [Microsoft — Pipes and Filters](https://learn.microsoft.com/en-us/azure/architecture/patterns/pipes-and-filters).
