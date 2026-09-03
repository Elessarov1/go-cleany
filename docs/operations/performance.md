---
title: Loco Place Local Performance Measurement
type: operations-runbook
status: active
scope: platform
updated: 2026-09-03
---

# Local performance measurement

Stage 7.5 establishes evidence before performance optimization. The reproducible harness lives in [`performance/`](../../performance/README.md) and is intentionally local-only.

The performance environment uses a dedicated Compose project, database and volume. It disables Telegram and all schedulers by default, binds host ports only to loopback, rejects remote load targets, and stores generated manifests, k6 results and JFR recordings outside version control.

The backend `performance` profile exposes health and metrics and enables Hibernate statistics. Production remains health-only and no JMX port is exposed.

Scheduler observability is shared across Smart Reminders, data retention and Rental Cleaning Benefit issuance. It records run duration plus candidate, processed and failure counters tagged by job/outcome and emits one structured completion/failure log per run. This instrumentation does not change schedules or business semantics.

MR P1A contains the harness and observability only. MR P1B records the unchanged baseline and ranks P2 Rental Media, P3 Frontend Boot and P4 Scheduler/DB/Runtime work from measured evidence.
