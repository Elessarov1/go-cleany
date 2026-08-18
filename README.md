# go-cleany

go-cleany is a mobile-first cleaning-order MVP for customers in Alanya. A customer creates an order, every configured cleaner receives it through the Telegram bot, and the first cleaner to accept becomes the assigned cleaner. The assigned cleaner sends the completed photo report through the bot, which forwards it to the customer and completes the order. Administrators can inspect operational statistics and the append-only order history in the web interface or through bot commands.

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

Open `http://localhost:5173`. The frontend uses `BrowserPlatform` and `MockCleaningApi` when no backend URL is configured, so Telegram, a backend, and PostgreSQL are not required for the visual prototype.

Developer scenarios are available at `http://localhost:5173/?preview=true`.

To connect the frontend to a locally running backend, create `frontend/.env.local`:

```env
VITE_API_BASE_URL=http://localhost:8080
```

## Backend foundation

The backend is a single Spring Boot application. It owns configuration, price calculation, customer-scoped order access, lifecycle validation, the concurrency-safe order claim operation, and Telegram photo-report delivery.

Local backend startup uses the isolated `local` profile. See [`backend/README.md`](backend/README.md) for environment and PostgreSQL setup. The default profile validates Telegram Mini App `initData`; browser-preview identity remains available only in the `local` profile.

## Full local stack

PostgreSQL, backend, frontend, Liquibase, and the Telegram bot long-polling flow can be run together
through Docker Compose. See the [local Docker runbook](docs/local-docker-runbook.md).

## VPS deployment

A production Compose stack, automatic HTTPS through Caddy, pre-deployment PostgreSQL backups, and
small release/rollback scripts are included under `deploy/`. Follow the
[VPS deployment runbook](docs/vps-deployment-runbook.md). Telegram remains on long polling, so one
backend instance must own the bot token.

## Product constraints

- Frontend UI remains independent from Telegram.
- Prices and customer identity are never trusted from the frontend in production.
- Backend order acceptance must be concurrency-safe: the first successful claim wins.
- The MVP intentionally excludes online payments, cleaner registration, automated dispatching, ratings, and object storage.
