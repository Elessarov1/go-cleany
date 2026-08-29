---
title: Loco Rental
type: vertical-context
status: active
scope: rental
updated: 2026-08-30
---

# Loco Rental

Loco Rental is the second implemented Loco Place vertical.

## Domain ownership

Rental owns:

```text
RentalProperty
RentalBooking
RentalOccupancy
rental availability
stay policy
rental pricing
property/admin workflow
```

Do not merge RentalBooking with CleaningOrder or TransferBooking.

## Booking model

Rental has dated occupancy/availability semantics. Pricing and stay rules are backend-authoritative; bookings preserve immutable price snapshots.

Admin owns property publication, occupancies and operational booking management.

## Strategic role

Rental is a higher-ticket, lower-frequency vertical and a strong source of real context for related tasks.

Natural lifecycle context includes:

```text
check-in date
checkout date
property address
```

That makes Rental a strong source for contextual actions such as airport transfer and checkout cleaning.

## Existing cross-service bridge

Implemented:

```text
RentalBooking
→ RentalCleaningBenefit
→ CleaningOrder
```

See [../rental-cleaning-benefit.md](../rental-cleaning-benefit.md).

## Retention/cross-service opportunities

Examples that satisfy the manifesto because the context is explicit:

```text
confirmed rental + arrival date
→ suggest FROM_AIRPORT transfer
→ prefill property destination

checkout approaching
→ suggest TO_AIRPORT transfer
→ prefill property pickup

checkout window
→ existing checkout cleaning benefit
```

Do not turn these into generic advertising cards.

## Metrics to watch

```text
completed bookings
booking cancellation rate
average check by currency
contribution when costs are known
Rental → Transfer conversion
Rental → Cleaning conversion
support/incident rate
ops minutes/booking
```
