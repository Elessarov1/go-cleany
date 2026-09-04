# Stage 7.5 P5 codebase baseline

Status: completed on the P5 working tree based on `18d035c`. Generated build output, dependencies,
performance results and Git metadata are excluded from the source inventory.

No load, stress or JFR scenario was run for this audit.

## Reproduction

The inventory uses PowerShell `Get-ChildItem` plus `Get-Content | Measure-Object -Line`. The reported
source lines are non-blank lines, so they are useful for comparing this pass but are not directly
comparable with jscpd parser line counts.

Duplication was inspected with transient `jscpd 5.1.2`; it was not added to `package.json`:

```powershell
npx --yes jscpd --min-lines 10 --min-tokens 80 --reporters console `
  --format "java,typescript,tsx,css" backend/src/main/java frontend/src

npx --yes jscpd --min-lines 12 --min-tokens 100 --reporters console `
  --format "java" backend/src/test/java
```

Dead-code checks used the regular TypeScript build plus a one-off stricter compiler pass:

```powershell
cd frontend
.\node_modules\.bin\tsc.cmd --noEmit --noUnusedLocals --noUnusedParameters
$env:VITE_PREVIEW_MODE = 'false'
npm run build
```

## Source inventory

| Category | Before files | Before lines | After files | After lines | Decision |
|---|---:|---:|---:|---:|---|
| Backend production Java | 537 | 22,643 | 537 | 22,623 | Removed exception-response boilerplate only |
| Backend test Java | 101 | 15,092 | 102 | 15,225 | Added one focused persistence query-plan regression |
| Frontend TS/TSX | 121 | 13,038 | 122 | 13,041 | Removed one unused binding and isolated preview bootstrap behind a production-elided boundary |
| Frontend CSS | 12 | 5,881 | 12 | 5,653 | Removed 228 obsolete or overridden non-blank lines and replaced page-specific iOS date fixes with one shared guard |
| Frontend locales | 2 | 2,608 | 2 | 2,608 | No change |
| Canonical docs | 36 | 3,588 | not compared | not compared | P5 adds closure evidence, so doc growth is intentional |

Largest files before the cleanup were `global.css` (4,097 non-blank lines),
`AnalyticsServiceIntegrationTest` (858), `AnalyticsQueryRepository` (827), `CreateOrderPage` (719),
`MockRentalApi` (678), `CleaningOrderService` (606) and `GlobalExceptionHandler` (562). After the
targeted changes, `global.css` has 3,979 non-blank lines and `GlobalExceptionHandler` has 538.

Size alone was not treated as a reason to split a file:

- `AnalyticsQueryRepository` remains a cohesive JDBC analytics read model;
- `CleaningOrderService` remains the Cleaning lifecycle application service;
- large form pages remain lazy route boundaries, so splitting them would not reduce the initial route
  without another product-driven reason;
- mock APIs are development-only and are absent from the production bundle;
- large integration tests keep scenario setup close to the business behavior they verify.

## Duplication audit

### Production

| Metric | Before | After |
|---|---:|---:|
| Parser-analyzed files | 418 | 419 |
| Clones | 24 | 23 |
| Duplicated lines | 406 / 44,528 (0.91%) | 396 / 44,257 (0.89%) |
| CSS clones | 2 / 20 lines (0.30%) | 1 / 10 lines (0.16%) |

The actionable CSS clone duplicated the wide customer-header layout between `global.css` and
`header-controls.css`; P5 removed it and made `header-controls.css` the owner of customer header
geometry. The remaining CSS clone repeats a small token block between base variables and the Stella
theme and is intentional theme fallback data.

Manual inspection found repeated no-detail responses in `GlobalExceptionHandler` that jscpd did not
classify as a clone because error codes differ. A three-argument response helper now owns the required
`Collections.emptyMap()` default while every explicit handler retains its status, code and message.

The remaining Java clones are mostly structural similarities between independent entities,
repositories and controllers. They are intentionally not replaced with generic base entities,
repositories or a universal vertical abstraction. Two repeated analytics SQL shapes describe
different metrics and stay inside the measured, cohesive JDBC read model.

Two small frontend clones are also retained deliberately:

- `PreviewPlatform` and `WebPlatform` remain explicit channel adapters;
- notification tabs and the header bell remain independent consumers. Extracting only their effect
  would not share state or avoid duplicate requests, while introducing a global provider is not
  justified by the current twelve-line overlap.

### Tests

The initial test scan analyzed 101 files and found 10 clones covering 213 of 17,136 parser lines
(1.24%). The final scan includes the new query-count regression: 102 files and the same 10 clones,
covering 213 of 17,295 parser lines (1.23%). These are primarily Testcontainers/Spring setup and
scenario fixtures. No production abstraction or broad shared fixture was introduced solely to reduce
test duplication.

## Dead-code and bundle audit

- The strict TypeScript unused-symbol pass found one unused `usePlatform` binding on the Rental
  property page; it was removed and the strict pass is clean.
- Obsolete `.language-switcher`, `.theme-switcher button`, unused `topbar__services-action` and
  superseded customer-header selectors were removed. Current markup uses `.language-selector` and a
  single button whose class is `.theme-switcher`.
- Java types with only one lexical reference were reviewed as Spring-discovered controllers,
  configuration, jobs or listeners. They are runtime entry points, not dead code.
- Legacy customer routes remain intentional compatibility surfaces.
- Preview APIs and `PreviewPanel` now live behind build-time dynamic boundaries. With
  `VITE_PREVIEW_MODE=false`, their modules and stable preview markers are absent from emitted JS;
  local preview behavior remains available when the flag is explicitly enabled.
- Admin pages remain separate lazy chunks and are not listed among the entry HTML module preloads.

## Frontend production output

| Artifact | Before | After P5 production build |
|---|---:|---:|
| Entry JS | 285.39 KB / 86.27 KB gzip before preview isolation | 278.53 KB / 84.40 KB gzip |
| Shared framework chunk | 91.85 KB / 30.50 KB gzip | 91.85 KB / 30.50 KB gzip |
| Main CSS | 161.01 KB / 26.46 KB gzip | 156.15 KB / 25.90 KB gzip |
| RU locale | 64.31 KB / 17.82 KB gzip | unchanged |
| EN locale | 42.07 KB / 14.04 KB gzip | unchanged |
| Emitted JS chunks | 61 | 61 |

P5 reduces main CSS by 4.86 KB raw and 0.56 KB gzip without changing route splitting. Explicit
preview isolation also removes 6.86 KB raw / 1.87 KB gzip from the production entry compared with
the production build taken immediately before that isolation. A build that inherits the local
`VITE_PREVIEW_MODE=true` is a development artifact and is not used as production evidence.

## Persistence decision

The current hybrid remains the selected persistence strategy:

- JPA owns aggregate loading, mutation, optimistic/pessimistic locking and transaction boundaries;
- JDBC/native SQL owns analytics projections, conflict-aware inserts and specialized batch queries;
- Liquibase remains the only schema-change mechanism.

There is no measured evidence for replacing Hibernate, adding jOOQ or adding another storage layer.
P5 corrects one concrete fetch-plan issue: Rental customer/admin lists and owned details fetch their
required `property` in the repository query. The integration regression creates three bookings for
different properties and verifies that each affected repository read plus property access uses one
prepared statement.

Customer Home and Activity still compose complete owned histories and can repeat some reads. The
measured mixed API path is healthy, so pagination or a persisted read model remains deferred until
data volume or telemetry demonstrates a problem.
