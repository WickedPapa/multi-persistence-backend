
---

# 🏗️ Architecture

This application is designed to support both relational and NoSQL databases using the same API and business logic.

The goal is to compare **PostgreSQL (SQL)** and **MongoDB (NoSQL)** by implementing the same domain and exposing identical REST endpoints.

---

## 🔍 Overview

The architecture follows a layered pattern:

```
Client → Controller → Service → Repository → Database
```

The system is **database-agnostic** at API and service level.  
The underlying database is selected **once at startup** via the `APP_DATASOURCE` environment variable.

---

## 🧩 Layers

### Controller
- Exposes REST endpoints
- Handles HTTP requests/responses
- Implements interfaces generated from `api.yaml` (OpenAPI Generator)
- Uses shared DTOs (also generated)

### Service
- Contains business logic
- Two implementations per domain, selected at startup via `@ConditionalOnDatasource`:
    - `XxxPostgresService` — JPA-backed, ACID transactions
    - `XxxMongoService` — MongoDB-backed, no multi-document transactions in standalone mode

### Repository
- Handles data access
- Two implementations per domain:
    - `XxxPostgresRepository` extends `JpaRepository`
    - `XxxMongoRepository` extends `MongoRepository`

---

## 🔄 Database Selection

The database is selected via environment variable **at startup**, not at runtime:

```
APP_DATASOURCE=postgres   # PostgreSQL backend
APP_DATASOURCE=mongo      # MongoDB backend
```

`DatasourceAutoConfigurationExcluder` (an `EnvironmentPostProcessor`) validates this value at bootstrap and excludes the irrelevant Spring autoconfiguration (e.g., JPA/Hibernate is fully excluded when running with MongoDB, and vice versa). Bean activation is controlled by `@ConditionalOnDatasource`, a custom meta-annotation wrapping `@ConditionalOnProperty`.

---

## 📊 Data Modeling

The domain consists of three entities: **User**, **Product**, and **Order**.

### PostgreSQL — Normalized relational schema

The schema is fully normalized:
- `users`, `products`, `orders`, `order_items` tables
- `orders.user_id` → foreign key to `users`
- `order_items.order_id` → foreign key to `orders` (with `ON DELETE CASCADE`)
- `order_items.product_id` → foreign key to `products`

Orders also maintain a **snapshot** of user and product data at the time of creation (e.g., `user_first_name_snapshot`, `order_items.name`, `order_items.price`). This ensures historical accuracy even if a user or product is later updated, while still preserving the relational FK for integrity.

### MongoDB — Denormalized document model

Orders are stored as **self-contained documents**. User and product data are embedded directly inside the order document at creation time:

```json
{
  "_id": "...",
  "user": { "userId": "...", "firstName": "...", "lastName": "...", "email": "..." },
  "items": [
    {
      "productEmbedded": { "productId": "...", "name": "...", "price": "..." },
      "quantity": 2
    }
  ],
  "total": "59.98"
}
```

This **embedding pattern** is the idiomatic MongoDB approach for one-to-many relationships where the child data is always accessed together with the parent and does not need independent queries. It eliminates joins at read time.

**Key modeling difference:** in PostgreSQL, `order_items` is a separate table linked by FK; in MongoDB, items are an embedded array in the same document. Both strategies produce equivalent read results, but with different write and query trade-offs.

---

## 🔐 Constraints and Data Integrity

### PostgreSQL — Database-level constraints

Constraints are declared in the schema and enforced by the database engine:

| Constraint | Column | Type |
|---|---|---|
| `PRIMARY KEY` | `id` on all tables | Uniqueness + not-null |
| `UNIQUE` | `users.email`, `products.name` | Uniqueness |
| `FOREIGN KEY` | `orders.user_id → users.id` | Referential integrity |
| `FOREIGN KEY` | `order_items.order_id → orders.id` | Referential integrity (cascades deletes) |
| `NOT NULL` | most columns | Presence |

Violations are rejected at the database level regardless of the application layer.

### MongoDB — Application-level schema + `$jsonSchema` validators

MongoDB is schema-less by default. To enforce equivalent constraints, the `DatabaseMigrationV1` Mongock changeunit applies:

- **`$jsonSchema` validators** on each collection with `validationLevel: strict` — define required fields and BSON types, making the schema explicit and enforced by the MongoDB engine.
- **Unique indexes** on `users.email` and `products.name` — equivalent to the SQL `UNIQUE` constraints.
- **No referential integrity** — MongoDB has no concept of foreign keys; the application is responsible for checking that a user or product exists before creating an order.

---

## 📇 Indexing

### PostgreSQL

Indexes are created by the Flyway `V1__init.sql` migration:

| Index | Type | Purpose |
|---|---|---|
| `PRIMARY KEY` on all `id` columns | B-tree | Unique + fast lookup |
| `UNIQUE` on `users.email`, `products.name` | B-tree | Uniqueness enforcement + lookup |
| `idx_orders_user_id` on `orders(user_id)` | B-tree | Fast `findByUserId` without sequential scan |

### MongoDB

Indexes are created by the Mongock `v1-init-indexes` changeunit:

| Index | Type | Purpose |
|---|---|---|
| `_id` (auto) on all collections | Default | Unique + fast lookup |
| `users_email_unique` on `users.email` | Ascending + unique | Mirrors SQL `UNIQUE` |
| `products_name_unique` on `products.name` | Ascending + unique | Mirrors SQL `UNIQUE` |
| `orders_user_id_idx` on `orders.user.userId` | Ascending | Fast `findByUserUserId` |

---

## 🔁 Data Access and Queries

### Simple reads

Both backends support simple lookups by ID and list queries via repository method naming conventions (`findById`, `findAll`, `findByUserId` / `findByUserUserId`).

### Aggregation: most sold products

**PostgreSQL** uses a native SQL aggregation via a custom `@Query` in `OrderPostgresRepository`:

```sql
SELECT oi.name AS productName, SUM(oi.quantity) AS totalQuantity
FROM order_items oi
GROUP BY oi.name
ORDER BY totalQuantity DESC
```

**MongoDB** uses an aggregation pipeline via a custom repository method in `OrderMongoRepository`:

```
$unwind: $items
→ $group: { _id: $items.productEmbedded.name, totalQuantity: { $sum: $items.quantity } }
→ $sort: { totalQuantity: -1 }
```

Both produce the same result. The SQL version uses a JOIN-implicit table scan (`order_items`); the MongoDB version unwinds the embedded array within each document.

### Aggregation: total spent per user

Similarly:

- **PostgreSQL** — `GROUP BY orders.user_id` with `SUM(total)`
- **MongoDB** — `$group` by `user.userId` with `$sum: $total`

---

## ⚡ Transactionality

### PostgreSQL — ACID transactions

Write operations in `OrderPostgresService` are annotated with `@Transactional`. Spring delegates to the underlying JPA/Hibernate transaction manager, which maps to PostgreSQL's ACID guarantees:

- **Atomicity** — if any step fails (e.g., saving an order item), the entire operation is rolled back.
- **Consistency** — constraints and foreign keys are enforced at commit time.
- **Isolation** — reads within the transaction see a consistent snapshot.
- **Durability** — committed data survives crashes.

Read-only queries use `@Transactional(readOnly = true)` to allow the database to optimize accordingly (e.g., avoid acquiring write locks).

### MongoDB — No multi-document transactions (standalone mode)

The MongoDB backend in this project runs in **standalone mode** (not a replica set), which means multi-document ACID transactions are unavailable (`mongock.transaction-enabled=false`). `OrderMongoService` intentionally does **not** carry `@Transactional`.

The demo relies on the **single-document atomicity** guarantee that MongoDB provides natively: since an order and all its items are stored as one document, the `save()` call is inherently atomic for that document. For operations spanning multiple documents (e.g., checking user existence and saving the order), partial failures are possible and are accepted as a known trade-off in a demo context without a replica set.

---

## 🔄 Schema Evolution

### PostgreSQL — Flyway

SQL schema changes are expressed as versioned migration scripts under `src/main/resources/db/migration` (e.g., `V1__init.sql`, `V2__...sql`). Flyway applies pending migrations at startup. JPA `ddl-auto` is set to `validate`, so the entity model must always match the migrated schema — the database is the source of truth.

### MongoDB — Mongock

MongoDB schema and index changes are expressed as `@ChangeUnit` classes under `src/main/java/.../config/mongock`. Mongock tracks applied changeunits in the `mongockChangeLog` collection and applies pending ones at startup, providing the same idempotent, versioned semantics as Flyway.

---

## 🔁 Mapping

MapStruct is used to convert between persistence models and generated API DTOs:

- `XxxEntity` ↔ `XxxResponse` / `XxxRequest` (PostgreSQL path)
- `XxxDocument` ↔ `XxxResponse` / `XxxRequest` (MongoDB path)

Mappers are generated at compile time with `defaultComponentModel=spring` and `unmappedTargetPolicy=ERROR`, so any missing mapping produces a compile error.

---

## ⚡ Caching

Caffeine powers `users`, `products`, `orders`, and `orders-by-user` caches, each with tailored TTL and size caps (defined in `CaffeineCacheConfig`).

- Write operations evict the relevant cache entries (`@CacheEvict`).
- Read operations populate the cache on miss (`@Cacheable`).
- Cache hit/miss/put metrics are exported via Spring Actuator for observability and tuning.

---

## 📜 Logging & Observability

- A request/response AOP aspect injects an MDC `requestId` into every REST call for end-to-end traceability.
- A cache AOP aspect reports cache operations (hit, miss, put, evict), complementing the Actuator metrics endpoints.
- All timing and cache data is exposed at `/actuator/metrics`.

---

## ❗ Error Handling

A global exception handler (`@RestControllerAdvice`) produces a consistent error response structure:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with id ...",
  "path": "/orders/...",
  "timestamp": "..."
}
```

---

## 🔐 Security Scope Note

Authentication and authorization are intentionally out of scope for this thesis implementation.

The project focuses on comparing SQL vs NoSQL persistence design, query behavior, constraints, and performance under the same API contract. Endpoints such as `createOrder` therefore do not enforce an auth layer in this demo baseline.

---
