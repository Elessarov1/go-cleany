#!/usr/bin/env bash
set -Eeuo pipefail

script_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
root=$(cd -- "${script_dir}/../.." && pwd)
state_file="${root}/.deploy-state/previous-revision"
target=${1:-}

if [[ -z ${target} ]]; then
  if [[ ! -s ${state_file} ]]; then
    echo "Previous deployed revision is unknown. Pass a Git tag or commit explicitly." >&2
    exit 1
  fi
  target=$(<"${state_file}")
fi

echo "Rolling application code back to ${target}. Database migrations are not rolled back."
exec "${script_dir}/release.sh" "${target}"
