---
title: Customer Identity
type: architecture
status: active
scope: platform
updated: 2026-08-30
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
TELEGRAM
```

Future mobile identity may be added when required.

Do not automatically merge accounts by:

```text
email
phone
display name
Telegram username
```

Google ↔ Telegram linking is explicit and verified.

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
