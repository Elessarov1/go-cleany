---
title: Loco Notifications
type: cross-functional
status: active
scope: platform
updated: 2026-09-10
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
durable delivery job
        ↓
Telegram / FCM
```

The inbox record and delivery jobs are committed in the same transaction as the business change.
Provider I/O runs later through a PostgreSQL worker using a lease and `FOR UPDATE SKIP LOCKED`, so a
provider outage cannot hold or roll back the business transaction and a process restart cannot lose
delivery work.

Delivery states are `PENDING`, `PROCESSING`, `RETRY`, `DELIVERED`, `DEAD`, `CANCELLED`. Retries use
1 minute, 5 minutes, 30 minutes, 2 hours and 8 hours, while honoring provider `Retry-After`. Network
and 5xx failures retry; permanent 4xx fail. Invalid FCM registrations disable the endpoint.

Cleaner and self-accepting driver broadcasts use the same PostgreSQL lease/retry policy through
`operational_telegram_delivery`. Their provider call is never made inside the order/booking
transaction; the event listener persists the deduplicated delivery before commit and a worker sends it.

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

Customers have explicit in-app/Telegram/push preferences and session-bound communication endpoints.
FCM registration tokens are encrypted and deduplicated by hash. Logout, account switch, identity
unlink and deletion detach affected endpoints.

## Security/privacy

Notifications and deep links must not authorize access by themselves. Backend resolves authenticated ownership for protected targets.
Push payloads carry only generic text, notification ID and a safe typed action; private details are
loaded from an authenticated API.
