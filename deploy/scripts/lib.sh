#!/usr/bin/env bash

deployment_root() {
  cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd
}

production_env_file() {
  local root=$1
  printf '%s\n' "${GO_CLEANY_ENV_FILE:-${root}/.env.production}"
}

compose_command() {
  local root=$1
  local env_file=$2
  COMPOSE=(docker compose --project-directory "${root}" --env-file "${env_file}" -f "${root}/compose.prod.yaml")
}

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Required command is missing: $1" >&2
    exit 1
  fi
}

require_production_env() {
  local env_file=$1
  if [[ ! -f ${env_file} ]]; then
    echo "Production environment file not found: ${env_file}" >&2
    echo "Copy .env.production.example to .env.production and fill every CHANGE_ME value." >&2
    exit 1
  fi
  if grep -q 'CHANGE_ME' "${env_file}"; then
    echo "Production environment still contains CHANGE_ME placeholders: ${env_file}" >&2
    exit 1
  fi
}

read_env_value() {
  local env_file=$1
  local key=$2
  local line
  line=$(grep -E "^[[:space:]]*${key}=" "${env_file}" | tail -n 1 || true)
  line=${line#*=}
  line=${line%$'\r'}
  if [[ ${line} == \"*\" && ${line} == *\" ]]; then
    line=${line:1:${#line}-2}
  elif [[ ${line} == \'*\' && ${line} == *\' ]]; then
    line=${line:1:${#line}-2}
  fi
  printf '%s\n' "${line}"
}
