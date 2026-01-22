#!/bin/bash

clear

# ---------------- Resolve paths ----------------
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$ROOT" || exit 1

echo "=============================================================="
echo " 🛍️  ShopVerse – FAST HYBRID START (SMART REBUILD)"
echo "=============================================================="
echo ""

LOG_DIR="$ROOT/logs"
mkdir -p "$LOG_DIR"

# ---------------- Health check ----------------
wait_for_health () {
  local NAME=$1
  local URL=$2

  for i in {1..120}; do
    if curl -s "$URL" | grep -q '"status":"UP"'; then
      echo "✅ $NAME → RUNNING"
      return 0
    fi
    sleep 1
  done

  echo "⚠️ $NAME → STARTED (no/slow health)"
  return 0
}

# ---------------- Hash-based rebuild check ----------------
needs_rebuild () {
  local SERVICE_DIR=$1
  local HASH_FILE="$SERVICE_DIR/.build-hash"

  if [ ! -d "$SERVICE_DIR/src" ]; then
    return 1
  fi

  NEW_HASH=$(find "$SERVICE_DIR/src" -type f -print0 \
    | sort -z \
    | xargs -0 sha256sum \
    | sha256sum | awk '{print $1}')

  if [ -f "$HASH_FILE" ]; then
    OLD_HASH=$(cat "$HASH_FILE")
  else
    OLD_HASH=""
  fi

  if [ "$NEW_HASH" != "$OLD_HASH" ]; then
    echo "$NEW_HASH" > "$HASH_FILE"
    return 0   # rebuild needed
  fi

  return 1     # no rebuild
}

# ---------------- Start Docker infra ----------------
echo "🐳 Starting Docker infrastructure..."
docker compose up -d kafka zookeeper mysql redis mongodb grafana prometheus zipkin >/dev/null 2>&1
sleep 5

echo "✅ Docker infrastructure ready"
echo ""
echo "📦 Running Docker Containers"
echo "--------------------------------------------------------------"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""

# ==============================================================
# 1️⃣ START DISCOVERY SERVICE (SEQUENTIAL)
# ==============================================================
DISCOVERY_NAME="discovery-service"
DISCOVERY_PORT=8761
DISCOVERY_DIR="$ROOT/$DISCOVERY_NAME"
DISCOVERY_JAR_DIR="$DISCOVERY_DIR/build/libs"

echo "▶️ Preparing discovery-service..."

if needs_rebuild "$DISCOVERY_DIR" || [ ! -f "$DISCOVERY_JAR_DIR"/*.jar ]; then
  echo "🔨 Rebuilding discovery-service (changes detected)"
  cd "$DISCOVERY_DIR" || exit 1
  chmod +x ./gradlew
  ./gradlew bootJar -x test --rerun-tasks >/dev/null 2>&1
  cd "$ROOT"
else
  echo "✅ discovery-service → no changes, using existing JAR"
fi

DISCOVERY_JAR=$(ls "$DISCOVERY_JAR_DIR"/*.jar | head -n 1)

echo "▶️ Starting discovery-service..."
java -Xmx256m -Xms256m \
  -Dspring.profiles.active=dev \
  -jar "$DISCOVERY_JAR" \
  > "$LOG_DIR/discovery-service.log" 2>&1 &

echo "⏳ Waiting for discovery-service to be HEALTHY..."
wait_for_health "discovery-service" "http://localhost:$DISCOVERY_PORT/actuator/health"

echo ""
echo "🚀 Discovery is UP — starting remaining services in PARALLEL"
echo ""

# ==============================================================
# 2️⃣ START OTHER SERVICES (PARALLEL)
# ==============================================================
SERVICES=(
  "auth-service:8081"
  "user:8082"
  "product:8083"
  "order-Service:8084"
  "payment-service:8085"
  "notification:8086"
  "analytics:8087"
  "recommendation:8088"
  "admin-server:8079"
  "gateway:8080"
)

declare -A SERVICE_PORTS

for ENTRY in "${SERVICES[@]}"; do
  NAME="${ENTRY%%:*}"
  PORT="${ENTRY##*:}"
  SERVICE_DIR="$ROOT/$NAME"
  JAR_DIR="$SERVICE_DIR/build/libs"

  echo "▶️ Preparing $NAME..."

  if needs_rebuild "$SERVICE_DIR" || [ ! -f "$JAR_DIR"/*.jar ]; then
    echo "🔨 Rebuilding $NAME (changes detected)"
    cd "$SERVICE_DIR" || continue
    chmod +x ./gradlew
    ./gradlew bootJar -x test --rerun-tasks >/dev/null 2>&1
    cd "$ROOT"
  else
    echo "✅ $NAME → no changes, using existing JAR"
  fi

  JAR=$(ls "$JAR_DIR"/*.jar 2>/dev/null | head -n 1)

  if [ ! -f "$JAR" ]; then
    echo "⚠️ $NAME → NO BOOTABLE JAR (skipping)"
    continue
  fi

  echo "▶️ Starting $NAME..."
  java -Xmx384m -Xms256m \
    -Dspring.profiles.active=dev \
    -jar "$JAR" \
    > "$LOG_DIR/$NAME.log" 2>&1 &

  SERVICE_PORTS[$NAME]=$PORT
done

echo ""
echo "⏳ Verifying service health..."
echo ""

for NAME in "${!SERVICE_PORTS[@]}"; do
  PORT="${SERVICE_PORTS[$NAME]}"
  wait_for_health "$NAME" "http://localhost:$PORT/actuator/health" &
done

wait

# ==============================================================
# DONE
# ==============================================================
echo ""
echo "=============================================================="
echo " ✅ ALL SERVICES STARTED (SMART MODE)"
echo "=============================================================="
echo ""
echo "🔍 Eureka Dashboard : http://localhost:8761"
echo "🚪 API Gateway      : http://localhost:8080"
echo ""
echo "📈 Tool	URL"
echo "🔍 Prometheus	http://localhost:9090"
echo "📈 Grafana	http://localhost:3000"
echo "🔑 Grafana Login	admin / admin"
echo ""
echo "   In Grafana → Import Dashboard"
echo "   Use these IDs:"
echo "   Purpose Dashboard    ID"
echo "   Spring Boot	       4701"
echo "   JVM Metrics	       4701"
echo "   Kafka	             721"
echo "   Docker	             893"
echo ""
echo ""
echo "📁 Logs directory   : logs/"
echo "📄 Example          : tail -f logs/gateway.log"
echo ""
echo "🛑 Stop services    : CTRL + C"

wait
