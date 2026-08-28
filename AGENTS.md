# Loco Place — Codex Project Instructions

## Purpose

This file gives Codex the stable context required to work in this repository.

Do not use this file as a task backlog or implementation log.

Task-specific Markdown files may be provided directly in a Codex session and do not need to be persisted in the repository unless explicitly requested.

---

## Project direction

This repository contains the Loco Place platform with two working service verticals:

```text
Loco Place
├── Loco Cleaning → apartment cleaning
└── Loco Rent     → apartment catalog, availability and rental bookings
```

The public canonical domain is:

```text
https://loco-place.com
```

Technical names intentionally remain stable where they are internal implementation details:

```text
repository: go-cleany
routes: /cleaning, /rent
packages/namespaces: cleaning, rent, rental, Cleaning*, Rental*
```

Public branding changes must not force unnecessary API/package/database migrations.

The product may later contain more independent verticals such as:

```text
Handyman / repair
Residence / relocation assistance
future services
```

Do not turn `CleaningOrder` or `RentalBooking` into a universal service order.

WhatsApp integration is not planned. Do not reintroduce WhatsApp-specific architecture, provider placeholders, configuration, or roadmap work unless a new explicit product decision requires it.

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

`ADMIN` is a persisted platform role of `CustomerAccount`, not a Telegram-specific property.

The only deployment bootstrap for ADMIN is a verified Google email listed in:

```text
ADMIN_GOOGLE_EMAILS
```

Telegram IDs must not bootstrap ADMIN. After explicit Google ↔ Telegram account linking, Telegram resolves the same `CustomerAccount` and therefore the same persisted role.

Service vertical customer availability is persisted platform state:

```text
ENABLED
IN_TEST
DISABLED
```

`IN_TEST` allows new customer flows only for `ADMIN`; `DISABLED` blocks all new customer flows.
Existing owned transactions and admin operational workflows remain available.

Standalone web authentication uses direct Google OIDC through Spring Security OAuth2 Client and PostgreSQL-backed server sessions. The application is not an OIDC authorization server. Google provider values are backend deployment secrets and must never be exposed through Vite.

Google ↔ Telegram account linking is implemented and must remain explicit and verified. Never automatically merge external identities by email, phone, display name, username or other correlation.

Telegram remains optional. A Google-only customer must be able to use Loco Place without linking Telegram.

---

### Channel neutrality

Business logic must not be duplicated for different customer channels.

Channel-specific concerns belong in adapters.

Today the relevant entry points/channels are:

```text
standalone WEB
Telegram Mini App / bot
```

Future mobile or other channels may be added only when there is a concrete requirement.

Do not encode Telegram-specific assumptions into reusable customer/domain/application logic.

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

Telegram is an optional delivery channel, not the canonical location of customer business data.

Existing Telegram cleaner-side interaction may remain Telegram-specific.

---

### Media

Important operational media must belong to the platform.

Do not make external provider file IDs the only canonical representation of important media.

Current direction:

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

### Multi-vertical platform architecture

The application is a multi-vertical platform implemented as one modular monolith.

`CustomerAccount` is the shared platform customer identity. Cleaning, rental and future verticals must reference that same identity rather than introduce service-specific customer records.

Every vertical owns its own business aggregates, for example:

```text
CleaningOrder
RentalBooking
future HandymanRequest
future ResidenceCase
```

Do not introduce `UniversalOrder`, `UniversalBooking`, or a generic JSON-based service aggregate to unify those verticals.

Cross-service features should use an explicit bridge/application model that may reference aggregates from more than one vertical without merging their domain models.

Frontend, Telegram, future mobile applications and future MCP/agent integrations are adapters or entry points. Business rules must stay in reusable backend application/domain services and must not be duplicated in channel adapters, frontend code, or MCP tools.

Prefer explicit operations such as:

```text
quoteCleaningOrder
createCleaningOrder
quoteRentalBooking
createRentalBooking
```

over a generic `createOrder(service, payload)` API. Keep read/quote operations separate from state-changing operations.

The backend remains authoritative for authenticated identity, prices, discounts, availability, ownership, eligibility and status transitions. Never trust a client- or agent-supplied customer ID, calculated price, discount, availability, or business status.

Design state-changing application operations so additional adapters can expose them safely later.
Future MCP/agent writes should support idempotency against retries and uncertain responses. Future MCP/agent access must use delegated authorization with explicit scopes, never an arbitrary `customerId`. Do not implement MCP, delegated OAuth, agent grants, or payment-agent flows until a concrete requirement exists.

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

Use Lombok in new Java code when it removes mechanical boilerplate such as dependency constructors, getters, builders, or simple value objects. Keep domain validation and other meaningful logic explicit.

For empty Java collections, use the explicit `Collections.empty*()` methods:

```java
Collections.emptyList()
Collections.emptySet()
Collections.emptyMap()
```

Do not use `List.of()`, `Set.of()`, `Map.of()` or similar zero-argument factories to declare empty
collections. Whenever a Java file is changed, fix violations of this rule in the touched code as part
of the same change. Do not perform unrelated repository-wide cleanup solely for this rule.

### Production constructors are not test compatibility APIs

Production classes and records must not gain shortened, overloaded, defaulting, or compatibility constructors solely to keep old tests compiling after production dependencies or invariants change.

When production construction changes, update tests instead:

```text
use the real production constructor
provide/mock the new dependency
extend a test fixture/builder when useful
```

Do not hide a newly required production dependency by inventing a default value in a shorter constructor only for tests.

Bad pattern:

```java
Service(DepA a, DepB b, DepC c) { ... }

// added only because old tests still call two args
Service(DepA a, DepB b) {
    this(a, b, new DefaultDepC(...));
}
```

The same rule applies to records and value types.

Do not create a new production abstraction solely to make tests easier.

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

Current pilot retention is configurable and defaults to approximately seven days for heavy operational payloads such as completion-report media.

Existing scheduled cleanup and backup retention already exist.

Do not create duplicate cleanup infrastructure.

When media storage changes, adapt the existing retention mechanism.

---

## Known intentionally deferred issues

The following are known and are not current blockers unless a task directly touches them:

```text
phone-based referral anti-abuse across multiple identities
durable external notification delivery checkpoints / partial-delivery retries
frontend date timezone vs backend Europe/Istanbul timezone
branch protection / required checks hardening
account unlinking policy
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
