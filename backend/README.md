# go-cleany backend

Single-module Spring Boot backend for the go-cleany MVP.

## Stack

- Java 25
- Spring Boot 4.1
- PostgreSQL
- Liquibase
- Gradle 9 Wrapper

## Local configuration

The `local` profile contains non-production sample prices and a fixed browser-preview identity. PostgreSQL must be available locally before starting the application.

```powershell
$env:JAVA_HOME = "C:\Users\Alexandr\.jdks\corretto-25.0.4"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:SPRING_PROFILES_ACTIVE = "local"
./gradlew.bat bootRun
```

Local PostgreSQL connection settings and placeholder prices are kept in `src/main/resources/application-local.yml`, following the same profile layout as the neighboring WAF services.

Gradle and the application use the declared Java 25 toolchain.

## Telegram Mini App authentication

The default profile requires a real `TELEGRAM_BOT_TOKEN`. Customer order endpoints accept the raw
`Telegram.WebApp.initData` value through one authorization scheme:

```http
Authorization: tma <telegram-init-data>
```

The backend verifies the Telegram HMAC-SHA-256 hash, rejects duplicate or malformed fields, checks
`auth_date`, and only then creates the trusted customer principal. The accepted age and future clock
skew are configured with `TELEGRAM_INIT_DATA_MAX_AGE` and
`TELEGRAM_INIT_DATA_ALLOWED_CLOCK_SKEW`.

The `local` profile never reuses or weakens production Telegram authentication. It selects a separate
fixed local identity so the frontend can be exercised in a regular browser.

## Cleaner Telegram bot

The cleaner bot is enabled by default outside the `local` and `test` profiles. It requires:

- `TELEGRAM_BOT_TOKEN` — the same bot token used to validate Mini App `initData`;
- `CLEANER_TELEGRAM_IDS` — the comma-separated whitelist of cleaner Telegram user IDs.

The default `TELEGRAM_UPDATE_MODE=polling` calls `deleteWebhook` at startup and receives only
`message` and `callback_query` updates through `getUpdates`. Polling uses a 25-second server timeout
and a three-second retry delay. Exactly one backend instance may poll a bot token at a time.

Outbound Bot API calls use three-second connection and 35-second read timeouts by default. They can
be changed through `TELEGRAM_API_CONNECT_TIMEOUT` and `TELEGRAM_API_READ_TIMEOUT`.

Webhook remains an optional transport. Set `TELEGRAM_UPDATE_MODE=webhook`, provide a random
1-256 character `TELEGRAM_WEBHOOK_SECRET` containing only letters, digits, `_`, or `-`, and register:

```text
POST https://<public-host>/api/v1/telegram/webhook
X-Telegram-Bot-Api-Secret-Token: <TELEGRAM_WEBHOOK_SECRET>
```

Register that endpoint through the Bot API `setWebhook` method, pass the same value as
`secret_token`, and configure `allowed_updates` as `["message", "callback_query"]`.

After an order transaction commits, every configured cleaner receives the same offer with `Accept`
and `Skip` buttons. `Accept` uses the database-level first-claim-wins update. `Finish cleaning` moves
the assigned order to `AWAITING_REPORT` and selects it as the cleaner's active report input.

The cleaner then sends one or more photos and an optional text comment directly to the bot. The
backend stores only Telegram `file_id` and `file_unique_id` values, without downloading media or
using object storage. Repeated delivery of the same Telegram photo is idempotent per order. After
the cleaner presses `Send report to customer`, the bot sends the summary, every stored photo, and
the comment to the customer. Only successful delivery of the complete report changes the order to
`COMPLETED`.

At least one photo is the current technical floor; no maximum is imposed yet because the final
product minimum/maximum remains open. Repeated `Finish cleaning` selects that order for subsequent
photo and text messages, so a cleaner can switch safely between accepted orders. The `local`
profile sets `telegram.bot-enabled=false` by default, so browser development never calls the real
Telegram API unless it is explicitly enabled. The complete Docker setup enables it in polling mode;
see [`docs/local-docker-runbook.md`](../docs/local-docker-runbook.md).

## Administration

`ADMIN_TELEGRAM_IDS` is a comma-separated whitelist independent from the cleaner whitelist. An
authorized user sees the `/admin` web section and can use these commands in the same bot chat:

- `/admin` — command reference;
- `/stats` — current order totals and completed revenue;
- `/orders` — the latest ten orders;
- `/order <id>` — one order and its latest audit events.

Every lifecycle transition, added photo, and cleaner-comment update is stored in
`cleaning_order_event`. Existing orders receive an `IMPORTED` event when the migration is first
applied. The admin APIs are read-only and verify the current Telegram user on every request.

## Tests

Integration tests extend a shared `BaseIntegrationTest`. It starts `postgres:16-alpine`, injects datasource properties through `@DynamicPropertySource`, and lets Spring run the production Liquibase changelog before Hibernate validates the schema. Telegram authentication has fixed-vector and MockMvc coverage; cleaner callbacks, webhook secret validation, broadcast isolation, report delivery ordering, admin authorization and statistics, and assigned-cleaner authorization have focused unit coverage. The photo-report integration test exercises the real PostgreSQL/Liquibase schema, photo deduplication, active-report switching, and completion transition.

```powershell
./gradlew.bat test
```

The production/default profile requires real prices and a Telegram bot token through the environment variables documented in the repository `.env.example` file.

## Important boundaries

- The backend calculates and stores the price; it never accepts a price from the client.
- Customer identity comes from an identity provider, never from an order request.
- The `local` identity provider exists only under the `local` Spring profile.
- Database schema changes belong in Liquibase.
- Tests are not run automatically by the implementation agent; run them explicitly when requested.
