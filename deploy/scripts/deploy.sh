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

requested_app_host=${APP_HOST:-}
if [[ -n ${requested_app_host} ]]; then
  "${script_dir}/configure-app-host.sh" "${requested_app_host}"
fi

require_production_env "${env_file}"
compose_command "${root}" "${env_file}"

mkdir -p -- "${root}/.deploy-state"
exec 9>"${root}/.deploy-state/deploy.lock"
if ! flock -n 9; then
  echo "Another go-cleany deployment is already running." >&2
  exit 1
fi

app_host=$(read_env_value "${env_file}" APP_HOST)
if [[ -z ${app_host} || ${app_host} == *://* || ${app_host} == */* ]]; then
  echo "APP_HOST must contain only a hostname, without protocol or path." >&2
  exit 1
fi

google_auth_enabled=${GOOGLE_AUTH_ENABLED:-}
if [[ -z ${google_auth_enabled} ]]; then
  google_auth_enabled=$(read_env_value "${env_file}" GOOGLE_AUTH_ENABLED)
fi
google_auth_enabled=${google_auth_enabled:-false}
if [[ ${google_auth_enabled} != true && ${google_auth_enabled} != false ]]; then
  echo "GOOGLE_AUTH_ENABLED must be true or false." >&2
  exit 1
fi
if [[ ${google_auth_enabled} == true ]]; then
  for required_google_setting in GOOGLE_CLIENT_ID GOOGLE_CLIENT_SECRET ADMIN_GOOGLE_EMAILS; do
    required_google_value=${!required_google_setting:-}
    if [[ -z ${required_google_value} ]]; then
      required_google_value=$(read_env_value "${env_file}" "${required_google_setting}")
    fi
    if [[ -z ${required_google_value} ]]; then
      echo "${required_google_setting} is required when GOOGLE_AUTH_ENABLED=true." >&2
      exit 1
    fi
  done
fi

web_session_timeout=${WEB_SESSION_TIMEOUT:-}
if [[ -z ${web_session_timeout} ]]; then
  web_session_timeout=$(read_env_value "${env_file}" WEB_SESSION_TIMEOUT)
fi
if [[ -n ${web_session_timeout} && ! ${web_session_timeout} =~ ^[1-9][0-9]*(ms|s|m|h|d)$ ]]; then
  echo "WEB_SESSION_TIMEOUT must be a positive duration such as 30m or 12h." >&2
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
