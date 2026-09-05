---
title: Loco Transfer
type: vertical-context
status: active
scope: transfer
updated: 2026-08-31
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

GZP, AYT, Sedan and Minivan are seeded business configuration. Commercial rates are admin-managed.
Bookings store base, discount, payable and currency snapshots; ordinary and legacy bookings have
equal base/payable values and a zero discount.

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

## Rental context

A confirmed owned Rental can open Transfer with explicit `ARRIVAL` or `CHECKOUT` context:

```text
/transfer?rentalBooking=<id>&rentalContext=ARRIVAL|CHECKOUT
```

The bridge derives direction, source date and current property address on the backend, and validates ownership, Rental status, service availability and Transfer booking horizon again at creation. Date/address may be edited; changing direction clears the source context. Airport, time, vehicle, passenger/luggage and flight details remain current customer inputs.

`TransferBooking` stores nullable typed Rental source fields. An active/completed linked booking is unique per Rental/context; cancellation or rejection permits a retry. An unlinked Transfer with the same customer, direction, source date and normalized property address also suppresses duplicate prompting. Rental and Transfer lifecycles remain independent.

The first linked ARRIVAL or CHECKOUT trip can apply `RENTAL_FIRST_TRANSFER`, configured by
`RENTAL_TRANSFER_BENEFIT_*` and defaulting to 10% for every tariff currency without a monetary cap.
`POST /api/v1/transfer/quote` recalculates the active rate and eligibility; creation repeats the same
checks and never silently falls back to full price when the client expects the benefit. Reservation
is atomic per Rental. Confirmation consumes the benefit permanently, while cancellation/rejection
before confirmation releases it. Notifications and driver messages use the payable snapshot.

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
