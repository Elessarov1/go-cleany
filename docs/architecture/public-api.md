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

The contract covers browser and TMA authentication, native authentication and account security, client
configuration, Home, Activity, notifications, support, and the complete customer-facing Cleaning,
Rental and Transfer flows. React/TMA consumes these operations through the generated TypeScript
client; only internal admin APIs remain on the legacy hand-written transport. The React adapter
serializes generated `Date` values back to their canonical ISO wire strings before exposing them to
the existing UI domain types and follows cursor pages until the former list-shaped interface is
complete.

Account deletion uses the generated `createAccountDeletionRequest` and
`confirmAccountDeletion` operations without a React-only wire contract. The public web deletion URL
is `/account/delete`; Web confirms through a freshly issued Google OIDC session without sending an ID
token to the deletion operation, while TMA sends fresh `telegramInitData`. Native clients use the same
operations with their provider proof. A successful client must immediately discard its local auth
state and stop customer requests. The generated Dart API already includes these operations; every
future Flutter application must also provide a discoverable in-app deletion entry, not only link to
the public website.

The current contract is `1.0.0-rc.1`. Promotion to `1.0.0` belongs to the Backend Ready Gate. After
promotion, breaking wire changes require `/api/v2`.
