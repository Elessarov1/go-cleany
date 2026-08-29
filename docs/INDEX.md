---
title: Loco Place Knowledge Routing Index
type: ai-routing-index
status: active
scope: platform
updated: 2026-08-30
---

# Loco Place — Knowledge Routing Index

Always start with [CONTEXT.md](CONTEXT.md). Then load only the route relevant to the task.

## Product strategy / new idea

Read:

1. [strategy/manifesto.md](strategy/manifesto.md)
2. [strategy/roadmap.md](strategy/roadmap.md)
3. the relevant current vertical or cross-functional document.

Use this route for new verticals, positioning, catalog/home changes, growth ideas and prioritization.

## Designing a new vertical

Read:

1. [CONTEXT.md](CONTEXT.md)
2. [strategy/manifesto.md](strategy/manifesto.md)
3. [architecture/overview.md](architecture/overview.md)
4. [architecture/identity.md](architecture/identity.md)
5. [cross-functional/notifications.md](cross-functional/notifications.md)
6. one or more existing vertical docs under `product/`.

Do not copy an existing aggregate just for consistency. Reuse platform capabilities; keep new business lifecycle vertical-owned.

## Cleaning

Read:

- [product/cleaning.md](product/cleaning.md)
- [referral-financial-model.md](referral-financial-model.md) for referral economics.

## Rental

Read:

- [product/rental.md](product/rental.md)
- [rental-cleaning-benefit.md](rental-cleaning-benefit.md) for the checkout cleaning bridge.

## Transfer

Read:

- [product/transfer.md](product/transfer.md)
- [loco-transfer.md](loco-transfer.md) for exact current transfer behavior.
- [architecture/telegram.md](architecture/telegram.md) for driver bot linking/self-accept.

Transfer is implemented; verify code before treating any old transfer task plan as outstanding work.

## Retention / repeat / home personalization

Read:

- [strategy/manifesto.md](strategy/manifesto.md)
- [cross-functional/retention.md](cross-functional/retention.md)
- [cross-functional/benefits.md](cross-functional/benefits.md)
- [cross-functional/analytics.md](cross-functional/analytics.md)

## Benefits / promos / cross-service bridges

Read:

- [cross-functional/benefits.md](cross-functional/benefits.md)
- [rental-cleaning-benefit.md](rental-cleaning-benefit.md)
- [referral-financial-model.md](referral-financial-model.md) when Cleaning referral money is involved.

Keep referral semantics separate from lifecycle/cross-service benefits unless a deliberate accepted decision changes that.

## Analytics / acquisition / KPIs

Read:

- [cross-functional/analytics.md](cross-functional/analytics.md)
- [acquisition-analytics.md](acquisition-analytics.md)
- [strategy/manifesto.md](strategy/manifesto.md) for business-health priorities.

## Support / incidents / guarantee / failure handling

Read:

- [cross-functional/support.md](cross-functional/support.md)
- the source vertical document.

## Notifications

Read:

- [cross-functional/notifications.md](cross-functional/notifications.md)
- [architecture/identity.md](architecture/identity.md)
- [architecture/telegram.md](architecture/telegram.md) if Telegram is involved.

## Authentication / account linking / ADMIN

Read:

- [architecture/identity.md](architecture/identity.md)
- [web-authentication.md](web-authentication.md)
- [architecture/telegram.md](architecture/telegram.md) for Telegram linking.

## Architecture / refactor / module boundary

Read:

- [architecture/overview.md](architecture/overview.md)
- [architecture/decisions/README.md](architecture/decisions/README.md)
- [architecture/platform-roadmap.md](architecture/platform-roadmap.md) for detailed historic direction.

## Deployment / CI / environment

Read:

- [operations/README.md](operations/README.md)
- [staging-continuous-deployment.md](staging-continuous-deployment.md)
- [vps-deployment-runbook.md](vps-deployment-runbook.md)

## Data cleanup / commercial launch

Read:

- [precommercial-data-reset.md](precommercial-data-reset.md)
- [acquisition-analytics.md](acquisition-analytics.md)

## Documentation changes

Update [CONTEXT.md](CONTEXT.md) only for durable project-level changes. Update this index when a new canonical document needs routing. Do not create permanent documents for temporary task execution logs.
