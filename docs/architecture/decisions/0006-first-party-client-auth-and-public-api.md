---
title: First-party client authentication and stable public API
type: architecture-decision
status: accepted
scope: platform
updated: 2026-09-11
---

# ADR 0006: First-party client authentication and stable public API

## Decision

All business APIs authorize canonical `CustomerAccount.id`. Standalone browser uses a server-side
HttpOnly cookie session with CSRF protection. Telegram Mini App sends signed `initData` through the
authentication adapter on each API request; the backend validates signature and age before resolving
the same canonical customer. TMA does not depend on WebView cookie persistence because Telegram
Desktop's Linux WebView cookie behavior differs from ordinary browsers and mobile Telegram clients.

Native clients use first-party random opaque access/refresh credentials. Only hashes are stored;
access credentials live for 15 minutes, refresh credentials slide for 30 days, and the session family
has an absolute 90-day limit. Refresh rotation requires `Idempotency-Key`; a replay under another key
revokes the family.

Google and Apple proofs are provider-bound login or link inputs, never bearer credentials for
business APIs. Telegram `initData` is a short-lived, provider-signed TMA request credential accepted
only by the Telegram authentication adapter. `provider + issuer + subject` uniquely identifies an
external identity. LOGIN and LINK are distinct challenge purposes and occupied identities are never
merged automatically.

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
