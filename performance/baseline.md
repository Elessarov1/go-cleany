# Stage 7.5 baseline — P1B template

Status: not measured. Populate this file in MR P1B without introducing application optimizations.

## Reproduction identity

| Field | Value |
|---|---|
| Commit SHA | pending |
| Date/time and timezone | pending |
| Java / Spring Boot | pending |
| Docker / Compose | pending |
| PostgreSQL | pending |
| k6 image | `grafana/k6:2.1.0` |
| Host CPU / RAM / OS | pending |
| Container CPU / memory settings | pending |

## Dataset and runtime state

| Field | Value |
|---|---|
| Seed | `42` |
| Scale | `1` |
| Anchor date | pending |
| Dataset counts | pending verification against manifest |
| Warm-up | one complete warm-up |
| Smart Reminders | disabled for API/image runs |
| Data retention | disabled for API/image runs |
| Rental Cleaning Benefit | disabled for API/image runs |

## Measurement protocol

1. Remove the dedicated `loco-perf` volume.
2. Start the performance Compose project and seed with seed `42`.
3. Keep all schedulers disabled.
4. Run one warm-up.
5. Run smoke, Rental browse, image burst and mixed API three times each.
6. Run progressive stress once until clear degradation is visible.
7. Recreate the dataset before each scheduler measurement; enable exactly one scheduler.
8. Capture JFR for image burst, mixed API, stress and each scheduler run.

Do not load-test staging or production.

## k6 results

Record each measured run, not only an average.

| Scenario / run | RPS | p50 | p95 | p99 | Error rate | Received bytes | Sent bytes |
|---|---:|---:|---:|---:|---:|---:|---:|
| smoke 1 | pending | pending | pending | pending | pending | pending | pending |
| smoke 2 | pending | pending | pending | pending | pending | pending | pending |
| smoke 3 | pending | pending | pending | pending | pending | pending | pending |
| rental-browse 1–3 | pending | pending | pending | pending | pending | pending | pending |
| image-burst 1–3 | pending | pending | pending | pending | pending | pending | pending |
| mixed-api 1–3 | pending | pending | pending | pending | pending | pending | pending |
| stress | pending | pending | pending | pending | pending | pending | pending |

## Runtime and database observations

For each representative steady-state window record:

- process/container CPU and RSS;
- heap used/committed/max and allocation rate;
- GC count and pause time;
- Hikari active, idle, pending and acquisition time;
- Hibernate query, entity load/fetch and flush counters;
- representative request query counts;
- JFR hot methods, allocations, locks and socket/file activity.

## Scheduler isolation

| Job | Candidates | Processed | Skipped | Failed | Duration | CPU/RSS/heap notes | DB/query notes |
|---|---:|---:|---:|---:|---:|---|---|
| smart-reminders | pending | pending | pending | pending | pending | pending | pending |
| data-retention | pending | pending | pending | pending | pending | pending | pending |
| rental-cleaning-benefit | pending | pending | pending | pending | pending | pending | pending |

## Frontend baseline

| Artifact | Raw | Gzip | Notes |
|---|---:|---:|---|
| JavaScript | pending | pending | routes/locales remain eager in P1B |
| CSS | pending | pending | pending |
| RU locale | pending | pending | pending |
| EN locale | pending | pending | pending |

## Saturation and prioritization

- First clear saturation point: pending.
- Primary measured bottleneck: pending.
- Secondary bottlenecks: pending.
- Evidence quality / caveats: pending.

Rank the evidence-backed work for P2/P3/P4 here. P1B may fix only defects in the harness itself.
