---
title: Loco Rental
type: vertical-context
status: active
scope: rental
updated: 2026-09-05
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

## Property media delivery

Rental keeps platform-owned full, card and thumbnail image variants. Upload normalization decodes the source once and creates the three representations; cards use the card URL and galleries use thumbnails until the full asset is opened. The one-off repair of media uploaded before responsive variants existed has completed, and no startup backfill remains in the runtime. Nullable legacy columns and the canonical-asset read fallback are retained for rollback and old-backup compatibility; restoring data with missing variants requires an explicit repair before it is considered fully optimized.

Public media URLs carry the selected asset ID as a version token and are immutable for one year. A dedicated weighted Caffeine cache retains up to 64 MiB of public full/card/thumbnail bytes per backend process, so a cache hit performs no PostgreSQL media or ownership query. Media and property lifecycle changes invalidate only the affected property after commit; generation keys prevent an in-flight pre-invalidation read from repopulating a reachable stale entry. Admin reads remain non-cacheable, and public property lists load cover media only instead of hydrating every gallery. The cache is intentionally local and cold after a backend restart; Caddy, Redis and CDN caching are not part of this design.

## Strategic role

Rental is a higher-ticket, lower-frequency vertical and a strong source of real context for related tasks.

Natural lifecycle context includes:

```text
check-in date
checkout date
property address
```

That makes Rental a strong source for contextual actions such as airport transfer and checkout cleaning.

## Existing cross-service bridges

Implemented:

```text
RentalBooking
→ RentalCleaningBenefit
→ CleaningOrder
```

See [../rental-cleaning-benefit.md](../rental-cleaning-benefit.md).

Also implemented:

```text
RentalBooking arrival/checkout context
→ typed RentalTransferContext
→ TransferBooking
```

The owned booking detail presents arrival and checkout in one contextual card. Transfer availability and booking horizon are recalculated from current configuration. The property address and source date are safe editable suggestions; Transfer remains responsible for current price, airport/vehicle capacity and fulfillment state.

A matching active/completed Transfer suppresses the corresponding action. Cancelled/rejected transfers permit another attempt. Rental cancellation does not cascade into Transfer cancellation.

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
