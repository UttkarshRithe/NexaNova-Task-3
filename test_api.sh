#!/bin/bash
TOKEN=$(curl -s -X POST http://localhost:9900/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@evaltrack.com","password":"Admin@123"}' \
  | python3 -c 'import sys,json; print(json.load(sys.stdin)["data"]["token"])')

echo "TOKEN obtained: ${TOKEN:0:20}..."
echo ""

test_endpoint() {
  local label=$1
  local url=$2
  local code=$(curl -s -o /dev/null -w '%{http_code}' -H "Authorization: Bearer $TOKEN" "$url")
  echo "$label -> HTTP $code"
}

test_endpoint "GET /api/batches"                  "http://localhost:9900/api/batches"
test_endpoint "GET /api/technologies"             "http://localhost:9900/api/technologies"
test_endpoint "GET /api/participants"             "http://localhost:9900/api/participants"
test_endpoint "GET /api/users"                    "http://localhost:9900/api/users"
test_endpoint "GET /api/enrollments"              "http://localhost:9900/api/enrollments"
test_endpoint "GET /api/evaluation-assignments"   "http://localhost:9900/api/evaluation-assignments"
test_endpoint "GET /api/evaluation-results"       "http://localhost:9900/api/evaluation-results"
test_endpoint "GET /api/reports"                  "http://localhost:9900/api/reports"
test_endpoint "GET /api/reports/batch-reports"    "http://localhost:9900/api/reports/batch-reports"
test_endpoint "GET /api/reports/participant-reports" "http://localhost:9900/api/reports/participant-reports"
