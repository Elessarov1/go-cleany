# go services: go-cleany + go-renty

This repository contains a mobile-first service platform for customers in Alanya with two independent verticals:

- **go-cleany** — apartment cleaning with Telegram cleaner dispatch and photo reports;
- **go-renty** — apartment catalog, date-range and monthly pricing, immediate booking and rental administration.

Both verticals share customer identity, communication, media, retention and the React application shell. Their aggregates and business rules remain separate: `CleaningOrder` is not reused as a rental booking.

`go-renty` is the customer-facing brand. Existing technical namespaces intentionally remain `/rent`,
`/api/v1/rental` and `Rental*`.

## Repository layout

```text
cleany/
├── frontend/  React + TypeScript + Vite application
├── backend/   Spring Boot application
├── deploy/    Deployment and operations tooling
└── docs/      Product and engineering notes
```

## Frontend preview

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. Without a backend URL the frontend uses `BrowserPlatform` and mock APIs for cleaning, rental and customer data, so Telegram and PostgreSQL are not required for visual inspection.

Developer scenarios are available at `http://localhost:5173/?preview=true`.

To connect the frontend to a locally running backend, create `frontend/.env.local`:

```env
VITE_API_BASE_URL=http://localhost:8080
```

## Backend foundation

The backend is a single Spring Boot application. It owns customer identity, canonical media, cleaning and rental configuration, server-side pricing, customer-scoped access and lifecycle validation. PostgreSQL protects both first-cleaner-wins claiming and non-overlapping rental occupancy under concurrency. Rental customers explicitly choose a date range or a monthly term; monthly checkout and pricing are derived by the backend.

Local backend startup uses the isolated `local` profile. See [`backend/README.md`](backend/README.md) for environment and PostgreSQL setup. The default profile validates Telegram Mini App `initData`; browser-preview identity remains available only in the `local` profile.

## Full local stack

PostgreSQL, backend, frontend, Liquibase, and the Telegram bot long-polling flow can be run together
through Docker Compose. See the [local Docker runbook](docs/local-docker-runbook.md).

## VPS deployment

A production Compose stack, automatic HTTPS through Caddy, pre-deployment PostgreSQL backups, and
small release/rollback scripts are included under `deploy/`. Follow the
[VPS deployment runbook](docs/vps-deployment-runbook.md). Telegram remains on long polling, so one
backend instance must own the bot token.

After the first manual launch, [staging continuous deployment](docs/staging-continuous-deployment.md)
can deploy each tested `main` revision automatically through GitHub Actions and SSH.

## Main routes

```text
/                         service catalog
/cleaning                 go-cleany customer flow
/cleaning/orders          cleaning history
/rent                     published rental catalog
/rent/bookings            customer rental bookings
/admin                    service-aware admin entry
/admin/cleaning           cleaning administration
/admin/rent               rental administration
```

## Product constraints

- Frontend UI remains independent from Telegram.
- Prices, availability and customer identity are never trusted from the frontend in production.
- Backend order acceptance must be concurrency-safe: the first successful claim wins.
- Rental bookings and cleaning orders are separate domain aggregates.
- Rental property slugs, prices, availability and normalized catalog images are backend-owned data.
- The pilot intentionally excludes online payments, rental marketplace integrations, cleaner registration, ratings, and object storage.
