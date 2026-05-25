#!/bin/bash
TOKEN=$(curl -s -X POST http://localhost:9900/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@evaltrack.com","password":"Admin@123"}' \
  | python3 -c 'import sys,json; print(json.load(sys.stdin)["data"]["token"])')

echo "=== GET /api/participants ==="
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:9900/api/participants

echo ""
echo "=== GET /api/enrollments ==="
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:9900/api/enrollments

echo ""
echo "=== GET /api/evaluation-results ==="
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:9900/api/evaluation-results

echo ""
echo "=== GET /api/reports/batch-reports ==="
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:9900/api/reports/batch-reports

echo ""
echo "=== REPORT-SERVICE direct (bypass gateway) ==="
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8086/api/reports/batch-reports
