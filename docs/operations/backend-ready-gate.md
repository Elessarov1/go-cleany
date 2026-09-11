---
title: Flutter Backend Ready Gate
type: operations
status: active
scope: platform
updated: 2026-09-10
---

# Flutter Backend Ready Gate

The gate is operational evidence, not a code-only checkbox. Run against staging with real provider
configuration and store no credential values in logs or artifacts.

Required evidence:

- Google valid login plus rejected signature, issuer, audience, nonce, expiry and replay;
- Apple valid token/code exchange, grouped app identity and credential revocation;
- Telegram one-time bot login/link handoff with driver-link priority unchanged;
- FCM HTTP v1 delivery, retry/outage behavior and invalid registration disablement;
- iOS AASA for `com.locoplace.app.staging` and Android `assetlinks.json` matching the release key;
- generated Dart headless configuration and authenticated business read flow, using a short-lived
  first-party access token obtained by the separately recorded provider login smoke;
- an in-app account deletion entry in every shipped Flutter application, exercising the generated
  create/confirm deletion operations with fresh provider proof and clearing local credentials after
  success; a public web deletion URL alone does not satisfy this client requirement;
- migration and pre-commercial reset rehearsal on a controlled copy;
- local `loco-perf` Caddy mixed-profile before/after comparison when the release candidate is fixed.

The generated Dart client has a provider-independent first smoke that must pass before credentialed
scenarios:

```bash
cd clients/dart-dio
LOCO_API_BASE_URL=https://staging.example.com \
LOCO_ACCESS_TOKEN=<short-lived-first-party-token> \
dart run tool/headless_configuration_smoke.dart
```

Only after all evidence passes: change OpenAPI info version and both SDK versions to `1.0.0`, set a
fixed `API_REVISION`, regenerate clients and tag the API release. Flutter feature work must not begin
with an unrecorded backend gap.
