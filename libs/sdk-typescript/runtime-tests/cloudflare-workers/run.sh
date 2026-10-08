#!/usr/bin/env bash
# Copyright Daytona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0

set -euo pipefail
rm -rf node_modules package-lock.json dist .wrangler
npm install --silent
npm install --silent "$API_CLIENT_TARBALL" "$TOOLBOX_API_CLIENT_TARBALL" "$SDK_TARBALL"

PORT=${RUNTIME_TEST_PORT:-3804}

cat > .dev.vars <<EOF
DAYTONA_API_KEY=$DAYTONA_API_KEY
DAYTONA_API_URL=$DAYTONA_API_URL
EOF

npx wrangler dev --local --port "$PORT" >/tmp/wrangler-runtime.log 2>&1 &
PID=$!
trap "kill -9 $PID 2>/dev/null || true; pkill -9 -f 'wrangler dev' 2>/dev/null || true; pkill -9 -f workerd 2>/dev/null || true; rm -f .dev.vars" EXIT

dump_wrangler_log() {
  echo "=== wrangler log (last 100 lines) ==="
  tail -100 /tmp/wrangler-runtime.log 2>/dev/null || echo "No wrangler log found"
}

# Readiness wait: wrangler shares the runner with the full E2E stack
# (api/runner/proxy/dashboard serves + docker infra) plus the cold npm
# installs of the preceding runtimes, so cold boot can be slow. Poll longer
# instead of failing fast; pass criteria below are unchanged.
READY=false
for i in $(seq 1 120); do
  if curl -sf "http://localhost:$PORT/" >/dev/null 2>&1; then READY=true; break; fi
  sleep 1
done
if [ "$READY" != "true" ]; then
  echo "FAIL: wrangler did not become ready within 120s"
  dump_wrangler_log
  exit 1
fi

if ! RESPONSE=$(curl -sf -m 30 "http://localhost:$PORT/"); then
  echo "FAIL: request to wrangler failed"
  dump_wrangler_log
  exit 1
fi
echo "Response: $RESPONSE"

echo "$RESPONSE" | grep -q '"imageOk":true' || { echo "FAIL: imageOk false"; dump_wrangler_log; exit 1; }
echo "$RESPONSE" | grep -q '"listOk":true' || { echo "FAIL: listOk false"; dump_wrangler_log; exit 1; }
echo "PASS"
