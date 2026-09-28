# Desafio Técnico – Cadastro e Consulta de Abastecimentos

API REST em Java 17 + Spring Boot + JPA + H2 (arquivo).

## Rodar
```
mvn spring-boot:run
```
API em http://localhost:8080 · Console H2 em /h2-console (jdbc:h2:file:./data/abastecimentos, user `sa`).

## Endpoints
- `/combustiveis` – GET, GET/{id}, POST, PUT/{id}, DELETE/{id}
- `/bombas` – GET, GET/{id}, POST, PUT/{id}, DELETE/{id} (envie `{"nome":"Bomba 1","combustivel":{"id":1}}`)
- `/abastecimentos` – (a fazer)
