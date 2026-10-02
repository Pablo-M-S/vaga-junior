# Desafio Técnico - Cadastro e Consulta de Abastecimentos

![CI](https://github.com/Pablo-M-S/vaga-junior/actions/workflows/ci.yml/badge.svg)

API REST em Java para cadastro e consulta de abastecimentos de um posto de combustível.

**Stack:** Java 17, Spring Boot 3.3, Spring Data JPA, Bean Validation, H2 (em arquivo), Maven e Docker.

## Funcionalidades

- CRUD de **Combustíveis** (nome e preço por litro)
- CRUD de **Bombas** (nome e combustível que abastece)
- CRUD de **Abastecimentos** (bomba, data, litros, preço praticado e valor total)
- Consulta de abastecimentos com filtros por bomba e período, e paginação
- Persistência em arquivo: os dados são mantidos após reiniciar
- Erros padronizados em JSON (400, 404 e 409)
- Documentação interativa com Swagger/OpenAPI
- Testes automatizados (API e unitários) e CI com GitHub Actions

## Como rodar

Requisitos: Java 17+. O Maven Wrapper baixa o Maven na primeira execução.

    ./mvnw spring-boot:run

No Windows, use `mvnw.cmd`.

A API sobe em http://localhost:8080 e os dados ficam na pasta data/.
Swagger: http://localhost:8080/swagger-ui/index.html
Console do banco: vem **desligado** por padrão, para não expor o banco na porta da API. Para usar em desenvolvimento:

    ./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.h2.console.enabled=true

Depois acesse http://localhost:8080/h2-console (URL jdbc:h2:file:./data/abastecimentos, usuário sa, senha em branco).

### Com Docker

    docker build -t abastecimentos .
    docker run -p 8080:8080 -v abastecimentos-data:/app/data abastecimentos

O volume `abastecimentos-data` guarda o banco; sem ele, os dados se perdem quando o container é removido.

## Testes

    ./mvnw test

Usam H2 em memória. São dois grupos:

- **API (MockMvc):** cálculo do valor total, preço praticado, filtros e paginação, GET por id, PUT e DELETE dos três recursos, nomes duplicados (inclusive requisições simultâneas), data futura e os erros 400, 404 e 409.
- **Unitários (Mockito):** regras dos services sem subir o Spring.

## Endpoints

Cada recurso aceita: GET /recurso, GET /recurso/{id}, POST /recurso, PUT /recurso/{id} e DELETE /recurso/{id}.

| Recurso | Corpo de exemplo (POST e PUT) |
|---|---|
| /combustiveis | {"nome":"Gasolina","precoPorLitro":5.89} |
| /bombas | {"nome":"Bomba 1","combustivelId":1} |
| /abastecimentos | {"bombaId":1,"data":"2026-09-01T17:40:00","litros":10} |

Um abastecimento de 10 litros com gasolina a 5,89 volta assim (o preço e o valor total são calculados pela API):

    {
      "id": 1,
      "bomba": { "id": 1, "nome": "Bomba 1", "combustivel": { "id": 1, "nome": "Gasolina", "precoPorLitro": 5.890 } },
      "data": "2026-09-01T17:40:00",
      "litros": 10.000,
      "precoPorLitro": 5.890,
      "valorTotal": 58.90
    }

Regras de entrada:

- Litros e preço aceitam até 7 dígitos inteiros e 3 casas decimais (mínimo 0,01).
- A data do abastecimento não pode estar no futuro.
- Combustível e bomba não podem repetir o nome (sem diferenciar maiúsculas de minúsculas).

## Consulta com filtros e paginação

    GET /abastecimentos?bombaId=1&de=2026-09-01&ate=2026-09-30&page=0&size=10

Todos os parâmetros são opcionais. As datas usam o formato yyyy-MM-dd e o período é inclusivo nas duas pontas, e `de` não pode ser depois de `ate` (senão a API responde 400). O resultado vem do mais recente para o mais antigo (20 por página, por padrão), num objeto com os campos content e page.

## Erros

Todos os erros saem em JSON com `timestamp`, `status`, `erro` e `mensagem`. Nos 400 de validação vem também o mapa `campos`, com o campo que falhou.

- **400**: dados inválidos (a resposta lista os campos que falharam), data no futuro, JSON malformado, parâmetro com tipo errado (ex.: `?de=abc`), período invertido (`de` depois de `ate`) ou ordenação por campo inexistente
- **404**: registro não encontrado (inclui bomba ou combustível informado que não existe) ou rota inexistente
- **405**: método HTTP não permitido para a rota
- **409**: nome já existente (combustível ou bomba), ou tentativa de apagar um combustível ou bomba que ainda está vinculado a outros dados
- **500**: erro inesperado (o detalhe vai só para o log do servidor)

## Decisões de projeto

- O **valor total** é calculado pela API (litros x preço por litro, com 2 casas), para evitar inconsistência. É o campo "quantidade em valores" do abastecimento.
- Cada abastecimento **guarda o preço por litro do momento**. Se o combustível for reajustado depois, o histórico não muda. No PUT, o preço guardado só é trocado se a bomba mudar (ou se o registro for anterior a este campo, e não tiver preço guardado).
- **DTOs** de entrada (`*Request`, com as validações) e de saída (`*Response`), para não expor as entidades. Nas relações a entrada usa só o id: `{"bombaId":1}`.
- Os dados ficam em H2 **em arquivo**, para sobreviverem a um restart.
- Nome único de combustível e de bomba: o service confere antes (sem diferenciar maiúsculas) para dar uma mensagem clara, e a restrição única do banco barra requisições simultâneas, que também respondem 409.

## Limitações conhecidas

- A restrição única do banco diferencia maiúsculas de minúsculas; só a checagem do service ignora. Duas requisições simultâneas que diferem apenas na caixa (ex.: "Gasolina" e "GASOLINA") ainda podem passar.
- Em um banco `data/` antigo que já tenha nomes repetidos, a restrição única não é criada; apague a pasta `data/` para recriar.
- Data "agora" enviada por um cliente com o relógio adiantado pode ser recusada como futura.
- Abastecimentos criados antes do campo de preço praticado ficam sem preço guardado até serem alterados.

## Estrutura

    controller/   rotas REST
    dto/          objetos de entrada (Request) e saída (Response)
    service/      regras de negócio (valor total, preço praticado, nomes únicos)
    repository/   acesso ao banco (Spring Data JPA)
    model/        entidades JPA
    exception/    tratamento padronizado de erros
    config/       Swagger e formato da paginação
