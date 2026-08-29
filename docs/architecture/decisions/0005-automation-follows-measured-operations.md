---
title: Automate measured operational bottlenecks
type: decision
status: accepted
scope: product-architecture
updated: 2026-08-30
---

# 0005 — Automation follows measured operations

## Decision

Understand a workflow manually, measure its operational cost/failure modes, then automate the proven bottleneck.

## Why

A technical team can easily build scheduling, dispatch and generic workflow systems before the business knows what it needs.

## Consequences

- track operational effort such as `ops minutes/order`;
- Transfer manual/admin dispatch is valid until volume proves otherwise;
- deterministic lifecycle rules precede recommendation AI;
- do not introduce speculative infrastructure merely to prepare for scale.
