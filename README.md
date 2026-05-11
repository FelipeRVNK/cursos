# Sistema de Gerenciamento de Cursos

API REST desenvolvida em Java com Spring Boot para gerenciamento de cursos e matrículas de alunos.

## Membros

- Felipe RVNK
- Gustavo Cotovicz

## Tecnologias

- Java 17
- Spring Boot 3.2.5
- Spring Security + JWT
- Spring Data JPA
- H2 Database (em memória)
- Maven

## Como executar

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Autenticação

As rotas de DELETE e PUT exigem autenticação via JWT.

**Login:**
POST /auth/login
Content-Type: application/json
{ "username": "admin", "password": "123456" }

Use o token retornado no header `Authorization: Bearer <token>` nas rotas protegidas.

## Endpoints

### Cursos

| Método | Rota | Descrição | Autenticação |
|--------|------|-----------|--------------|
| GET | /cursos | Lista cursos (com filtros) | Não |
| GET | /cursos/{id} | Busca curso por ID | Não |
| POST | /cursos | Cria curso | Não |
| PUT | /cursos/{id} | Atualiza curso | Sim |
| DELETE | /cursos/{id} | Remove curso | Sim |
| GET | /cursos/{id}/alunos | Lista alunos do curso | Não |
| POST | /cursos/{cursoId}/alunos/{alunoId} | Matricula aluno no curso | Não |
| DELETE | /cursos/{cursoId}/alunos/{alunoId} | Cancela matrícula | Sim |

### Alunos

| Método | Rota | Descrição | Autenticação |
|--------|------|-----------|--------------|
| GET | /alunos | Lista todos os alunos | Não |
| GET | /alunos/{id} | Busca aluno por ID | Não |
| POST | /alunos | Cria aluno | Não |
| PUT | /alunos/{id} | Atualiza aluno | Sim |
| DELETE | /alunos/{id} | Remove aluno | Sim |
| GET | /alunos/{id}/cursos | Lista cursos do aluno | Não |

## Filtros e ordenação

O endpoint `GET /cursos` aceita os seguintes query params:

| Parâmetro | Descrição | Exemplo |
|-----------|-----------|---------|
| nome | Filtra por nome (parcial) | ?nome=java |
| categoria | Filtra por categoria | ?categoria=backend |
| ordenar | Ordena por campo | ?ordenar=nome |

Exemplo combinado:
GET /cursos?categoria=backend&ordenar=nome


## Vídeo

 [[Link do vídeo de apresentação](https://youtu.be/ZYSJ3_iGJ5U)]
 
