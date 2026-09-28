# Desafio Técnico - Cadastro e Consulta de Abastecimentos

![CI](https://github.com/Pablo-M-S/vaga-junior/actions/workflows/ci.yml/badge.svg)

API REST em Java para cadastro e consulta de abastecimentos de um posto de combustível.

**Stack:** Java 17, Spring Boot 3.3, Spring Data JPA, Bean Validation, H2 (em arquivo) e Maven.

## Funcionalidades

- CRUD de **Combustíveis** (nome e preço por litro)
- CRUD de **Bombas** (nome e combustível que abastece)
- CRUD de **Abastecimentos** (bomba, data, litros e valor total)
- Consulta de abastecimentos com filtros por bomba e período, e paginação
- Persistência em arquivo: os dados são mantidos após reiniciar
- Erros padronizados em JSON (400, 404 e 409)
- Documentação interativa com Swagger/OpenAPI
- Testes automatizados e CI com GitHub Actions

## Como rodar

Requisitos: Java 17+ e Maven.

    mvn spring-boot:run

A API sobe em http://localhost:8080 e os dados ficam na pasta data/.
Swagger: http://localhost:8080/swagger-ui/index.html
Console do banco: http://localhost:8080/h2-console (URL jdbc:h2:file:./data/abastecimentos, usuário sa, senha em branco). Ele fica habilitado só por conveniência de desenvolvimento.

## Testes

    mvn test

Usam H2 em memória e cobrem o cálculo do valor total, filtros e paginação, os erros 400, 404 e 409 e os parâmetros inválidos.

## Endpoints

Cada recurso aceita: GET /recurso, GET /recurso/{id}, POST /recurso, PUT /recurso/{id} e DELETE /recurso/{id}.

| Recurso | Corpo de exemplo |
|---|---|
| /combustiveis | {"nome":"Gasolina","precoPorLitro":5.89} |
| /bombas | {"nome":"Bomba 1","combustivel":{"id":1}} |
| /abastecimentos | {"bomba":{"id":1},"data":"2026-09-28T17:40:00","litros":10} |

Um abastecimento de 10 litros com gasolina a 5,89 volta com valorTotal 58.90.
Litros e preço aceitam até 7 dígitos inteiros e 3 casas decimais.

## Consulta com filtros e paginação

    GET /abastecimentos?bombaId=1&de=2026-09-01&ate=2026-09-30&page=0&size=10

Todos os parâmetros são opcionais. As datas usam o formato yyyy-MM-dd e o período é inclusivo nas duas pontas. O resultado vem do mais recente para o mais antigo (20 por página, por padrão), num objeto com os campos content e page.

## Erros

- **400**: dados inválidos (a resposta lista os campos que falharam), JSON malformado, parâmetro com tipo errado (ex.: `?de=abc`) ou ordenação por campo inexistente
- **404**: registro não encontrado
- **409**: tentativa de apagar um combustível ou bomba que ainda está vinculado a outros dados

## Decisões de projeto

- O **valor total** é calculado pela API (litros x preço por litro, com 2 casas), para evitar inconsistência. É o campo "quantidade em valores" do abastecimento.
- Os dados ficam em H2 **em arquivo**, para sobreviverem a um restart.
- Nas relações, o id vai em JSON aninhado: `{"bomba":{"id":1}}`.
- As entidades são usadas direto como corpo de entrada e saída, sem DTOs, para manter o projeto simples.

## Limitações conhecidas

- Ao alterar um abastecimento (PUT), o valor total é recalculado com o preço **atual** do combustível.
- Nomes duplicados de combustível e de bomba são aceitos.
- Datas de abastecimento no futuro são aceitas.

## Estrutura

    controller/   rotas REST
    service/      regras de negócio (cálculo do valor total)
    repository/   acesso ao banco (Spring Data JPA)
    model/        entidades JPA
    exception/    tratamento padronizado de erros
    config/       Swagger e paginação
