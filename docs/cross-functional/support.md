---
title: Loco Support and Guarantee Direction
type: cross-functional
status: proposed
scope: platform
updated: 2026-08-30
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

## Unified entry point

Every owned Cleaning/Rental/Transfer transaction should eventually expose an obvious support action with the originating transaction already attached.

A platform-level `SupportCase` is a reasonable future model:

```text
id
customerId
service
sourceEntityId
category
status
description
createdAt
resolvedAt
```

Possible shared categories:

```text
PROVIDER_LATE
PROVIDER_NO_SHOW
QUALITY_PROBLEM
BOOKING_PROBLEM
OTHER
```

Do not force all remediation into the shared model. The case is the common ownership/triage layer; each vertical may have its own business resolution.

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

A support case can expose sensitive address/travel/order information. Reuse authenticated ownership checks and show only operationally necessary data.

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
