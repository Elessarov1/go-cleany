#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
REPOSITORY_ROOT=$(CDPATH= cd -- "$SCRIPT_DIR/../.." && pwd)
COMPOSE_FILE="$REPOSITORY_ROOT/performance/compose.perf.yaml"
RESULTS_DIR="$REPOSITORY_ROOT/performance/results"

NAME=${NAME:-profile}
DURATION_SECONDS=${DURATION_SECONDS:-30}

case "$NAME" in
    *[!a-zA-Z0-9-]*) echo "NAME may contain only letters, numbers and hyphens" >&2; exit 2 ;;
esac
case "$DURATION_SECONDS" in
    *[!0-9]*) echo "DURATION_SECONDS must be an integer" >&2; exit 2 ;;
esac
if [ "$DURATION_SECONDS" -lt 5 ] || [ "$DURATION_SECONDS" -gt 3600 ]; then
    echo "DURATION_SECONDS must be between 5 and 3600" >&2
    exit 2
fi

mkdir -p "$RESULTS_DIR"
timestamp=$(date -u '+%Y%m%d-%H%M%S')
file_name="$NAME-$timestamp.jfr"
container_path="/tmp/$file_name"
recording_name="loco-$NAME-$timestamp"
container_id=$(docker compose -f "$COMPOSE_FILE" ps -q backend)
if [ -z "$container_id" ]; then
    echo "The loco-perf backend container is not running" >&2
    exit 1
fi

recording_started=false
cleanup() {
    if [ "$recording_started" = true ]; then
        docker compose -f "$COMPOSE_FILE" exec -T backend \
            jcmd 1 JFR.stop "name=$recording_name" >/dev/null 2>&1 || true
    fi
}
trap cleanup EXIT INT TERM

# Corretto 25 on the Docker Desktop overlay filesystem cannot copy a disk-backed
# recording. An in-memory recording followed by an explicit stop is portable.
docker compose -f "$COMPOSE_FILE" exec -T backend \
    jcmd 1 JFR.start "name=$recording_name" settings=profile disk=false
recording_started=true
sleep "$DURATION_SECONDS"
docker compose -f "$COMPOSE_FILE" exec -T backend \
    jcmd 1 JFR.stop "name=$recording_name" "filename=$container_path"
recording_started=false
docker compose -f "$COMPOSE_FILE" exec -T backend jfr summary "$container_path"
docker cp "$container_id:$container_path" "$RESULTS_DIR/$file_name"
echo "JFR recording saved to $RESULTS_DIR/$file_name"
