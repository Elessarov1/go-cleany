# Автоматический деплой go-cleany на staging

Этот runbook дополняет [основную инструкцию по VPS](vps-deployment-runbook.md) и описывает актуальную GitHub Actions цепочку.

```text
push в main ИЛИ ручной Run workflow
        ↓
определение изменённых областей
        ↓
backend tests — только если менялся backend
frontend build — только если менялся frontend
        ↓
deploy_staging — если менялось приложение или production deployment configuration
        ↓
SSH на VPS → release.sh → backup → Docker build → health checks
```

Автоматический workflow **не запускается при создании или обновлении pull request**. Это сознательное решение: одни и те же backend/frontend проверки не выполняются автоматически сначала на PR, а затем повторно после merge. Обычная автоматическая проверка выполняется один раз — на результирующем push в `main`.

Когда нужна проверка до merge, запустите `Actions → CI → Run workflow` вручную на feature branch. Такой запуск проверит backend и frontend полностью, но не выполнит staging deploy, потому что deploy разрешён только для `main`.

Долгоживущие infrastructure secrets (`TELEGRAM_BOT_TOKEN`, пароль PostgreSQL и т. д.) остаются в `.env.production` только на VPS. Google OIDC credentials и web-admin allowlist передаются из GitHub Environment Secrets только в runtime конкретной выкладки.

## 1. Предварительные условия

Перед настройкой автоматизации обычный ручной деплой уже должен работать:

```bash
cd /opt/go-cleany
git fetch origin
./deploy/scripts/status.sh
```

Пользователь-владелец `/opt/go-cleany` должен иметь доступ к Docker и возможность читать репозиторий `Elessarov1/go-cleany`. Скрипты деплоя не используют `sudo`.

Проверьте рабочую копию:

```bash
cd /opt/go-cleany
git remote -v
git status --short --branch
```

Если проект установлен не в `/opt/go-cleany`, измените путь в job `deploy_staging` файла `.github/workflows/ci.yml`.

## 2. Доступ VPS к GitHub

`release.sh` выполняет `git fetch origin`. Для публичного репозитория достаточно HTTPS remote. Для приватного репозитория создайте на VPS отдельный read-only deploy key:

```bash
install -m 700 -d ~/.ssh
ssh-keygen -t ed25519 -C "go-cleany-vps-readonly" -f ~/.ssh/go-cleany-github -N ""
cat ~/.ssh/go-cleany-github.pub
```

В GitHub:

```text
Repository → Settings → Deploy keys → Add deploy key
```

Не включайте `Allow write access`.

Пример `~/.ssh/config` на VPS:

```sshconfig
Host github.com
  HostName github.com
  User git
  IdentityFile ~/.ssh/go-cleany-github
  IdentitiesOnly yes
```

```bash
chmod 600 ~/.ssh/config
cd /opt/go-cleany
git remote set-url origin git@github.com:Elessarov1/go-cleany.git
git fetch origin
```

Не копируйте на VPS личный SSH-ключ основного GitHub-аккаунта.

## 3. Ключ GitHub Actions для входа на VPS

Создайте отдельный ключ на доверенном компьютере вне репозитория. В PowerShell:

```powershell
$keyPath = "$env:USERPROFILE\.ssh\go-cleany-staging-actions"
ssh-keygen -t ed25519 -C "go-cleany-staging-actions" -f $keyPath
Get-Content "$keyPath.pub"
```

Для automation-ключа passphrase должна быть пустой. Приватный файл никогда не помещайте в репозиторий.

На VPS добавьте public key в `~/.ssh/authorized_keys` пользователя деплоя:

```text
restrict ssh-ed25519 AAAA... go-cleany-staging-actions
```

```bash
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

Пользователь из группы `docker` фактически имеет root-level возможности, поэтому этот ключ является полноценным deployment credential.

## 4. Проверка SSH host key

Workflow использует `StrictHostKeyChecking=yes`.

На VPS:

```bash
sudo ssh-keygen -lf /etc/ssh/ssh_host_ed25519_key.pub
```

На доверенном компьютере:

```bash
ssh-keyscan -H -p 22 YOUR_VPS_HOST > go-cleany-staging-known-hosts
ssh-keygen -lf go-cleany-staging-known-hosts
```

Сравните fingerprint и только после совпадения сохраните содержимое файла в GitHub secret.

## 5. GitHub Environment `staging`

Откройте:

```text
Repository → Settings → Environments → staging
```

### Environment secrets

| Secret | Значение |
| --- | --- |
| `STAGING_SSH_PRIVATE_KEY` | приватный `go-cleany-staging-actions` |
| `STAGING_SSH_KNOWN_HOSTS` | проверенная строка SSH host key VPS |
| `GOOGLE_CLIENT_ID` | Google OAuth web client ID для staging |
| `GOOGLE_CLIENT_SECRET` | Google OAuth web client secret |
| `ADMIN_GOOGLE_EMAILS` | allowlist verified Google emails через запятую |

### Environment variables

| Variable | Пример / default |
| --- | --- |
| `STAGING_SSH_HOST` | VPS IP или hostname |
| `STAGING_SSH_PORT` | `22` |
| `STAGING_SSH_USER` | пользователь-владелец `/opt/go-cleany` |
| `APP_HOST` | `loco-place.com` |
| `CLEANER_TELEGRAM_IDS` | `123456789,987654321` |
| `TELEGRAM_MINI_APP_LINK_BASE` | `https://t.me/<bot>/<mini-app>` |
| `TELEGRAM_BOT_USERNAME` | `go_cleany_bot` без `@` |
| `TRANSFER_ASSIGNMENT_MODE` | `ADMIN_ASSIGNMENT` или `DRIVER_SELF_ACCEPT` |
| `GOOGLE_AUTH_ENABLED` | `true` / `false` |
| `WEB_SESSION_TIMEOUT` | `12h` |
| `ANALYTICS_ZONE_ID` | `Europe/Istanbul` |
| `COMMERCIAL_LAUNCH_AT` | ISO-8601 timestamp после предкоммерческой очистки |
| `RENTAL_MIN_STAY_DAYS` | `7` |
| `RENTAL_LONG_TERM_MIN_DAYS` | `30` |
| `RENTAL_LONG_TERM_DISCOUNT_RATE` | `0.10` |
| `RENTAL_MAX_STAY_DAYS` | `365` |
| `RENTAL_BOOKING_START_MONTHS_AHEAD` | `6` |
| `RENTAL_MAX_ACTIVE_BOOKINGS_PER_CUSTOMER` | `3` |
| `RENTAL_MEDIA_CACHE_ENABLED` | `true` |
| `RENTAL_MEDIA_CACHE_MAX_SIZE` | `64MB` |
| `RENTAL_TRANSFER_BENEFIT_ENABLED` | `true` |
| `RENTAL_TRANSFER_BENEFIT_DISCOUNT_RATE` | `0.10` |
| `RENTAL_CLEANING_DISCOUNT_RATE` | `0.10` |
| `RENTAL_CLEANING_MAX_DISCOUNT` | `2000` |

`CLEANER_TELEGRAM_IDS` содержит numeric Telegram IDs через запятую без пробелов. `TELEGRAM_MINI_APP_LINK_BASE` задаёт deep link без bot token. `TELEGRAM_BOT_USERNAME` используется для одноразовых driver-link URL.

Google credentials и admin allowlist должны храниться в Environment **Secrets**, не Variables. При `GOOGLE_AUTH_ENABLED=true` workflow проверяет их наличие и передаёт только в backend runtime. Для canonical host callback должен быть зарегистрирован как:

```text
https://loco-place.com/login/oauth2/code/google
```

`RENTAL_MEDIA_CACHE_*` управляют общим для backend-instance weighted cache публичных Rental-фотографий. `64MB` — pilot default.

`RENTAL_TRANSFER_BENEFIT_*` управляют скидкой первого связанного трансфера по подтверждённой аренде. Default `0.10` означает 10% в валюте текущего Transfer-тарифа без денежного cap.

`RENTAL_CLEANING_*` управляют checkout Cleaning benefit Loco Rental. Backend дополнительно защищает экономические ограничения.

`ANALYTICS_ZONE_ID` определяет календарные границы отчётов. `COMMERCIAL_LAUNCH_AT` остаётся пустым до финальной предкоммерческой очистки. См. [pre-commercial reset runbook](precommercial-data-reset.md).

Repository variables:

```text
Repository → Settings → Secrets and variables → Actions → Variables
```

| Variable | Значение |
| --- | --- |
| `STAGING_URL` | `https://loco-place.com` |
| `STAGING_DEPLOY_ENABLED` | `true` включает staging deploy job |

`STAGING_DEPLOY_ENABLED` остаётся repository variable, потому что условие job вычисляется до загрузки environment `staging`.

## 6. Как работает change detection

Workflow запускается автоматически только на `push` в `main`; также доступен ручной `workflow_dispatch`.

| Изменения | Backend tests | Frontend build | Deploy staging |
| --- | --- | --- | --- |
| `backend/**` | запускаются | пропускается, если frontend не менялся | запускается после успешной проверки |
| `frontend/**` | пропускаются, если backend не менялся | запускается | запускается после успешной проверки |
| `deploy/**` | пропускаются | пропускается | запускается |
| `compose.prod.yaml` | пропускаются | пропускается | запускается |
| только `docs/**`, README или другие non-runtime файлы | пропускаются | пропускается | пропускается |
| ручной workflow | запускаются обе проверки | запускаются обе проверки | только если выбран `main` |

Deployment-only изменения раньше могли быть ошибочно классифицированы как `app=false`. Теперь у них отдельный признак `deployment`, а итоговый `deploy` становится true для любого изменения backend, frontend или production deployment configuration.

Изменения `.github/workflows/ci.yml` сами по себе не требуют выкладки runtime. Они применяются в GitHub Actions после push.

## 7. Как работает `deploy_staging`

`deploy_staging`:

1. допускается только для ref `main`;
2. требует `STAGING_DEPLOY_ENABLED=true`;
3. запускается, когда detector установил `deploy=true`;
4. ждёт успеха изменённых application jobs, а неизменённые jobs принимает как `skipped`;
5. загружает environment `staging`;
6. валидирует SSH credentials и runtime variables;
7. подключается к VPS через проверенный host key;
8. передаёт deployment/runtime configuration в remote process environment;
9. вызывает `./deploy/scripts/release.sh "$GITHUB_SHA"`.

На VPS release разворачивает точный commit SHA, создаёт pre-deployment PostgreSQL backup, пересобирает Compose images, ждёт health checks и проверяет публичный HTTPS endpoint.

`concurrency` допускает только одну staging-выкладку одновременно.

## 8. Проверка feature branch до merge

Pull request сам по себе workflow не запускает.

Для ручной проверки:

```text
Repository → Actions → CI → Run workflow
```

Выберите feature branch. `workflow_dispatch` намеренно прогонит backend и frontend полностью. `deploy_staging` останется skipped, потому что ref не равен `main`.

Это полезно перед рискованным merge, но не создаёт обязательный двойной прогон для каждого PR.

## 9. Изменить runtime variables без нового commit

Измените значения в environment `staging`, затем запустите `Actions → CI → Run workflow` на ветке `main`.

Ручной запуск на `main`:

```text
backend tests
+
frontend build
+
deploy текущего main
```

Новый пустой commit не нужен.

## 10. Первый автоматический запуск

Убедитесь, что существует:

```text
STAGING_DEPLOY_ENABLED=true
```

После merge/push в `main` workflow проверит только затронутые application areas и выполнит deploy, если commit влияет на backend, frontend или production deployment configuration.

Проверка на VPS:

```bash
cd /opt/go-cleany
./deploy/scripts/status.sh
cat .deploy-state/current-revision
git rev-parse HEAD
```

## 11. Откат и повторный запуск

Application rollback:

```bash
cd /opt/go-cleany
./deploy/scripts/rollback.sh
```

Он не откатывает Liquibase migrations.

При временной CI/SSH ошибке используйте `Re-run failed jobs`.

## 12. Диагностика

| Ошибка | Что проверить |
| --- | --- |
| `Missing CLEANER_TELEGRAM_IDS staging variable` | variable в environment `staging` |
| `Missing ADMIN_GOOGLE_EMAILS staging secret` | secret в environment при включённом Google login |
| `must be comma-separated numeric Telegram IDs` | убрать пробелы и посторонние символы |
| `Host key verification failed` | `STAGING_SSH_KNOWN_HOSTS`, host и SSH port |
| `Permission denied (publickey)` | Actions private key, `authorized_keys`, SSH user |
| `git fetch` запрашивает пароль | HTTPS credentials или read-only VPS deploy key |
| `Tracked files ... contain local changes` | не редактировать tracked files на VPS |
| `Another go-cleany deployment is already running` | дождаться текущего deploy и повторить workflow |
| Telegram long polling конфликтует | один bot token должен обслуживаться одним backend instance |
| deployment-only commit не запускает deploy | путь должен находиться в `deploy/**` или быть `compose.prod.yaml` |

## 13. Security note

Telegram numeric user IDs не являются authentication secrets: backend доверяет только подписанному Telegram `initData` и Telegram Bot updates. Но role lists влияют на authorization, поэтому environment settings должны изменять только пользователи с соответствующими правами.

SSH deployment key, Google secrets и доступ пользователя VPS к Docker являются привилегированными credentials и не должны попадать в репозиторий или логи.
