#!/bin/bash
TOKEN=$(curl -s -X POST http://localhost:9900/api/auth/login -H 'Content-Type: application/json' -d '{"email":"admin@evaltrack.com","password":"Admin@123"}' | python3 -c 'import sys,json; print(json.load(sys.stdin)["data"]["token"])')

echo "=== VERBOSE GET /api/participants (Port 8084) ==="
curl -v -H "Authorization: Bearer $TOKEN" http://localhost:8084/api/participants
