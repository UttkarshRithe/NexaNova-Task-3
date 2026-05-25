#!/bin/bash
TOKEN=$(curl -s -X POST http://localhost:9900/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@evaltrack.com","password":"Admin@123"}' \
  | python3 -c 'import sys,json; print(json.load(sys.stdin)["data"]["token"])')

echo "=== TRIGGERING participant-service directly ==="
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8084/api/participants
sleep 1

echo ""
echo "=== participant-service fresh logs ==="
docker logs --tail=30 participant-service 2>&1 | grep -E "ERROR|WARN.*Exception|Caused by|MethodNotAllowed|relation|table" | head -20

echo ""
echo "=== TRIGGERING evaluation-results directly ==="
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8085/api/evaluation-results
sleep 1

echo ""
echo "=== evaluation-service fresh logs ==="
docker logs --tail=30 evaluation-service 2>&1 | grep -E "ERROR|WARN.*Exception|Caused by" | head -20

echo ""
echo "=== report-service routes available ==="
curl -s http://localhost:8086/actuator/mappings 2>/dev/null | python3 -c "import sys,json; d=json.load(sys.stdin); [print(k) for k in d.get('contexts',{}).get('application',{}).get('mappings',{}).get('dispatcherServlets',{}).get('dispatcherServlet',[{} ])[0].get('details',{}).get('handlerMethods',{}).keys()]" 2>/dev/null || echo "actuator not available"

echo ""
echo "=== report-service controllers ==="
docker logs report-service 2>&1 | grep -i "Mapped\|mapping\|Controller" | head -20
