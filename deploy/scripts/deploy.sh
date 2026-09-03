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

requested_telegram_mini_app_link_base=${TELEGRAM_MINI_APP_LINK_BASE:-}
if [[ -n ${requested_telegram_mini_app_link_base} ]]; then
  upsert_env_value \
    "${env_file}" \
    TELEGRAM_MINI_APP_LINK_BASE \
    "${requested_telegram_mini_app_link_base}"
fi

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

docker_prune_enabled=${GO_CLEANY_DOCKER_PRUNE_ENABLED:-}
if [[ -z ${docker_prune_enabled} ]]; then
  docker_prune_enabled=$(read_env_value "${env_file}" GO_CLEANY_DOCKER_PRUNE_ENABLED)
fi
docker_prune_enabled=${docker_prune_enabled:-true}
if [[ ${docker_prune_enabled} != true && ${docker_prune_enabled} != false ]]; then
  echo "GO_CLEANY_DOCKER_PRUNE_ENABLED must be true or false." >&2
  exit 1
fi

docker_build_cache_retention=${GO_CLEANY_DOCKER_BUILD_CACHE_RETENTION:-}
if [[ -z ${docker_build_cache_retention} ]]; then
  docker_build_cache_retention=$(read_env_value "${env_file}" GO_CLEANY_DOCKER_BUILD_CACHE_RETENTION)
fi
docker_build_cache_retention=${docker_build_cache_retention:-24h}
if [[ ! ${docker_build_cache_retention} =~ ^[1-9][0-9]*(m|h)$ ]]; then
  echo "GO_CLEANY_DOCKER_BUILD_CACHE_RETENTION must be a positive duration such as 30m or 24h." >&2
  exit 1
fi

prune_old_build_cache() {
  if [[ ${docker_prune_enabled} != true ]]; then
    echo "Docker build cache cleanup is disabled."
    return
  fi

  echo "Pruning unused Docker build cache older than ${docker_build_cache_retention}..."
  if ! docker builder prune \
    --all \
    --force \
    --filter "until=${docker_build_cache_retention}" | tail -n 1; then
    echo "Warning: Docker build cache cleanup failed; deployment will continue." >&2
  fi
}

prune_dangling_images() {
  if [[ ${docker_prune_enabled} != true ]]; then
    return
  fi

  echo "Pruning dangling Docker images after successful deployment..."
  if ! docker image prune --force | tail -n 1; then
    echo "Warning: dangling Docker image cleanup failed; deployed services remain available." >&2
  fi
}

echo "Validating production configuration..."
"${COMPOSE[@]}" config --quiet

prune_old_build_cache

echo "Building application images..."
"${COMPOSE[@]}" build --pull

if "${COMPOSE[@]}" ps --status running --services | grep -qx postgres; then
  echo "Creating a pre-deployment database backup..."
  "${script_dir}/backup.sh"
fi

revision=$(git -C "${root}" rev-parse HEAD)

echo "Starting revision ${revision}..."
if ! "${COMPOSE[@]}" up -d --remove-orphans --wait --wait-timeout 240; then
  echo "Deployment failed while waiting for containers. Current compose state:" >&2
  "${COMPOSE[@]}" ps >&2 || true
  echo "Recent backend logs:" >&2
  "${COMPOSE[@]}" logs --no-color --tail=250 backend >&2 || true
  echo "Recent Caddy logs:" >&2
  "${COMPOSE[@]}" logs --no-color --tail=250 caddy >&2 || true
  exit 1
fi

echo "Checking public HTTPS endpoint..."
if ! curl --fail --silent --show-error \
  --retry 20 --retry-delay 3 --retry-all-errors \
  "https://${app_host}/healthz" >/dev/null; then
  echo "Public health check failed. Current compose state:" >&2
  "${COMPOSE[@]}" ps >&2 || true
  echo "Recent backend logs:" >&2
  "${COMPOSE[@]}" logs --no-color --tail=250 backend >&2 || true
  echo "Recent Caddy logs:" >&2
  "${COMPOSE[@]}" logs --no-color --tail=250 caddy >&2 || true
  exit 1
fi

state_dir="${root}/.deploy-state"
if [[ -s ${state_dir}/current-revision ]]; then
  cp -- "${state_dir}/current-revision" "${state_dir}/previous-revision"
fi
printf '%s\n' "${revision}" > "${state_dir}/current-revision"

prune_dangling_images

echo "go-cleany ${revision} is available at https://${app_host}"
