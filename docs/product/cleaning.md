---
title: Loco Cleaning
type: vertical-context
status: active
scope: cleaning
updated: 2026-08-30
---

# Loco Cleaning

Loco Cleaning is the first implemented Loco Place vertical.

## Domain ownership

Cleaning owns:

```text
CleaningOrder
cleaning quote/pricing
cleaner workflow
completion report/media
onsite issue flow
cleaning-specific referral economics
```

Do not move these semantics into a generic platform order.

## Fulfillment

Multiple cleaners may be eligible to receive a request; the first accepted assignment wins atomically. This is an operational implementation detail behind the Loco promise — the customer should not coordinate cleaners.

## Customer value

Cleaning is strategically important because it can become a recurring resident use case, not only a seasonal/tourist transaction.

Retention opportunities:

```text
Repeat / Book again — implemented for completed owned orders
safe prefill from previous cleaning — area/address/apartment/duplex/type
saved address
contextual repeat reminder
automatic source-order context for support
```

Repeat creation stores `CleaningOrder.repeatSourceOrderId`. It never inherits scheduling, price, incentives, comments, fulfillment assignment or status. Current service availability, pricing, phone and eligibility remain authoritative at creation time.

Do not invent a generic recurrence engine before repeat behavior is observed.

## Referral economics

Current friend/partner referral economics are Cleaning-specific. Read [../referral-financial-model.md](../referral-financial-model.md) before changing discounts, referrer rewards, partner payouts or commission invariants.

Do not reuse those semantics for lifecycle benefits just because both produce discounts.

## Cross-service connections

Implemented bridge:

```text
RentalBooking
→ RentalCleaningBenefit
→ CleaningOrder
```

Read [../rental-cleaning-benefit.md](../rental-cleaning-benefit.md).

Future cross-service prefill/benefits should follow explicit bridge/application models rather than coupling Cleaning to other vertical aggregates.

## Metrics to watch

```text
completed cleanings
repeat rate
orders/customer
fulfillment
cleaner cancellation/no-show
customer issue rate
average check by currency
contribution when cost data exists
ops minutes/order
```
