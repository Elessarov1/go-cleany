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

configured_backup_dir=$(read_env_value "${env_file}" GO_CLEANY_BACKUP_DIR)
backup_dir=${GO_CLEANY_BACKUP_DIR:-${configured_backup_dir:-${root}/backups}}
if [[ ${backup_dir} != /* ]]; then
  backup_dir="${root}/${backup_dir}"
fi
mkdir -p -- "${backup_dir}"
backup_dir=$(cd -- "${backup_dir}" && pwd -P)
if [[ ${backup_dir} == / ]]; then
  echo "GO_CLEANY_BACKUP_DIR must not be the filesystem root." >&2
  exit 1
fi
chmod 700 "${backup_dir}"
umask 077

configured_retention_days=$(read_env_value "${env_file}" GO_CLEANY_BACKUP_RETENTION_DAYS)
retention_days=${GO_CLEANY_BACKUP_RETENTION_DAYS:-${configured_retention_days:-7}}
if [[ ! ${retention_days} =~ ^[1-9][0-9]*$ ]]; then
  echo "GO_CLEANY_BACKUP_RETENTION_DAYS must be a positive integer." >&2
  exit 1
fi
retention_minutes=$((10#${retention_days} * 24 * 60))

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

pruned_count=0
while IFS= read -r -d '' candidate; do
  if [[ ${candidate} == "${backup_file}" ]]; then
    continue
  fi
  rm -f -- "${candidate}"
  ((pruned_count += 1))
done < <(
  find "${backup_dir}" -maxdepth 1 -type f \
    -name 'go-cleany-*.dump' \
    -mmin "+${retention_minutes}" \
    -print0
)

echo "Backup retention: removed ${pruned_count} dump file(s) older than ${retention_days} day(s)."
