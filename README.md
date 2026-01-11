# Projeto Cupom - Spring Boot

Aplicação de gerenciamento de cupons, construída com **Spring Boot**, **H2** (em memória) e **Flyway** para versionamento do banco de dados.

## Tecnologias

- Java 21
- Spring Boot 3.x
- Spring Data JPA
- H2 Database (em memória)
- Flyway (migrations)
- Maven
- Docker / Docker Compose

## Rodando localmente (sem Docker)

1. Clonar o projeto:

```bash
git clone <repo-url>
cd cupom
mvn clean install | mvn clean package

run aplication
```

## Acessando banco

URL: jdbc:h2:mem:banco
Usuário: userDev
Senha: user123
Path: http://localhost:8080/h2-console

## Rodando com Docker

docker compose -f compose.yaml up --build

docker build -t cupom-api .
docker run -p 8080:8080 cupom-api
http://localhost:8080

## Acesso Swagger

URL: http://localhost:8080/swagger-ui/index.html