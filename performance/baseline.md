# Stage 7.5 local baseline and optimization evidence

Status: baseline and first optimization pass measured on the dedicated local Docker contour. No scenario in this
document was run against the VPS, staging, production, or a CI worker.

## Reproduction identity

| Field | Value |
|---|---|
| Commit SHA | `3153ce6762623bc93068b58182fa7c90a54df05c` |
| Date/time and timezone | 2026-09-03, Europe/Moscow |
| Java / Spring Boot | Java 25.0.4.1 / Spring Boot 4.1.0 |
| Docker / Compose | Docker Desktop 4.37.1, Engine 27.4.0 / Compose 2.31.0 |
| PostgreSQL | 16.14 Alpine |
| k6 image | `grafana/k6:2.1.0` |
| Host CPU / RAM / OS | AMD Ryzen 7 3700X, 16 logical CPU, 64 GiB RAM, Windows 11 10.0.22621 |
| Container CPU / memory settings | Docker Desktop: 16 CPU, 31.32 GiB RAM; no per-service limits |

## Dataset and runtime state

| Field | Value |
|---|---|
| Seed | `42` |
| Scale | `1` |
| Anchor date | `2026-09-03` |
| Dataset counts | 100 customers, 500 Cleaning, 20 Rental properties × 6 images, 200 Rental bookings, 300 Transfer bookings, 1,000 notifications, 150 reminders, 75 support cases |
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
8. Capture JFR for the image-heavy and mixed-API bottleneck investigations. Use the
   structured counters for sub-second isolated scheduler runs, where interval sampling
   would be less reliable than the job's own measured duration.

Do not load-test staging or production.

## k6 results

Record each measured run, not only an average.

| Scenario / run | RPS | p50 | p95 | p99 | Error rate | Received bytes | Sent bytes |
|---|---:|---:|---:|---:|---:|---:|---:|
| smoke 1 | 35.07 | 3.21 ms | 8.39 ms | not captured | 0% | 4,633,244 | 61,008 |
| smoke 2 | 36.44 | 2.93 ms | 7.16 ms | not captured | 0% | 4,683,072 | 61,664 |
| smoke 3 | 34.19 | 2.71 ms | 7.03 ms | not captured | 0% | 4,683,016 | 61,664 |
| rental-browse 1 | 73.53 | 3.33 ms | 9.85 ms | not captured | 0% | 29,034,863 | 391,336 |
| rental-browse 2 | 72.16 | 2.43 ms | 8.90 ms | not captured | 0% | 29,239,318 | 394,064 |
| rental-browse 3 | 74.38 | 2.31 ms | 8.61 ms | not captured | 0% | 29,236,384 | 394,064 |
| image-burst 1 | 113.91 | 2.86 ms | 4.75 ms | not captured | 0% | 608,525,928 | 537,732 |
| image-burst 2 | 116.81 | 2.56 ms | 4.29 ms | not captured | 0% | 608,525,928 | 537,732 |
| image-burst 3 | 113.66 | 2.56 ms | 4.23 ms | not captured | 0% | 608,525,928 | 537,732 |
| mixed-api diagnostic 1 | 1.23 | 31,238.68 ms | 31,411.93 ms | not captured | 58.26% | 133,796 | 11,525 |
| mixed-api diagnostic 2 | 1.98 | 31,245.58 ms | 31,455.68 ms | not captured | 58.40% | 146,580 | 11,525 |
| post-fix smoke | 32.74 | 3.41 ms | 11.47 ms | 23.41 ms | 0% | 3,100,000 | 60,000 |
| post-fix rental-browse | 70.40 | 3.50 ms | 5.69 ms | 7.92 ms | 0% | 21,000,000 | 395,000 |
| post-fix image-burst 1 | 128.36 | 3.05 ms | 4.77 ms | 7.15 ms | 0% | 65,000,000 | 682,000 |
| post-fix image-burst 2 | 128.03 | 2.58 ms | 3.09 ms | 3.72 ms | 0% | 65,000,000 | 681,000 |
| post-fix mixed-api | 215.59 | 4.32 ms | 9.26 ms | 15.00 ms | 0% | 29,000,000 | 1,300,000 |
| post-fix direct-backend stress | 5,022.91 | 3.49 ms | 26.76 ms | not captured | 0% | 2,300,000,000 | 123,000,000 |
| final Caddy smoke | 38.34 | 1.25 ms | 5.23 ms | 6.37 ms | 0% | 3,700,000 | 62,000 |
| final Caddy rental-browse | 73.48 | 3.38 ms | 5.14 ms | 7.09 ms | 0% | 21,000,000 | 397,000 |
| final Caddy image-burst | 127.73 | 2.78 ms | 3.43 ms | 4.11 ms | 0% | 65,000,000 | 681,000 |
| final Caddy mixed-api | 215.48 | 3.91 ms | 9.17 ms | 17.43 ms | 0% | 29,000,000 | 1,300,000 |
| final Caddy stress | 4,163.10 | 4.63 ms | 31.24 ms | 55.06 ms | 0% | 1,956,919,257 | 98,412,472 |

## Runtime and database observations

### Confirmed before optimization

- The three ordinary Rental browse runs remained below 10 ms p95 with no errors. The
  public JSON browse path is not the first saturation point at this dataset scale.
- Image burst returned about 609 MB per 45-second run at only 20 six-image gallery
  iterations per second (about 13 MB/s). Latency stayed low on the development machine,
  but transfer volume makes the single 1920 px JPEG representation unsuitable for cards
  and thumbnails on a small VPS. The 61-second JFR contained 12,889 allocation samples
  and 19 young-GC cycles, with no old GC.
- Mixed API exhausted all ten Hikari connections. Backend logs report
  `total=10, active=10, idle=0` followed by 30-second acquisition timeouts and up to 30
  waiters. CPU remained mostly below 5% and RSS around 607 MiB, so this is connection
  starvation rather than CPU or heap saturation.
- Root cause is the request pattern in which transactional customer services acquire an
  outer connection and then call `CustomerAccountService.currentCustomer()` with
  `REQUIRES_NEW`. Under concurrent account requests, every outer transaction can hold one
  connection while waiting for a second one. Increasing the pool would only move the
  deadlock threshold and is not an acceptable fix.
- The first mixed diagnostic also inherited `spring.jpa.show-sql=true` from the local
  profile. The performance profile now explicitly disables it. The second diagnostic
  reproduced the Hikari starvation without SQL logging, proving that logging was noise,
  not the root cause.
- Raw k6 summaries, Docker samples and JFR recordings remain ignored under
  `performance/results/`. Starting with the next measurement, k6 summaries include p99.

### Verified after optimization

- Resolving the authenticated customer once before opening customer-owned service
  transactions removed the nested `REQUIRES_NEW` pool deadlock. The identical mixed API
  profile improved from 1–2 requests/second with 58.4% errors and roughly 31-second waits
  to 215.59 requests/second, zero errors, 9.26 ms p95 and 15 ms p99.
- Rental images now have full, card and thumbnail variants. The UI-shaped image burst is
  intentionally stricter after the change (one full image plus six thumbnails rather than
  six full images), yet transferred data fell from 608.5 MB to 65 MB per run: about 89.3%.
- The public Rental list loads only cover media. Its payload traffic fell from about 29 MB
  to 21 MB per run and p95 improved from 8.61–9.85 ms to 5.69 ms. The smoke profile, which
  also reads the list, fell from 4.6–4.7 MB to 3.1 MB.
- The production-shaped direct backend stress run completed through 100 VU with 1,052,694
  successful requests, zero errors and 26.76 ms p95. No application saturation point was
  reached inside the configured 100-VU envelope on this workstation.
- An earlier stress attempt through the performance frontend proxy produced 72.43% nginx
  `502 Address not available` responses after exhausting proxy-side ephemeral ports. It is
  retained only as the evidence that exposed an inaccurate two-proxy test topology; no nginx
  tuning was retained.
- The final contour uses the same single Caddy runtime as production: it serves Vite assets and
  proxies API/OAuth routes directly to backend. One end-to-end control run of every scenario
  completed with zero errors. The 100-VU stress run processed 874,391 requests at 4,163.10 RPS,
  31.24 ms p95 and 55.06 ms p99. The lower throughput than the direct-backend diagnostic is the
  expected cost of compression/proxy handling; it does not expose a pilot-relevant saturation
  point.
- The closure persistence audit confirmed that Liquibase change set `20260912-01` was applied.
  All 120 seeded Rental media rows had card and thumbnail variants, and all 360 full/card/thumbnail
  reference slots resolved to existing `media_asset` rows. Existing integration coverage verifies
  variant cleanup on property/media deletion; no additional persistence repair was justified.

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
| smart-reminders | 27 | 23 | 4 | 0 | 710 ms | sub-second run; no sustained CPU/heap pressure observed | notification creation plus eligibility reads; Telegram disabled |
| data-retention | 308 | 308 | 0 | 0 | 176 ms | sub-second run; no sustained CPU/heap pressure observed | four cleanup batches; following empty run was 5 ms |
| rental-cleaning-benefit | 20 | 20 | 0 | 0 | 531 ms | sub-second run; no sustained CPU/heap pressure observed | one configured issuance batch |

All scheduler measurements used a freshly recreated seed-42 dataset and exactly one
enabled job. At this scale none is a CPU or database bottleneck. Smart Reminders did have
an architectural latency risk not represented with Telegram disabled: external delivery
occurred while the database transaction remained open. The fix commits the durable inbox
and reminder state first, then performs optional channel delivery after commit.

## Frontend baseline

| Artifact | Raw | Gzip | Notes |
|---|---:|---:|---|
| JavaScript before | 727.38 KB | 199.31 KB | one eager application chunk containing every route and both locales |
| Initial shell after | 434.23 KB | 137.81 KB | entry and statically preloaded shared modules |
| Initial EN home after | 484.45 KB | 154.25 KB | shell + EN locale + home route; about 22.6% less gzip |
| Initial RU home after | 506.69 KB | 157.95 KB | shell + RU locale + home route; about 20.8% less gzip |
| CSS before / after | 164.95 / 164.95 KB | 26.99 / 26.99 KB | CSS extraction is effectively unchanged |
| RU locale after | 64.31 KB | 17.68 KB | loaded on demand only when active |
| EN locale after | 42.07 KB | 13.97 KB | loaded on demand only when active |

## Saturation and prioritization

- Fixed P2/P3/P4 priorities, in measured order: customer identity connection starvation;
  Rental image over-delivery and list overfetch; eager routes/locales; external Telegram
  calls inside the Smart Reminder transaction.
- No pool-size increase, cache tier, storage migration, JVM flag tuning or speculative index
  was justified. The final Caddy stress envelope is already far beyond pilot traffic on the
  development workstation, so further runtime tuning should wait for production-shaped
  telemetry and real usage.
- Evidence quality / caveats: before measurements contain three warm-JVM runs for steady
  scenarios. Following the request to avoid redundant load, after measurements use one
  control run except image burst, which was repeated once and produced the same 65 MB.
