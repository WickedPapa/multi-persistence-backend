
---

# 🏗️ Architecture

This application is designed to support both relational and NoSQL databases using the same API and business logic.

The goal is to compare **PostgreSQL (SQL)** and **MongoDB (NoSQL)** by implementing the same domain and exposing identical REST endpoints.

---

## 🔍 Overview

The architecture follows a layered pattern:

Client → Controller → Service → Repository → Database

The system is **database-agnostic** at API and service level.  
The underlying database is selected at runtime.

---

## 🧩 Layers

### Controller
- Exposes REST endpoints
- Handles HTTP requests/responses
- Uses DTOs

### Service
- Contains business logic
- Orchestrates operations
- Database-specific implementations:
    - PostgreSQL service
    - MongoDB service

### Repository
- Handles data access
- Two implementations:
    - JPA (PostgreSQL)
    - MongoRepository (MongoDB)

---

## 🔄 Database Switching

The database is selected via environment variable:
- > APP_DATASOURCE=postgres
- > APP_DATASOURCE=mongo

Spring configuration enables the correct repository implementation at runtime.

---

## 📊 Data Modeling

### PostgreSQL (Relational)
- Normalized schema
- Tables and relationships (joins)
- Strong constraints

### MongoDB (NoSQL)
- Document-based model
- Embedded data (no joins)
- Flexible schema

---

## 🔁 Mapping

MapStruct is used to map:

- DTO ↔ Entity (PostgreSQL)
- DTO ↔ Document (MongoDB)

This ensures separation between API and persistence models.

---

## ⚡ Caching
- Caffeine powers `users`, `products`, `orders`, and `orders-by-user` caches, each with tailored TTL and size caps (defined in `CaffeineCacheConfig`).
- Cache hit/miss/put metrics are exported via Spring Actuator for observability and tuning.

---

## 📜 Logging & Observability
- A request/response AOP aspect logs every REST call with an MDC `requestId`.
- A cache AOP aspect reports cache operations (hit, miss, put, evict), complementing the Actuator metrics/health endpoints.

---

## ❗ Error Handling

A global exception handler provides a consistent error response structure:

- status
- error
- message
- path
- timestamp

---
