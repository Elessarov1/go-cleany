# Loco Place local performance harness

This directory is the reproducible, local-only measurement environment for Stage 7.5. It is deliberately separate from normal local development and from production:

- Compose project: `loco-perf`;
- database: `loco_performance` in its own Docker volume;
- loopback-only host ports: PostgreSQL `15432`, backend `18080`, frontend `15173`;
- Telegram, Smart Reminders, data retention and Rental Cleaning Benefit schedulers are disabled by default;
- k6 runs from the pinned `grafana/k6:2.1.0` image;
- both the runner and every k6 script reject non-local targets.

Do not use this harness against staging, production or any remote URL.

## Prerequisites

- Docker Desktop / Docker Engine with Compose v2;
- JDK 25 for the host-side `performanceSeed` Gradle task;
- PowerShell 7+ on Windows, or POSIX `sh` plus `curl` on Linux/macOS.

No global k6 installation is needed. VisualVM is optional and is not part of the reproducible procedure.

## First run

From the repository root on Windows:

```powershell
.\performance\scripts\run-local.ps1 -Reset -Scenario smoke
```

On Linux/macOS:

```bash
RESET=true SCENARIO=smoke sh ./performance/scripts/run-local.sh
```

`Reset` removes only the fixed `loco-perf` Compose project and its dedicated volume. The script then builds the app, applies Liquibase, recreates the synthetic dataset, writes `performance/results/manifest.json`, and runs the selected scenario.

## Dataset

The default scale (`PERF_SCALE=1`) creates:

```text
100 customers
500 Cleaning orders
20 Rental properties × 6 images
200 Rental bookings
300 Transfer bookings
1,000 notifications
150 reminders
75 support cases
```

The default random seed is `42`. IDs, slugs, image content, source links and manifest ordering are deterministic for the same seed, scale and anchor date. The default anchor date is the current date in `Europe/Istanbul`; set `PERF_ANCHOR_DATE=YYYY-MM-DD` when exact cross-day reproducibility is required.

Useful Windows examples:

```powershell
$env:PERF_ANCHOR_DATE = '2026-09-03'
.\performance\scripts\run-local.ps1 -Reset -Scale 2 -Seed 42 -Scenario rental-browse
```

```powershell
$env:PERF_IMAGE_DIR = 'D:\private-rental-photos'
.\performance\scripts\run-local.ps1 -Reset -Scenario image-burst
```

`PERF_IMAGE_DIR` is optional. Supported local JPEG/PNG files are normalized into deterministic-size JPEG samples and never copied into Git. `performance/local-images/` is ignored if that location is preferred.

The seeder refuses to run unless the `performance` profile is active and `DB_URL` points to the dedicated `loco_performance` database on `localhost`, `127.0.0.1`, or the internal `postgres` Compose host.

## Scenarios

```powershell
.\performance\scripts\run-local.ps1 -Scenario rental-browse -SkipSeed -ReuseStack
.\performance\scripts\run-local.ps1 -Scenario image-burst -SkipSeed -ReuseStack
.\performance\scripts\run-local.ps1 -Scenario mixed-api -SkipSeed -ReuseStack
.\performance\scripts\run-local.ps1 -Scenario stress -SkipSeed -ReuseStack
```

Use `-Validation -Scenario all` for three short iterations of every scenario. Without `-Validation`, the scripts use their measurement durations and concurrency profiles. `stress.js` is the only progressive stress scenario.
`-ReuseStack` (or `REUSE_STACK=true` for the shell runner) skips Compose build/recreation while
still checking backend health. Use it for warm measurements after the initial reset so every run
uses the same warmed JVM and connection pool. It cannot be combined with `-Reset`.

Full k6 JSON summaries, CSV output, JFR files and the generated manifest belong in `performance/results/` and are ignored by Git.

## Metrics

Only the performance profile exposes `health` and `metrics`, and its host port is bound to loopback:

```powershell
Invoke-RestMethod http://127.0.0.1:18080/actuator/metrics
Invoke-RestMethod http://127.0.0.1:18080/actuator/metrics/hikaricp.connections.active
Invoke-RestMethod http://127.0.0.1:18080/actuator/metrics/hibernate.sessions.open
Invoke-RestMethod http://127.0.0.1:18080/actuator/metrics/loco.scheduler.runs
```

JVM, Hikari and Hibernate statistics are available in this profile. Production configuration remains health-only. JMX is not exposed by Compose.

Scheduler metrics use these names:

```text
loco.scheduler.runs
loco.scheduler.duration
loco.scheduler.candidates
loco.scheduler.processed
loco.scheduler.failures
```

Every meter has `job` and `outcome` tags. Every run also writes one structured `scheduler_run` log containing start/end, duration, candidates, processed, skipped and failed counters.

To measure one scheduler, enable only it and temporarily use a frequent local cron, then recreate the backend. Example for Smart Reminders in PowerShell:

```powershell
$env:PERF_SMART_REMINDERS_ENABLED = 'true'
$env:PERF_SMART_REMINDERS_CRON = '0 * * * * *'
docker compose -f performance/compose.perf.yaml up -d --force-recreate backend
```

Equivalent variables exist for `PERF_DATA_CLEANUP_*` and `PERF_RENTAL_CLEANING_BENEFIT_*`. Clear the variables and recreate the backend when done. Never enable multiple schedulers during an isolated scheduler measurement.

## JFR

Start a recording in one terminal immediately before the selected load in another:

```powershell
.\performance\scripts\capture-jfr.ps1 -Name image-burst -DurationSeconds 60
```

```bash
NAME=image-burst DURATION_SECONDS=60 sh ./performance/scripts/capture-jfr.sh
```

The script uses `jcmd` inside the backend container, records in memory for compatibility with Docker Desktop overlay filesystems, prints `jfr summary`, and copies the recording to `performance/results/`. It does not expose JMX or a remote JVM port.

## Stop

Keep the volume for another run:

```powershell
docker compose -f performance/compose.perf.yaml down
```

Remove the dedicated dataset as well:

```powershell
docker compose -f performance/compose.perf.yaml down --volumes
```

The measurement protocol and results template for MR P1B are in [baseline.md](baseline.md).
