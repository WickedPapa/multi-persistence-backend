
---

## 🚀 Prerequisites

* Docker
* Docker Compose

Install Docker Desktop (includes Docker Compose):

👉 https://docs.docker.com/get-docker/

---

## ⚙️ Credentials

Credentials are defined in `.env` at the project root and injected into Compose files via `${VAR}` references (no inline defaults — if `.env` is missing, Compose fails explicitly).

> **⚠️ Demo/thesis note** — `.env` is committed here intentionally to make the demo self-contained. In a production project `.env` must be git-ignored and secrets injected via CI/CD or a secrets manager.

---

## ▶️ How to run the application

To start the application, open a terminal in the **root of the project** and run **one of the following commands**, choosing based on:

- your operating system: **Linux** or **Windows**
- the datasource: **🐘 PostgreSQL (relational)** or **🍃 MongoDB (NoSQL)**

### Linux or Windows (PowerShell)
```bash
docker compose -f compose.base.yaml -f compose.postgres.yaml up -d --build
```
```bash
docker compose -f compose.base.yaml -f compose.mongo.yaml up -d --build
```

## Startup scripts

To avoid long commands, use the dedicated root scripts:

### Windows (PowerShell)
```powershell
./run-app-postgres.ps1   # PostgreSQL stack
./run-app-mongo.ps1      # MongoDB stack
```

### Linux
(First time, if needed: `chmod +x ./run-app-postgres.sh ./run-app-mongo.sh`)
```bash
./run-app-postgres.sh    # PostgreSQL stack
./run-app-mongo.sh       # MongoDB stack
```

Each script runs the standard stack by default. To switch to debug mode (port 5005), comment the normal line and uncomment the debug variant inside the script.

---

## 🧪 Run API tests (automatic)

> ### ⚠️ Prerequisite
> The application must be up and running before executing the tests.

You can run all API tests (including minimal automatic data setup) using Newman via Docker with the following command:
```bash
docker compose -f compose.base.yaml --profile test-newman run --rm newman
```

or use `run-tests.sh` / `run-tests.ps1`.

For heavier benchmark seeding, run the x20 scripts:

- `./run-tests-20x.sh`
- `./run-tests-20x.ps1`

See [Startup scripts section](#startup-scripts) above for details.

---

## ⚡ Run lightweight benchmark (k6)

After startup (and optionally after `run-tests-20x.*` data seeding for stress campaigns), run:

```bash
docker run --rm -i -e BASE_URL=http://host.docker.internal:8080 -v "$(pwd):/work" -w /work grafana/k6 run benchmark/k6/read-api-baseline.js
```

For the full methodology and comparison table template, see [Benchmark](benchmark.md).

---

## 🌐 Available services

Once the application is up and running, all services (API, UIs, and monitoring endpoints) become available.

👉 See [Services](services.md) for the full list of accessible endpoints and tools.

---

## 🔄 Reset databases

Use the run-compose-down.sh or run-compose-down.ps1 script.

Then restart the app.

See [Startup scripts section](#startup-scripts) above for details.

---

## 🧠 Notes

* The selected compose override sets the datasource automatically (`compose.postgres.yaml` -> Postgres, `compose.mongo.yaml` -> Mongo) over shared `compose.base.yaml`
* No manual configuration required
* The runtime `api-server` container does not mount `${HOME}/.m2`
* On PostgreSQL runs, relational schema is managed by Flyway migrations (`src/main/resources/db/migration`)
* MongoDB schema/index evolution is managed by Mongock (`src/main/java/it/montano/multipersistencebackend/config/mongock`), including collection `$jsonSchema` validators
* Data is persisted using Docker volumes (removed only with `-v`)

---
