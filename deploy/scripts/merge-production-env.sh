#!/usr/bin/env bash
set -Eeuo pipefail

base_file=${1:?Usage: merge-production-env.sh BASE_FILE OVERRIDES_FILE}
overrides_file=${2:?Usage: merge-production-env.sh BASE_FILE OVERRIDES_FILE}

if [[ ! -f ${base_file} ]]; then
  echo "Production environment file not found: ${base_file}" >&2
  exit 1
fi
if [[ ! -s ${overrides_file} ]]; then
  echo "GitHub Environment overrides file is missing or empty: ${overrides_file}" >&2
  exit 1
fi

temp_file=$(mktemp "${base_file}.XXXXXX")
trap 'rm -f -- "${temp_file}"' EXIT

awk '
  function env_key(line, key) {
    key = line
    sub(/^[[:space:]]*/, "", key)
    sub(/=.*/, "", key)
    if (key ~ /^[A-Za-z_][A-Za-z0-9_]*$/) {
      return key
    }
    return ""
  }
  NR == FNR {
    key = env_key($0)
    if (key != "") {
      overrides[key] = $0
      order[++count] = key
    }
    next
  }
  {
    key = env_key($0)
    if (key != "" && key in overrides) {
      print overrides[key]
      delete overrides[key]
    } else {
      print
    }
  }
  END {
    for (position = 1; position <= count; position++) {
      key = order[position]
      if (key in overrides) {
        print overrides[key]
        delete overrides[key]
      }
    }
  }
' "${overrides_file}" "${base_file}" > "${temp_file}"

chmod 600 "${temp_file}"
mv -f -- "${temp_file}" "${base_file}"
rm -f -- "${overrides_file}"
trap - EXIT
