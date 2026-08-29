---
title: CustomerAccount is canonical identity
type: decision
status: accepted
scope: platform
updated: 2026-08-30
---

# 0002 — Canonical customer identity

## Decision

`CustomerAccount.id` is the business-level customer identity across all Loco verticals.

Google, Telegram and future providers are external identities/adapters.

## Why

Customers must be able to use multiple channels without creating multiple business identities or coupling orders to Telegram.

## Consequences

- vertical aggregates reference canonical customer identity;
- analytics and first-touch attribution key to canonical identity;
- explicit verified account linking is required;
- do not auto-merge by email/phone/name/username;
- provider IDs do not become general authorization/customer IDs.
