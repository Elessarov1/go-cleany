# Loco Place

Loco Place is a mobile-first local-service platform for Alanya.

> **Loco Place is a local service to which a customer delegates a household or local task instead of searching for and coordinating providers alone.**

Strategic direction:

> **From catalog to habit.**

The platform currently contains three implemented verticals:

- **Loco Cleaning** — apartment cleaning with Telegram cleaner dispatch, first-accept concurrency and platform-owned completion/issue media;
- **Loco Rental** — apartment catalog, date-range and monthly pricing, availability, booking administration and responsive property media;
- **Loco Transfer** — scheduled fixed-price airport transfers with configurable airports, vehicle classes, driver assignment and optional Telegram self-accept.

Their aggregates and business rules remain separate:

```text
CleaningOrder
RentalBooking / RentalOccupancy
TransferBooking / TransferDriver
```

Loco Place does not use a universal order aggregate. Cross-service behavior is implemented through explicit bridges and read models.

## Current cross-functional product layer

Implemented platform capabilities include:

```text
canonical CustomerAccount identity
Google and Telegram account access/linking
service availability controls
unified customer Activity
same-service Cleaning/Transfer repeat
safe reusable customer context
Rental → Transfer contextual prefill
contextual customer home
support cases and transaction feedback
smart lifecycle reminders
first-touch acquisition analytics
business-health, retention and funnel analytics
Rental → Cleaning and Rental → first-Transfer benefits
```

The canonical project context and product constraints are maintained under [`docs/`](docs/):

- [current AI/human context](docs/CONTEXT.md);
- [knowledge routing index](docs/INDEX.md);
- [product manifesto](docs/strategy/manifesto.md);
- [product roadmap](docs/strategy/roadmap.md).

## Architecture

```text
Standalone web / Telegram Mini App
                 │
                 ▼
      Caddy application runtime
       ├── serves Vite assets
       └── proxies API / OAuth
                 │
                 ▼
     Spring Boot modular monolith
                 │
                 ▼
             PostgreSQL
```

Backend:

```text
Java 25
Spring Boot 4
Gradle
PostgreSQL
Liquibase
JPA/Hibernate + JDBC/native SQL
Testcontainers
```

Frontend:

```text
React 19
TypeScript
Vite
RU / EN
mobile-first
standalone web + Telegram Mini App
```

Persistence is intentionally hybrid: JPA owns aggregate mutation and locking, while JDBC/native SQL owns analytics and specialized atomic or batch operations. Liquibase is the only schema-change mechanism.

## Repository layout

```text
cleany/
├── frontend/      React, Vite and the Caddy application image
├── backend/       Spring Boot modular monolith
├── deploy/        VPS release, backup and operational tooling
├── performance/   local-only k6/JFR measurement contour
└── docs/          canonical product, architecture and operations knowledge base
```

## Frontend preview

```bash
cd frontend
npm install
npm run dev
```

Copy `frontend/.env.example` to `frontend/.env.local` and keep `VITE_PREVIEW_MODE=true` to use preview adapters and mock APIs without Telegram or PostgreSQL.

To connect the Vite development server to a locally running backend:

```env
VITE_PREVIEW_MODE=false
VITE_API_BASE_URL=
```

The development server proxies `/api`, `/oauth2` and Google OAuth callbacks to `localhost:8080`.

## Full local stack

The complete PostgreSQL, backend, Caddy/frontend and Telegram long-polling stack can be started with Docker Compose.

See the [local Docker runbook](docs/local-docker-runbook.md).

## Performance contour

Stage 7.5 added a reproducible local-only performance environment with deterministic synthetic data, k6 scenarios, JFR capture and performance-profile JVM/Hikari/Hibernate/scheduler metrics.

See:

- [performance harness](performance/README.md);
- [measured baseline](performance/baseline.md);
- [codebase audit](performance/codebase-baseline.md);
- [after-hardening report](performance/after-hardening.md).

Do not run the stress harness against staging, production, the public domain or a CI worker.

## Deployment and CI

Production/staging deployment uses Docker Compose, a single Caddy application image, automatic HTTPS, pre-deployment PostgreSQL backups and exact-revision releases over SSH.

See the [VPS deployment runbook](docs/vps-deployment-runbook.md) and [staging continuous-deployment guide](docs/staging-continuous-deployment.md).

The GitHub Actions workflow intentionally runs automatically **only after a push to `main`**. Creating or updating a pull request does not start the workflow, so the same backend/frontend checks are not executed both before and after merge. A pre-merge full validation remains available through manual `workflow_dispatch` on the feature branch; deployment is still restricted to `main`.

Change detection is path-aware:

| Changed area | Backend tests | Frontend build | Staging deploy |
| --- | --- | --- | --- |
| `backend/**` | run | skip unless frontend also changed | eligible after successful checks |
| `frontend/**` | skip unless backend also changed | run | eligible after successful checks |
| `deploy/**` or `compose.prod.yaml` | skip | skip | eligible |
| documentation/other non-runtime files only | skip | skip | skip |
| manual workflow on `main` | run | run | eligible |

This means deployment-only changes are no longer ignored, while documentation-only commits remain cheap.

## Main customer routes

```text
/                         contextual home / service catalog
/cleaning                 Cleaning order flow
/cleaning/orders/:id      Cleaning order detail
/rent                     published Rental catalog
/rent/properties/:slug    Rental property detail
/rent/bookings/:id        Rental booking detail
/transfer                 Transfer booking flow
/transfer/bookings/:id    Transfer booking detail
/account/activity         unified customer activity
/notifications            durable notification inbox
/account                  customer account
```

Main administration routes:

```text
/admin
/admin/analytics
/admin/support
/admin/cleaning
/admin/rent/properties
/admin/rent/bookings
/admin/transfer/bookings
/admin/transfer/configuration
```

## Product and engineering constraints

- Loco solves the task; it does not expose a provider catalog as the product.
- The customer must not become the dispatcher after placing an order.
- Known safe customer context should reduce actions in later orders.
- Prices, discounts, identity, ownership, availability and lifecycle transitions are backend-authoritative.
- Cleaning, Rental and Transfer remain separate business aggregates.
- Cross-service features use explicit bridges/read models rather than a universal transaction model.
- Performance work starts from a measured regression or real telemetry.
- Do not introduce microservices, Kafka, S3/MinIO, another database or a generic benefit engine without a concrete measured need.
- The pilot still excludes online payments and external rental marketplace integrations.
