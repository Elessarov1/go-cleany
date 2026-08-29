---
title: Loco Notifications
type: cross-functional
status: active
scope: platform
updated: 2026-08-30
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

Future reminders should be contextual and deduplicated:

```text
repeat cleaning when due
return transfer when checkout approaches
upcoming booking reminder
```

Do not send a reminder when an equivalent future transaction already exists.

Add preferences/rate controls as reminder volume grows.

## Security/privacy

Notifications and deep links must not authorize access by themselves. Backend resolves authenticated ownership for protected targets.
