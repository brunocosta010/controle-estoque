# Controle de Doações

Projeto acadêmico desenvolvido em Java, Swing e SQLite para cadastro e controle de doações comunitárias.

## Requisitos

- Java 17 ou superior
- Maven

## Executar

Na raiz do projeto:

```bash
mvn clean compile
mvn exec:java "-Dexec.mainClass=br.com.controledoacoes.Main"
```

Se preferir executar pela IDE, rode a classe:

`br.com.controledoacoes.Main`

## Funcionalidades

- Cadastro, edição e exclusão de doadores
- Cadastro, edição e exclusão de itens
- Registro de entrada de doações
- Registro de distribuição/saída
- Consulta de estoque
- Histórico de movimentações
- Bloqueio de saída maior que o estoque disponível

O banco SQLite é criado automaticamente em `data/doacoes.db`.