---
title: Loco Support and Guarantee Direction
type: cross-functional
status: active
scope: platform
updated: 2026-08-31
---

# Support & Guarantee

Support is part of the Loco product promise.

Customer mental model:

```text
I ordered through Loco
        ↓
If something is wrong, I go to Loco
```

Do not make the customer discover which provider owns the problem.

## Implemented unified entry point

Every owned Cleaning/Rental/Transfer detail page exposes the shared `TransactionCarePanel`. The panel attaches the originating transaction without asking the customer to identify it again, and remains available for every transaction status even when the service is `IN_TEST` or `DISABLED`.

The platform `support` module owns two records without changing any vertical aggregate:

```text
SupportCase
├── customerId + service + sourceEntityId
├── category + optional description
├── OPEN | RESOLVED
├── createdAt / resolvedAt / resolvedByCustomerId
├── resolutionComment
└── optimistic version

TransactionFeedback
├── customerId + service + sourceEntityId
├── GOOD | PROBLEM
├── optional category/comment
├── optional supportCaseId
└── createdAt
```

Shared categories:

```text
PROVIDER_LATE
PROVIDER_NO_SHOW
QUALITY_PROBLEM
BOOKING_PROBLEM
OTHER
```

The backend resolves the polymorphic reference through the corresponding Cleaning, Rental or Transfer repository and verifies `CustomerAccount.id` ownership. Missing and foreign sources both return `404`. The reference is a platform triage/read boundary, not a universal order aggregate.

At most one `OPEN` case exists for a customer/source tuple. Repeated opening returns that case; after `RESOLVED`, a new case may be opened. Resolution is final and requires a non-empty comment. Database constraints and a partial unique index reinforce the application rules.

## Feedback

Completed transactions without feedback ask one simple question: `GOOD` or `PROBLEM`.

- feedback is immutable and accepted once per customer/source;
- `GOOD` stores a quality signal without opening a case;
- `PROBLEM` requires a category and atomically stores feedback plus a new case, or links to the already-open case;
- free-form descriptions/comments are optional and limited to 2000 characters.

This is an operational signal, not a public provider rating.

## API and admin workflow

Customer API:

```text
GET  /api/v1/account/support/sources/{service}/{sourceEntityId}
POST /api/v1/account/support/cases
POST /api/v1/account/support/feedback
```

Admin API and UI:

```text
GET  /api/v1/admin/support/cases
GET  /api/v1/admin/support/cases/{caseId}
POST /api/v1/admin/support/cases/{caseId}/resolve

/admin/support
/admin/support/cases/:id
```

The default queue contains the oldest open cases and supports status/service filters and pagination. Only persisted `ADMIN` roles can use it. Customer/source summaries are intentionally limited, and the source link points to the vertical-owned admin detail page.

The customer sees the case status and final resolution comment in the original transaction. Closing a case does not create a separate customer notification.

## Notifications

Only actual case creation publishes an after-commit event. `SUPPORT_CASE_CREATED` is recorded once per persisted administrator in the durable inbox, then routed through the existing communication dispatcher. A linked Telegram identity receives it only when write access is allowed.

The Telegram payload contains service, transaction number, category and a safe local admin link. The customer description is never copied into the external notification, and the support service has no Telegram dependency.

## Loco Guarantee principle

We do not need to promise an automatic refund policy before business rules exist. We do need a clear operational promise:

> **If fulfillment fails, Loco does not leave the customer alone with the provider.**

Resolution may involve:

```text
replacement provider
new time
operator contact
incident investigation
appropriate compensation under explicit policy
```

## Failure playbooks

Each vertical should document answers to:

```text
provider cancels
provider late/no-show
customer unavailable
quality complaint
Loco cannot fulfill
customer cancellation
```

These are product flows, not only support scripts.

## Privacy

A support case can expose sensitive address/travel/order information. Customer APIs always reuse authenticated ownership checks. Admin queue responses expose only operationally necessary customer/source summaries; full vertical data stays behind the existing persisted-admin detail endpoints.

Ownership of cases and feedback follows explicit verified Google ↔ Telegram account merge. Both tables are purged before source transactions by the pre-commercial reset.

## Current limits

Attachments, assignment, `IN_PROGRESS`, SLA automation, compensation workflows, reopening, full customer case history, public ratings and support analytics are intentionally deferred. Vertical-specific remediation stays vertical-owned until concrete repeated semantics justify a shared abstraction.

## Metrics

```text
incident rate
no-show rate
time to first support response
time to resolution
replacement success
repeat after incident
provider issue rate
```

The purpose is not to optimize ticket volume down by hiding support. The purpose is to improve reliable fulfillment and trust.
