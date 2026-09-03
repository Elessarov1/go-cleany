---
title: Loco Place Current Context
type: ai-context
status: active
scope: platform
updated: 2026-09-04
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

Same-service repeat is implemented for owned completed Cleaning and Transfer transactions. Their detail pages open safe prefilled forms; the backend rechecks ownership, completed status, service availability and current business configuration at both prefill and creation. Only explicitly reusable fields are copied, current account phone is used, and new scheduling/current price/incentives/assignment/status are never inherited. Typed self-source links preserve attribution without a universal transaction model.

Rental → Transfer contextual flow is implemented through an explicit bridge. A confirmed owned Rental offers arrival (`FROM_AIRPORT`, check-in, property destination) and checkout (`TO_AIRPORT`, checkout, property pickup) independently. Backend-calculated availability can expose an option now or state when its Transfer window opens. Related transfers keep typed source/context fields for deduplication and analytics while both vertical lifecycles remain independent.

The platform home at `/` is contextual for authenticated returning customers and stays a plain catalog for guests/new customers. `GET /api/v1/account/home` composes owned Activity, the nearest currently actionable Rental cross-service context and the latest eligible Cleaning/Transfer repeat without persistence. It exposes no `customerId`, never shows `AVAILABLE_LATER` as a home action, suppresses duplicate target services and continues to show owned active work when a vertical is unavailable for new customer flows. The catalog and personalized context load independently.

Unified Support & Feedback is implemented without modifying vertical aggregates. Every owned Cleaning, Rental and Transfer detail page embeds the shared panel for opening a categorized case in any source status; completed sources additionally accept one immutable `GOOD` or `PROBLEM` feedback. Negative feedback atomically opens or reuses the single open case for that source. Customer ownership is resolved through the vertical repository and unavailable services do not hide support.

Smart Reminders v1 is implemented through persisted `customer_reminder` lifecycle state and the durable notification inbox. Customers explicitly choose 14/30/off after a completed Cleaning; Rental checkout reminders reuse the `CHECKOUT` Transfer context three days before checkout; confirmed Transfers receive an operational reminder one day before pickup. The job runs daily at 09:00 Europe/Istanbul by default, is idempotent, suppresses already satisfied needs and treats Telegram as optional secondary delivery. Admin analytics attributes reminder outcomes through existing typed source links.

Stage 7.5 provides a local-only reproducible measurement contour with deterministic data, pinned Docker k6 scenarios, JFR capture and performance-only Actuator/Hibernate metrics; it is not run in CI, staging or on the VPS. Its measured pass removed nested customer-identity connection starvation, added right-sized Rental image variants and cover-only list reads, lazy-loaded frontend routes/locales, moved optional Smart Reminder Telegram I/O after the database commit and consolidated runtime web serving on Caddy. Production now uses one Caddy application image to serve fingerprinted Vite assets with immutable caching and proxy API/OAuth directly to backend; local/performance Compose retains the service name `frontend` but runs the same Caddy server. The final end-to-end contour verified zero-error mixed traffic, approximately 89% lower UI-shaped Rental image transfer and no saturation through 100 VU on the development workstation. Smart Reminders, data retention and Rental Cleaning Benefit issuance expose tagged counters and structured per-run logs. Further performance work requires a measured regression or real telemetry rather than speculative infrastructure. Frequent source builds on the small VPS remain bounded operationally by pruning unused BuildKit cache older than a configurable retention window before a deploy and dangling images after a successful health check; runtime containers, volumes, database data and backups are never part of that cleanup.

Rental media uploaded before responsive variants existed is repaired automatically at backend startup in small idempotent transactions; readiness stays down until the repair completes and corrupted legacy input fails startup with an actionable property/media ID. Public Rental full/card/thumbnail bytes are shared across users through a bounded 64 MiB in-process cache. Versioned immutable URLs prevent browsers from retaining a previous full-size fallback under a responsive path, while per-property after-commit invalidation and generation keys keep lifecycle changes from serving stale media. This remains a single-instance local cache, not new infrastructure.

Persisted administrators use the oldest-first queue at `/admin/support`, resolve cases with a required final comment and follow safe links to vertical admin detail pages. The customer sees the result only in the originating transaction. New cases produce deduplicated durable `SUPPORT_CASE_CREATED` notifications for every persisted admin and optional dispatcher-routed Telegram delivery without including the customer description.

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

Cleaning/Transfer repeat UX is measured as shown → prefill opened → linked repeat created → linked repeat completed, including median time from source completion to repeat creation. Interaction events are deduplicated; creation/completion come from vertical source links.

Rental → Transfer is additionally measured as shown → prefill opened → related Transfer created → related Transfer completed, both overall and by arrival/checkout context. Its source cohort follows the selected Rental reporting period; median conversion time runs from first contextual display to Transfer creation.

See [cross-functional/analytics.md](cross-functional/analytics.md).

## Benefits direction

Current concrete cross-service benefit:

```text
RentalBooking → RentalCleaningBenefit → CleaningOrder
```

Future contextual benefits may connect other service pairs, but do not introduce a generic benefit engine until repeated semantics justify it.

Benefits must be measurable and must not hide bad unit economics.

## Support

The platform `support` module owns `SupportCase` and `TransactionFeedback` records keyed by `customerId + service + sourceEntityId`. This polymorphic reference is deliberately not a universal transaction aggregate. There can be one open case per source; resolved cases are final and a later incident creates a new case.

Loco owns the problem in the customer's eyes. Shared support provides intake, feedback, queue and resolution visibility; each vertical still owns its remediation semantics. Attachments, assignment, SLA, compensation, public ratings and support analytics remain deferred.

See [cross-functional/support.md](cross-functional/support.md).

## Operational rule

Understand the real manual process first, measure it, then automate the bottleneck.

Track `ops minutes/order` before building sophisticated scheduling/dispatch machinery.

## Knowledge routing

For a task, do not read every document. Start here, then use [INDEX.md](INDEX.md).

When a durable platform decision changes, update this file in the same change so future sessions do not restore stale context.
