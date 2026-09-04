# Stage 7.5 after-hardening report

Status: Stage 7.5 is complete through P5. All performance scenarios referenced below were executed
locally during P1–P4 and are preserved in [`baseline.md`](baseline.md). P5 did not rerun k6, stress,
image-burst, JFR, VPS or CI performance workloads.

## Delivered changes

### P1 — measurement and observability

- Added the isolated `loco-perf` Compose project, deterministic seed data, local-only k6 scenarios
  and JFR capture.
- Added performance-profile JVM, Hikari, Hibernate and scheduler metrics.
- Instrumented Smart Reminders, data retention and Rental Cleaning Benefit jobs with consistent
  counters and structured run summaries.
- Enforced local target validation; the performance harness is not part of CI or deployment.

### P2 — measured backend and Rental media fixes

- Removed nested customer identity resolution that could hold one Hikari connection while waiting
  for another.
- Reused request-resolved `CurrentCustomer` across customer-owned services.
- Added full/card/thumbnail Rental media variants and cover-only list projections.
- Completed the one-off legacy media repair and then removed the startup backfill runtime.
- Added a bounded 64 MiB shared Caffeine cache for public Rental bytes with versioned URLs,
  per-property after-commit invalidation and generation protection.

### P3 — frontend boot

- Lazy-loaded top-level routes.
- Loaded only the active RU or EN locale.
- Kept preview mocks and the preview panel out of production output through explicit build-time
  dynamic boundaries.
- Kept fingerprinted assets immutable while HTML and SPA fallbacks remain non-cacheable.

### P4 — scheduler, database and runtime boundaries

- Moved optional Smart Reminder Telegram delivery after the durable database commit.
- Kept scheduler failure isolation and inbox notification semantics intact.
- Removed the obsolete Nginx layer and aligned local/performance/production delivery on Caddy.
- Retained the existing Hikari pool, PostgreSQL, Hibernate and JVM settings because measurements did
  not justify speculative tuning.

### P5 — audit and targeted simplification

- Recorded source, duplication, largest-file, dead-code and production-bundle evidence in
  [`codebase-baseline.md`](codebase-baseline.md).
- Consolidated no-detail API error construction without changing public responses.
- Made Rental booking fetch plans explicit and added a constant-query-count integration regression.
- Removed obsolete header/language/theme selectors and centralized header geometry.
- Replaced the icon conditional chain with an exhaustive typed mapping while preserving SVG output.
- Removed the single TypeScript unused binding reported by the strict audit.
- Replaced Cleaning- and Transfer-specific iOS date-input workarounds with one shell-wide form guard.

## Measured outcomes retained from P1–P4

| Area | Before | Verified result |
|---|---:|---:|
| Mixed API | 1–2 RPS, 58.4% errors, about 31 s waits | 215.59 RPS, 0% errors, 9.26 ms p95 |
| UI-shaped Rental image traffic | 608.5 MB per run | 65 MB per run, about 89.3% lower |
| Final Caddy stress envelope | not available | 4,163.10 RPS, 0% errors, 31.24 ms p95, 55.06 ms p99 through 100 VU |
| Initial EN home gzip | 199.31 KB eager JS baseline | 154.25 KB after route/locale splitting |
| Initial RU home gzip | 199.31 KB eager JS baseline | 157.95 KB after route/locale splitting |

The development workstation did not reach a pilot-relevant application saturation point inside the
configured 100-VU envelope. This is evidence against more speculative runtime infrastructure, not a
production capacity guarantee.

## Current frontend closure

The P5 production build contains 61 JS chunks. Entry JS is 278.53 KB raw / 84.40 KB gzip, the shared
framework chunk is 91.85 KB / 30.50 KB gzip, RU is 64.31 KB / 17.82 KB gzip and EN is 42.07 KB /
14.04 KB gzip. Main CSS fell from 161.01 KB / 26.46 KB gzip to 156.15 KB / 25.90 KB gzip.

Admin routes remain lazy and absent from entry module preloads. Preview mocks are absent from emitted
production JavaScript when the audit build explicitly uses `VITE_PREVIEW_MODE=false`; local
`.env.local` preview settings are intentionally excluded from these figures. The route count and
customer/admin chunk boundaries were deliberately not changed without a measured startup regression.

## Persistence conclusion

Keep the existing JPA/JDBC balance. JPA remains appropriate for aggregates and locking; JDBC/native
queries remain appropriate for analytics and specialized atomic/batch operations. The existing
measurement does not support Hibernate replacement, jOOQ adoption, new indexes, a larger Hikari pool
or another storage tier.

P5 fixed the concrete Rental property N+1 risk using method-level entity graphs. Home/Activity full
history composition is documented as a future pagination/read-model candidate, but its measured path
is currently healthy.

## Cache evidence boundary

The public Rental memory cache was added after the original image-size measurement. Integration tests
prove that a warm read does not execute another database statement, mutation invalidates only the
affected property, and archived/deleted properties cannot be served from stale entries. Micrometer
exposes hit, miss, eviction, entry-count and weighted-byte metrics in the local performance profile.

No post-cache image-burst or stress run was performed by explicit project decision. Therefore this
report does not claim a measured latency or throughput improvement from Caffeine itself.

## Deferred work

- production-shaped telemetry and capacity planning after real customer traffic exists;
- Home/Activity pagination or a dedicated read model only if volume/query telemetry requires it;
- durable external notification delivery checkpoints and partial-delivery retries;
- broader stylesheet modularization only alongside affected product screens;
- another local benchmark only after a change or regression touches a measured hot path.

## Closure decision

Stage 7.5 has a reproducible local harness, retained evidence, operational guardrails and no remaining
measured P2/P3/P4 bottleneck. The platform can proceed to Contextual Benefits without another
performance optimization phase. Future optimization work starts from an observed regression or real
telemetry and compares one relevant local scenario before and after the change.
