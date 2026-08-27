#!/usr/bin/env bash
set -Eeuo pipefail

script_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
# shellcheck source=lib.sh
source "${script_dir}/lib.sh"

root=$(deployment_root)
env_file=$(production_env_file "${root}")
app_host=${1:-}

if [[ -z ${app_host} || ${app_host} == *://* || ${app_host} == */* || ! ${app_host} =~ ^[A-Za-z0-9]([A-Za-z0-9.-]*[A-Za-z0-9])?$ ]]; then
  echo "APP_HOST must contain only a valid hostname, without protocol or path." >&2
  exit 1
fi

require_production_env "${env_file}"

current_host=$(read_env_value "${env_file}" APP_HOST)
if [[ ${current_host} == "${app_host}" ]]; then
  chmod 600 "${env_file}"
  echo "APP_HOST already uses ${app_host}."
  exit 0
fi

timestamp=$(date -u +'%Y%m%dT%H%M%SZ')
backup_file="${env_file}.backup-${timestamp}"
temporary_file=$(mktemp "${env_file}.tmp.XXXXXX")

cleanup() {
  rm -f -- "${temporary_file}"
}
trap cleanup EXIT

umask 077
cp --preserve=mode -- "${env_file}" "${backup_file}"
chmod 600 "${backup_file}"

awk -v app_host="${app_host}" '
  BEGIN { updated = 0 }
  /^APP_HOST=/ {
    if (!updated) {
      print "APP_HOST=" app_host
      updated = 1
    }
    next
  }
  { print }
  END {
    if (!updated) {
      print "APP_HOST=" app_host
    }
  }
' "${env_file}" > "${temporary_file}"

chmod 600 "${temporary_file}"
mv -- "${temporary_file}" "${env_file}"
trap - EXIT

echo "APP_HOST changed from ${current_host:-<unset>} to ${app_host}."
echo "Previous production environment saved with mode 600 at ${backup_file}."
