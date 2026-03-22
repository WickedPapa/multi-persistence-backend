
---

![Language](https://img.shields.io/badge/language-Java_21-blue)
![Framework](https://img.shields.io/badge/framework-Spring_Boot-brightgreen)
![Database](https://img.shields.io/badge/database-PostgreSQL-blue)
![Database](https://img.shields.io/badge/database-MongoDB-green)
![Container](https://img.shields.io/badge/container-Docker-blue)
![API](https://img.shields.io/badge/API-OpenAPI-orange)
![Codegen](https://img.shields.io/badge/codegen-Swagger_Codegen-orange)
![Mapper](https://img.shields.io/badge/mapper-MapStruct-yellow)
![Boilerplate](https://img.shields.io/badge/boilerplate-Lombok-red)

# SQL vs NoSQL Backend

This project demonstrates a backend application that can run using either **PostgreSQL (relational)** or **MongoDB (NoSQL)**.

Everything is fully containerized with Docker, so no local setup (Java, Maven, DB) is required.

---

## 🚀 Prerequisites

* Docker
* Docker Compose

Install Docker Desktop (includes Docker Compose):

👉 https://docs.docker.com/get-docker/

---

## ▶️ Run the application

### PostgreSQL (relational) (default)

#### Linux
```bash
APP_DATASOURCE=POSTGRES docker compose up -d --build
```
#### Windows (PowerShell)
```bash
$env:APP_DATASOURCE="POSTGRES"; docker compose up -d --build
```

### MongoDB (NoSQL)

#### Linux
```bash
APP_DATASOURCE=MONGODB docker compose up -d --build
```

#### Windows (PowerShell)
```bash
$env:APP_DATASOURCE="MONGODB"; docker compose up -d --build
```

## 🧪 Run API tests (automatic)

You can run all API tests (including minimal automatic data setup) using Newman via Docker.
```bash
docker compose --profile test-newman run --rm newman
```

### ⚠️ Prerequisite
The application must be already running and fully started before executing the tests

---

## 🌐 Services

### 🧩 Application (API)

* Swagger UI → http://localhost:8080/swagger-ui/index.html
  → Test APIs directly from the browser

* OpenAPI JSON → http://localhost:8080/v3/api-docs
  → API specification in JSON

* OpenAPI YAML → http://localhost:8080/v3/api-docs.yaml
  → Downloadable API specification

### ⚠️ Prerequisite
The application must be up and running

---

### 🐘 PostgreSQL (pgAdmin)

* URL → http://localhost:5050
* Email → `pg@admin.com`
* Password → `pg-psw`

→ Web interface to explore relational data (tables, queries)

**First time setup:**

* Add new server
* Name → `postgres`
* Host → `postgres`
* Port → `5432`
* Username → `postgres-admin`
* Password → `postgres-psw`

---

### 🍃 MongoDB (Mongo Express)

* URL → http://localhost:8081
* Username → `admin-basic`
* Password → `psw-basic`

→ Web interface to explore MongoDB documents and collections

---

## 🔄 Reset databases

```bash
docker compose down -v
```

Then restart the app

---

## 🧠 Notes

* The application switches database based on `APP_DATASOURCE`
* No manual configuration required
* Data is persisted using Docker volumes (removed only with `-v`)

---

## 📦 Tech stack

* Java 21 + Spring Boot
* PostgreSQL (relational)
* MongoDB (NoSQL)
* Docker & Docker Compose
* OpenAPI (API specification)
* Swagger UI (API exploration)
* Swagger Codegen (code generation)

---
