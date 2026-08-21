# Architecture Documentation Index

This directory contains stable architecture context for go-cleany and the future service platform.

Task-specific implementation specs are intentionally not stored here by default.

A task-specific Markdown file may be provided directly to a Codex session as temporary execution context.

---

## 1. Platform roadmap

```text
platform-roadmap.md
```

Read this for:

- long-term product vision;
- go-cleany as the cleaning vertical;
- Telegram → WhatsApp → Flutter direction;
- future multi-service platform;
- platform vs vertical boundaries;
- customer identity direction;
- media and notification architecture direction;
- future service catalog;
- architecture rules that should survive individual tasks.

This is the strategic architecture document.

It should change only when the overall product or architecture direction changes.

Before acting on anything described there, verify the current implementation and recent git history.

---

## 2. Referral financial model

```text
../referral-financial-model.md
```

Read this for changes involving:

- friend referral discounts;
- referrer rewards;
- partner referrals;
- partner payouts;
- cleaning commission economics;
- financial invariants.

Referral financial rules are currently cleaning-specific.

Do not generalize them to all future service verticals without a concrete second-vertical requirement.

---

## 3. Runbooks

Operational documentation lives outside this architecture directory.

Typical examples:

```text
../local-docker-runbook.md
../vps-deployment-runbook.md
```

Use these for deployment and local environment work rather than architecture decisions.

---

## Task-specific specifications

Implementation plans for concrete work should normally be supplied directly to the active Codex session.

Examples:

```text
media migration
WhatsApp integration
API namespace migration
mobile authentication
specific feature implementation
```

Treat them as temporary execution context.

Do not copy them into `docs/architecture/` simply to preserve task history.

If a task produces a durable architectural decision, update `platform-roadmap.md` or another stable domain document instead.

---

## Documentation policy

Keep repository documentation focused on stable context:

```text
architecture
product/domain invariants
operations/runbooks
```

Do not maintain:

```text
daily task logs
Codex session history
completed implementation diaries
temporary TODO archives
ephemeral implementation plans
```

unless explicitly requested.

Current code is the source of truth for implementation state.

Architecture documents define intended long-term direction and constraints.
