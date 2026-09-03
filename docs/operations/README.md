---
title: Loco Place Operations Index
type: operations-index
status: active
scope: platform
updated: 2026-09-04
---

# Operations

Use existing runbooks rather than duplicating deployment/cleanup instructions.

## Local

- [../local-docker-runbook.md](../local-docker-runbook.md)
- [performance.md](performance.md) — isolated local k6/JFR performance measurement.
- [local-stress-test-runbook.md](local-stress-test-runbook.md) — пошаговый локальный smoke/stress запуск и разбор результата.

## Staging / production operations

- [../staging-continuous-deployment.md](../staging-continuous-deployment.md)
- [../vps-deployment-runbook.md](../vps-deployment-runbook.md)

## Commercial launch cleanup

- [../precommercial-data-reset.md](../precommercial-data-reset.md)

The reset preserves business configuration such as Rental catalog/configuration, acquisition campaigns and Transfer airports/vehicles/prices/drivers while purging test transactional/customer/analytics data according to the runbook.

## CI behavior

CI detects changed application areas:

```text
backend/**  → backend tests
frontend/** → frontend build
```

Documentation-only/repository-metadata changes should not run backend/frontend jobs and should not deploy staging.

A manual `workflow_dispatch` intentionally validates both application areas; on `main`, staging deployment can still run when explicitly enabled.
