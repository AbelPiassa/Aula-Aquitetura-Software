# Estilos arquiteturais

Análise dos estilos [cliente-servidor](<Cliente - Servidor.md>) e [pipes e filtros](<Pipes e Filtros.md>), aplicada ao sistema de vendas e estoque.

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
