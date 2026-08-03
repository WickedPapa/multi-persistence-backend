for ($i = 1; $i -le 20; $i++) {
  Write-Host "Running Newman seed $i/20..."
  docker compose -f compose.base.yaml --profile test-newman run --rm newman
  if (-not $?) {
    throw "Newman run failed at iteration $i."
  }
}
