#!/usr/bin/env bash
set -Eeuo pipefail

target=${1:?Usage: render-channel-env-overrides.sh TARGET_FILE}

keys=(
  TELEGRAM_BOT_TOKEN
  CLEANER_TELEGRAM_IDS
  ADMIN_TELEGRAM_IDS
  WHATSAPP_ENABLED
  WHATSAPP_GRAPH_API_BASE_URL
  WHATSAPP_GRAPH_API_VERSION
  WHATSAPP_APP_ID
  WHATSAPP_BUSINESS_PORTFOLIO_ID
  WHATSAPP_BUSINESS_ACCOUNT_ID
  WHATSAPP_PHONE_NUMBER_ID
  WHATSAPP_ACCESS_TOKEN
  WHATSAPP_SYSTEM_USER_ID
  WHATSAPP_ACCESS_TOKEN_TYPE
  WHATSAPP_ACCESS_TOKEN_EXPIRES_AT
  WHATSAPP_APP_SECRET
  WHATSAPP_WEBHOOK_VERIFY_TOKEN
  WHATSAPP_TEST_REPLY_ENABLED
)

if [[ ${WHATSAPP_ENABLED:-} == true ]]; then
  required_whatsapp=(
    WHATSAPP_APP_ID
    WHATSAPP_BUSINESS_PORTFOLIO_ID
    WHATSAPP_BUSINESS_ACCOUNT_ID
    WHATSAPP_PHONE_NUMBER_ID
    WHATSAPP_ACCESS_TOKEN
    WHATSAPP_SYSTEM_USER_ID
    WHATSAPP_APP_SECRET
    WHATSAPP_WEBHOOK_VERIFY_TOKEN
  )
  for key in "${required_whatsapp[@]}"; do
    if [[ -z ${!key:-} || ${!key} == *CHANGE_ME* ]]; then
      echo "Missing required GitHub Environment value: ${key}" >&2
      exit 1
    fi
  done
fi

for key in WHATSAPP_ENABLED WHATSAPP_TEST_REPLY_ENABLED; do
  value=${!key:-}
  if [[ -n ${value} && ${value} != true && ${value} != false ]]; then
    echo "${key} must be true or false." >&2
    exit 1
  fi
done

for key in CLEANER_TELEGRAM_IDS ADMIN_TELEGRAM_IDS; do
  value=${!key:-}
  if [[ -n ${value} && ! ${value} =~ ^[0-9]+(,[0-9]+)*$ ]]; then
    echo "${key} must be comma-separated numeric Telegram IDs without spaces." >&2
    exit 1
  fi
done

target_dir=$(dirname -- "${target}")
mkdir -p -- "${target_dir}"
temp_file=$(mktemp "${target}.XXXXXX")
trap 'rm -f -- "${temp_file}"' EXIT
chmod 600 "${temp_file}"

written=0
for key in "${keys[@]}"; do
  value=${!key:-}
  if [[ -z ${value} ]]; then
    continue
  fi
  if [[ ${value} == *CHANGE_ME* || ${value} == *$'\n'* || ${value} == *$'\r'* || ${value} == *"'"* ]]; then
    echo "${key} contains a placeholder or characters that cannot be represented safely." >&2
    exit 1
  fi
  printf "%s='%s'\n" "${key}" "${value}" >> "${temp_file}"
  written=$((written + 1))
done

if ((written == 0)); then
  echo "GitHub Environment did not provide any channel overrides." >&2
  exit 1
fi

mv -f -- "${temp_file}" "${target}"
trap - EXIT
