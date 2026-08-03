#!/usr/bin/env bash
set -euo pipefail

for i in $(seq 1 20); do
  echo "Running Newman seed ${i}/20..."
  docker compose -f compose.base.yaml --profile test-newman run --rm newman
done
