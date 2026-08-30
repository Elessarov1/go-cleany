---
title: Loco Transfer
type: vertical-context
status: active
scope: transfer
updated: 2026-08-30
---

# Loco Transfer

**Implementation status: implemented.**

Do not treat the Transfer MVP specification as future work without checking current code. The detailed current implementation is documented in [../loco-transfer.md](../loco-transfer.md).

## Product

Loco Transfer provides scheduled fixed-price rides between Alanya and configured airports.

Customer selects:

```text
direction
airport
vehicle class
pickup date/time
address
passengers/luggage
contact details
flight details for airport pickup
```

No distance pricing, map routing, online payment or live flight API is part of the current model.

## Domain ownership

Transfer owns:

```text
TransferAirport
TransferVehicleType
TransferPrice
TransferDriver
TransferBooking
```

`TransferBooking` is not a generic platform order.

## Configuration

GZP, AYT, Sedan and Minivan are seeded business configuration. Commercial rates are admin-managed and price snapshots are stored on bookings.

The service remains controllable through platform availability state; configuration records are preserved by the pre-commercial reset while transaction history is purged.

## Workflow

```text
REQUESTED → CONFIRMED → COMPLETED
     ├────→ REJECTED
     └────→ CANCELLED
```

Admin completes a ride in the current MVP.

## Assignment

Supported modes:

```text
ADMIN_ASSIGNMENT
DRIVER_SELF_ACCEPT
```

Manual assignment may use any enabled driver. A phone is sufficient; Telegram is optional.

Self-accept broadcasts only to enabled, connected drivers whose Telegram notifications are enabled. Driver and admin assignment paths use conditional database updates so concurrent claims have exactly one winner.

## Telegram driver onboarding

Admin may configure a numeric Telegram ID, but that alone does not authorize bot delivery. The driver must open the short-lived one-time bot link using the configured account. The bot verifies actual Telegram identity before storing private chat/connection data.

Changing/clearing the configured Telegram identity invalidates the old connection.

See [architecture/telegram.md](../architecture/telegram.md).

## Time rules

Business time uses `Europe/Istanbul`. Current configuration includes minimum booking days ahead, maximum months ahead and 30-minute slot granularity.

## Repeat ride

An owned completed transfer can start a new ride through `/transfer?repeatFrom=<bookingId>`. Safe prefill includes direction, address and passenger/luggage counts. Airport and vehicle are retained only if their current direction-specific price pair is still enabled; otherwise the form selects a currently available pair. Date, time, current account phone, flight details and comment must be supplied or confirmed for the new ride.

The new `TransferBooking` stores a typed source-booking reference but receives fresh configuration/price snapshots and begins in `REQUESTED`. Driver, status, old price and other fulfillment state are never copied.

## Visual identity

Transfer intentionally uses the implemented monochrome/warm-neutral visual direction rather than inheriting Cleaning blue or Rental warm service color. Use the existing implementation as visual source of truth; do not introduce AI-generated decorative vehicle artwork.

## Strategic role

Transfer is a natural cross-service companion to Rental and a potential acquisition entry point for visitors. Its frequency is lower than Cleaning, so evaluate it through fulfillment, economics and contextual cross-service conversion rather than standalone repeat alone.

## Metrics to watch

```text
completed transfers
REQUESTED → CONFIRMED rate
admin vs driver self-accept
provider cancellation/no-show
incident rate
average check by currency
contribution
Rental → Transfer conversion
Transfer repeat
ops minutes/transfer
```
