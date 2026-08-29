#!/usr/bin/env bash
set -Eeuo pipefail

script_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
# shellcheck source=lib.sh
source "${script_dir}/lib.sh"

readonly confirmation_text='RESET LOCO PLACE PRECOMMERCIAL DATA'
execute=false

usage() {
  echo "Usage: $0 [--execute]"
  echo "Without --execute, prints a read-only reset plan and record counts."
}

if [[ $# -gt 1 ]]; then
  usage >&2
  exit 2
fi
if [[ $# -eq 1 ]]; then
  if [[ $1 != '--execute' ]]; then
    usage >&2
    exit 2
  fi
  execute=true
fi

root=$(deployment_root)
env_file=$(production_env_file "${root}")
require_command docker
require_production_env "${env_file}"
compose_command "${root}" "${env_file}"

if ! "${COMPOSE[@]}" ps --status running --services | grep -qx postgres; then
  echo "PostgreSQL container is not running; reset preflight cannot continue." >&2
  exit 1
fi

database_name=$(read_env_value "${env_file}" POSTGRES_DB)
application_host=$(read_env_value "${env_file}" APP_HOST)
configured_reset_allowed=$(read_env_value "${env_file}" PRECOMMERCIAL_DATA_RESET_ALLOWED)
reset_allowed=${PRECOMMERCIAL_DATA_RESET_ALLOWED:-${configured_reset_allowed:-false}}

psql_query() {
  "${COMPOSE[@]}" exec -T postgres sh -c \
    'exec psql -X -v ON_ERROR_STOP=1 -At -F "|" -U "$POSTGRES_USER" -d "$POSTGRES_DB"'
}

counts_sql=$(cat <<'SQL'
select section, table_name, record_count
  from (
    values
      ('DELETE', 'acquisition_campaign_entry', (select count(*) from acquisition_campaign_entry)),
      ('DELETE', 'customer_acquisition', (select count(*) from customer_acquisition)),
      ('DELETE', 'customer_notification', (select count(*) from customer_notification)),
      ('DELETE', 'customer_identity_link_request', (select count(*) from customer_identity_link_request)),
      ('DELETE', 'ordinary_customer_account', (
          select count(*) from customer_account account
           where not exists (
               select 1 from customer_role role
                where role.customer_id = account.id and role.role = 'ADMIN'
           )
      )),
      ('DELETE', 'customer_referral_code', (select count(*) from referral_code where customer_id is not null)),
      ('DELETE', 'cleaning_order', (select count(*) from cleaning_order)),
      ('DELETE', 'cleaning_order_event', (select count(*) from cleaning_order_event)),
      ('DELETE', 'cleaning_order_photo', (select count(*) from cleaning_order_photo)),
      ('DELETE', 'cleaning_order_issue_report', (select count(*) from cleaning_order_issue_report)),
      ('DELETE', 'cleaning_order_issue_photo', (select count(*) from cleaning_order_issue_photo)),
      ('DELETE', 'referral_reward', (select count(*) from referral_reward)),
      ('DELETE', 'partner_payout', (select count(*) from partner_payout)),
      ('DELETE', 'rental_booking', (select count(*) from rental_booking)),
      ('DELETE', 'transfer_booking', (select count(*) from transfer_booking)),
      ('DELETE', 'rental_booking_occupancy', (select count(*) from rental_occupancy where type = 'BOOKING')),
      ('DELETE', 'rental_cleaning_benefit', (select count(*) from rental_cleaning_benefit)),
      ('DELETE', 'operational_media_asset', (
          select count(*) from media_asset asset
           where not exists (
               select 1 from rental_property_media media where media.media_asset_id = asset.id
           )
      )),
      ('DELETE', 'spring_session', (select count(*) from spring_session)),
      ('KEEP', 'admin_customer_account', (
          select count(distinct account.id)
            from customer_account account
            join customer_role role on role.customer_id = account.id and role.role = 'ADMIN'
      )),
      ('KEEP', 'rental_property', (select count(*) from rental_property)),
      ('KEEP', 'transfer_airport', (select count(*) from transfer_airport)),
      ('KEEP', 'transfer_vehicle_type', (select count(*) from transfer_vehicle_type)),
      ('KEEP', 'transfer_price', (select count(*) from transfer_price)),
      ('KEEP', 'transfer_driver', (select count(*) from transfer_driver)),
      ('KEEP', 'rental_property_media', (select count(*) from rental_property_media)),
      ('KEEP', 'non_booking_occupancy', (select count(*) from rental_occupancy where type <> 'BOOKING')),
      ('KEEP', 'referral_partner', (select count(*) from referral_partner)),
      ('KEEP', 'partner_referral_code', (select count(*) from referral_code where partner_id is not null)),
      ('KEEP', 'acquisition_campaign', (select count(*) from acquisition_campaign))
  ) counts(section, table_name, record_count)
 order by case section when 'DELETE' then 0 else 1 end, table_name;
SQL
)

counts_output=$(printf '%s\n' "${counts_sql}" | psql_query)
if [[ -z ${counts_output} ]]; then
  echo "Reset preflight returned no record counts; nothing was changed." >&2
  exit 1
fi
mapfile -t counts <<< "${counts_output}"

echo "Loco Place pre-commercial reset"
echo
echo "Database: ${database_name}"
echo "Environment: production (${application_host})"
echo
echo "Will delete:"
for row in "${counts[@]}"; do
  IFS='|' read -r section table_name record_count <<< "${row}"
  if [[ ${section} == 'DELETE' ]]; then
    printf '  %-34s %10s\n' "${table_name}" "${record_count}"
  fi
done
echo
echo "Will preserve:"
for row in "${counts[@]}"; do
  IFS='|' read -r section table_name record_count <<< "${row}"
  if [[ ${section} == 'KEEP' ]]; then
    printf '  %-34s %10s\n' "${table_name}" "${record_count}"
  fi
done
echo
echo "PURGE: customer analytics and sessions; all Cleaning transactions/history/media;"
echo "       Rental/Transfer bookings, BOOKING occupancy and booking-derived benefits; ordinary customers."
echo "PRESERVE: ADMIN accounts/identities/roles; Rental catalog/media and non-BOOKING occupancy;"
echo "          referral partners/partner codes; acquisition campaigns; platform configuration."
echo "          Transfer airports, vehicles, rates and drivers."

if [[ ${execute} != true ]]; then
  echo
  echo "DRY RUN ONLY — no data was changed."
  echo "Use --execute only after reviewing docs/precommercial-data-reset.md."
  exit 0
fi

if [[ ${reset_allowed} != true ]]; then
  echo "Destructive reset is locked. Set PRECOMMERCIAL_DATA_RESET_ALLOWED=true in ${env_file}." >&2
  exit 1
fi
if [[ ! -t 0 || ! -t 1 || ! -r /dev/tty ]]; then
  echo "Destructive reset requires an interactive terminal; automation and piped confirmation are blocked." >&2
  exit 1
fi

echo
echo "Type exactly: ${confirmation_text}"
IFS= read -r entered_confirmation </dev/tty
if [[ ${entered_confirmation} != "${confirmation_text}" ]]; then
  echo "Confirmation did not match. Nothing was changed." >&2
  exit 1
fi

echo "Creating mandatory backup..."
backup_output=$("${script_dir}/backup.sh")
printf '%s\n' "${backup_output}"
backup_file=$(printf '%s\n' "${backup_output}" | sed -n 's/^Backup created: //p' | tail -n 1)
if [[ -z ${backup_file} || ! -s ${backup_file} ]]; then
  echo "Backup could not be verified as a non-empty file; reset cancelled." >&2
  exit 1
fi

declare -A preserve_before=()
for row in "${counts[@]}"; do
  IFS='|' read -r section table_name record_count <<< "${row}"
  if [[ ${section} == 'KEEP' ]]; then
    preserve_before["${table_name}"]=${record_count}
  fi
done

backend_was_running=false
if "${COMPOSE[@]}" ps --status running --services | grep -qx backend; then
  backend_was_running=true
  "${COMPOSE[@]}" stop backend
fi
restart_backend() {
  if [[ ${backend_was_running} == true ]]; then
    "${COMPOSE[@]}" up -d backend >/dev/null
  fi
}
trap restart_backend EXIT

echo "Executing transactional reset..."
psql_query < "${script_dir}/reset-precommercial-data.sql"

verification_sql=$(cat <<'SQL'
select 'customer_acquisition', count(*) from customer_acquisition
union all select 'acquisition_campaign_entry', count(*) from acquisition_campaign_entry
union all select 'cleaning_order', count(*) from cleaning_order
union all select 'rental_booking', count(*) from rental_booking
union all select 'transfer_booking', count(*) from transfer_booking
union all select 'customer_notification', count(*) from customer_notification
union all select 'ordinary_customer_account', count(*) from customer_account account
 where not exists (
     select 1 from customer_role role
      where role.customer_id = account.id and role.role = 'ADMIN'
 )
union all select 'spring_session', count(*) from spring_session;
SQL
)
verification_output=$(printf '%s\n' "${verification_sql}" | psql_query)
while IFS='|' read -r table_name record_count; do
  if [[ ${record_count} != 0 ]]; then
    echo "Post-reset validation failed: ${table_name} still contains ${record_count} record(s)." >&2
    exit 1
  fi
done <<< "${verification_output}"

preserve_sql=$(cat <<'SQL'
select 'admin_customer_account', count(distinct account.id)
  from customer_account account join customer_role role on role.customer_id = account.id and role.role = 'ADMIN'
union all select 'rental_property', count(*) from rental_property
union all select 'rental_property_media', count(*) from rental_property_media
union all select 'transfer_airport', count(*) from transfer_airport
union all select 'transfer_vehicle_type', count(*) from transfer_vehicle_type
union all select 'transfer_price', count(*) from transfer_price
union all select 'transfer_driver', count(*) from transfer_driver
union all select 'non_booking_occupancy', count(*) from rental_occupancy where type <> 'BOOKING'
union all select 'referral_partner', count(*) from referral_partner
union all select 'partner_referral_code', count(*) from referral_code where partner_id is not null
union all select 'acquisition_campaign', count(*) from acquisition_campaign;
SQL
)
preserve_output=$(printf '%s\n' "${preserve_sql}" | psql_query)
while IFS='|' read -r table_name record_count; do
  if [[ ${preserve_before[${table_name}]} != "${record_count}" ]]; then
    echo "Preserved table check failed for ${table_name}: before=${preserve_before[${table_name}]}, after=${record_count}." >&2
    exit 1
  fi
done <<< "${preserve_output}"

upsert_env_value "${env_file}" PRECOMMERCIAL_DATA_RESET_ALLOWED false
trap - EXIT
restart_backend

echo
echo "Pre-commercial data reset completed successfully."
echo "Verified backup: ${backup_file}"
echo "PRECOMMERCIAL_DATA_RESET_ALLOWED was set to false."
echo "Set COMMERCIAL_LAUNCH_AT to the exact launch timestamp and redeploy before accepting customers."
