# 🌐 Services

All services are available once the application is up and running.

---

## 🧩 Application API

- 👉 [Swagger UI](http://localhost:8080/swagger-ui/index.html) → Test APIs directly from the browser
- 👉 [OpenAPI JSON](http://localhost:8080/v3/api-docs) → API specification in JSON
- 👉 [OpenAPI YAML](http://localhost:8080/v3/api-docs.yaml) → Downloadable API specification

---

## 🐘 PostgreSQL (pgAdmin)

- 👉 [Home page](http://localhost:5050)
- Email → `pg@admin.com`
- Password → `pg-psw`

Web interface to explore relational data (tables, queries).

### First-time setup

- General
  - Add new server
- Connection
  - Add new server
  - Name → `postgres`
  - Host → `postgres`
  - Port → `5432`
  - Username → `postgres-admin`
  - Password → `postgres-psw`

---

## 🍃 MongoDB (Mongo Express)

- 👉 [Home page](http://localhost:8081)
- Username → `admin-basic`
- Password → `psw-basic`

Web interface to explore MongoDB documents and collections.

---

## 📊 Monitoring & Cache Metrics

Spring Boot Actuator exposes runtime metrics, including cache statistics.

### Available endpoints

- 👉 [All metrics](http://localhost:8080/actuator/metrics) → List of all available metrics
- 👉 [Cache gets](http://localhost:8080/actuator/metrics/cache.gets) → Total cache accesses (hit + miss)
- 👉 [Cache puts](http://localhost:8080/actuator/metrics/cache.puts) → Number of cache insertions
- 👉 [Cache evictions](http://localhost:8080/actuator/metrics/cache.evictions) → Automatic cache removals (TTL / size)

---

### 🔍 Filter by cache

You can filter metrics by cache name using query parameters:

- 👉 [Users cache](http://localhost:8080/actuator/metrics/cache.gets?tag=cache:users)  
- 👉 [Products cache](http://localhost:8080/actuator/metrics/cache.gets?tag=cache:products)  
- 👉 [Orders cache](http://localhost:8080/actuator/metrics/cache.gets?tag=cache:orders)  
- 👉 [Orders by user cache](http://localhost:8080/actuator/metrics/cache.gets?tag=cache:orders-by-user)

---

### 📈 Hit vs Miss analysis

- 👉 [Cache hits](http://localhost:8080/actuator/metrics/cache.gets?tag=cache:users&tag=result:hit)  
- 👉 [Cache misses](http://localhost:8080/actuator/metrics/cache.gets?tag=cache:users&tag=result:miss)  

These metrics can be used to evaluate cache efficiency.

---
