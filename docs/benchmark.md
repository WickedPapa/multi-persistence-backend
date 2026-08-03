# Benchmark & Comparative Results

This page defines a **minimal, repeatable** benchmark workflow for comparing the PostgreSQL and MongoDB backends under the same API contract.

The goal is not an exhaustive performance campaign, but a lightweight thesis-ready baseline.

---

## Scope

Current benchmark script:

- `benchmark/k6/read-api-baseline.js`

It runs only read-oriented endpoints to keep the SQL-vs-NoSQL comparison focused on query behavior:

- `GET /users`
- `GET /products`
- `GET /orders`
- `GET /orders/stats/most-sold-products`
- `GET /orders/stats/total-spent-per-user`

`GET /actuator/health` is executed once in `setup()` only as a pre-flight availability check and is excluded from load traffic.

Traffic distribution in the default stress profile:

- `GET /users` -> 14%
- `GET /products` -> 14%
- `GET /orders` -> 14%
- `GET /orders/stats/most-sold-products` -> 29%
- `GET /orders/stats/total-spent-per-user` -> 29%

---

## Prerequisites

1. Start exactly one backend stack:
   - PostgreSQL: `./run-app-postgres.ps1` or `./run-app-postgres.sh`
   - MongoDB: `./run-app-mongo.ps1` or `./run-app-mongo.sh`
2. (Optional but recommended) seed data with Newman; to better populate the database, run Newman tests 3 times:
   - `./run-tests.ps1` or `./run-tests.sh`
3. Ensure API is reachable at `http://localhost:8080`.

---

## Run k6 (stress profile)

```bash
docker run --rm -i -e BASE_URL=http://host.docker.internal:8080 -v "$(pwd):/work" -w /work grafana/k6 run benchmark/k6/read-api-baseline.js
```

---

## Comparison protocol (PostgreSQL vs MongoDB)

Use the same protocol on both backends:

1. Reset volumes (`run-compose-down.*`) and start the selected stack.
2. Seed data with the same Newman collection.
3. Run one warm-up execution (discard result).
4. Run 3 measured executions with the same `RATE`, `DURATION`, `PREALLOCATED_VUS`, and `MAX_VUS`.
5. Compare median of p95 latency and request rate.

Keep hardware and background workload as stable as possible while measuring.

---

## Results

Measured with the configured benchmark profile for the campaign.  
For the results reported below, database seeding was performed by running the Newman test suite 3 times before measurements.

---

| Cache | Backend | VUS | Duration | iterations | req/s (avg) | p90 latency (ms) | p95 latency (ms) | avg latency (ms) | max latency (ms) | error rate (%) |
|---|---|---:|---|---:|---:|---:|---:|---:|---:|---:|
| With Cache | PostgreSQL | 80 | 5m | 36001 | 127.88 | 2.52 | 2.84 | 1.93 | 66.75 | 0.00 |
| With Cache | MongoDB | 80 | 5m | 36000 | 127.87 | 2.94 | 3.29 | 2.26 | 44.54 | 0.00 |
| Without Cache | PostgreSQL | 80 | 5m | 36001 | 127.97 | 2.64 | 2.93 | 2.02 | 65.89 | 0.00 |
| Without Cache | MongoDB | 80 | 5m | 36001 | 127.94 | 2.96 | 3.29 | 2.28 | 56.88 | 0.00 |

---

## Interpretation checklist

- If MongoDB has lower p95 on order reads, verify whether embedding reduced join cost.
- If PostgreSQL has lower error/variance, relate it to stronger schema constraints and transactional guarantees.
- Correlate outliers with Actuator metrics (`/actuator/metrics`) and cache hit/miss trends.

---
