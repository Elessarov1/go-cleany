---
title: Loco Place Local Performance Measurement
type: operations-runbook
status: active
scope: platform
updated: 2026-09-05
---

# Local performance measurement

Stage 7.5 establishes evidence before performance optimization. The reproducible harness lives in [`performance/`](../../performance/README.md) and is intentionally local-only. The measured before/after record is kept in [`performance/baseline.md`](../../performance/baseline.md), the final codebase audit in [`performance/codebase-baseline.md`](../../performance/codebase-baseline.md), and the closure decision in [`performance/after-hardening.md`](../../performance/after-hardening.md). Raw k6 dumps, CSV and JFR recordings remain local and ignored.

The performance environment uses a dedicated Compose project, database and volume. It disables Telegram and all schedulers by default, binds host ports only to loopback, rejects remote load targets, and stores generated manifests, k6 results and JFR recordings outside version control.

The backend `performance` profile exposes health and metrics and enables Hibernate statistics. Production remains health-only and no JMX port is exposed.

Scheduler observability is shared across Smart Reminders, data retention and Rental Cleaning Benefit issuance. It records run duration plus candidate, processed and failure counters tagged by job/outcome and emits one structured completion/failure log per run. This instrumentation does not change schedules or business semantics.

The local baseline identified four concrete priorities and the same branch verified their first fixes:

1. nested customer resolution exhausted the ten-connection Hikari pool under mixed traffic;
2. Rental served full-size images to card and thumbnail consumers;
3. frontend routes and both locales were included eagerly in the initial bundle;
4. Smart Reminder Telegram delivery ran while its database transaction was open.

Customer resolution is now performed once at the authenticated request boundary and reused by customer-owned services. Rental stores full/card/thumbnail variants and public lists load only covers. Frontend routes and locales load on demand. Reminder inbox persistence commits before optional Telegram delivery begins.

The post-fix mixed profile completed at 215.59 requests/second with zero errors and 9.26 ms p95 instead of 1–2 requests/second, 58.4% errors and approximately 31-second waits. The UI-shaped image run transferred 65 MB instead of 608.5 MB, an approximately 89.3% reduction. The final production-shaped contour uses one Caddy runtime for both the Vite build and direct API/OAuth proxying. Its end-to-end stress run completed through 100 VU at 4,163.10 requests/second with zero errors, 31.24 ms p95 and 55.06 ms p99; no pilot-relevant saturation point was reached on the development workstation.

The Stage 7.5 closure audit found no evidence for another database/index/runtime optimization. Responsive Rental media keeps the canonical full asset plus card/thumbnail variants, removal deletes all unreferenced variant assets, and existing nullable variant columns safely fall back to the canonical asset. The obsolete Nginx runtime was removed instead of tuned, production Compose now has PostgreSQL, backend and a single Caddy application image, and fingerprinted `/assets/*` responses have immutable caching while HTML/SPA fallbacks remain non-cacheable.

The one-off Rental media repair closed the nullable-variant rollout gap for deployed data and was then removed from the runtime. New uploads always create all three variants; an old backup with missing variants must be repaired explicitly rather than during every startup. Public media bytes use a dedicated 64 MiB weighted in-process Caffeine cache with per-property after-commit invalidation and generation protection. Standard Micrometer cache meters expose hits, misses, evictions and entry count under cache name `rental-public-media`; `loco.rental.media.cache.bytes` reports the current weighted byte size. These meters are reachable through Actuator only in the local `performance` profile, like the other Stage 7.5 diagnostics.

The P5 closure kept the current JPA/JDBC division: JPA owns aggregate mutation and locking, while JDBC/native SQL owns analytics and specialized atomic or batch queries. A focused Rental fetch plan now loads the property required by customer/admin booking responses in the same query and is protected by a constant-query-count integration test. The audit also removed obsolete header/theme CSS, preserved lazy route boundaries, isolated preview APIs and the preview panel from production output, and replaced page-specific iOS date-input workarounds with one shell-wide guard. Production bundle audits explicitly use `VITE_PREVIEW_MODE=false` because the local `.env.local` is a preview environment. Main CSS moved from 161.01 KB / 26.46 KB gzip to 156.15 KB / 25.90 KB gzip without a UI redesign.

The in-process Rental media cache is functionally verified but was deliberately not load-benchmarked after introduction. Do not infer a measured cache throughput improvement from the earlier image-size result; run a new local before/after scenario only when a later hot-path change or regression requires it.

Do not run this harness from CI, a pipeline worker, staging or the VPS. Later optimizations require a new measured regression or production telemetry; the baseline does not justify a larger connection pool, Redis/CDN, storage migration or speculative JVM/index tuning.
