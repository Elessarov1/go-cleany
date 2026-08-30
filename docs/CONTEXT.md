---
title: Loco Place Current Context
type: ai-context
status: active
scope: platform
updated: 2026-08-30
---

# Loco Place — Current Context

This is the fast context restore file for humans and AI agents. It intentionally stays compact. For detail, follow [INDEX.md](INDEX.md).

## Product thesis

> **Loco Place is a local service to which a customer delegates a household/local task instead of searching for and coordinating providers alone.**

Russian customer-facing formulation:

> **Loco Place — локальный сервис, которому можно поручить бытовую задачу и не искать исполнителя самому.**

Strategic direction:

> **From catalog to habit.**

The customer delegates a task; Loco owns the coordination needed to get to the outcome.

## Current implemented verticals

```text
Loco Place
├── Loco Cleaning  — apartment cleaning
├── Loco Rental    — apartment rental
└── Loco Transfer  — scheduled fixed-price airport transfer
```

All three are implemented. Do not treat Transfer as a future/planned module.

Technical names intentionally remain stable:

```text
repo: go-cleany
routes: /cleaning, /rent, /transfer
backend namespaces: cleaning, rental, transfer
aggregates: CleaningOrder, RentalBooking, TransferBooking
```

Public canonical domain:

```text
https://loco-place.com
```

## Non-negotiable product principles

1. Loco solves the task; it does not expose a provider catalog as the product.
2. After placing an order, the customer must not become the dispatcher.
3. Every subsequent interaction should require fewer customer actions where context is safely reusable.
4. Do not ask again for data Loco already knows when it can be safely prefilled.
5. Cross-service offers must arise from real context, not generic advertising.
6. Repeat and successful fulfillment matter more than registrations.
7. Support and failure handling are part of the product, not an afterthought.
8. Density, repeat and contribution matter more than the number of verticals.
9. Automate measured operational pain, not hypothetical future complexity.
10. Backend remains authoritative for identity, price, eligibility, ownership, availability and state transitions.

Canonical rationale: [strategy/manifesto.md](strategy/manifesto.md).

## North Star and business health

Proposed North Star:

> **Completed tasks per active customer** (typically viewed on a rolling window such as 90 days).

Always read it together with:

```text
30/90-day repeat rate
customers with 2+ completed tasks
customers using 2+ services
cross-service conversion
contribution margin
CAC and CAC payback
fulfillment success
cancellation / incident / no-show rate
ops minutes per order
```

GMV and registrations alone are not success metrics.

## Intended next product phase

The core vertical set is now large enough to test the platform thesis. Before rushing into many new categories, prioritize **Retention & Trust**:

```text
unified history
repeat / book again
saved places and reusable customer context
same-service and cross-service prefill
contextual next actions
unified support entry point
simple quality feedback
smart reminders
contextual benefits
cross-service analytics
```

See [cross-functional/retention.md](cross-functional/retention.md).

## Cross-service design rule

Verticals remain independent aggregates. Cross-service features use explicit bridges/read models/application services.

Do not create:

```text
UniversalOrder
UniversalBooking
one JSONB mega-aggregate
```

Existing proof: `RentalCleaningBenefit` explicitly connects Rental and Cleaning without merging them.

Generalize only after a second/third real use case proves the abstraction.

## Backend architecture

```text
Java 25
Spring Boot 4
Gradle
PostgreSQL
Liquibase
Testcontainers
```

Architecture: one modular monolith.

Shared platform concerns include customer identity, authentication, service availability/catalog, notifications, communication, media, analytics and shared admin shell.

Vertical-owned concerns remain inside Cleaning, Rental and Transfer.

Do not introduce microservices, Kafka, workflow engines, S3/MinIO or another database without a concrete requirement.

## Frontend architecture

```text
React 19
TypeScript
Vite
RU / EN
mobile-first
standalone web + Telegram Mini App
```

The same business backend is used across channels. Channel-specific behavior belongs at adapters/shell boundaries.

The UI should progressively use known context to reduce friction. New users provide required data; returning users get safe prefill; cross-service flows inherit relevant source context; mature flows can expose an explainable next action.

Unified customer Activity is implemented at `/account/activity`. Its API composes customer-owned Cleaning, Rental and Transfer data at request time into active/upcoming and terminal-history sections while keeping each vertical aggregate and detail workflow independent. Activity and `/notifications` form one visually unified customer section with persistent tabs while remaining separate read models and routes. The shared customer navigation uses Activity as its stable history destination; legacy vertical list routes remain available.

## Identity

Canonical internal identity:

```text
CustomerAccount.id
```

External providers are identities/adapters, currently including Google and Telegram.

Do not merge identities automatically by email, phone, display name or username.

Google ↔ Telegram linking is explicit and verified.

Telegram is optional for customers.

`ADMIN` is a persisted CustomerAccount role. Deployment bootstrap uses verified Google emails configured by `ADMIN_GOOGLE_EMAILS`; Telegram IDs do not bootstrap ADMIN.

See [architecture/identity.md](architecture/identity.md).

## Service availability

Platform service states:

```text
ENABLED
IN_TEST
DISABLED
```

`IN_TEST` permits new customer flows only for ADMIN. `DISABLED` blocks new customer flows but must not hide owned history or disable operational admin work.

## Notifications

The durable in-app notification inbox records important updates. The separate unified Activity read model is the customer's cross-service transaction history. External delivery such as Telegram is optional.

Preferred boundary:

```text
domain/application event
→ persistent semantic notification
→ communication routing
→ optional channel adapter
```

Domain services should not be coupled to Telegram rendering.

## Loco Cleaning

Cleaning owns `CleaningOrder`, pricing, cleaner first-accept workflow, completion report/media, onsite issues and cleaning-specific referral economics.

Multiple cleaners may receive work; first acceptance wins atomically.

Referral financial semantics are currently Cleaning-specific. Do not globalize them because a generic referral abstraction seems attractive.

## Loco Rental

Rental owns `RentalProperty`, `RentalBooking`, `RentalOccupancy`, availability, rental pricing/stay rules and rental administration.

Bookings use explicit occupancy/availability rules and immutable price snapshots.

`RentalCleaningBenefit` is an implemented explicit cross-service bridge.

## Loco Transfer

Transfer is implemented and owns:

```text
TransferAirport
TransferVehicleType
TransferPrice
TransferDriver
TransferBooking
```

It provides fixed-price scheduled rides between Alanya and configured airports. GZP, AYT, Sedan and Minivan are seeded configuration; commercial rates are admin-managed.

Directions are `TO_AIRPORT` and `FROM_AIRPORT`. Booking time rules use `Europe/Istanbul` and a configurable lead horizon.

Workflow:

```text
REQUESTED → CONFIRMED → COMPLETED
     ├────→ REJECTED
     └────→ CANCELLED
```

Driver assignment supports:

```text
ADMIN_ASSIGNMENT
DRIVER_SELF_ACCEPT
```

A driver's phone is enough for manual assignment. Telegram is optional. A configured Telegram ID is not sufficient for bot messages: the driver must complete verified bot linking. Self-accept uses connected enabled drivers and an atomic first-wins assignment.

See [product/transfer.md](product/transfer.md) and [loco-transfer.md](loco-transfer.md).

## Analytics currently implemented

Platform first-touch acquisition is attached to `CustomerAccount.id` and shared by Cleaning, Rental and Transfer.

Stable campaign entry:

```text
/a/<publicCode>
```

Admin analytics supports ALL/CLEANING/RENTAL/TRANSFER and calculates new/active customer acquisition, average checks from completed price snapshots without mixing currencies, and Business Health metrics from completed Cleaning, Rental and Transfer tasks.

Retention uses first-completed-task mature 30/90-day cohorts, cumulative mature second-order conversion and median time to the second task. Initial cross-service funnels count only the immediate next completed task. Empty mature cohorts are reported as insufficient data, not zero retention.

See [cross-functional/analytics.md](cross-functional/analytics.md).

## Benefits direction

Current concrete cross-service benefit:

```text
RentalBooking → RentalCleaningBenefit → CleaningOrder
```

Future contextual benefits may connect other service pairs, but do not introduce a generic benefit engine until repeated semantics justify it.

Benefits must be measurable and must not hide bad unit economics.

## Support direction

The platform should evolve toward one obvious support entry point for any Loco transaction and a shared `SupportCase`-style platform record referencing the originating service/entity.

Loco owns the problem in the customer's eyes. Each vertical may still own its own remediation semantics.

## Operational rule

Understand the real manual process first, measure it, then automate the bottleneck.

Track `ops minutes/order` before building sophisticated scheduling/dispatch machinery.

## Knowledge routing

For a task, do not read every document. Start here, then use [INDEX.md](INDEX.md).

When a durable platform decision changes, update this file in the same change so future sessions do not restore stale context.
