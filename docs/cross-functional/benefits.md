---
title: Loco Benefits Direction
type: cross-functional
status: active-direction
scope: platform
updated: 2026-09-05
---

# Benefits

Benefits are tools for useful repeat/cross-service behavior, not a substitute for product-market fit or positive unit economics.

## Existing implemented cases

```text
RentalBooking
→ RentalCleaningBenefit
→ CleaningOrder
```

Read [../rental-cleaning-benefit.md](../rental-cleaning-benefit.md) before changing it.

This bridge is intentionally not the Cleaning referral program.

```text
RentalBooking
→ RentalTransferBenefit
→ first linked TransferBooking
```

A confirmed owned Rental offers one percentage benefit for either its ARRIVAL or CHECKOUT Transfer,
whichever is requested first. The backend identifies the right through `CustomerAccount`, the owned
Rental and typed `rentalSource`; no promo code is involved. The default is 10% in every tariff
currency with two-decimal `HALF_UP` rounding and no monetary cap.

Creation atomically reserves the benefit. `REQUESTED → CONFIRMED` consumes it permanently;
cancellation/rejection while still `REQUESTED` releases it. A previously created matching active or
completed Transfer closes the first-ride opportunity even if it did not receive a discount. The
other Rental context remains bookable at its ordinary current price.

`TransferBooking` stores immutable base, discount, payable, currency, benefit type and rate
snapshots. Quote and create both recalculate current price and eligibility. The explicit bridge is
kept separate from `RentalCleaningBenefit`; a generic benefit engine is still unjustified.

## Product rule

Prefer a benefit with a clear context:

```text
source service/action
→ reason for benefit
→ target service/action
→ eligibility window
```

Further examples:

```text
first completed cleaning
→ second-cleaning benefit
```

These are examples, not commitments.

## Benefit types

A future shared vocabulary may include:

```text
fixed discount
percentage discount
special fixed price
priority booking
free extra
service upgrade
```

Do not build the generic engine until multiple real use cases share lifecycle and financial semantics.

## Smart does not mean AI

A benefit can be smart because its eligibility uses actual context and timing. Deterministic rules are preferred initially.

## Financial rule

Benefits must preserve vertical financial invariants. Never trust a client-supplied discount or final amount.

Do not generalize Cleaning referral commission rules to other verticals.

## Measurement

Track enough to evaluate:

```text
issued
redeemed
conversion vs comparable non-benefit cohort when possible
repeat/cross-service behavior
contribution after benefit
```

Do not optimize for redemption at the expense of contribution.
