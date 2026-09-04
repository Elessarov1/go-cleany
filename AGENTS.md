# Loco Place — Codex Project Instructions

## Purpose

This file gives Codex stable repository-wide instructions. It is not a task backlog or session log.

## Knowledge base — mandatory routing

The canonical project knowledge base is `docs/`.

Before substantial product, architecture or cross-cutting work:

1. read `docs/CONTEXT.md` to restore current project state;
2. use `docs/INDEX.md` to identify only the documents relevant to the task;
3. read `docs/strategy/manifesto.md` for product decisions;
4. inspect current code/tests/recent git history before assuming a documented capability is missing;
5. when a durable platform decision changes, update the relevant canonical doc and `docs/CONTEXT.md` in the same change.

The current implementation is the source of truth for implementation state. Stable docs define intended product/architecture constraints. Old task plans must not cause reimplementation of completed work.

### Core product principles

- Loco solves the task; it does not expose a provider catalog as the product.
- The customer must not become the dispatcher after placing an order.
- Every subsequent interaction should require fewer user actions when known context can be safely reused.
- Do not ask again for customer context Loco already knows without a business reason.
- Cross-service actions should arise from real context, not generic advertising.
- Repeat and completed tasks/customer matter more than registrations.
- Support and failure handling are part of the product.
- Density, contribution and fulfillment matter more than the number of verticals.
- Automate measured operational pain, not hypothetical future problems.
- Strategic direction: **From catalog to habit.**

Canonical source: `docs/strategy/manifesto.md`.

---

## Project direction

This repository contains Loco Place with three implemented verticals:

```text
Loco Place
├── Loco Cleaning → apartment cleaning
├── Loco Rental   → apartment rental
└── Loco Transfer → scheduled fixed-price airport transfer
```

Public canonical domain:

```text
https://loco-place.com
```

Technical names remain stable when they are internal details:

```text
repository: go-cleany
routes: /cleaning, /rent, /transfer
packages/namespaces: cleaning, rental, transfer
aggregates: CleaningOrder, RentalBooking, TransferBooking
```

Public branding changes must not force unnecessary API/package/database migrations.

Do not create `UniversalOrder`, `UniversalBooking`, or a generic JSONB mega-aggregate. Future verticals own their own business aggregates.

WhatsApp integration is not planned. Do not reintroduce it without a new explicit product decision.

---

## Architecture principles

### Modular monolith

Loco Place is one Spring Boot modular monolith. Shared platform concerns can include customer/identity, authentication, catalog/service availability, notification/communication, media/retention, analytics/acquisition and shared admin shell.

Vertical business rules remain vertical-owned.

Cross-service features use explicit bridge/application/read models. Existing example: `RentalCleaningBenefit` connects Rental and Cleaning without merging their aggregates.

### Customer identity

Canonical business identity:

```text
CustomerAccount.id
```

External channels/providers belong behind identity/adapters.

`ADMIN` is a persisted `CustomerAccount` role. The deployment bootstrap is verified Google email in `ADMIN_GOOGLE_EMAILS`; Telegram IDs must not bootstrap ADMIN.

Google ↔ Telegram linking is explicit and verified. Never auto-merge identities by email, phone, display name or username.

Telegram is optional. A Google-only customer must remain fully usable.

### Service availability

Platform availability states:

```text
ENABLED
IN_TEST
DISABLED
```

`IN_TEST` allows new customer flows only for ADMIN. `DISABLED` blocks new customer flows. Existing owned history and admin operations remain available.

### Channel neutrality

Current channels:

```text
standalone web
Telegram Mini App / bot
```

Business logic must not be duplicated by channel. Telegram-specific authentication/callback behavior stays at adapter boundaries.

### Notifications

Prefer:

```text
domain/application event
→ durable semantic notification
→ communication routing
→ optional channel adapter
```

Domain/application code should express what happened, not how Telegram renders it.

### Media

Important operational media belongs to the platform. Current pilot direction is internal `MediaAsset` / `MediaStorage` with PostgreSQL BYTEA. Provider file IDs may be retained as metadata/optimization but not as the only canonical representation.

Do not store binary content as Base64 in PostgreSQL.

### Vertical ownership

Cleaning owns `CleaningOrder`, cleaning pricing, cleaner workflow, completion report/media, onsite issues and Cleaning-specific referral economics.

Rental owns `RentalProperty`, `RentalBooking`, `RentalOccupancy`, rental availability/pricing/stay policy and admin workflow.

Transfer owns `TransferAirport`, `TransferVehicleType`, `TransferPrice`, `TransferDriver`, `TransferBooking`, fixed-price snapshots and driver assignment workflow.

Transfer is implemented. Manual driver assignment supports phone-only drivers; Telegram driver self-accept requires verified bot connection and atomic first-wins assignment.

### Backend authority

Backend remains authoritative for authenticated identity, prices, discounts, availability, ownership, eligibility and status transitions. Never trust client/agent-supplied customer IDs, calculated price, discount, availability or business status.

---

## Technical stack

### Backend

```text
Java 25
Spring Boot 4
Gradle
Lombok for appropriate boilerplate
PostgreSQL
Liquibase
Testcontainers
```

### Frontend

```text
React 19
TypeScript
Vite
RU / EN i18n
mobile-first
```

### Deployment

```text
Docker Compose
Caddy
GitHub Actions CI/CD
PostgreSQL backups
```

---

## Working rules

Before a non-trivial change:

1. inspect existing implementation;
2. inspect relevant tests;
3. load relevant knowledge through `docs/INDEX.md`;
4. read task-specific temporary specification if supplied;
5. preserve unrelated behavior;
6. prefer incremental changes over speculative abstraction.

Avoid unrelated refactoring.

Use Lombok in new Java code when it removes mechanical boilerplate while keeping domain validation meaningful and explicit.

For empty Java collections use:

```java
Collections.emptyList()
Collections.emptySet()
Collections.emptyMap()
```

Do not use zero-argument `List.of()`, `Set.of()` or `Map.of()` to declare empty collections. When touching a Java file, fix violations in that file; do not do unrelated repository-wide cleanup.

### Production constructors are not test compatibility APIs

Do not add shortened/defaulting production constructors only to keep old tests compiling. Update tests/fixtures to provide real required dependencies.

Do not create a production abstraction solely to make tests easier. Create interfaces where there is a real boundary/alternative implementation, e.g. notification channel, media storage or external provider.

---

## Infrastructure constraints

Do not introduce without a concrete measured requirement:

```text
microservices
Kafka
distributed workflow engines
S3 / MinIO
new databases
generic event buses
```

The Spring Boot monolith + PostgreSQL architecture is intentional.

## Database changes

All schema changes use Liquibase. Application rollback does not automatically roll migrations back. Prefer backward-compatible expand/contract where practical. Do not use stored procedures unless explicitly required.

## Retention

Current pilot retention is configurable and defaults to approximately seven days for heavy operational payloads. Reuse existing cleanup infrastructure; do not create duplicates.

## Performance and reliability guardrails

The reproducible performance harness is kept under `performance/`. Use [docs/operations/local-stress-test-runbook.md](docs/operations/local-stress-test-runbook.md) when a change affects a hot API path, database concurrency, media delivery, scheduler throughput or frontend startup weight.

Never run k6/JFR load scenarios against the VPS, staging, production, a public domain or a CI/GitHub Actions worker. Use only the dedicated local `loco-perf` Compose project and keep its remote-target rejection in place. Performance jobs must not be added to CI or deployment workflows.

Do not optimize from intuition alone. Record a reproducible before measurement, change one relevant cause, then run the same local scenario once after the change. Repeat only when results are noisy. Keep aggregate evidence in `performance/baseline.md`; keep raw k6/JFR output ignored.

When writing backend code:

- do not routinely call a `REQUIRES_NEW` identity/helper method from inside an already active transaction; account for simultaneous connection demand and avoid holding one Hikari connection while waiting for another;
- reuse the request-resolved `CurrentCustomer`; do not re-resolve the same external identity from nested customer-owned services;
- never keep a database transaction open while calling Telegram, HTTP APIs or another slow external channel; persist authoritative state first and deliver after a successful commit through the existing notification/communication boundary;
- do not treat a larger Hikari pool as a fix for nested connection acquisition;
- add a concurrent integration scenario when changing claims, booking conflicts, identity resolution, scheduler processing or another first-wins/locking flow;
- avoid loading whole child collections or binary payloads when an endpoint needs only a cover, count, projection or metadata;
- do not copy large `byte[]` values without need, and use fit-for-purpose full/card/thumbnail media variants rather than sending a full image to every consumer;
- apply immutable public caching only to stable fingerprinted asset URLs; HTML and SPA fallbacks must remain non-cacheable;
- Caddy is the only runtime web server. In production the Caddy container serves the Vite build and proxies API/OAuth routes directly to backend. Local/performance Compose keeps the service name `frontend`, but that image also runs Caddy. Do not add Nginx or another proxy layer without a measured requirement;
- performance checks that are meant to represent the deployed path must use the Caddy service. Direct-backend runs are allowed only as an explicitly labelled component diagnostic, never as the sole end-to-end baseline.

When writing frontend code:

- new top-level routes should remain lazy-loaded unless eager loading is measured to be better;
- locale data should load only for the active language;
- after adding a large dependency, route or locale payload, inspect `npm run build` chunk sizes and compare initial-route gzip size rather than only the total emitted assets;
- production bundle audits must explicitly set `VITE_PREVIEW_MODE=false`; the local `.env.local` enables preview tooling and is not a production-sized build;
- Rental lists/cards use card images, gallery selectors use thumbnails and full media is reserved for the main/opened image.
- all date inputs rely on the shared `.service-shell input[type="date"]` iOS overflow guard; keep containing grid/flex items shrinkable with `min-width: 0` and do not add page-specific date-width fixes.

Do not run the full stress suite for ordinary isolated business-rule edits. Relevant integration tests remain the primary safety net; use the performance contour when the change can plausibly affect resource use or a previously measured bottleneck.

## Known intentionally deferred issues

Do not repeatedly rediscover these as urgent unless a task touches them:

```text
phone-based referral anti-abuse across multiple identities
durable external notification delivery checkpoints / partial-delivery retries
frontend date timezone vs backend Europe/Istanbul timezone
branch protection / required checks hardening
account unlinking policy
```

## Task-specific plans

Task-specific Markdown supplied in a Codex session is temporary execution context unless explicitly requested as permanent documentation.

Do not maintain session logs, implementation diaries or completed-task archives. Durable decisions belong in `docs/architecture/decisions/`; stable product/domain context belongs in canonical `docs/` pages.

## Validation expectations

- backend changes → run relevant backend tests;
- frontend changes → run frontend build/tests/lint where applicable;
- database/cross-layer changes → focused integration coverage;
- docs-only changes do not require application builds;
- verify Telegram flows when changing shared identity/notification boundaries.

Summaries should explain behavioral/architectural consequences, not only list files.
