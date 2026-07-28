#!/usr/bin/env bash
set -euo pipefail

docker compose \
  -f compose.base.yaml \
  -f compose.postgres.yaml \
  -f compose.mongo.yaml \
  -f compose.debug.yaml \
  down -v --remove-orphans
