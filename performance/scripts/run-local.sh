#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
REPOSITORY_ROOT=$(CDPATH= cd -- "$SCRIPT_DIR/../.." && pwd)
COMPOSE_FILE="$REPOSITORY_ROOT/performance/compose.perf.yaml"
RESULTS_DIR="$REPOSITORY_ROOT/performance/results"
MANIFEST_PATH="$RESULTS_DIR/manifest.json"

SCENARIO=${SCENARIO:-smoke}
PERF_SCALE=${PERF_SCALE:-1}
PERF_SEED=${PERF_SEED:-42}
PERF_VALIDATION=${PERF_VALIDATION:-false}
BASE_URL=${BASE_URL:-http://frontend}
API_BASE_URL=${API_BASE_URL:-http://frontend}
RESET=${RESET:-false}
SKIP_SEED=${SKIP_SEED:-false}
REUSE_STACK=${REUSE_STACK:-false}
PERF_BACKEND_PORT=${PERF_BACKEND_PORT:-18080}

assert_local_target() {
    name=$1
    value=$2
    host=$(printf '%s' "$value" | sed -nE 's#^https?://([^/:]+)(:[0-9]+)?(/.*)?$#\1#p')
    case "$host" in
        127.0.0.1|localhost|host.docker.internal|frontend|backend) ;;
        *) echo "$name must target localhost or the internal loco-perf Compose network" >&2; exit 2 ;;
    esac
}

compose() {
    docker compose -f "$COMPOSE_FILE" "$@"
}

assert_local_target BASE_URL "$BASE_URL"
assert_local_target API_BASE_URL "$API_BASE_URL"
mkdir -p "$RESULTS_DIR"

if [ "$RESET" = true ] && [ "$REUSE_STACK" = true ]; then
    echo "RESET and REUSE_STACK cannot be used together" >&2
    exit 2
fi

if [ "$RESET" = true ]; then
    echo "Removing only the dedicated loco-perf containers and volume..."
    compose down --volumes --remove-orphans
fi

if [ "$REUSE_STACK" != true ]; then
    compose up --build -d postgres backend frontend
fi

attempt=0
until curl --fail --silent "http://127.0.0.1:$PERF_BACKEND_PORT/actuator/health" | grep -q '"status":"UP"'; do
    attempt=$((attempt + 1))
    if [ "$attempt" -ge 60 ]; then
        compose logs --tail=150 backend
        echo "Performance backend did not become healthy" >&2
        exit 1
    fi
    sleep 1
done

if [ "$SKIP_SEED" != true ]; then
    (
        cd "$REPOSITORY_ROOT/backend"
        PERF_SCALE="$PERF_SCALE" \
        PERF_SEED="$PERF_SEED" \
        PERF_MANIFEST="$MANIFEST_PATH" \
        ./gradlew performanceSeed
    )
fi

if [ ! -f "$MANIFEST_PATH" ]; then
    echo "Performance manifest is missing: $MANIFEST_PATH" >&2
    exit 1
fi

run_scenario() {
    current_scenario=$1
    timestamp=$(date -u '+%Y%m%d-%H%M%S')
    validation_args=""
    if [ "$PERF_VALIDATION" = true ]; then
        validation_args="-e PERF_VALIDATION=true"
    fi
    echo "Running k6 scenario: $current_scenario"
    # shellcheck disable=SC2086
    compose --profile tools run --rm \
        -e "BASE_URL=$BASE_URL" \
        -e "API_BASE_URL=$API_BASE_URL" \
        -e PERF_MANIFEST=/results/manifest.json \
        $validation_args \
        k6 run \
        --summary-export "/results/$current_scenario-$timestamp.json" \
        "/scripts/$current_scenario.js"
}

if [ "$SCENARIO" = all ]; then
    for item in smoke rental-browse image-burst mixed-api stress; do
        run_scenario "$item"
    done
else
    run_scenario "$SCENARIO"
fi
