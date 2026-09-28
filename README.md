# Desafio Técnico - Cadastro e Consulta de Abastecimentos











API REST em Java para cadastro e consulta de abastecimentos de um posto de combustível.

**Stack:** Java 17, Spring Boot 3, Spring Data JPA, Bean Validation, H2 (em arquivo) e Maven.

## Funcionalidades

- CRUD de **Combustíveis** (nome e preço por litro)
- CRUD de **Bombas** (nome e combustível que abastece)
- CRUD de **Abastecimentos** (bomba, data e litros)
- O **valor total** do abastecimento é calculado (litros x preço por litro)
- Persistência em arquivo: os dados são mantidos após reiniciar
- Validações (400) e recurso inexistente (404), com mensagem
- Consulta de abastecimentos com filtros por bomba e período, e paginação
- Erro 409 ao apagar registros vinculados a outros dados
- Documentação interativa com Swagger/OpenAPI
- Testes automatizados e CI com GitHub Actions

## Como rodar

Requisitos: Java 17+ e Maven.

    mvn spring-boot:run

A API sobe em http://localhost:8080 e os dados ficam na pasta data/.
Console do banco: /h2-console (URL jdbc:h2:file:./data/abastecimentos, usuário sa).

## Testes

    mvn test

Os testes usam H2 em memória e cobrem o cálculo do valor total, o 404 para bomba inexistente e o 400 para dados inválidos.

## Endpoints

Cada recurso aceita: GET /recurso, GET /recurso/{id}, POST /recurso, PUT /recurso/{id} e DELETE /recurso/{id}.

| Recurso | Corpo de exemplo |
|---|---|
| /combustiveis | {"nome":"Gasolina","precoPorLitro":5.89} |
| /bombas | {"nome":"Bomba 1","combustivel":{"id":1}} |
| /abastecimentos | {"bomba":{"id":1},"data":"2026-09-28T17:40:00","litros":10} |

Exemplo de resposta de um abastecimento de 10 litros com gasolina a 5,89: o campo valorTotal vem 58.90.

## Estrutura

    controller/   rotas REST
    service/      regras de negócio (cálculo do valor total)
    repository/   acesso ao banco (Spring Data JPA)
    model/        entidades JPA

Documentação interativa (Swagger): http://localhost:8080/swagger-ui/index.html

## Consulta com filtros e paginação

    GET /abastecimentos?bombaId=1&de=2026-09-01&ate=2026-09-30&page=0&size=10

Todos os parâmetros são opcionais. As datas usam o formato yyyy-MM-dd (período inclusivo) e o resultado vem ordenado do mais recente para o mais antigo, dentro de um objeto de página com os campos content e page.
