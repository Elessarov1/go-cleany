---
title: Retention and Progressive Convenience
type: cross-functional
status: proposed
scope: platform
updated: 2026-08-30
---

# Retention & Progressive Convenience

This is the primary strategic cross-functional phase after the three core verticals.

Goal:

> Turn the first completed Loco task into the beginning of a relationship, not the end of a funnel.

## P0 — make returning objectively easier

### Unified history

A customer should have a coherent place to see owned Cleaning, Rental and Transfer transactions without merging their domain models.

Use read models/composition, not `UniversalOrder`.

### Repeat / Book again

For suitable completed tasks, create a new transaction with safe previous choices prefilled.

Do not copy stale price, eligibility, availability or status. The backend recalculates current business truth.

### Saved places

Introduce shared places only when multiple verticals benefit from them:

```text
Home
Office
Apartment
other named place
```

Avoid a generic profile JSON dump.

### Same-service prefill

Examples:

```text
Cleaning → previous address/options
Transfer → airport/vehicle/passengers/address
```

The user changes what changed.

### Cross-order prefill

Use real source context:

```text
Rental arrival + property address
→ airport pickup transfer with suggested date/destination

Rental checkout + property address
→ airport return transfer with suggested date/pickup
```

Do not trust client-supplied copied ownership data; resolve source records for the authenticated customer on the backend.

### Contextual next action

The customer home should gradually evolve from static catalog to:

```text
current active task
one primary next useful action
repeat opportunity
service catalog
```

Avoid an advertising feed.

### Support entry point

Every transaction should provide an obvious "Need help" path with source context already attached.

### Simple feedback

Start with useful operational signal, e.g. positive vs problem + issue category. A public five-star marketplace rating is not required to improve supply quality.

## P1 — use lifecycle context

### Smart reminders

Good reminder:

```text
Your last cleaning was two weeks ago. Repeat it?
```

or:

```text
Your rental checkout is in 3 days. Need a transfer to the airport?
```

Bad reminder:

```text
We miss you — buy something.
```

Start with deterministic scheduled evaluation over domain state + existing notifications.

### Explainable next-best actions

Use rules that are testable and understandable. Do not build ML recommendations before data volume warrants them.

### Notification hygiene

Need deduplication, reasonable frequency, customer preferences and "do not remind when matching transaction already exists" behavior.

## P2 — deepen proven patterns

Only after data proves the behavior:

```text
bundles
advanced lifecycle rules
experiments
platform-level ServiceBenefit if semantics repeat
membership/subscription if recurring value is proven
```

## Progressive convenience ladder

```text
new customer
→ supplies required data

returning customer
→ receives saved/default context

cross-service customer
→ receives safe source-order context

mature relationship
→ receives an explainable likely next action
```

## Success metrics

```text
completed tasks/customer
30/90-day repeat
second-order conversion
time to second order
2+ service adoption
form steps/fields saved where measurable
support/incident outcomes
contribution after benefits
```
