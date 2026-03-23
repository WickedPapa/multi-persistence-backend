
---

![Language](https://img.shields.io/badge/language-Java_21-blue)
![Framework](https://img.shields.io/badge/framework-Spring_Boot_3-brightgreen)
![Databases](https://img.shields.io/badge/databases-PostgreSQL%20%7C%20MongoDB-blue)
![ORM](https://img.shields.io/badge/ORM-JPA%20(Hibernate)-orange)
![NoSQL](https://img.shields.io/badge/NoSQL-Spring_Data_MongoDB-green)
![Container](https://img.shields.io/badge/container-Docker-blue)
![API](https://img.shields.io/badge/API-OpenAPI_3-orange)
![Docs](https://img.shields.io/badge/docs-Swagger_UI-orange)
![Codegen](https://img.shields.io/badge/codegen-OpenAPI_Generator-yellow)
![Mapper](https://img.shields.io/badge/mapper-MapStruct-yellow)
![Boilerplate](https://img.shields.io/badge/boilerplate-Lombok-red)
![Cache](https://img.shields.io/badge/cache-Caffeine-lightgrey)
![Testing](https://img.shields.io/badge/testing-Testcontainers%20%7C%20JUnit5-blue)
![Coverage](https://img.shields.io/badge/coverage-JaCoCo-green)
![Formatting](https://img.shields.io/badge/formatting-Spotless-purple)
![Observability](https://img.shields.io/badge/monitoring-Spring_Actuator-brightgreen)
![AOP](https://img.shields.io/badge/logging-AOP-lightgrey)

# SQL vs NoSQL Backend

This project was developed as part of a Bachelor's thesis in Computer Engineering.

The goal is to compare **relational (SQL)** and **NoSQL** database paradigms by implementing the same backend application using:

- PostgreSQL (relational model via JPA/Hibernate)
- MongoDB (document model via Spring Data MongoDB)

The application is designed to **dynamically switch between the two databases** using a configuration property, enabling a direct comparison of:

- data modeling approaches
- query strategies
- performance characteristics
- consistency and constraint handling

---

## 🎯 Objectives

- Provide a unified API layer independent from the underlying database
- Compare relational vs document-based data modeling
- Analyze trade-offs between SQL and NoSQL
- Demonstrate real-world backend patterns using both approaches

---

## 🧩 Key Features

- Dual database support (PostgreSQL / MongoDB)
- Runtime switch via configuration property (`app.datasource`)
- Shared business logic across implementations
- REST API generated from OpenAPI specification
- Swagger UI for API exploration
- Validation with Jakarta Bean Validation
- Caching with Caffeine
- Health checks and metrics via Spring Actuator
- Containerized environment (Docker & Docker Compose)
- Integration testing with Testcontainers
- Code formatting with Spotless
- Code coverage with JaCoCo
- Cross-cutting logging and HTTP tracing via Spring AOP aspects

---

## 📚 Documentation

- 👉 [Quick Start](docs/quick-start.md) → Step-by-step guide to run the application locally with Docker
- 👉 [Architecture](docs/architecture.md) → Overview of the system design, components, and SQL vs NoSQL approach
- 👉 [Services](docs/services.md) → List of all available services, APIs, UIs, and monitoring endpoints

---

## 📦 Tech Stack

### Backend
- Java 21
- Spring Boot 3.x
- Spring Web (REST APIs)
- Spring Data JPA (Hibernate)
- Spring Data MongoDB
- Spring Validation (Jakarta Bean Validation)
- Spring Cache + Caffeine
- Spring Boot Actuator

### API & Code Generation
- OpenAPI 3
- springdoc-openapi (Swagger UI)
- OpenAPI Generator (interface-first approach)

### Mapping & Boilerplate
- MapStruct
- Lombok

### Database
- PostgreSQL
- MongoDB

### Testing
- JUnit 5
- Testcontainers (PostgreSQL + MongoDB)
- Instancio (test data generation)

### DevOps & Tooling
- Docker & Docker Compose
- Maven
- Spotless (code formatting)
- JaCoCo (code coverage)

### Logs
- Spring AOP for centralized logging

---

## 🤖 AI Usage

Part of this project's documentation and JavaDoc comments were generated with the support of Artificial Intelligence tools.

All generated content has been reviewed and refined to ensure correctness, clarity, and alignment with the intended design and implementation.

---
