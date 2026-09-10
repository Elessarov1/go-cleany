---
title: Customer Identity
type: architecture
status: active
scope: platform
updated: 2026-09-10
---

# Identity

Canonical internal customer identity:

```text
CustomerAccount.id
```

This ID should be used by vertical ownership, analytics and cross-service features.

## External identity

External providers are adapters/identity records, not business customer IDs.

Current relevant providers:

```text
GOOGLE
APPLE
TELEGRAM
```

Identity uniqueness is `provider + issuer + subject`, with at most one identity of each provider on
one account. The former synthetic `MOBILE_APP` provider is not a business identity and is not used.

Do not automatically merge accounts by:

```text
email
phone
display name
Telegram username
```

Google ↔ Telegram linking is explicit and verified.

LOGIN and LINK use separate nonce-bound challenges. A LINK to an identity owned by another account
returns `409 identity_already_linked`; there is no customer-facing account merge in v1. Link, unlink
and deletion require a current-provider proof no older than five minutes. The last login method cannot
be removed.

## Native sessions

Native clients exchange Google, Apple or one-time Telegram bot proof for Loco-owned opaque tokens.
Only hashes are stored. Access lives 15 minutes, refresh slides for 30 days and a family expires after
90 days. Refresh is rotating and idempotent for two minutes under the same `Idempotency-Key`; used
refresh hashes stay attached to the session so reuse from any older rotation revokes the family.

Apple authorization-code exchange happens outside database transactions. Stored Apple refresh
credentials are encrypted; unlink/deletion enqueue durable credential revocation work.

## Account deletion

Deletion first verifies that every active Cleaning, Rental and Transfer operation can be cancelled,
then cancels all of them atomically. Any non-cancellable operation returns
`account_deletion_blocked_by_active_operation` with no partial changes. ADMIN self-deletion is denied.
The account row remains as an anonymized tombstone so required operational and financial history can
keep its internal customer ID; PII, identities, roles, sessions, endpoints, inbox and user comments are
removed.

## Web

Standalone web uses direct Google OIDC via Spring Security OAuth2 Client and server-side PostgreSQL-backed sessions.

The app is not its own OIDC authorization server.

Read [../web-authentication.md](../web-authentication.md) for exact configuration/security behavior.

## ADMIN

`ADMIN` is a persisted `CustomerAccount` role.

Deployment bootstrap uses verified Google email configuration:

```text
ADMIN_GOOGLE_EMAILS
```

Telegram IDs must not grant/admin-bootstrap the role. Once an explicitly linked Telegram identity resolves the same CustomerAccount, it naturally sees the same persisted role.

## Telegram optionality

A Google-only customer must be able to use Loco Place without linking Telegram.

Do not make reusable customer logic depend on Telegram user/chat IDs.

## Cross-service context

When a cross-service flow starts from an existing booking/order, backend must resolve the authenticated customer and verify ownership of the source entity. Query parameters or promo codes are context hints, not identity authorization.

## Request resolution

Authenticated HTTP requests resolve their canonical `CustomerAccount` once before customer-owned application transactions begin and reuse that result for the request. This avoids nested independent identity transactions holding one database connection while waiting for another under concurrency. Non-HTTP jobs and adapters keep an explicit resolution boundary.

External-identity activity timestamps are observational metadata, not a reason to write on every request. Their refresh is throttled and must not change ownership or linking semantics.
