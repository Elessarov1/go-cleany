---
title: Loco Notifications
type: cross-functional
status: active
scope: platform
updated: 2026-09-04
---

# Notifications

Notifications express product/domain facts and customer lifecycle context. They are not synonymous with Telegram messages.

## Customer-side principle

Preferred flow:

```text
domain/application event
        ↓
durable semantic customer notification
        ↓
optional communication routing
        ↓
Telegram / future channels
```

The durable Loco inbox/history is primary. External delivery failure must not erase the notification fact.

## Semantic content

Store/route stable notification meaning and safe application targets. Avoid making provider-rendered text the canonical business record.

## Channel neutrality

Telegram is optional for customers. Google-only web customers must remain functional.

Do not call Telegram directly from reusable vertical business services when a platform notification/event boundary is appropriate.

## Operational actors

Some provider-side flows can remain channel-specific where Telegram is genuinely the operational interface, such as cleaner/driver interactions. Even there, database state is authoritative.

Transfer self-accept is an example: callback identity is resolved from verified Telegram linkage; the atomic assignment update decides the winner, not message state.

## Smart reminders

Implemented reminders are contextual and deduplicated:

```text
repeat cleaning when due
return transfer when checkout approaches
confirmed Transfer approaching its pickup
```

`customer_reminder` owns scheduling and lifecycle (`PENDING`, `NOTIFIED`, `DISABLED`, `SUPERSEDED`, `EXPIRED`); `customer_notification` remains the durable customer-facing fact. The scheduler writes the inbox in its database transaction and registers optional Telegram delivery only after a successful commit. External I/O therefore does not hold the scheduler transaction or erase the durable reminder when a channel fails.

Action reminders obey current `ENABLED`/`IN_TEST`/`DISABLED` customer-flow rules. An operational reminder for an already confirmed Transfer ignores current catalog availability. Deep links never authorize access and are revalidated by their vertical backend.

Do not send a reminder when an equivalent future transaction already exists. Cleaning address comparison and Rental → Transfer matching share the same NFKC/trim/case-fold/whitespace normalization.

Add preferences/rate controls as reminder volume grows.

## Security/privacy

Notifications and deep links must not authorize access by themselves. Backend resolves authenticated ownership for protected targets.
