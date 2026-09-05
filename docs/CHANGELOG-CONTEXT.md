---
title: Loco Place Context Changelog
type: decision-history
status: active
scope: platform
updated: 2026-09-05
---

# Context Changelog

This is not a release changelog. It records only major changes that alter how future product/architecture discussions should be framed.

For the current project state, read [CONTEXT.md](CONTEXT.md). For task-specific document routing, use [INDEX.md](INDEX.md). For accepted durable decisions, see [architecture/decisions/README.md](architecture/decisions/README.md).

## 2026-09-05

- Completed the planned Retention & Trust sequence across Analytics, unified Activity, same-service repeat, reusable context, Rental → Transfer, contextual Home, Support & Feedback and Smart Reminders.
- Completed Contextual Benefits Stage 8 with an explicit `RentalTransferBenefit` for the first linked ARRIVAL or CHECKOUT Transfer from a confirmed Rental.
- Kept `RentalTransferBenefit` separate from `RentalCleaningBenefit`; the two bridges have different lifecycle and financial semantics and do not justify a generic benefit engine yet.
- Transfer now stores immutable base, discount, payable, currency, benefit type and benefit-rate snapshots, with backend and database invariants.
- Added benefit-specific funnel and amount analytics while explicitly treating before/after comparison as observational rather than causal.
- Updated CI policy: pull requests no longer trigger automatic duplicate checks. CI runs on pushes to `main` and manual workflow dispatches.
- Extended path-aware release detection so `deploy/**` and `compose.prod.yaml` changes can deploy even when backend/frontend sources are unchanged; documentation-only changes still skip tests and deployment.

## 2026-09-04

- Completed Stage 7.5 — Platform Hardening, Performance & Simplification.
- Added the isolated local-only performance contour with deterministic synthetic data, k6 scenarios, JFR capture and performance-profile JVM/Hikari/Hibernate/scheduler metrics.
- Removed nested customer-resolution connection starvation and kept request-resolved `CurrentCustomer` reusable throughout one authenticated request.
- Added full/card/thumbnail Rental media variants, cover-only public list reads, versioned immutable public URLs and a bounded shared 64 MiB in-process Caffeine media cache.
- Completed the one-off repair of legacy Rental media and removed the temporary startup backfill runtime.
- Lazy-loaded top-level frontend routes and active locale data; kept preview-only APIs/UI out of production bundles.
- Moved optional Smart Reminder external delivery after the durable database commit.
- Consolidated runtime web serving on Caddy and removed the obsolete Nginx proxy layer.
- Retained the measured hybrid persistence strategy: JPA for aggregate mutation/locking, JDBC/native SQL for analytics and specialized atomic or batch operations.
- Recorded measured baseline, codebase audit and after-hardening closure reports under `performance/`.

## 2026-08-30

- Formalized the Loco Place product manifesto.
- Product thesis: Loco is a local service to which a customer delegates a task instead of searching for providers alone.
- Strategic direction: **From catalog to habit**.
- Proposed North Star: **Completed tasks per active customer**.
- Defined `Retention & Trust` as the next major cross-functional phase after the implemented core verticals.
- Established an AI-first repository knowledge base with [CONTEXT.md](CONTEXT.md) and [INDEX.md](INDEX.md).
- CI became path-aware so documentation-only changes could skip backend/frontend builds and staging deployment.

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
