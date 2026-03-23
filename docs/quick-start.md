
---

## 🚀 Prerequisites

* Docker
* Docker Compose

Install Docker Desktop (includes Docker Compose):

👉 https://docs.docker.com/get-docker/

---

## ▶️ Run the application

### 🐘 PostgreSQL (relational) (default)

#### ![Linux](https://img.shields.io/badge/Linux-FCC624?logo=linux&logoColor=black)
```bash
APP_DATASOURCE=POSTGRES docker compose up -d --build
```
#### ![Windows PowerShell](https://img.shields.io/badge/Windows-PowerShell-0078D6?logo=powershell&logoColor=white)
```bash
$env:APP_DATASOURCE="POSTGRES"; docker compose up -d --build
```

### 🍃 MongoDB (NoSQL)

#### ![Linux](https://img.shields.io/badge/Linux-FCC624?logo=linux&logoColor=black)
```bash
APP_DATASOURCE=MONGODB docker compose up -d --build
```

#### ![Windows PowerShell](https://img.shields.io/badge/Windows-PowerShell-0078D6?logo=powershell&logoColor=white)
```bash
$env:APP_DATASOURCE="MONGODB"; docker compose up -d --build
```

---

## 🧪 Run API tests (automatic)

You can run all API tests (including minimal automatic data setup) using Newman via Docker.
```bash
docker compose --profile test-newman run --rm newman
```

### ⚠️ Prerequisite
The application must be already running and fully started before executing the tests

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
