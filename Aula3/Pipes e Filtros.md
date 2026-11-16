# Pipes e filtros

## Conceito e funcionamento
O processamento é dividido em etapas chamadas filtros. Cada filtro recebe dados, realiza uma transformação e passa o resultado por uma conexão, o pipe, para a próxima etapa. Os filtros devem ter interfaces claras e responsabilidades delimitadas. A comunicação pode ocorrer por fluxos, arquivos ou filas, dependendo da implementação.

## Casos de uso — exemplos práticos
1. **Importação de produtos por CSV:** leitura do arquivo → validação dos campos → normalização dos nomes e preços → identificação de duplicidades → gravação dos registros aceitos. Linhas inválidas seguem para um relatório de erros.
2. **Tratamento de logs:** leitura de eventos → remoção de informações desnecessárias → conversão para um formato comum → agrupamento por categoria → geração de relatório. Cada etapa pode ser testada com uma entrada conhecida.

É adequado para transformações sequenciais de dados e etapas reutilizáveis. Nem todo processo de negócio deve ser forçado a virar um pipeline.

## Principais vantagens
- Etapas pequenas podem ser testadas e reutilizadas separadamente.
- Uma transformação pode ser substituída sem reescrever todo o fluxo, respeitando o contrato.
- Etapas independentes podem permitir paralelismo, dependendo dos dados e da implementação.
- A divisão deixa explícito o caminho de processamento.

## Principais desvantagens
- Conversões e transferências entre etapas acrescentam custo.
- Uma etapa lenta pode acumular dados e limitar o fluxo inteiro.
- Tratamento de falhas, reprocessamento e duplicidades exige cuidado.
- Transações globais e estado compartilhado tornam o desenho mais complexo.
