---
title: Loco Place Analytics
type: cross-functional
status: active
scope: platform
updated: 2026-08-30
---

# Analytics

Analytics is a platform capability, not a fourth vertical.

For exact implemented acquisition rules and API semantics read [../acquisition-analytics.md](../acquisition-analytics.md).

## Current implemented baseline

Canonical analytics identity is `CustomerAccount.id`.

First-touch attribution supports campaigns, partners, customer referral and organic fallback. A later touch does not overwrite the canonical first touch.

Stable campaign entry:

```text
/a/<publicCode>
```

Admin overview supports:

```text
ALL
CLEANING
RENTAL
TRANSFER
```

Current successful transaction values use immutable completed snapshots and never combine currencies into one average.

## Business dashboard direction

The next analytics work should help answer in about 30 seconds:

```text
Are customers returning?
Are they using more than one service?
Are we earning contribution on growth?
Where does fulfillment fail?
Which acquisition sources create good customers?
Which vertical consumes too much manual operations time?
```

## North Star

> **Completed tasks per active customer**

Use a defined reporting window such as rolling 90 days.

Do not use this alone; it must not hide bad economics or quality.

## Priority metrics

```text
completed tasks/orders
new customers
30-day repeat
90-day repeat
customers with 2+ lifetime completed tasks
customers using 2+ services
cross-service conversion
average check by service + currency
GMV
Loco revenue when available
contribution margin when costs are available
CAC
CAC payback
fulfillment rate
cancellation rate
incident rate
provider no-show rate
ops minutes/order
```

## Cross-service funnels

Useful examples:

```text
Cleaning → Cleaning repeat
Rental → Transfer
Rental → Cleaning
Transfer → Transfer repeat
Transfer → other service when context is real
```

For each, prefer to measure conversion, time-to-next-task, contribution and benefit usage rather than raw clicks only.

## Benefits analytics

A benefit should expose enough data to understand:

```text
issued
activated/visible if relevant
redeemed
incremental conversion where measurable
contribution after benefit
```

A high redemption count is not automatically a successful benefit if users would have purchased anyway or contribution becomes negative.

## Acquisition quality

Do not stop at QR scans/entries. Compare acquisition sources by downstream behavior:

```text
first completed task
repeat
cross-service adoption
contribution
CAC payback
```

## Time/currency rules

Business calendar boundaries use configured analytics timezone (Europe/Istanbul by default). Respect `COMMERCIAL_LAUNCH_AT` where configured.

Never sum/average unlike currencies without an explicit currency-conversion product decision.
