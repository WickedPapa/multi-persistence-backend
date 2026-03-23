
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
- > APP_DATASOURCE=POSTGRES
- > APP_DATASOURCE=MONGODB

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

## ❗ Error Handling

A global exception handler provides a consistent error response structure:

- status
- error
- message
- path
- timestamp

---
