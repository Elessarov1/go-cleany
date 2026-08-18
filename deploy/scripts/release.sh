#!/usr/bin/env bash
set -Eeuo pipefail

script_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
root=$(cd -- "${script_dir}/../.." && pwd)
ref=${1:-main}

if [[ -n $(git -C "${root}" status --porcelain --untracked-files=no) ]]; then
  echo "Tracked files on the VPS contain local changes; release aborted." >&2
  exit 1
fi

git -C "${root}" fetch --prune --tags origin

if git -C "${root}" show-ref --verify --quiet "refs/remotes/origin/${ref}"; then
  if git -C "${root}" show-ref --verify --quiet "refs/heads/${ref}"; then
    git -C "${root}" switch "${ref}"
  else
    git -C "${root}" switch --track -c "${ref}" "origin/${ref}"
  fi
  git -C "${root}" merge --ff-only "origin/${ref}"
  if [[ $(git -C "${root}" rev-parse HEAD) != $(git -C "${root}" rev-parse "origin/${ref}") ]]; then
    echo "Local branch differs from origin/${ref}; release aborted." >&2
    exit 1
  fi
else
  git -C "${root}" rev-parse --verify "${ref}^{commit}" >/dev/null
  git -C "${root}" switch --detach "${ref}"
fi

exec "${root}/deploy/scripts/deploy.sh"
