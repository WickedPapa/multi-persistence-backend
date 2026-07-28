#!/usr/bin/env bash
set -euo pipefail

# Startup helper for Linux/macOS Bash
# Uncomment the command you want to run.

# Start PostgreSQL stack (API + Postgres + pgAdmin)
#docker compose -f compose.base.yaml -f compose.postgres.yaml up -d --build

# Start PostgreSQL stack in debug mode (opens port 5005)
# docker compose -f compose.base.yaml -f compose.postgres.yaml -f compose.debug.yaml up -d --build

# Start MongoDB stack (API + Mongo + mongo-express)
# docker compose -f compose.base.yaml -f compose.mongo.yaml up -d --build

# Start MongoDB stack in debug mode (opens port 5005)
 docker compose -f compose.base.yaml -f compose.mongo.yaml -f compose.debug.yaml up -d --build
