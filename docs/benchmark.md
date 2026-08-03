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

Load model: `ramping-arrival-rate` with default stages:

- 120 req/s for 2m
- 300 req/s for 2m
- 600 req/s for 2m
- 900 req/s for 2m

Global thresholds include both p95 and p99 latency.

---

## Prerequisites

1. Start exactly one backend stack:
   - PostgreSQL: `./run-app-postgres.ps1` or `./run-app-postgres.sh`
   - MongoDB: `./run-app-mongo.ps1` or `./run-app-mongo.sh`
2. (Optional but recommended) seed data with Newman:
   - quick seed: `./run-tests.ps1` or `./run-tests.sh`
   - stress-campaign seed (recommended): `./run-tests-20x.ps1` or `./run-tests-20x.sh`
3. Ensure API is reachable at `http://localhost:8080`.

---

## Run k6 (stress profile)

```bash
docker run --rm -i -e BASE_URL=http://host.docker.internal:8080 -v "$(pwd):/work" -w /work grafana/k6 run benchmark/k6/read-api-baseline.js
```

Optional stage override example:

```bash
docker run --rm -i -e BASE_URL=http://host.docker.internal:8080 -e STAGE_1_RATE=150 -e STAGE_2_RATE=350 -e STAGE_3_RATE=700 -e STAGE_4_RATE=1000 -e PREALLOCATED_VUS=120 -e MAX_VUS=500 -v "$(pwd):/work" -w /work grafana/k6 run benchmark/k6/read-api-baseline.js
```

---

## Comparison protocol (PostgreSQL vs MongoDB)

Use the same protocol on both backends:

1. Reset volumes (`run-compose-down.*`) and start the selected stack.
2. Seed data with the same Newman collection.
3. Run one warm-up execution (discard result).
4. Run 3 measured executions with the same stage configuration (`STAGE_1_RATE`, `STAGE_2_RATE`, `STAGE_3_RATE`, `STAGE_4_RATE`, stage durations, `PREALLOCATED_VUS`, and `MAX_VUS`).
5. Compare median of p95 latency and request rate.

Keep hardware and background workload as stable as possible while measuring.

---

## Results

Measured with the configured benchmark profile for the campaign.  
For stress-campaign runs, seed data was executed with `run-tests-20x.*` before measurements.

---

| Cache | Backend | Load profile | Max VUs | Duration | iterations | req/s (avg) | p90 latency (ms) | p95 latency (ms) | p99 latency (ms) | avg latency (ms) | max latency (ms) | error rate (%) | failed reqs |
|---|---|---|---:|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| With Cache | PostgreSQL | ramping-arrival-rate (120→300→600→900 req/s) | 120 | 8m | 176399 | 387.74 | 2.72 | 3.07 | 4.02 | 1.92 | 193.56 | 0.00 | 15 |
| With Cache | MongoDB | ramping-arrival-rate (120→300→600→900 req/s) | 120 | 8m | 176399 | 379.00 | 2.93 | 3.27 | 4.27 | 2.24 | 121.15 | 0.01 | 27 |
| Without Cache | PostgreSQL | ramping-arrival-rate (120→300→600→900 req/s) | 120 | 8m | 176399 | 392.65 | 2.74 | 3.08 | 4.00 | 1.94 | 21.29 | 0.01 | 34 |
| Without Cache | MongoDB | ramping-arrival-rate (120→300→600→900 req/s) | 120 | 8m | 176399 | 392.09 | 2.90 | 3.26 | 4.31 | 2.36 | 357.54 | 0.01 | 25 |

---

## Interpretation checklist

- If MongoDB has lower p95 on order reads, verify whether embedding reduced join cost.
- If PostgreSQL has lower error/variance, relate it to stronger schema constraints and transactional guarantees.
- Correlate outliers with Actuator metrics (`/actuator/metrics`) and cache hit/miss trends.

---
