#!/usr/bin/env bash
set -Eeuo pipefail

script_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
# shellcheck source=lib.sh
source "${script_dir}/lib.sh"

root=$(deployment_root)
env_file=$(production_env_file "${root}")
require_command docker
require_command curl
require_command flock
require_command git
require_production_env "${env_file}"
compose_command "${root}" "${env_file}"

mkdir -p -- "${root}/.deploy-state"
exec 9>"${root}/.deploy-state/deploy.lock"
if ! flock -n 9; then
  echo "Another go-cleany deployment is already running." >&2
  exit 1
fi

channel_env_overrides="${root}/.env.production.channels.next"
if [[ -f ${channel_env_overrides} ]]; then
  require_command awk
  bash "${script_dir}/merge-production-env.sh" "${env_file}" "${channel_env_overrides}"
  require_production_env "${env_file}"
fi

app_host=$(read_env_value "${env_file}" APP_HOST)
if [[ -z ${app_host} || ${app_host} == *://* || ${app_host} == */* ]]; then
  echo "APP_HOST must contain only a hostname, without protocol or path." >&2
  exit 1
fi

echo "Validating production configuration..."
"${COMPOSE[@]}" config --quiet

echo "Building application images..."
"${COMPOSE[@]}" build --pull

if "${COMPOSE[@]}" ps --status running --services | grep -qx postgres; then
  echo "Creating a pre-deployment database backup..."
  "${script_dir}/backup.sh"
fi

revision=$(git -C "${root}" rev-parse HEAD)

echo "Starting revision ${revision}..."
"${COMPOSE[@]}" up -d --remove-orphans --wait --wait-timeout 240

echo "Checking public HTTPS endpoint..."
curl --fail --silent --show-error \
  --retry 20 --retry-delay 3 --retry-all-errors \
  "https://${app_host}/healthz" >/dev/null

state_dir="${root}/.deploy-state"
if [[ -s ${state_dir}/current-revision ]]; then
  cp -- "${state_dir}/current-revision" "${state_dir}/previous-revision"
fi
printf '%s\n' "${revision}" > "${state_dir}/current-revision"

echo "go-cleany ${revision} is available at https://${app_host}"
