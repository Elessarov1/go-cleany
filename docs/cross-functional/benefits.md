---
title: Loco Benefits Direction
type: cross-functional
status: active-direction
scope: platform
updated: 2026-08-30
---

# Benefits

Benefits are tools for useful repeat/cross-service behavior, not a substitute for product-market fit or positive unit economics.

## Existing implemented case

```text
RentalBooking
→ RentalCleaningBenefit
→ CleaningOrder
```

Read [../rental-cleaning-benefit.md](../rental-cleaning-benefit.md) before changing it.

This bridge is intentionally not the Cleaning referral program.

## Product rule

Prefer a benefit with a clear context:

```text
source service/action
→ reason for benefit
→ target service/action
→ eligibility window
```

Examples:

```text
confirmed rental
→ contextual Transfer benefit

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
