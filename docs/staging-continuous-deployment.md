# Автоматический деплой go-cleany на staging

Этот runbook дополняет [основную инструкцию по VPS](vps-deployment-runbook.md). Он настраивает
автоматическую цепочку:

```text
push в main ИЛИ ручной Run workflow
  -> backend и frontend CI выполняются параллельно
  -> deploy_staging запускается только после двух успешных jobs
  -> GitHub Actions подключается к VPS по SSH
  -> GitHub staging variables передают актуальные cleaner/admin Telegram IDs
  -> release.sh разворачивает точный commit SHA
  -> backup, Docker build, health checks
```

GitHub Environment `staging` является источником Telegram/WhatsApp channel configuration и secrets.
Workflow проверяет заданные значения, формирует временный overrides-файл на GitHub-hosted runner,
передаёт его на VPS через зашифрованный SSH stdin и атомарно объединяет с
`/opt/go-cleany/.env.production`. Пароль PostgreSQL, цены, hostname и остальные VPS-настройки этот
workflow не читает и не изменяет.

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
| `TELEGRAM_BOT_TOKEN` | токен staging Telegram-бота |
| `WHATSAPP_ACCESS_TOKEN` | постоянный Meta System User token |
| `WHATSAPP_APP_SECRET` | Meta App Secret |
| `WHATSAPP_WEBHOOK_VERIFY_TOKEN` | случайный verify token для webhook |

### Environment variables

| Variable | Пример |
| --- | --- |
| `STAGING_SSH_HOST` | `203.0.113.10` или hostname VPS |
| `STAGING_SSH_PORT` | `22` |
| `STAGING_SSH_USER` | пользователь-владелец `/opt/go-cleany` |
| `CLEANER_TELEGRAM_IDS` | `123456789,987654321` |
| `ADMIN_TELEGRAM_IDS` | `123456789,555555555` |
| `WHATSAPP_ENABLED` | `true` после заполнения WhatsApp credentials |
| `WHATSAPP_GRAPH_API_BASE_URL` | `https://graph.facebook.com` |
| `WHATSAPP_GRAPH_API_VERSION` | `v25.0` |
| `WHATSAPP_APP_ID` | Meta App ID |
| `WHATSAPP_BUSINESS_PORTFOLIO_ID` | Meta Business Portfolio ID |
| `WHATSAPP_BUSINESS_ACCOUNT_ID` | WABA ID |
| `WHATSAPP_PHONE_NUMBER_ID` | Cloud API Phone Number ID |
| `WHATSAPP_SYSTEM_USER_ID` | Meta System User ID |
| `WHATSAPP_ACCESS_TOKEN_TYPE` | `SYSTEM_USER` |
| `WHATSAPP_ACCESS_TOKEN_EXPIRES_AT` | `NEVER` |
| `WHATSAPP_TEST_REPLY_ENABLED` | `true` только на тестовом стенде |

`CLEANER_TELEGRAM_IDS` и `ADMIN_TELEGRAM_IDS` должны содержать только numeric Telegram IDs через
запятую, без пробелов. При `WHATSAPP_ENABLED=true` workflow также требует все WhatsApp IDs и три
WhatsApp secrets. Пустые channel variables не удаляют существующие значения на VPS.

Secrets нельзя помещать в GitHub Variables: значения Variables не маскируются как credentials.

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
5. проверяет Telegram/WhatsApp secrets/variables и форматы значений;
6. формирует временный channel overrides-файл с правами `600` на GitHub-hosted runner;
7. передаёт файл через SSH stdin, не помещая secrets в remote command arguments;
8. атомарно объединяет overrides с `/opt/go-cleany/.env.production`;
9. вызывает `./deploy/scripts/release.sh "$GITHUB_SHA"`;
10. удаляет временный файл с runner даже при ошибке deploy.

Передаётся точный SHA, прошедший CI. `concurrency` разрешает только одну staging-выкладку одновременно.

## 7. Изменить конфигурацию перед демо

Откройте:

```text
Repository -> Settings -> Environments -> staging
```

Измените нужную Environment variable или secret, например:

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

Новый commit для изменения конфигурации не нужен. Workflow снова прогонит backend/frontend проверки,
сформирует channel overrides и передеплоит текущий `main`.

После deploy участникам демо достаточно заново открыть Mini App/бот. Backend уже будет работать с
новыми списками ролей.

## 8. Первый автоматический запуск

До первого запуска нового workflow добавьте `TELEGRAM_BOT_TOKEN` и WhatsApp credentials в
Environment. Renderer завершит job до SSH и не изменит файл на VPS, если при
`WHATSAPP_ENABLED=true` хотя бы одно обязательное WhatsApp-значение отсутствует.

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
| `Missing required GitHub Environment value` | указанный secret/variable в environment `staging` |
| `must be comma-separated numeric Telegram IDs` | убрать пробелы и посторонние символы |
| `contains a placeholder or characters that cannot be represented safely` | убрать placeholder, перевод строки или одинарную кавычку |
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
