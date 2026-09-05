# Loco Transfer

Loco Transfer is the third Loco Place vertical. It provides scheduled fixed-price rides between
Alanya and configured airports. The technical aggregate is `TransferBooking`; it is not a generic
platform order and does not share lifecycle state with Cleaning or Rental.

## Customer workflow

The customer chooses direction, airport, vehicle class, pickup date/time, address, capacity and
contact details. Airport pickup additionally requires the flight number and scheduled arrival time.
The backend validates the booking horizon and 30-minute slots in `Europe/Istanbul`, selects an active
rate and persists airport, vehicle and price snapshots. No online payment, map, flight-status API or
distance pricing is involved.

For a typed Rental ARRIVAL/CHECKOUT source, the customer may receive the one-time
`RENTAL_FIRST_TRANSFER` percentage benefit. `POST /api/v1/transfer/quote` and booking creation both
resolve the current customer, Rental ownership/status, service availability, current rate and
benefit state. The booking persists base price, discount, payable price, currency, benefit type and
rate. Ordinary bookings keep a zero discount.

```text
REQUESTED → CONFIRMED → COMPLETED
     ├────→ REJECTED
     └────→ CANCELLED
```

Only the administrator completes a ride in the MVP. The customer sees durable status notifications
in the Loco Place inbox; Telegram delivery is optional when a linked identity permits it.

## Administration

Use `/admin/transfer/configuration` to manage airports, vehicle classes, fixed-rate matrix and
drivers. Liquibase creates GZP, AYT, Sedan and Minivan, but intentionally does not guess commercial
rates. Transfer remains `IN_TEST` until an administrator configures rates and enables the service.

A driver's phone is sufficient for manual assignment. Telegram is optional. To connect it:

1. save the driver's numeric Telegram ID;
2. click **Create and copy link**;
3. send the short-lived one-time link to that driver;
4. the driver opens the link using the configured Telegram account.

Only the token hash is persisted. The bot verifies the actual Telegram user ID before storing the
private chat and enabling notifications. Clearing or changing the configured ID invalidates the old
connection.

## Assignment modes

`TRANSFER_ASSIGNMENT_MODE=ADMIN_ASSIGNMENT` is the safe default. An administrator selects any enabled
driver, including a phone-only driver.

`DRIVER_SELF_ACCEPT` broadcasts new requests only to enabled, connected drivers whose Telegram
notifications are enabled. A callback never supplies a trusted driver ID: the backend resolves the
driver from the verified Telegram identity. Driver acceptance and admin fallback use the same
conditional database update, so concurrent attempts have exactly one winner.

Relevant deployment settings:

```text
TELEGRAM_BOT_USERNAME=go_cleany_bot
TRANSFER_ASSIGNMENT_MODE=ADMIN_ASSIGNMENT
TRANSFER_DRIVER_LINK_TOKEN_TTL=24h
TRANSFER_MIN_BOOKING_DAYS_AHEAD=1
TRANSFER_BOOKING_MONTHS_AHEAD=6
TRANSFER_TIME_SLOT_MINUTES=30
TRANSFER_ZONE_ID=Europe/Istanbul
RENTAL_TRANSFER_BENEFIT_ENABLED=true
RENTAL_TRANSFER_BENEFIT_DISCOUNT_RATE=0.10
```

Rates, airports, vehicles and drivers are PostgreSQL business configuration and are preserved by the
pre-commercial reset. Transfer bookings and their notification/analytics outcomes are purged.
