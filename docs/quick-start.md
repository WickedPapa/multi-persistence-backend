
---

## 🚀 Prerequisites

* Docker
* Docker Compose

Install Docker Desktop (includes Docker Compose):

👉 https://docs.docker.com/get-docker/

---

## ▶️ How to run the application

To start the application, open a terminal in the **root of the project** and run **one of the following commands**, choosing based on:

- your operating system: **Linux** or **Windows**
- the datasource: **🐘 PostgreSQL (relational)** or **🍃 MongoDB (NoSQL)**

### Windows (PowerShell)
```bash
$env:HOME=$env:USERPROFILE; $env:APP_DATASOURCE="POSTGRES"; docker compose up -d --build
```
```bash
$env:HOME=$env:USERPROFILE; $env:APP_DATASOURCE="MONGODB"; docker compose up -d --build
```

### Linux
```bash
APP_DATASOURCE="POSTGRES" docker compose up -d --build
```

```bash
APP_DATASOURCE="MONGODB" docker compose up -d --build
```

## 🧪 Run API tests (automatic)

> ### ⚠️ Prerequisite
> The application must be already running and fully started before executing the tests

You can run all API tests (including minimal automatic data setup) using Newman via Docker.
```bash
docker compose --profile test-newman run --rm newman
```

---

## 🔄 Reset databases

```bash
docker compose down -v
```

Then restart the app

---

## 🧠 Notes

* The application switches database based on `APP_DATASOURCE`
* No manual configuration required
* Data is persisted using Docker volumes (removed only with `-v`)

---
