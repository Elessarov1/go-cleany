---
title: First-party client authentication and stable public API
type: architecture-decision
status: accepted
scope: platform
updated: 2026-09-10
---

# ADR 0006: First-party client authentication and stable public API

## Decision

All business APIs authorize canonical `CustomerAccount.id`. Browser and Telegram Mini App use
server-side HttpOnly cookie sessions with CSRF protection. Telegram `initData` is accepted only once,
by `POST /api/v1/auth/tma/session`, to create the TMA cookie session.

Native clients use first-party random opaque access/refresh credentials. Only hashes are stored;
access credentials live for 15 minutes, refresh credentials slide for 30 days, and the session family
has an absolute 90-day limit. Refresh rotation requires `Idempotency-Key`; a replay under another key
revokes the family.

Google, Apple and Telegram proofs are provider-bound login or link inputs, never bearer credentials
for business APIs. `provider + issuer + subject` uniquely identifies an external identity. LOGIN and
LINK are distinct challenge purposes and occupied identities are never merged automatically.

The contract under `/api/v1` is described by OpenAPI 3.1 and produces TypeScript Fetch and Dart Dio
clients. After the contract is released as `1.0.0`, breaking changes require `/api/v2`; v1 accepts only
backward-compatible additions.

## Consequences

- Vertical domains receive `CustomerContext`; they do not know cookies, provider proofs, Flutter,
  FCM or browser routes.
- Navigation leaves the domain as discriminated `ActionTarget`; web paths are adapter-owned.
- Security-sensitive account changes require proof no older than five minutes.
- Account deletion is an atomic cancellation gate followed by a tombstone and PII removal.
- `1.0.0` cannot be declared until real staging Google, Apple, Telegram, FCM and app-link smoke tests
  are recorded.
