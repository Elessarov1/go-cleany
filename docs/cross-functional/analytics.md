---
title: Loco Place Analytics
type: cross-functional
status: active
scope: platform
updated: 2026-09-02
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

The admin overview also exposes Business Health, mature retention cohorts and immediate-next-task transitions directly from current transactional data. No separate analytics database or generic transaction aggregate is used.

The same overview exposes a lightweight repeat-action funnel for Cleaning and Transfer. Its source cohort is completed source transactions inside the selected reporting period and service filter:

```text
repeat action shown
→ safe prefill opened
→ linked repeat transaction created
→ linked repeat transaction completed
```

Shown/opened interactions are durable semantic events, deduplicated per customer, service, source and event type. Created/completed counts come from typed source links on the vertical transactions. Start rate is opened/shown, completion rate is completed/created, and time to repeat is the median from source completion to linked repeat creation. A missing denominator is returned as `null`, not as 0%.

The overview also exposes the contextual Rental → Transfer funnel, as a total and separately for arrival and checkout:

```text
context action shown
→ Transfer prefill opened
→ typed related Transfer created
→ related Transfer completed
```

Its cohort is the first deduplicated `CTA_SHOWN` for a Rental booking/context inside the selected period. It is available for `ALL` and source-service `RENTAL` filters. Start rate is opened/shown, creation rate is created/shown, completion rate is completed/created, and conversion time is the median from first shown to first linked Transfer creation. Events live in the explicit Rental/Transfer bridge; this does not introduce a universal cross-service event or transaction model.

The Smart Reminders section uses reminders whose durable notification was created (`notifiedAt`) in the selected period. Service filters apply to the source vertical. Cleaning repeat and Rental checkout reminders attribute created/completed targets through their existing typed source fields; Transfer operational reminders measure whether the reminded source Transfer completed. Empty denominators return `null`, and Telegram delivery is intentionally not a separate metric until durable external delivery checkpoints exist.

## Business Health semantics

For the selected reporting period and service filter:

```text
completed tasks
active customers
completed tasks / active customer
active customers with 2+ completed Loco tasks
active customers with 2+ completed services
cross-service customer rate
```

Only `COMPLETED` Cleaning, Rental and Transfer transactions participate. The completed-task and active-customer counts use the selected service. Lifetime depth for those active customers can span all services, starts no earlier than `COMMERCIAL_LAUNCH_AT` and is observed only through the selected period end.

Average checks remain grouped by both service and currency.

## Retention cohort semantics

Retention starts with each customer's first completed Loco task on or after `COMMERCIAL_LAUNCH_AT`:

- the service filter selects the service of that first task;
- the immediate second completed task may belong to any vertical;
- 30/90-day repeat means the second task was completed no later than 30/90 days after the first;
- a rate denominator includes only customers whose complete observation window ends by the selected reporting end;
- period repeat cohorts require the first task to be inside the selected reporting period;
- second-order conversion uses the cumulative mature 90-day cohort from commercial launch through the selected reporting end;
- median time to second task uses converted customers from that same cumulative cohort.

When a cohort has no fully observed customers, the API returns a `null` rate and the admin UI shows **Insufficient data** rather than a misleading zero.

## Immediate-next transitions

Initial funnels are:

```text
Cleaning → Cleaning
Rental → Transfer
Rental → Cleaning
Transfer → Transfer
```

They use customers whose first completed task is inside the selected period. A transition matches only when the target is the customer's immediate second completed task. For example, `Rental → Cleaning → Transfer` counts as `Rental → Cleaning`, never as `Rental → Transfer`.

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
completed tasks/orders — implemented
new customers
30-day repeat — implemented with mature cohorts
90-day repeat — implemented with mature cohorts
customers with 2+ lifetime completed tasks — implemented for period-active customers
customers using 2+ services — implemented for period-active customers
cross-service conversion — immediate-next and contextual Rental → Transfer funnels implemented
average check by service + currency — implemented
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
