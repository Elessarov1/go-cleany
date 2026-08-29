---
title: Cross-service features use explicit bridges, not a universal aggregate
type: decision
status: accepted
scope: architecture
updated: 2026-08-30
---

# 0004 — Explicit cross-service bridges

## Decision

Cleaning, Rental, Transfer and future verticals keep their own aggregates. Cross-service behavior uses explicit bridge/application/read models.

## Existing example

```text
RentalBooking
→ RentalCleaningBenefit
→ CleaningOrder
```

## Consequences

Do not create `UniversalOrder`, `UniversalBooking` or a generic JSONB service payload as the primary domain model.

Generalize a cross-service abstraction only after multiple real use cases share the same semantics.
