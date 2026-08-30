# Loco Place acquisition analytics

Analytics is a platform capability shared by Loco Cleaning, Loco Rental and Loco Transfer. Canonical customer
identity is `CustomerAccount.id`; Telegram and standalone web are only capture/authentication adapters.

## First-touch rules

At most one `customer_acquisition` row exists per canonical customer. Priority is:

1. explicit active campaign link;
2. existing partner code;
3. existing customer referral code;
4. organic fallback on the first Cleaning order, Rental booking or Transfer request.

A later campaign or code never overwrites first touch. Account merge keeps the earlier of the two
attributions. Existing customers are deterministically backfilled; historical QR sources are not guessed.

Web campaigns use the stable public URL:

```text
https://loco-place.com/a/<public_code>
```

The anonymous entry is counted immediately and the campaign is kept in the server-side session until
login. Telegram Mini App may pass `acq_<public_code>` as `startapp`; the frontend forwards it to the
same backend attribution service. Business logic does not depend on Telegram deep-link mechanics.

## Campaign administration

An administrator normally creates a campaign from `/admin/analytics` using the **Create campaign**
button. The form returns the complete stable tracking URL that should be encoded into the QR code.
The endpoints below remain available for integrations and operational diagnostics.

ADMIN-only endpoints:

```text
GET   /api/v1/admin/acquisition-campaigns
POST  /api/v1/admin/acquisition-campaigns
PATCH /api/v1/admin/acquisition-campaigns/{id}
```

Example campaign definition:

```json
{
  "publicCode": "mh-magnet-sep-2026-a",
  "name": "Mahmutlar magnets / Sep 2026 / batch A",
  "channel": "QR",
  "medium": "QR_MAGNET",
  "targetService": "CLEANING",
  "partnerId": null
}
```

`publicCode` is immutable and never hard-deleted after printing. PATCH changes metadata and can set
`active=false`. Unknown codes safely redirect to `/`; inactive codes keep their configured target but
do not create new entries or attribution.

Separate physical placements should have separate campaigns. `entries` counts campaign link openings,
not unique visitors.

## Business metrics

The ADMIN-only overview is:

```text
GET /api/v1/admin/analytics/overview?from=2026-08-01&to=2026-08-31&service=ALL
```

The `/admin/analytics` UI provides Today, 7-day, 30-day, current-month and custom filters plus the
ALL/CLEANING/RENTAL/TRANSFER dimension.

- new customers use `CustomerAccount.created_at` and first-touch service;
- active customers have at least one successful transaction in the selected period/service;
- repeat customers are active in the period and have at least two lifetime successful transactions
  across platform verticals;
- Cleaning average check uses completed `final_customer_price` snapshots;
- Rental average check uses completed `total_price` snapshots;
- Transfer average check uses completed `price_amount` snapshots;
- currencies are never summed or averaged together.

The same response now includes `businessHealth`, `retention` and `transitions`. Their canonical
definitions live in [cross-functional/analytics.md](cross-functional/analytics.md). Acquisition keeps
its existing first-touch semantics; the service filter for retention and transitions applies to the
customer's first completed task rather than to acquisition attribution.

Each metric uses its own business event timestamp. Calendar boundaries use `ANALYTICS_ZONE_ID`
(`Europe/Istanbul` by default). When configured, `COMMERCIAL_LAUNCH_AT` clamps the earliest included
timestamp.

The one-time launch cleanup and the list of preserved data are documented in
[precommercial-data-reset.md](precommercial-data-reset.md).
