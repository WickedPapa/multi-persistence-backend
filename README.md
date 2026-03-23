
---

![Language](https://img.shields.io/badge/language-Java_21-blue)
![Framework](https://img.shields.io/badge/framework-Spring_Boot-brightgreen)
![Databases](https://img.shields.io/badge/databases-PostgreSQL%20%7C%20MongoDB-blue)
![Container](https://img.shields.io/badge/container-Docker-blue)
![API](https://img.shields.io/badge/codegen-OpenAPI-orange)
![Swagger](https://img.shields.io/badge/API_docs-Swagger-orange)
![Mapper](https://img.shields.io/badge/mapper-MapStruct-yellow)
![Boilerplate](https://img.shields.io/badge/boilerplate-Lombok-red)

# SQL vs NoSQL Backend

This project was developed as part of a Bachelor's thesis in Computer Engineering.

The goal is to compare **relational (SQL)** and **NoSQL** database paradigms by implementing the same backend application using:

- PostgreSQL (relational model)
- MongoDB (document model)

The application is designed to **dynamically switch between the two databases** using a configuration property, allowing direct comparison of:

- data modeling approaches
- query strategies
- performance characteristics
- consistency and constraints handling

---

## 🎯 Objectives

- Provide a unified API layer independent from the underlying database
- Compare relational vs document-based data modeling
- Analyze trade-offs between SQL and NoSQL
- Demonstrate real-world backend patterns using both approaches

---

## 🧩 Key Features

- Dual database support (PostgreSQL / MongoDB)
- Same business logic for both implementations
- REST API with OpenAPI documentation
- Fully containerized environment (Docker)
- Automated API testing with Newman

---

## 📚 Documentation

- 👉 [Quick Start](docs/quick-start.md)
- 👉 [Architecture](docs/architecture.md)
- 👉 [Services](docs/services.md)

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
