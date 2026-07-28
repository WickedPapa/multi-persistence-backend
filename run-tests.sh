#!/usr/bin/env bash
set -euo pipefail

docker compose -f compose.base.yaml --profile test-newman run --rm newman
