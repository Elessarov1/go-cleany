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

## Rental period-first search contour

Measured on 2026-09-09 from the working tree based on commit
`7190f5b618150f8420b6cfbecb091fae185add49`, with scale `1`, seed `42` and anchor date
`2026-09-09`. Both runs used the dedicated local `loco-perf` Compose project and the Caddy
service at `http://frontend`; no remote environment was used. The host was Linux
6.8.0 x86_64 with Docker Engine 29.7.2 / Compose 2.26.1; the backend image used Java 25.

| Rental search run | RPS | p50 | p95 | p99 | Error rate | Received bytes | Sent bytes |
|---|---:|---:|---:|---:|---:|---:|---:|
| first search-path run | 25.05 | 17.56 ms | 24.23 ms | 38.14 ms | 0% | 9.4 MB | 174 KB |
| same-stack warm run | 25.46 | 15.28 ms | 19.27 ms | 25.25 ms | 0% | 9.6 MB | 177 KB |

The 45-second warm run completed 1,152 DATE_RANGE/MONTHLY searches and all 3,456 checks
(status, `Cache-Control: no-store`, execution ID) passed. Its summary is retained under the
ignored `performance/results/` directory. The first run's JSON export exposed a Linux bind-mount
UID mismatch; the harness now runs k6 as configurable `PERF_CONTAINER_USER` and the warm summary
was persisted successfully.

The application read path remains two data queries regardless of result count: one availability
query followed by one batched cover query. Pricing is calculated from the compact row projection
and performs no SQL. On the seeded 20-property/160-occupancy dataset, `EXPLAIN (ANALYZE, BUFFERS)`
used `idx_rental_property_status_order` and completed in 0.288 ms. PostgreSQL chose a sequential
scan of the small occupancy table; adding another search index is not justified by this evidence.

### Period-first legacy removal follow-up

After making period-first search the only customer Rental flow, the same local contour was run
again on 2026-09-09 with scale `1`, seed `42`, anchor date `2026-09-09`, eight VUs and a 45-second
duration through Caddy. One reset-and-seed run warmed the rebuilt stack; the second same-stack run
is the measured result below. No remote environment was used.

| Rental search run | RPS | p50 | p95 | p99 | Error rate | Received bytes | Sent bytes |
|---|---:|---:|---:|---:|---:|---:|---:|
| compatibility baseline warm | 25.46 | 15.28 ms | 19.27 ms | 25.25 ms | 0% | 9.6 MB | 177 KB |
| replacement warm-up | 25.22 | 16.25 ms | 23.46 ms | 33.51 ms | 0% | 9.4 MB | 175 KB |
| replacement measured warm | 25.50 | 15.16 ms | 18.68 ms | 20.36 ms | 0% | 9.6 MB | 177 KB |
| measured delta | +0.18% | -0.80% | -3.06% | -19.38% | unchanged | unchanged | unchanged |

The measured run completed 1,152 searches and all 3,456 checks passed. This single controlled
comparison shows no regression, but the small RPS/p50/p95 differences and the lower p99 are not
claimed as an optimization result: the removed compatibility code was outside the hot search
path and run-to-run variance can explain the change. JFR was therefore not justified.

The repeated search `EXPLAIN (ANALYZE, BUFFERS)` kept the same plan: an index scan through
`idx_rental_property_status_order` plus an anti-join against the small occupancy table. Execution
time was 0.300 ms versus 0.288 ms in the baseline. The 0.012 ms difference is not material and no
new index was added.

Physical production-code counts, excluding `package-lock.json`, tests and documentation, changed
as follows: the Rental backend package fell from 5,229 to 5,117 lines (-112, -2.14%); frontend
production TypeScript/TSX fell by 49 lines after accounting for the new 37-line contract test.
The shared HTTP exception mapper gained 9 lines to return a stable 405 for the removed POST route,
so the net production reduction in these measured scopes is 152 lines. Removed production surface
includes two HTTP endpoints, one frontend route alias, two legacy quote records and the
intermediate search-result record.

### Cursor pagination follow-up

Measured on 2026-09-11 from the working tree based on commit `cb04fc6`, using the dedicated local
`loco-perf` Compose project through Caddy, scale `2`, seed `42`, anchor date `2026-09-11`, eight VUs
and 45-second runs. The scale-2 dataset contained 40 published Rental properties and 320 occupancy
rows. A reset/seed run warmed the rebuilt stack; the next same-stack first-page run is the measured
comparison. No remote environment was used.

| First-page Rental search | RPS | p50 | p95 | p99 | Error rate | Received bytes | Representative payload |
|---|---:|---:|---:|---:|---:|---:|---:|
| unbounded baseline warm | 25.46 | 15.52 ms | 17.79 ms | 18.83 ms | 0% | 19 MB | 24,687 B / 40 properties |
| paginated warm-up | 25.21 | 16.49 ms | 23.36 ms | 34.62 ms | 0% | 9.6 MB | at most 20 properties |
| paginated measured warm | 25.50 | 15.42 ms | 17.90 ms | 19.66 ms | 0% | 9.8 MB | 12,625 B / 20 properties |
| measured delta | +0.14% | -0.64% | +0.62% | +4.41% | unchanged | about -48% | -48.86% |

The measured first-page run completed 1,152 searches and all 3,456 checks passed. Throughput and
p50/p95 are effectively unchanged; the p99 increase is 0.83 ms and does not justify JFR. The
material outcome is bounded work and transfer: the representative first response fell by 12,062
bytes, and total received traffic was approximately halved.

A separate warm cursor-only contour reused one first-page execution across every request. It
completed 1,184 cursor iterations plus one setup request with 0% errors: 26.21 RPS, 4.37 ms p50,
5.64 ms p95 and 6.34 ms p99. The representative second page was 12,556 bytes / 20 properties,
retained the same execution ID and had no duplicates with the first page. This contour is reported
separately rather than mixed into the comparable first-page measurement.

`EXPLAIN (ANALYZE, BUFFERS)` used `idx_rental_property_status_order` for both requests. The bounded
first query completed in 0.544 ms and stopped after the `LIMIT 21` probe; the cursor query applied
`ROW(display_order, id) > ROW(20, 20)` as an index condition and completed in 1.067 ms in the single
diagnostic sample. Each non-empty application page still performs one property/availability query
and one batched cover query, prices only its returned 20 items, and performs no `COUNT` or `OFFSET`.
No new index is justified by this evidence.

An exact production build from the pre-change `cb04fc6` tree was compared with the changed build;
no runtime dependency was added:

| Production artifact | Before raw / gzip | After raw / gzip | Delta |
|---|---:|---:|---:|
| Rental search route | 9.05 / 2.94 KB | 10.33 / 3.30 KB | +1.28 / +0.36 KB |
| Shared CSS | 170.16 / 27.86 KB | 170.43 / 27.89 KB | +0.27 / +0.03 KB |
| EN locale | 47.26 / 15.52 KB | 47.44 / 15.58 KB | +0.18 / +0.06 KB |
| RU locale | 72.90 / 20.03 KB | 73.18 / 20.11 KB | +0.28 / +0.08 KB |

The route-level increase is the load-more state, retry/race protection and deduplication; it remains
lazy-loaded and does not affect routes that never open Rental search.

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

The period-first production build keeps the new pages lazy and adds no runtime dependency:

| Period-first artifact | Raw | Gzip |
|---|---:|---:|
| Rental search route | 9.05 KB | 2.94 KB |
| Rental property route | 19.38 KB | 5.62 KB |
| Shared CSS | 164.10 KB | 27.03 KB |
| EN locale | 47.19 KB | 15.48 KB |
| RU locale | 72.75 KB | 19.99 KB |

Vitest, jsdom and Testing Library are development-only dependencies and therefore do not enter
the production chunks.

After legacy removal, the Rental search chunk remains 9.05 KB / 2.94 KB gzip. The property chunk
is 19.43 KB / 5.63 KB gzip (+0.05 KB raw and +0.01 KB gzip); shared CSS and both locale chunks are
unchanged. The code reduction is therefore a maintenance improvement, not a bundle-size or runtime
performance claim.

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
