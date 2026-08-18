# Deployment

The production deployment targets a single Ubuntu VPS and keeps Telegram updates on long polling.
Only Caddy ports `80` and `443` are published; PostgreSQL, backend, and frontend stay on Docker
networks. Caddy provides automatic HTTPS, while Liquibase runs as part of backend startup.

The operational entry points are deliberately small:

```bash
# Deploy the code currently checked out on the VPS
./deploy/scripts/deploy.sh

# Fetch origin/main and deploy it
./deploy/scripts/release.sh main

# Show container and public health status
./deploy/scripts/status.sh

# Create an explicit PostgreSQL backup
./deploy/scripts/backup.sh

# Roll application code back to the previously deployed revision
./deploy/scripts/rollback.sh
```

`deploy.sh` validates configuration, builds images, creates a database backup when PostgreSQL is
already running, starts the stack, waits for container health checks, and verifies the public HTTPS
endpoint. A rollback never reverses Liquibase changes; migrations must remain backward-compatible.

Follow the complete [VPS deployment runbook](../docs/vps-deployment-runbook.md) for the first server
setup, temporary HTTPS without a purchased domain, Telegram configuration, backups, and releases.
