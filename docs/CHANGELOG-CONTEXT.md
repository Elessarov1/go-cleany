---
title: Loco Place Context Changelog
type: decision-history
status: active
scope: platform
updated: 2026-08-30
---

# Context Changelog

This is not a release changelog. It records only major changes that alter how future product/architecture discussions should be framed.

For the current project state, read [CONTEXT.md](CONTEXT.md). For task-specific document routing, use [INDEX.md](INDEX.md). For accepted durable decisions, see [architecture/decisions/README.md](architecture/decisions/README.md).

## 2026-08-30

- Formalized the Loco Place product manifesto.
- Product thesis: Loco is a local service to which a customer delegates a task instead of searching for providers alone.
- Strategic direction: **From catalog to habit**.
- Proposed North Star: **Completed tasks per active customer**.
- Defined `Retention & Trust` as the next major cross-functional phase after the implemented core verticals.
- Established an AI-first repository knowledge base with [CONTEXT.md](CONTEXT.md) and [INDEX.md](INDEX.md).
- CI is expected to test/deploy only application areas that changed; documentation-only changes must not trigger backend/frontend builds or staging deployment.

## 2026-08-29

- Loco Transfer became the third implemented vertical.
- Transfer uses fixed route/vehicle/direction pricing, configurable airport/vehicle availability and explicit `TransferBooking` lifecycle.
- Transfer supports admin driver assignment and atomic Telegram driver self-accept.
- Telegram is optional for drivers; phone-only manual dispatch is a supported operational path.

## Earlier stable foundations

- Loco Place uses one Spring Boot modular monolith and one React frontend.
- `CustomerAccount.id` is canonical customer identity.
- Cleaning and Rental remain separate vertical aggregates.
- `RentalCleaningBenefit` established the explicit bridge pattern for cross-service features.
- First-touch acquisition/analytics is platform-owned and keyed by canonical customer identity.
