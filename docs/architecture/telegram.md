---
title: Telegram Integration Boundaries
type: architecture
status: active
scope: platform
updated: 2026-09-08
---

# Telegram

Telegram is an adapter/channel, not the canonical location of Loco business state.

## Customer channel

Telegram Mini App authentication resolves a verified external identity to canonical `CustomerAccount.id`.

Customer business data, order ownership and durable notifications must not exist only inside Telegram.

Google ↔ Telegram linking is explicit. Do not merge by correlated profile fields.

The bot is also the customer-facing Telegram entry point for Loco Place. Its public profile, `/start`, `/help`, inline web-app buttons and persistent menu are configured by the backend through Bot API and lead to the application or public `/support` page. Customer-facing text follows Telegram `language_code` for RU/EN. Profile configuration is idempotent, runs outside transactions and fails without disabling update handling or notification delivery.

The deployed Mini App must be designated as the bot's Main Mini App once through BotFather. Runtime profile text, commands and the menu button are subsequently owned by code.

Public commands remain limited to `/start` and `/help`. Operational `/whoami`, cleaner and admin commands may remain available without appearing in the general customer command list. A plain or otherwise unrecognized `/start` parameter falls back to the customer welcome after more specific start handlers have had a chance to consume it.

## Start parameters

Existing acquisition and account-linking semantics may share Telegram start/deep-link handling. New start parameter types must coexist without breaking existing acquisition/link flows.

Routing order is significant: verified provider connection parameters such as `driver_<token>` are handled before the generic customer welcome. Mini App start parameters continue to be interpreted at the frontend boundary; bot presentation must not treat them as authorization.

## Provider-side flows

Telegram may be the practical operational UI for some providers (cleaners, connected Transfer drivers). Database state remains authoritative and callbacks must be authenticated/validated.

## Transfer driver connection

A numeric Telegram ID configured by admin does **not** grant a bot permission to message the driver.

Current flow:

```text
admin stores driver Telegram ID
→ admin creates short-lived one-time link
→ driver opens bot link from that Telegram account
→ bot verifies actual Telegram user ID
→ private chat/connection is stored
→ driver becomes eligible for notifications/self-accept
```

Only token hash is persisted. Changing/clearing configured ID invalidates prior connection.

Phone-only drivers remain valid for manual admin assignment.

## Transfer self-accept

`DRIVER_SELF_ACCEPT` broadcasts only to enabled connected drivers with Telegram notifications enabled.

Callback payload must not be trusted to supply arbitrary driver identity. Resolve driver from verified Telegram connection and use conditional DB assignment so the first valid claimant wins.

## Notification boundary

Customer notification semantics belong to the shared notification layer. Provider operational messages may use Telegram-specific adapters where appropriate.
