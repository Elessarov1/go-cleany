---
title: Loco Place Architecture Overview
type: architecture
status: active
scope: platform
updated: 2026-08-30
---

# Architecture Overview

## Shape

Loco Place is intentionally one modular monolith for the pilot/commercial validation stage.

```text
React web / Telegram Mini App
            │
            ▼
Spring Boot modular monolith
            │
            ▼
PostgreSQL
```

Do not introduce microservices, Kafka, distributed workflows, S3/MinIO or additional databases without a measured requirement.

## Backend stack

```text
Java 25
Spring Boot 4
Gradle
PostgreSQL
Liquibase
Testcontainers
```

## Frontend stack

```text
React 19
TypeScript
Vite
RU / EN
mobile-first
```

## Module boundary

Platform capabilities can include:

```text
customer/identity/authentication
catalog/service availability
notifications/communication
media/retention
analytics/acquisition
shared admin shell
```

Verticals own their business lifecycle:

```text
CleaningOrder
RentalBooking / RentalOccupancy
TransferBooking / TransferDriver / Transfer pricing configuration
```

Dependency direction should generally be:

```text
vertical → platform
```

not arbitrary vertical-to-vertical domain coupling.

## Cross-service pattern

Use explicit bridge/application/read models to coordinate independent aggregates.

Existing proof:

```text
RentalBooking
→ RentalCleaningBenefit
→ CleaningOrder
```

Do not create a generic universal transaction aggregate or move service payloads into one JSONB structure.

## Backend authority

Clients/adapters never own truth for:

```text
customer identity
price/discount
availability
eligibility
ownership
status transition
```

Frontend may prefill/suggest to reduce friction; backend validates current truth.

## Channels

Current channels:

```text
standalone web
Telegram Mini App / bot
```

Business logic must not be duplicated per channel. Telegram-specific authentication/callbacks stay at adapter boundaries.

## Data change policy

All schema changes use Liquibase. Prefer backward-compatible migrations where practical. No stored procedures unless explicitly required.

## Further detail

- [identity.md](identity.md)
- [telegram.md](telegram.md)
- [decisions](decisions/README.md)
- historic/detailed [platform roadmap](platform-roadmap.md)
