---
title: Retention and Progressive Convenience
type: cross-functional
status: active
scope: platform
updated: 2026-09-02
---

# Retention & Progressive Convenience

This is the primary strategic cross-functional phase after the three core verticals.

Goal:

> Turn the first completed Loco task into the beginning of a relationship, not the end of a funnel.

## P0 — make returning objectively easier

### Unified history

A customer has a coherent Activity screen at `/account/activity` for owned Cleaning, Rental and Transfer transactions without merging their domain models.

`GET /api/v1/account/activity` resolves `CustomerAccount.id` from the authenticated identity and composes the response from the three vertical repositories at request time. It returns active/upcoming items separately from terminal history, preserves price/currency snapshots and links each item to its vertical-owned detail page.

The read model remains available when a service is `IN_TEST` or `DISABLED`; service availability limits new customer flows, not owned history. All mutations remain in the vertical services and detail pages.

The shared customer navigation uses Activity as its stable history destination. Activity and the notification inbox form one visually unified customer section with persistent tabs, while remaining separate read models and routes. Legacy vertical history routes remain available for vertical-specific capabilities and compatibility.

Continue to use read models/composition, not `UniversalOrder`.

### Repeat / Book again

Cleaning and Transfer support repeat actions from an owned `COMPLETED` source transaction:

```text
/cleaning?repeatFrom=<orderId>
/transfer?repeatFrom=<bookingId>
```

The detail page exposes the action only while the service catalog permits a new customer flow. The backend independently verifies current identity, ownership, completed source status and service availability when opening prefill and again when creating the target transaction.

Cleaning reuses area, address, apartment type, duplex and cleaning type. Transfer reuses direction, address and passenger/luggage counts; airport and vehicle are reused only while that exact current configuration pair remains bookable. The form uses the current `CustomerAccount` phone.

Never copy source date/time, price, assignment, status, discount/referral/benefit, comment, flight data or historical phone. The customer chooses new scheduling data and the backend recalculates current price, availability and eligibility.

The new transaction stores only a typed self-reference to its source (`CleaningOrder` → `CleaningOrder`, `TransferBooking` → `TransferBooking`). No universal context/order aggregate is introduced.

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

Rental → Transfer is implemented as an explicit bridge. A confirmed owned Rental exposes independent arrival and checkout actions while their dates are still actionable:

```text
ARRIVAL  → FROM_AIRPORT → check-in date  → property destination
CHECKOUT → TO_AIRPORT   → checkout date → property pickup
```

The backend computes whether each option is bookable now or when its Transfer booking window opens. A current matching Transfer suppresses only the corresponding option. Typed links close the option even when the customer edits the suggested date/address; conservative matching also recognizes an unlinked Transfer for the same customer, direction, source date and normalized property address.

The customer still chooses airport, time, vehicle, passenger/luggage and flight data. Changing direction leaves the Rental flow and creates a normal Transfer. Cancelling Rental never silently cancels its separately created Transfer.

### Contextual next action

The customer home at `/` is a contextual read model for an authenticated returning customer while remaining a clear service catalog for guests and new customers:

```text
current active task
one primary next useful action
repeat opportunity
service catalog
```

`GET /api/v1/account/home` resolves the current `CustomerAccount`, reuses the first active item from Activity and selects at most one backend-verified contextual action plus one eligible Cleaning/Transfer repeat. Rental → Transfer participates only while immediately bookable; Rental → Cleaning participates only while its existing benefit is available. Repeat is suppressed when the same service is already active or is the primary action target.

The read model is composed at request time and persists nothing. Owned active work remains visible when its service is `IN_TEST` or `DISABLED`, while every new action still follows current customer-flow availability. The frontend records displays through the existing idempotent Rental → Transfer and repeat funnels; HOME and DETAIL are intentionally one funnel.

Avoid an advertising feed, recommendation engine or client-calculated eligibility.

### Support entry point

Every owned Cleaning, Rental and Transfer detail page now provides the shared "Need help" panel with source context already attached. It works for every transaction status and independently of current service availability. The backend verifies source ownership through the vertical repository and exposes the latest case and resolution in the originating transaction.

The platform support queue gives persisted administrators one oldest-first operational inbox while preserving vertical aggregates and vertical admin detail pages. See [support.md](support.md).

### Simple feedback

Completed transactions now accept one immutable `GOOD` or `PROBLEM` result. Negative feedback requires an issue category and atomically opens or reuses a support case; positive feedback remains a private quality signal. No public five-star marketplace rating or analytics dashboard is introduced.

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

Smart Reminders v1 is implemented as deterministic scheduled evaluation over domain state and the existing notification pipeline. A single daily job runs at `09:00 Europe/Istanbul` by default:

```text
explicit Cleaning choice → repeat after 14 or 30 days
confirmed Rental → checkout Transfer offer 3 days before checkout
confirmed Transfer → operational reminder 1 day before pickup
```

Cleaning is opt-in per completed source order and can be switched between 14/30/off until it is notified, superseded or expired. A later non-cancelled Cleaning for the same customer, area and normalized address supersedes the need. Temporary Cleaning unavailability is retried for seven days.

Rental reminders reuse the explicit checkout bridge and appear only while the checkout Transfer context is currently `BOOKABLE` with no matching active Transfer. Transfer operational reminders describe an already existing confirmed operation and therefore remain valid even when the Transfer service is unavailable for new requests.

Every reminder becomes a durable semantic Loco inbox notification. Telegram is an optional secondary delivery when a linked identity allows writes. Notification and reminder uniqueness make repeated/concurrent evaluations idempotent.

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
