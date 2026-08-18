#!/usr/bin/env bash
set -Eeuo pipefail

script_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
# shellcheck source=lib.sh
source "${script_dir}/lib.sh"

root=$(deployment_root)
env_file=$(production_env_file "${root}")
require_command curl
require_command docker
require_production_env "${env_file}"
compose_command "${root}" "${env_file}"

"${COMPOSE[@]}" ps
app_host=$(read_env_value "${env_file}" APP_HOST)
echo
curl --fail --silent --show-error "https://${app_host}/healthz"
echo
