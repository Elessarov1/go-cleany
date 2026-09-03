---
title: Loco Place Local Performance Measurement
type: operations-runbook
status: active
scope: platform
updated: 2026-09-04
---

# Local performance measurement

Stage 7.5 establishes evidence before performance optimization. The reproducible harness lives in [`performance/`](../../performance/README.md) and is intentionally local-only. The measured before/after record is kept in [`performance/baseline.md`](../../performance/baseline.md); raw k6 dumps, CSV and JFR recordings remain local and ignored.

The performance environment uses a dedicated Compose project, database and volume. It disables Telegram and all schedulers by default, binds host ports only to loopback, rejects remote load targets, and stores generated manifests, k6 results and JFR recordings outside version control.

The backend `performance` profile exposes health and metrics and enables Hibernate statistics. Production remains health-only and no JMX port is exposed.

Scheduler observability is shared across Smart Reminders, data retention and Rental Cleaning Benefit issuance. It records run duration plus candidate, processed and failure counters tagged by job/outcome and emits one structured completion/failure log per run. This instrumentation does not change schedules or business semantics.

The local baseline identified four concrete priorities and the same branch verified their first fixes:

1. nested customer resolution exhausted the ten-connection Hikari pool under mixed traffic;
2. Rental served full-size images to card and thumbnail consumers;
3. frontend routes and both locales were included eagerly in the initial bundle;
4. Smart Reminder Telegram delivery ran while its database transaction was open.

Customer resolution is now performed once at the authenticated request boundary and reused by customer-owned services. Rental stores full/card/thumbnail variants and public lists load only covers. Frontend routes and locales load on demand. Reminder inbox persistence commits before optional Telegram delivery begins.

The post-fix mixed profile completed at 215.59 requests/second with zero errors and 9.26 ms p95 instead of 1–2 requests/second, 58.4% errors and approximately 31-second waits. The UI-shaped image run transferred 65 MB instead of 608.5 MB, an approximately 89.3% reduction. The direct backend stress segment completed through 100 VU with zero errors and 26.76 ms p95; no pilot-relevant saturation point was reached on the development workstation.

Do not run this harness from CI, a pipeline worker, staging or the VPS. Later optimizations require a new measured regression or production telemetry; the baseline does not justify a larger connection pool, Redis/CDN, storage migration or speculative JVM/index tuning.
