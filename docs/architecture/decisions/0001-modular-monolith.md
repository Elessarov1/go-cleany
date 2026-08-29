---
title: Keep Loco Place as a modular monolith
type: decision
status: accepted
scope: platform
updated: 2026-08-30
---

# 0001 — Modular monolith

## Decision

Loco Place remains one Spring Boot modular monolith with PostgreSQL while the product is validating local demand, retention, supply and economics.

## Why

Current complexity is business/operational, not distributed-systems scale. One deployment and database make cross-cutting iteration, transactions and operations simpler.

## Consequences

- verticals still need clear package/domain ownership;
- cross-service features may coordinate modules without network boundaries;
- do not add microservices/Kafka/workflow engines because of hypothetical future scale;
- revisit only when measured constraints justify distribution.
