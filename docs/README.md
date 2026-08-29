---
title: Loco Place Knowledge Base
type: index
status: active
scope: platform
updated: 2026-08-30
---

# Loco Place Knowledge Base

`docs/` is the canonical long-lived knowledge base for Loco Place.

It exists for three readers at once:

- developers;
- Codex and other repository agents;
- ChatGPT when reasoning about the project from GitHub.

Start with [CONTEXT.md](CONTEXT.md), then use [INDEX.md](INDEX.md) to load only the documents relevant to the task.

## Project context

- [Current context](CONTEXT.md) — compact current-state restore point for humans and AI agents.
- [Knowledge routing index](INDEX.md) — task-oriented routing to the minimum relevant documentation.
- [Context changelog](CHANGELOG-CONTEXT.md) — major product and architecture changes that explain how the current direction was reached.

## Canonical strategy

- [Product manifesto](strategy/manifesto.md) — why Loco Place exists and the principles that constrain product decisions.
- [Product roadmap](strategy/roadmap.md) — current stage and intended progression from vertical proof to retention, cross-service intelligence and density.

## Product verticals

- [Loco Cleaning](product/cleaning.md)
- [Loco Rental](product/rental.md)
- [Loco Transfer](product/transfer.md)

All three are implemented verticals. The current code remains authoritative for implementation details.

## Cross-functional capabilities

- [Analytics](cross-functional/analytics.md)
- [Retention & convenience](cross-functional/retention.md)
- [Support & guarantee](cross-functional/support.md)
- [Benefits](cross-functional/benefits.md)
- [Notifications](cross-functional/notifications.md)

## Architecture

- [Architecture overview](architecture/overview.md)
- [Identity](architecture/identity.md)
- [Telegram](architecture/telegram.md)
- [Architecture decisions](architecture/decisions/README.md)

The older [architecture roadmap](architecture/platform-roadmap.md) remains useful detailed background. When it conflicts with the newer manifesto/CONTEXT, verify current code and prefer an explicit newer accepted decision.

## Operations

- [Operations index](operations/README.md)
- [Pre-commercial reset](precommercial-data-reset.md)
- [Local Docker runbook](local-docker-runbook.md)
- [Staging continuous deployment](staging-continuous-deployment.md)
- [VPS deployment runbook](vps-deployment-runbook.md)

## Detailed existing references

These remain canonical for their narrow domains:

- [Acquisition analytics](acquisition-analytics.md)
- [Loco Transfer implementation](loco-transfer.md)
- [Rental → Cleaning benefit](rental-cleaning-benefit.md)
- [Cleaning referral financial model](referral-financial-model.md)
- [Web authentication](web-authentication.md)

## Documentation policy

Keep permanent docs focused on stable product intent, domain invariants, accepted architecture and operational runbooks.

Do not turn the repository into a diary of Codex sessions or completed task plans. Git history already preserves versions. Update a canonical document when a durable decision changes.
