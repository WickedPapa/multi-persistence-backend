
---

## 🚀 Prerequisites

* Docker
* Docker Compose

Install Docker Desktop (includes Docker Compose):

👉 https://docs.docker.com/get-docker/

---

## ⚙️ Credentials

Credentials are defined in `.env` at the project root and injected into `compose.yaml` via `${VAR}` references (no inline defaults — if `.env` is missing, Compose fails explicitly).

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

To avoid long commands, use the root scripts:

### Windows (PowerShell)
```bash
./start-app.ps1
```

### Linux
(First time, if needed, run `chmod +x ./start-app.sh`)
```bash
./start-app.sh
```

The scripts contain commented commands for Postgres/Mongo and debug mode. Uncomment the one you need.

---

## 🧪 Run API tests (automatic)

> ### ⚠️ Prerequisite
> The application must be up and running before executing the tests.

You can run all API tests (including minimal automatic data setup) using Newman via Docker with the following command:
```bash
docker compose -f compose.base.yaml --profile test-newman run --rm newman
```

or use the run-tests.sh or run-tests.ps1 script.

See [Startup scripts section](#startup-scripts) above for details.

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
