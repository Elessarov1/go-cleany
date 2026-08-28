# Backend Codex Instructions

These instructions apply to the `backend/` tree and extend the repository root `AGENTS.md`.

---

## Stack

```text
Java 25
Spring Boot 4
Gradle
PostgreSQL
Liquibase
Testcontainers
```

---

## General backend style

Prefer simple, explicit Java code.

Use constructor injection.

Production constructors represent real runtime dependencies and invariants.

Do not add shortened, overloaded, defaulting, or compatibility constructors to production classes/records solely to keep old tests compiling. When a required dependency is added, update tests to pass/mock it or extend a test fixture/builder.

Do not hide required production dependencies behind test-only defaults.

Do not introduce an interface unless there is a real implementation boundary or realistic future alternative.

Good examples for interfaces:

```text
media storage
notification delivery
external provider integration
authentication/identity adapter
```

Avoid interface + single implementation pairs for ordinary domain/application services without a concrete need.

---

## Domain boundaries

Keep platform concerns separate from cleaning and rental business concerns.

Platform-oriented backend code includes:

```text
customer
identity
notification
communication
media
retention
```

Cleaning-specific logic includes:

```text
order lifecycle
pricing
cleaner assignment
completion reports
onsite issue
cleaning referral economics
```

Rental-specific logic includes:

```text
rental properties and catalog publication
availability and occupancy
rental pricing and stay policies
rental booking lifecycle
```

Do not move vertical-specific rules into generic platform classes merely to make them reusable, and do not couple rental workflows directly to cleaning aggregates.

---

## Customer identity

Prefer:

```text
customerId
externalIdentityId
provider
externalSubject
```

over Telegram-specific identifiers in reusable application/domain APIs.

Telegram parsing and validation may remain inside Telegram adapters.

Do not make `TelegramPrincipal` a long-term dependency of generic customer/application services.

`ADMIN` authorization always comes from the persisted `CustomerAccount` role. Verified Google email allowlisting is the only deployment bootstrap; Telegram identity gains the same role only after explicit account linking to that CustomerAccount.

---

## Notifications

Customer-facing reusable business logic should not call Telegram APIs directly.

Preferred direction:

```text
business/application event
→ notification dispatcher/service
→ channel adapter
```

Telegram is an optional delivery channel.

Cleaner bot interaction may remain Telegram-specific.

When adding another notification sender in the future, route by customer identity/communication capability rather than broadcasting blindly.

---

## Media

Important images should be retrievable independently of Telegram.

Current direction:

```text
MediaAsset
MediaStorage
PostgresMediaStorage
provider references
```

Use PostgreSQL `BYTEA` for pilot binary storage.

Do not persist Base64 as media storage.

Validate content size/type based on actual bytes where relevant.

Preserve SHA-256/integrity metadata where already used.

Do not expose large binary fields through normal order JSON responses.

---

## Database

All schema changes use Liquibase.

`ddl-auto` remains validation-only.

Prefer explicit DB constraints for important invariants where practical.

For migrations that affect core entities:

```text
add new structure
backfill
switch reads/writes
validate
remove old structure later
```

when this improves deployment rollback safety.

Avoid destructive one-step migrations for major platform-neutralization changes.

---

## Transactions

Do not hold database transactions open around slow external network calls unless unavoidable.

External provider downloads or sends should generally happen outside unnecessarily long DB transactions.

Use AFTER_COMMIT behavior when external notification must not happen before the business transaction succeeds.

---

## Tests

Use existing unit/integration test style.

Prefer Testcontainers-backed integration tests for:

```text
Liquibase/schema behavior
repository queries
transactional invariants
cleanup
concurrency
cross-service persistence behavior
```

Do not start a full Spring context for trivial pure-unit logic when a small unit test is sufficient.

When a production constructor gains a dependency, update tests to construct the real production shape. Do not preserve obsolete test signatures in production code.

When changing channel-neutral architecture, include regression coverage for current Telegram behavior.

---

## Current backend architecture constraints

Do not introduce:

```text
Kafka
microservices
S3/MinIO
generic workflow engines
WhatsApp-specific integrations
```

without a new concrete product requirement.

The current monolith is intentional.

---

## Before finishing

Run the relevant Gradle test/build commands.

If a migration was added, verify application startup/schema validation through the existing integration test infrastructure where practical.

If notification/media/customer identity code changed, explicitly check for new Telegram-specific coupling in reusable services.
