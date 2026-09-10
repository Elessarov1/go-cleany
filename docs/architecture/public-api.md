---
title: Public API and generated SDKs
type: architecture
status: active
scope: platform
updated: 2026-09-10
---

# Public API and generated SDKs

The source contract is `api/openapi/loco-place-v1.yaml`. `openapitools.json` pins OpenAPI Generator
7.25.0 and produces:

```text
clients/typescript-fetch
clients/dart-dio
```

Regenerate with `scripts/generate-api-clients.sh` or `.ps1`. CI validates the specification,
regenerates both clients, rejects drift, compiles TypeScript and runs Dart generation/analyze/tests.
Generated enum models include an unknown-value fallback. Application code must also treat an unknown
typed action as a safe no-op/home navigation rather than constructing an arbitrary URL. The Dart
package exposes `deserializeActionTargetSafely` from
`package:loco_place_api/safe_action_target.dart`, because the generator's `oneOf` discriminator
serializer otherwise throws for a future action subtype.

The shared wire rules are decimal-string `Money`, ISO date/time/date-time, opaque cursor pages and
`ApiError { code, message, fieldErrors, requestId }`. Creation of Cleaning, Rental and Transfer work
requires `Idempotency-Key`, retained for seven days. A same-key/same-payload retry returns the original
resource; a changed payload conflicts.

The contract covers browser/TMA bootstrap, native authentication and account security, client
configuration, Home, Activity, notifications, support, and the complete customer-facing Cleaning,
Rental and Transfer flows. React/TMA consumes these operations through the generated TypeScript
client; only internal admin APIs remain on the legacy hand-written transport. The React adapter
serializes generated `Date` values back to their canonical ISO wire strings before exposing them to
the existing UI domain types and follows cursor pages until the former list-shaped interface is
complete.

The current contract is `1.0.0-rc.1`. Promotion to `1.0.0` belongs to the Backend Ready Gate. After
promotion, breaking wire changes require `/api/v2`.
