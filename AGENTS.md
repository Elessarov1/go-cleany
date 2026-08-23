# go-cleany — Codex Project Instructions

## Purpose

This file gives Codex the stable context required to work in this repository.

Do not use this file as a task backlog or implementation log.

Task-specific Markdown files may be provided directly in a Codex session and do not need to be persisted in the repository unless explicitly requested.

---

## Project direction

This repository currently contains two working service verticals in one platform shell:

```text
go-cleany → apartment cleaning through a Telegram Mini App and cleaner bot
go-rent   → apartment catalog, availability and rental bookings
```

Long-term direction:

```text
Telegram
→ WhatsApp
→ standalone Flutter mobile apps
```

The product is also expected to evolve into a broader service platform.

`go-cleany` remains the cleaning vertical and `go-rent` remains the rental vertical. The platform may later contain more independent verticals such as:

```text
Cleaning
Handyman / repair
Residence / relocation assistance
future services
```

Do not turn `CleaningOrder` into a universal service order.

---

## Required project context

Before architectural or cross-cutting changes, read:

```text
docs/architecture/index.md
docs/architecture/platform-roadmap.md
```

For cleaning referral economics read:

```text
docs/referral-financial-model.md
```

Task-specific implementation plans may be supplied directly in the current Codex session.

Treat those files as temporary execution context. Do not persist them in the repository unless explicitly requested.

Documentation describes intent.

The current implementation is the source of truth for actual repository state.

Before implementing any task or roadmap-related change:

1. inspect current code;
2. inspect relevant tests;
3. inspect recent git history if the area changed recently;
4. verify the requested work is not already implemented.

Do not reimplement completed work just because an older task specification mentions it.

---

## Architecture principles

### Customer identity

The internal customer identity is:

```text
CustomerAccount.id
```

Do not introduce new business dependencies on Telegram user IDs when `customerId` or an external identity reference is sufficient.

External channels and authentication providers belong behind identity/adapters.

---

### Channel neutrality

Business logic must not be duplicated for different customer channels.

Do not create:

```text
WhatsAppOrderService
WhatsAppPricingService
WhatsAppReferralService
```

when the existing cleaning application/domain logic can be reused.

Channel-specific concerns belong in adapters:

```text
Telegram
WhatsApp
Push / mobile
```

---

### Notifications

Domain/application code should express what happened, not how Telegram should display it.

Prefer:

```text
application/domain event
→ notification layer
→ communication target
→ channel adapter
```

over direct Telegram delivery from reusable business services.

Existing Telegram cleaner-side interaction may remain Telegram-specific.

---

### Media

Important operational media must belong to the platform.

Do not make external provider file IDs the only canonical representation of important media.

Target direction:

```text
internal MediaAsset
→ MediaStorage
→ PostgreSQL BYTEA for the pilot
```

Provider-specific metadata such as Telegram file IDs may still be retained for delivery optimizations and diagnostics.

Do not store binary content as Base64 in PostgreSQL.

---

### Service neutrality

Platform-level capabilities may include:

```text
customer
identity
communication
notification
media
retention
authentication
catalog
shared admin shell
```

Cleaning-specific capabilities remain cleaning-specific:

```text
CleaningOrder
cleaning pricing
cleaner workflow
photo completion report
onsite issue
cleaning referral economics
```

Rental-specific capabilities remain rental-specific:

```text
RentalProperty
RentalBooking
RentalOccupancy
rental pricing and stay policy
rental administration
```

Future service verticals should have their own aggregates.

Do not create a generic `UniversalOrder`.

Do not move all service-specific fields into one generic JSONB payload as the primary domain model.

---

## Technical stack

### Backend

```text
Java 25
Spring Boot 4
Gradle
Lombok for new Java boilerplate
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

Before implementing a non-trivial change:

1. inspect the existing implementation;
2. inspect relevant tests;
3. read only the stable documentation relevant to the task;
4. read the task-specific specification supplied in the current session, if any;
5. preserve unrelated behavior;
6. prefer incremental changes over speculative abstraction.

Avoid unrelated refactoring.

Use Lombok in new Java code when it removes mechanical boilerplate such as dependency
constructors, getters, builders, or simple value objects. Keep domain validation and
other meaningful logic explicit.

Do not create interfaces merely because abstraction is possible.

Create an interface when there is a real boundary or realistic alternative implementation, for example:

```text
notification channel
media storage
external provider integration
```

---

## Infrastructure constraints

Do not introduce the following without a concrete requirement:

```text
microservices
Kafka
distributed workflow engines
S3 / MinIO
new databases
generic event buses
```

The current Spring Boot monolith + PostgreSQL architecture is intentional for the pilot.

---

## Database changes

All schema changes must use Liquibase.

Application rollback does not automatically roll database migrations back.

Prefer backward-compatible expand/contract migrations where practical, especially for cross-cutting architecture changes.

Do not use database stored procedures unless explicitly required.

---

## Retention

Current pilot retention is configurable and defaults to approximately seven days for heavy operational payloads.

Existing scheduled cleanup and backup retention already exist.

Do not create duplicate cleanup infrastructure.

When media storage changes, adapt the existing retention mechanism.

---

## Known intentionally deferred issues

The following are known and are not current blockers unless a task directly touches them:

```text
phone-based referral anti-abuse across multiple identities
durable notification delivery checkpoints / duplicate partial delivery
frontend date timezone vs backend Europe/Istanbul timezone
branch protection / required checks hardening
```

Do not repeatedly rediscover these as new urgent tasks.

---

## Task-specific plans

Task-specific Markdown files supplied directly in a Codex session are temporary execution context.

They may contain:

```text
implementation steps
migration details
acceptance criteria
Definition of Done
temporary architectural work
```

Do not add these files to the repository by default.

Do not maintain:

```text
task history
completed-task archives
session logs
implementation diaries
```

unless explicitly requested.

Stable architectural decisions belong in:

```text
docs/architecture/
```

Stable product/domain rules belong in an appropriate permanent `docs/` document.

If implementation of a task changes a long-term architectural decision, update the relevant permanent documentation rather than preserving the temporary task specification.

---

## Validation expectations

After implementation:

- run relevant backend tests for backend changes;
- run frontend build/tests/lint where applicable for frontend changes;
- run focused integration tests for database or cross-layer changes;
- verify existing Telegram flow is not broken by platform-neutral refactoring.

Summaries should explain behavioral and architectural consequences, not only list modified files.
