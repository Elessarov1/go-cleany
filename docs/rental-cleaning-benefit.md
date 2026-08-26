# Rental checkout-cleaning benefit

## Purpose and boundary

A confirmed go-renty booking can grant its customer one personal go-cleany benefit for cleaning near
checkout. `RentalCleaningBenefit` is an explicit cross-service bridge between `RentalBooking` and
`CleaningOrder`; it does not merge those aggregates and does not reuse referral semantics.

The benefit belongs to the shared `CustomerAccount.id`. A promo code is only a user-facing lookup
value and is never sufficient authorization: the backend resolves the authenticated customer and
checks ownership.

## Issuance and cancellation

The daily job considers bookings with:

```text
status = CONFIRMED
today is between checkOutDate - RENTAL_CLEANING_CHECKOUT_WINDOW_DAYS and checkOutDate
no existing benefit for the booking
the booking customer may currently start the CLEANING flow
```

The inclusive checkout window lets the scheduler recover a missed run inside that window without
advertising cleaning at check-in. Unique constraints on both booking ID and code are the final
idempotency/concurrency guards. Historical stays and future bookings outside the window are not
backfilled.

Service state applies at issuance and notification time: `ENABLED` allows eligible customers,
`IN_TEST` allows only `ADMIN`, and `DISABLED` allows nobody. An already issued benefit is not revoked
by a later service-state change, but its promo/CTA is hidden and new cleaning creation remains blocked.

A rental cancelled before check-in cannot receive a benefit. If a still-available benefit exists
when an eligible rental is cancelled by the service, it becomes `REVOKED`. Redeemed history is not
rewritten.

## Eligibility and lifecycle

The pilot checkout window is inclusive:

```text
RentalBooking.checkOutDate - RENTAL_CLEANING_CHECKOUT_WINDOW_DAYS
through
RentalBooking.checkOutDate
```

The default window is three days. The normal cleaning booking horizon remains authoritative.

Lifecycle:

```text
AVAILABLE -> RESERVED -> REDEEMED
    ^            |
    +------------+  cleaning order cancelled or terminated by an onsite issue

AVAILABLE -> REVOKED  source rental becomes invalid
```

Only one cleaning order can reserve a benefit. Reservation uses a database lock, and the cleaning
order stores the applied benefit ID. A copied code cannot be used by a different customer, for an
unrelated date, concurrently, or after redemption. Rental benefits do not stack with referrals or
other customer discounts.

## Financial configuration

The backend calculates the discount from the server-side cleaning base price:

```text
min(basePrice * RENTAL_CLEANING_DISCOUNT_RATE, RENTAL_CLEANING_MAX_DISCOUNT)
```

Defaults are `0.10` and `2000 TRY`. The rate and cap are deployment configuration and can be supplied
by GitHub Environment variables for staging. Startup validation rejects a benefit rate above the
cleaning commission rate, preserving a non-negative platform commission pool.

## Notification and entry points

Benefit issuance publishes a stable application event after its transaction commits. The
transport-neutral notification dispatcher selects the active customer channel; Telegram is only the
current adapter. Delivery failure is logged and does not roll back issuance.

The customer API accepts a rental booking ID, resolves the current customer on the backend, verifies
booking ownership, and returns reusable address/phone plus benefit state. Frontend query parameters
carry only rental context and an optional code. They are never trusted for customer identity,
address, phone, eligibility, or financial values.
