#!/usr/bin/env bash
set -Eeuo pipefail

script_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
# shellcheck source=lib.sh
source "${script_dir}/lib.sh"

root=$(deployment_root)
env_file=$(production_env_file "${root}")
require_command docker
require_production_env "${env_file}"
compose_command "${root}" "${env_file}"

if ! "${COMPOSE[@]}" ps --status running --services | grep -qx postgres; then
  echo "PostgreSQL container is not running; backup was not created." >&2
  exit 1
fi

backup_dir=${GO_CLEANY_BACKUP_DIR:-${root}/backups}
mkdir -p -- "${backup_dir}"
chmod 700 "${backup_dir}"
umask 077

timestamp=$(date -u +'%Y%m%dT%H%M%SZ')
revision=$(git -C "${root}" rev-parse --short HEAD 2>/dev/null || printf 'unknown')
backup_file="${backup_dir}/go-cleany-${timestamp}-${revision}.dump"
partial_file="${backup_file}.partial"

cleanup() {
  rm -f -- "${partial_file}"
}
trap cleanup EXIT

"${COMPOSE[@]}" exec -T postgres sh -c \
  'exec pg_dump --format=custom --compress=6 --no-owner --no-privileges -U "$POSTGRES_USER" -d "$POSTGRES_DB"' \
  > "${partial_file}"

if [[ ! -s ${partial_file} ]]; then
  echo "PostgreSQL returned an empty backup." >&2
  exit 1
fi

mv -- "${partial_file}" "${backup_file}"
trap - EXIT
echo "Backup created: ${backup_file}"
