#!/usr/bin/env bash
set -euo pipefail

# Startup helper for Linux/macOS Bash — MongoDB stack

# Start MongoDB stack (API + Mongo + mongo-express)
docker compose -f compose.base.yaml -f compose.mongo.yaml up -d --build

# Start MongoDB stack in debug mode (opens port 5005)
# docker compose -f compose.base.yaml -f compose.mongo.yaml -f compose.debug.yaml up -d --build
