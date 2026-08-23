# Автоматический деплой go-cleany на staging

Этот runbook дополняет [основную инструкцию по VPS](vps-deployment-runbook.md). Он настраивает
автоматическую цепочку:

```text
push в main ИЛИ ручной Run workflow
  -> backend и frontend CI выполняются параллельно
  -> deploy_staging запускается только после двух успешных jobs
  -> GitHub Actions подключается к VPS по SSH
  -> GitHub staging variables передают роли и общую rental policy
  -> release.sh разворачивает точный commit SHA
  -> backup, Docker build, health checks
```

Чувствительные application secrets (`TELEGRAM_BOT_TOKEN`, пароль PostgreSQL и т.д.) остаются в
`.env.production` только на VPS. Списки Telegram ID клинеров и администраторов для staging управляются
через GitHub Environment variables, чтобы их можно было менять перед демо без SSH на сервер и без
коммита в репозиторий.

## 1. Предварительные условия

Перед настройкой автоматизации обычный ручной деплой уже должен работать:

```bash
cd /opt/go-cleany
git fetch origin
./deploy/scripts/status.sh
```

Пользователь, которому принадлежит `/opt/go-cleany`, должен иметь доступ к Docker и возможность
читать репозиторий `Elessarov1/go-cleany`. Скрипт `release.sh` не использует `sudo`.

Проверьте путь проекта:

```bash
cd /opt/go-cleany
git remote -v
git status --short --branch
```

Если проект установлен в другом каталоге, измените `/opt/go-cleany` в job `deploy_staging` файла
`.github/workflows/ci.yml` до первого автоматического запуска.

## 2. Доступ VPS к GitHub

`release.sh` выполняет `git fetch origin`, поэтому VPS должен самостоятельно читать репозиторий.
Для публичного репозитория достаточно HTTPS remote. Для приватного репозитория создайте на VPS
отдельный read-only deploy key от имени пользователя деплоя:

```bash
install -m 700 -d ~/.ssh
ssh-keygen -t ed25519 -C "go-cleany-vps-readonly" -f ~/.ssh/go-cleany-github -N ""
cat ~/.ssh/go-cleany-github.pub
```

В GitHub откройте:

```text
Repository -> Settings -> Deploy keys -> Add deploy key
```

Добавьте показанный публичный ключ и **не включайте** `Allow write access`. Затем настройте на VPS
`~/.ssh/config`:

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

Не копируйте на VPS личный SSH-ключ от основного GitHub-аккаунта.

## 3. Отдельный ключ GitHub Actions для входа на VPS

Создайте ключ на доверенном компьютере вне каталога репозитория. В PowerShell:

```powershell
$keyPath = "$env:USERPROFILE\.ssh\go-cleany-staging-actions"
ssh-keygen -t ed25519 -C "go-cleany-staging-actions" -f $keyPath
Get-Content "$keyPath.pub"
```

Для automation-ключа оставьте passphrase пустой. Приватный файл никогда не помещайте в репозиторий.

На VPS добавьте публичную строку в `~/.ssh/authorized_keys` пользователя деплоя:

```text
restrict ssh-ed25519 AAAA... go-cleany-staging-actions
```

Затем:

```bash
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

Пользователь из группы `docker` фактически имеет root-level возможности, поэтому этот ключ является
deployment credential.

## 4. Получить и проверить SSH host key VPS

Workflow использует `StrictHostKeyChecking=yes`.

На VPS покажите fingerprint:

```bash
sudo ssh-keygen -lf /etc/ssh/ssh_host_ed25519_key.pub
```

На доверенном компьютере:

```bash
ssh-keyscan -H -p 22 YOUR_VPS_HOST > go-cleany-staging-known-hosts
ssh-keygen -lf go-cleany-staging-known-hosts
```

Сравните fingerprint и только после совпадения используйте содержимое файла как GitHub secret.

## 5. GitHub Environment `staging`

В GitHub:

```text
Repository -> Settings -> Environments -> staging
```

### Environment secrets

| Secret | Значение |
| --- | --- |
| `STAGING_SSH_PRIVATE_KEY` | приватный `go-cleany-staging-actions` |
| `STAGING_SSH_KNOWN_HOSTS` | проверенный SSH host key VPS |

### Environment variables

| Variable | Пример |
| --- | --- |
| `STAGING_SSH_HOST` | `203.0.113.10` или hostname VPS |
| `STAGING_SSH_PORT` | `22` |
| `STAGING_SSH_USER` | пользователь-владелец `/opt/go-cleany` |
| `CLEANER_TELEGRAM_IDS` | `123456789,987654321` |
| `ADMIN_TELEGRAM_IDS` | `123456789,555555555` |
| `RENTAL_MIN_STAY_DAYS` | `7` |
| `RENTAL_LONG_TERM_MIN_DAYS` | `30` |
| `RENTAL_LONG_TERM_DISCOUNT_RATE` | `0.10` |
| `RENTAL_MAX_STAY_DAYS` | `365` |
| `RENTAL_BOOKING_START_MONTHS_AHEAD` | `6` |
| `RENTAL_MAX_ACTIVE_BOOKINGS_PER_CUSTOMER` | `3` |

`CLEANER_TELEGRAM_IDS` и `ADMIN_TELEGRAM_IDS` должны содержать только numeric Telegram IDs через
запятую, без пробелов. Workflow валидирует этот формат до SSH/deploy.

Эти два значения передаются `release.sh` как process environment и имеют приоритет над одноимёнными
fallback-значениями из `.env.production` при Docker Compose interpolation. Файл `.env.production` на
VPS не переписывается.

Такой подход позволяет менять демо-роли непосредственно в GitHub и затем передеплоить staging.

`RENTAL_*` определяют общую политику бронирования стенда. Workflow проверяет целочисленные значения,
взаимное соотношение минимального/долгосрочного/максимального срока и диапазон скидки. Если variables
не заданы, используются показанные безопасные defaults. Суточные цены и описания конкретных квартир
не относятся к deployment configuration: администратор меняет их в `/admin/rent`, и они сохраняются
в PostgreSQL.

Отдельно в:

```text
Repository -> Settings -> Secrets and variables -> Actions -> Variables
```

хранятся repository variables:

| Variable | Значение |
| --- | --- |
| `STAGING_URL` | публичный URL стенда |
| `STAGING_DEPLOY_ENABLED` | `true` включает staging deploy job |

`STAGING_DEPLOY_ENABLED` остаётся repository variable, поскольку условие job вычисляется до загрузки
staging environment.

## 6. Как работает workflow

`deploy_staging`:

1. запускается для `push` в `main` или ручного `workflow_dispatch` на `main`;
2. ждёт успешные `backend` и `frontend` jobs;
3. загружает `staging` environment;
4. валидирует SSH credentials;
5. валидирует `CLEANER_TELEGRAM_IDS`, `ADMIN_TELEGRAM_IDS` и rental policy;
6. подключается к VPS через проверенный host key;
7. передаёт списки ID и `RENTAL_*` в process environment remote command;
8. вызывает `./deploy/scripts/release.sh "$GITHUB_SHA"`.

В Docker Compose shell/process environment имеет приоритет над `--env-file`, поэтому backend
контейнер получает именно значения, заданные в GitHub для этого deploy.

Передаётся точный SHA, прошедший CI. `concurrency` разрешает только одну staging-выкладку одновременно.

## 7. Изменить роли перед демо

Откройте:

```text
Repository -> Settings -> Environments -> staging
```

Измените, например:

```text
CLEANER_TELEGRAM_IDS=111111111,222222222
ADMIN_TELEGRAM_IDS=111111111,333333333
```

Затем откройте:

```text
Repository -> Actions -> CI -> Run workflow
```

Выберите ветку:

```text
main
```

и запустите workflow.

Новый commit для изменения ролей не нужен. Workflow снова прогонит backend/frontend проверки и
передеплоит текущий `main` с новым набором staging access IDs.

После deploy участникам демо достаточно заново открыть Mini App/бот. Backend уже будет работать с
новыми списками ролей.

## 8. Первый автоматический запуск

Убедитесь, что существует repository variable:

```text
STAGING_DEPLOY_ENABLED=true
```

После этого push в `main` автоматически выполняет:

```text
backend  --\
           -> deploy_staging
frontend --/
```

Проверка на VPS:

```bash
cd /opt/go-cleany
./deploy/scripts/status.sh
cat .deploy-state/current-revision
git rev-parse HEAD
```

## 9. Откат и повторный запуск

Application rollback:

```bash
cd /opt/go-cleany
./deploy/scripts/rollback.sh
```

Он не откатывает Liquibase migrations.

При временной CI/SSH ошибке используйте `Re-run failed jobs`.

Если нужно только применить новые cleaner/admin IDs, используйте `Run workflow` вместо пустого
коммита.

## 10. Диагностика

| Ошибка | Что проверить |
| --- | --- |
| `Missing CLEANER_TELEGRAM_IDS staging variable` | variable в environment `staging` |
| `Missing ADMIN_TELEGRAM_IDS staging variable` | variable в environment `staging` |
| `must be comma-separated numeric Telegram IDs` | убрать пробелы и посторонние символы |
| `Host key verification failed` | `STAGING_SSH_KNOWN_HOSTS`, host и SSH port |
| `Permission denied (publickey)` | Actions private key, authorized_keys, SSH user |
| `git fetch` запрашивает пароль | HTTPS credentials или read-only VPS deploy key |
| `Tracked files ... contain local changes` | не редактировать tracked files на VPS |
| `Another go-cleany deployment is already running` | дождаться текущего deploy и повторить workflow |
| Telegram long polling конфликтует | один bot token должен обслуживаться одним backend instance |

## 11. Security note

Telegram numeric user IDs сами по себе не являются authentication secrets: backend всё равно
доверяет только подписанному Telegram `initData`/Telegram Bot updates. Однако role lists влияют на
authorization, поэтому изменять GitHub Environment variables должны только пользователи с правами на
настройку staging environment.
