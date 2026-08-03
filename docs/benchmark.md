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

### With Cache

| Backend | VUS | Duration | req/s (avg) | p95 latency (ms) | error rate (%) |
|---|---:|---|---:|---:|---:|
| PostgreSQL | 80 | 5m | 127.88 | 2.84 | 0.00 |
| MongoDB | 80 | 5m | 127.87 | 3.29 | 0.00 |

Additional observed metrics from the same run:

| Backend | avg latency (ms) | p90 latency (ms) | max latency (ms) | iterations |
|---|---:|---:|---:|---:|
| PostgreSQL | 1.93 | 2.52 | 66.75 | 36001 |
| MongoDB | 2.26 | 2.94 | 44.54 | 36000 |

---

### Without Cache

| Backend | VUS | Duration | req/s (avg) | p95 latency (ms) | error rate (%) |
|---|---:|---|---:|---:|---:|
| PostgreSQL | 80 | 5m | 127.97 | 2.93 | 0.00 |
| MongoDB | 80 | 5m | 127.94 | 3.29 | 0.00 |

Additional observed metrics from the same run:

| Backend | avg latency (ms) | p90 latency (ms) | max latency (ms) | iterations |
|---|---:|---:|---:|---:|
| PostgreSQL | 2.02 | 2.64 | 65.89 | 36001 |
| MongoDB | 2.28 | 2.96 | 56.88 | 36001 |

---

## Interpretation checklist

- If MongoDB has lower p95 on order reads, verify whether embedding reduced join cost.
- If PostgreSQL has lower error/variance, relate it to stronger schema constraints and transactional guarantees.
- Correlate outliers with Actuator metrics (`/actuator/metrics`) and cache hit/miss trends.

---
