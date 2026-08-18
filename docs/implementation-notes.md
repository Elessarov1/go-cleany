# Implementation notes

## Current phase

Phase 1 browser UX has been reviewed. Phase 2 added the single-module backend foundation:

- typed pricing and cleaner configuration;
- PostgreSQL schema managed by Liquibase;
- backend-owned price calculation;
- customer-scoped order CRUD API;
- explicit lifecycle rules;
- one-statement atomic order claim;
- isolated `local` identity for browser development.

Phase 3 adds the production Telegram Mini App authentication boundary:

- the frontend sends raw `Telegram.WebApp.initData` as `Authorization: tma ...`;
- the backend validates the Telegram HMAC-SHA-256 hash before trusting user fields;
- `auth_date` is restricted by configurable maximum age and future clock skew;
- malformed, duplicated, tampered, or expired data produces a uniform `401` response;
- production request identity and local browser identity remain separate Spring profiles.

Phase 4 adds the configured-cleaner Telegram bot flow:

- a webhook protected by Telegram's secret-token header;
- a Bot API client that does not expose the bot token in application error messages;
- after-commit broadcast of new orders to every configured cleaner;
- compact `Accept`, `Skip`, `Finish cleaning`, and `Cancel` callbacks;
- whitelist checks before every cleaner callback;
- atomic first-accept-wins handling with idempotent feedback for repeated callbacks;
- assigned-cleaner checks for every post-acceptance state transition;
- customer notifications when an order is accepted or cancelled;
- delivery failure isolation between cleaners.

Phase 5 adds the Telegram photo-report flow:

- private-chat photo and optional comment collection for the assigned cleaner;
- persistent active-report selection when a cleaner has several orders;
- storage of Telegram `file_id` and `file_unique_id` values without media downloads;
- per-order photo deduplication for repeated webhook delivery;
- a compact `Send report to customer` callback after at least one photo;
- ordered delivery of the summary, photos, and cleaner comment to the customer;
- `AWAITING_REPORT -> COMPLETED` only after every outbound Bot API call succeeds.

Phase 6 adds a local deployment and acceptance environment:

- Telegram `getUpdates` long polling as the default bot transport;
- automatic webhook removal before polling, without dropping queued updates by default;
- Docker images for the Java 25 backend and React/Nginx frontend;
- Docker Compose orchestration for PostgreSQL, Liquibase, backend, and frontend;
- container health checks and same-origin `/api` proxying;
- a local runbook covering bot registration, Telegram ID discovery, and the full order/report flow.

Phase 7 adds go-cleany administration and the visual identity refresh:

- a separate `ADMIN_TELEGRAM_IDS` whitelist for web and bot access;
- read-only operational statistics and recent-order inspection;
- `/admin`, `/stats`, `/orders`, and `/order <id>` bot commands in Russian;
- an append-only order-event history populated by every lifecycle transition and report action;
- an administrative web dashboard and detailed event timeline;
- a restrained light-blue go-cleany design system with a consistent inline SVG icon set.

Phase 8 prepares a single-VPS production release:

- production-only Docker networks with no public PostgreSQL/backend ports;
- Caddy 2 automatic HTTPS and an external health endpoint;
- real Telegram Mini App script initialization and same-origin API proxying;
- one-command releases with configuration validation and health waiting;
- automatic pre-deployment PostgreSQL dumps and Git-based application rollback;
- a temporary `nip.io` staging hostname until an owned domain is connected;
- an Ubuntu bootstrap and full Russian VPS operations runbook.

The next delivery phase is the first VPS launch: final prices and service definitions, production secrets,
Telegram Mini App URL configuration, an off-server backup destination, and basic uptime/error monitoring.

The browser-only mode remains available whenever `VITE_API_BASE_URL` is unset.

## Temporary development choices

- Same-day booking is currently accepted by backend validation because the final product decision is open.
- Mock/local prices exist only to make the complete UI and API flow reviewable.
- Customer cancellation is exposed only while an order is `NEW`; the post-acceptance policy remains open.

## Product decisions still open

The following values are placeholders in the mock frontend and must be confirmed before production launch:

- actual prices and duplex surcharges;
- exact Regular and Deep service contents and exclusions;
- fixed-price property size limits;
- whether same-day booking is allowed;
- final booking horizon;
- customer cancellation rules after acceptance;
- final photo-report minimum and maximum (the implementation currently uses one photo as a
  technical floor and imposes no maximum);
- whether a cleaner comment is required.

These decisions must not silently become backend business rules.
