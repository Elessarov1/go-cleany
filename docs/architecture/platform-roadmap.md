# go-cleany / Service Platform — Product & Architecture Roadmap

## Purpose

This document is the long-lived architectural and product context for go-cleany.

It is intended to be read by developers and Codex sessions when working on architectural or cross-cutting changes.

It describes:

- long-term product direction;
- stable architecture principles;
- channel-neutral and service-neutral boundaries;
- intended evolution from Telegram to WhatsApp and Flutter;
- intended evolution from a cleaning product to a broader service platform.

This document is NOT:

```text
a task backlog
an implementation checklist
a session log
a completed-work history
```

Concrete implementation plans should normally be supplied directly to the active development/Codex session.

Always verify current code and recent git history before assuming an architectural capability is or is not implemented.

---

# 1. Product vision

`go-cleany` was the first vertical of the service platform. `go-renty` is now the second real vertical.
The rental brand is user-facing; technical routes and code namespaces remain `rent`, `rental` and
`Rental*` so a brand change does not force an API migration.

Today:

```text
Service Platform

├── go-cleany
│   └── apartment cleaning
└── go-renty
    └── apartment rental
```

Future:

```text
Service Platform

├── go-cleany
│   └── cleaning
│
├── go-renty
│   └── apartment rental
│
├── handyman / repair
│   ├── electrician
│   ├── minor repairs
│   └── handyman services
│
├── relocation / residence
│   ├── residence-permit assistance
│   ├── documents
│   └── consultations
│
└── future verticals
```

`go-cleany` remains the cleaning brand/vertical.

The parent platform may receive a separate brand later.

---

# 2. Service catalog

The entry screen is now a service catalog because more than one real vertical exists.

Concept:

```text
What do you need?

[ 🧹 Cleaning ]
[ 🏢 Apartment rental ]
```

After selection:

```text
Cleaning
→ go-cleany flow

Apartment rental
→ go-renty flow

Handyman
→ handyman-specific flow

Residence
→ case/consultation-specific flow
```

Only implemented verticals belong in the catalog. Do not add placeholder Handyman or Residence cards before those services exist.

Customer availability is platform-owned operational state persisted as `ENABLED`, `IN_TEST` or
`DISABLED`. `IN_TEST` customer flows are available only to `CustomerAccount` records with `ADMIN`;
`DISABLED` blocks every new customer flow. Neither state may hide existing owned transactions or
disable vertical administration.

---

# 3. Two dimensions of neutrality

The architecture should remain neutral in two separate dimensions.

## Channel neutrality

Customer channels may include:

```text
Telegram
WhatsApp
Flutter mobile application
future channels
```

Business/domain logic should not be copied per channel.

## Service neutrality

Service verticals may include:

```text
Cleaning
Rental
Handyman
Residence assistance
future services
```

Shared platform infrastructure should not depend on cleaning-specific business rules.

---

# 4. Target high-level architecture

```text
                         SERVICE PLATFORM

                              Customer
                                 │
             ┌───────────────────┼───────────────────┐
             │                   │                   │
         Telegram            WhatsApp            Flutter
             │                   │                   │
             └────────────── adapters ──────────────┘
                                 │
          ┌──────────────────────┼───────────────────────┐
          │                      │                       │
       Identity            Communication              Media
          │                      │                       │
          └──────────────────────┼───────────────────────┘
                                 │
                           Service Catalog
                                 │
            ┌────────────────────┼────────────────────┐
            │                    │                    │
        Cleaning              Handyman             Residence
        go-cleany
```

---

# 5. Platform-level capabilities

Capabilities that may be shared across verticals:

```text
customer
identity
communication
notification
media
retention
authentication
catalog
shared admin infrastructure
```

Typical platform responsibilities:

- internal customer identity;
- external identities;
- communication routing;
- customer notifications;
- media storage;
- retention;
- future standalone authentication;
- service catalog;
- shared application/navigation shell.

---

# 6. Vertical-specific capabilities

Do not force business processes from different services into one generic aggregate.

Cleaning may contain:

```text
CleaningOrder
fixed pricing
requested cleaning date
cleaner assignment
completion report
onsite issue
cleaning-specific referral economics
```

Handyman may require:

```text
HandymanRequest
problem description
photos
inspection
quote
customer approval
executor assignment
completion
```

Residence assistance may require:

```text
ResidenceCase
country
permit/case type
documents
consultation
specialist
appointments
case lifecycle
```

Rental currently contains:

```text
RentalProperty
RentalBooking
RentalOccupancy
DATE_RANGE and MONTHLY terms
stay policy and pricing
availability calendar
rental administration
```

Monthly rental remains a concrete dated occupancy: checkout is derived from check-in with
`plusMonths`, while pricing is based on `daily × 30 × (1 - configured discount)`. The booking keeps
the term, month count where applicable, and immutable price snapshots.

Keep separate aggregates such as:

```text
CleaningOrder
HandymanRequest
ResidenceCase
```

Do not create `UniversalOrder`.

Do not use a single generic JSONB payload as the primary domain model for all service verticals.

---

# 7. Dependency direction

Desired rule:

```text
vertical → platform
```

Allowed:

```text
cleaning → customer
cleaning → notification
cleaning → media

rental → customer
rental → notification
rental → media

handyman → customer
handyman → media

residence → customer
residence → notification
```

Avoid:

```text
platform → cleaning business logic
handyman → cleaning
residence → cleaning
rental → cleaning
cleaning → rental
```

Verticals must remain independent from one another.

---

# 8. Customer identity

The internal customer identity is:

```text
CustomerAccount.id
```

This should remain the business-level customer identifier.

External identities may include:

```text
TELEGRAM
WHATSAPP
MOBILE_APP
GOOGLE
APPLE
PHONE
```

Concept:

```text
CustomerAccount
    ↓
CustomerExternalIdentity
```

Do not make Telegram ID, WhatsApp ID, phone number, Google ID or Apple ID the primary domain identity.

---

# 9. Authenticated customer context

Channel-specific authentication may parse channel-specific principals.

Example:

```text
Telegram initData
→ Telegram adapter
→ generic authenticated external identity
→ CustomerAccount
```

Reusable customer/application code should operate on a generic identity representation rather than a Telegram-specific principal.

Target concepts may include:

```text
customerId
externalIdentityId
provider
externalSubject
displayName
languageCode
```

The exact implementation can evolve.

The stable rule is:

```text
channel-specific authentication details stay at the adapter boundary
```

---

# 10. Identity and communication

Authentication identity and communication destination are not necessarily the same concept.

Example:

```text
user authenticates with GOOGLE
user receives operational notifications through PUSH
```

The platform may therefore evolve toward separate concepts such as:

```text
CustomerExternalIdentity
→ who/authentication identity

CustomerCommunicationEndpoint
→ where notifications are delivered
```

Do not overbuild this until multiple channels require it.

However, new reusable business APIs should not assume that every customer is reachable through Telegram.

---

# 11. Operational communication context

A customer may eventually have multiple channels:

```text
Customer #42
├── Telegram
├── WhatsApp
└── Mobile app
```

An order/case may need to remember which communication identity/channel owns its operational conversation.

Conceptually:

```text
Service workflow
    customerId
    communication identity / endpoint
```

This allows:

```text
WhatsApp-created order
→ operational updates go back to WhatsApp
```

without making WhatsApp part of the order domain logic.

---

# 12. Notification architecture

Domain/application code should express facts/events.

Prefer:

```text
OrderCompleted
OrderAccepted
OrderCancelled
OnsiteIssueReported
ReferralUnlocked
```

over:

```text
SendTelegramMessage
```

Target:

```text
domain/application event
        ↓
notification layer
        ↓
communication routing
        ↓
channel adapter
```

Adapters may include:

```text
Telegram
WhatsApp
Push
```

Cleaner-side interaction may remain Telegram-specific while the customer side becomes channel-neutral.

The current rental admin flow follows this boundary: booking creation and customer cancellation
publish rental events, an `AFTER_COMMIT` listener prepares the operational notification, and the
Telegram adapter delivers it. Per-admin rental notification preferences belong to the adapter-facing
rental notification layer; they do not alter booking state or other verticals' notifications.

---

# 13. Media architecture

Important operational media should belong to the platform rather than to an external messaging provider.

Target:

```text
channel upload
    ↓
backend obtains bytes
    ↓
MediaStorage
    ↓
MediaAsset
```

For the pilot:

```text
binary content
→ PostgreSQL BYTEA
```

Future storage may become:

```text
S3 / object storage
```

without changing service-domain behavior.

---

# 14. Media assets and provider references

Canonical binary content and provider references are different concerns.

Concept:

```text
MediaAsset
    id
    content/content-location
    contentType
    sizeBytes
    sha256
    createdAt
```

Provider references may include:

```text
Telegram file_id
WhatsApp media_id
```

One internal media asset may eventually have multiple provider references.

Do not design internal media as fundamentally Telegram-specific.

---

# 15. Media storage boundary

A storage abstraction is justified because storage backend replacement is realistic.

Concept:

```java
interface MediaStorage {
    StoredMedia store(MediaUpload upload);
    MediaContent get(long mediaId);
    void delete(long mediaId);
}
```

Pilot implementation:

```text
PostgresMediaStorage
```

Future:

```text
ObjectStorageMediaStorage
```

Switching storage should not affect cleaning business rules.

---

# 16. Retention

Heavy operational payloads do not need indefinite retention during the pilot.

Retention is configurable.

The default policy is approximately:

```text
7 days
```

Cleanup must not remove data belonging to active workflows.

A typical long-term pattern:

```text
heavy binary evidence
→ removed after retention

lightweight business metadata
→ retained
```

Database backup retention is also necessary because older dumps may still contain already-cleaned media.

Do not duplicate cleanup mechanisms when one already exists.

---

# 17. Cleaning vertical

`go-cleany` remains the cleaning vertical.

Cleaning-specific concepts may include:

```text
CleaningOrder
CleaningPriceService
CleaningOrderStatus
Cleaner assignment
Completion report
Onsite issue
Cleaning referral economics
```

Do not move these into generic platform services unless another real vertical shares the same business semantics.

---

# 18. Referral direction

The current referral economics are cleaning-specific.

Current conceptual model:

```text
service commission pool

friend first-order benefit
referrer future reward
partner customer discount
partner payout
```

The addition of go-renty does not generalize those financial rules: rental bookings currently have no cleaning referral discounts, rewards or partner payouts.

Future shared concepts might eventually include:

```text
ReferralCode
Partner
ReferralCampaign
```

but campaign economics may differ per vertical.

Wait for actual second-vertical requirements before generalizing.

---

# 19. API direction

As multiple verticals and clients appear, vertical APIs should be explicit.

Target direction:

```text
/api/v1/cleaning/...
/api/v1/rental/...
/api/v1/handyman/...
/api/v1/residence/...
```

Shared platform APIs may include:

```text
/api/v1/customer/...
/api/v1/referrals/...
/api/v1/media/...
/api/v1/catalog/...
```

Do not preserve ambiguous global `/orders` semantics once multiple order/case types exist.

API migration timing should be chosen pragmatically while clients are still controlled by this project.

---

# 20. Frontend direction

The current React application is the cleaning UI.

Future routing may evolve toward:

```text
/
→ ServiceCatalogPage

/cleaning
/cleaning/orders

/handyman
/residence
```

The current routes include:

```text
/
→ ServiceCatalogPage

/cleaning
/cleaning/orders

/rent
/rent/properties/:slug
/rent/bookings
```

Do not build placeholder verticals solely to satisfy future architecture.

---

# 21. Admin direction

The admin has a shared service entry and separate cleaning and rental sections:

```text
/admin
├── /admin/cleaning
└── /admin/rent
```

Future shared shell may look like:

```text
Admin
├── Cleaning
├── Handyman
├── Residence
├── Customers
├── Partners
└── System
```

Shared navigation and access control belong to the platform shell. Cleaning and rental operations remain vertical-specific.

Rental administration owns property publication, occupancy, bookings and the current administrator's
own Telegram booking-notification preference. Public property slugs are backend-generated and stable.
Descriptions are authored in English, while media uploads are normalized into canonical platform
assets before being attached to the rental property.

---

# 22. WhatsApp direction

WhatsApp is the next customer channel after Telegram.

Desired shape:

```text
WhatsApp customer
        ↓
channel adapter
        ↓
existing platform/cleaning backend
        ↓
Cleaner may remain Telegram
Admin may remain current web UI
```

WhatsApp-specific code should handle:

```text
webhooks
provider authentication
identity mapping
message/media API
channel UI/Flows
delivery adapter
```

Do not duplicate:

```text
cleaning pricing
order lifecycle
referral logic
onsite issue rules
```

for WhatsApp.

---

# 23. Flutter direction

After multi-channel boundaries are proven with WhatsApp, standalone applications should use:

```text
Flutter / Dart
```

for Android and iOS from one codebase.

Likely mobile capabilities:

```text
REST API
authentication
push notifications
deep links
forms
media
local storage
camera/gallery where needed
```

Separate Kotlin and Swift applications are not the intended approach.

Some native platform integration may still be needed for specific functionality.

iOS building/signing will require macOS/Xcode at that stage.

---

# 24. Standalone authentication

The standalone browser currently authenticates through direct Google OIDC using Spring Security
OAuth2 Client. Google is an external identity provider only. The application owns `CustomerAccount`,
platform roles, authorization and PostgreSQL-backed server sessions; it is not an authorization
server and does not depend on Keycloak, Firebase Auth or Google Identity Platform.

`ADMIN` is a persisted role of `CustomerAccount`. Telegram IDs and verified Google emails may grant
it through deployment bootstrap allowlists, but reusable authorization always checks the role.

Telegram TMA headers and Google web sessions resolve through one generic authenticated customer
boundary. Cookie-authenticated writes require CSRF; explicit TMA credentials remain stateless.

Cross-provider account linking is intentionally deferred. Telegram and Google identities may belong
to separate accounts until a later explicit verified linking flow. Never merge identities
automatically by email, phone, display name or username.

Do not solve mobile authentication prematurely.

Future possibilities may include:

```text
PHONE OTP
GOOGLE
APPLE
```

They should resolve/create external identities attached to `CustomerAccount`.

Adding mobile authentication must not require rewriting cleaning/referral business logic.

---

# 25. API contract before mobile clients mature

Before a Flutter client becomes a long-lived consumer, formalize the public backend contract.

Preferred direction:

```text
Spring backend
→ OpenAPI
   ├── TypeScript client
   └── Dart client
```

Important contract areas:

```text
customer
service configuration/catalog
orders/cases
quotes
referrals
media
```

---

# 26. Multiple verticals

go-renty is the second real vertical and validates that shared identity, communication, media, retention and UI infrastructure do not require a generic order aggregate.

A future Handyman/repair vertical will further test the boundaries because its lifecycle should differ substantially from both cleaning and rental.

Good architecture should allow:

```text
new HandymanRequest
```

without adding handyman-specific fields or statuses to:

```text
CleaningOrder
```

If adding a vertical requires modifying the cleaning aggregate into a generic mega-model, revisit the boundaries.

---

# 27. Current infrastructure philosophy

Prefer incremental architecture.

The current product intentionally favors:

```text
Spring Boot monolith
PostgreSQL
React
Docker Compose
simple scheduled jobs
```

Do not introduce:

```text
microservices
Kafka
workflow engines
additional databases
object storage
```

until a concrete requirement justifies them.

PostgreSQL BYTEA is acceptable for pilot media while retention controls growth.

---

# 28. Known deferred issues

Known issues may intentionally remain deferred during the pilot:

```text
phone-based referral anti-abuse across multiple external identities

durable outbound delivery state/checkpoints for partial notification failures

frontend local-date timezone differences vs service timezone

deployment rollback vs forward-only database schema evolution

stronger branch protection / required checks
```

Do not repeatedly treat these as newly discovered blockers unless a task directly depends on them.

---

# 29. Architecture rules for future work

Before implementing a cross-cutting feature, ask:

```text
Is this a platform capability or vertical business logic?
```

Platform examples:

```text
customer
identity
communication
notification
media
retention
auth
catalog
```

Vertical examples:

```text
cleaning pricing
cleaning lifecycle
cleaner workflow
onsite issue
cleaning referral economics
```

Also ask:

```text
Does this reusable code really require Telegram ID?
```

If `customerId` or external identity is enough, avoid new Telegram coupling.

Ask:

```text
Does important media exist only as an external provider ID?
```

If yes, prefer internal media ownership.

Ask:

```text
Does reusable business code send directly through Telegram?
```

If yes, consider a notification/application boundary.

---

# 30. What not to do

Do not:

```text
rewrite the monolith into microservices
introduce Kafka merely for abstraction
create interfaces around every service
create UniversalOrder
generalize referral economics before a real second vertical
create WhatsApp-specific copies of cleaning business services
make Telegram ID the core customer identity
use external provider media IDs as the only durable media source
build separate Kotlin and Swift applications
store task/session history as permanent architecture documentation
```

---

# 31. Documentation policy

This document contains stable intent, not current execution status.

Concrete work should be driven by:

```text
current repository state
+
task-specific specification supplied to the active session
```

If a completed task changes a long-term architectural rule, update this document.

Do not add temporary task plans or completed-work logs here.

---

# 32. Success criteria

The architecture is moving in the correct direction if:

```text
Adding WhatsApp
does not require rewriting cleaning business rules.
```

```text
Adding Flutter
does not require rewriting Telegram/WhatsApp business logic.
```

```text
Adding Handyman
does not require modifying CleaningOrder into a generic order.
```

```text
Moving media from PostgreSQL to object storage
does not change vertical business behavior.
```

These are the core long-term architecture goals.
