# Автоматический деплой go-cleany на staging

Этот runbook дополняет [основную инструкцию по VPS](vps-deployment-runbook.md). Он настраивает
автоматическую цепочку:

```text
push в main
  -> backend и frontend CI выполняются параллельно
  -> deploy_staging запускается только после двух успешных jobs
  -> GitHub Actions подключается к VPS по SSH
  -> release.sh разворачивает точный commit SHA
  -> backup, Docker build, health checks
```

Application secrets из `.env.production` остаются только на VPS. В GitHub сохраняются лишь отдельный
SSH-ключ для деплоя и проверенный host key сервера.

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

Для этого automation-ключа оставьте passphrase пустой: GitHub runner не сможет вводить её
интерактивно. Приватный файл никому не отправляйте и никогда не помещайте в репозиторий.

На VPS откройте `~/.ssh/authorized_keys` пользователя деплоя и добавьте публичную строку с
ограничениями:

```text
restrict ssh-ed25519 AAAA... go-cleany-staging-actions
```

Затем проверьте права:

```bash
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

Опция `restrict` запрещает forwarding, PTY и ряд дополнительных SSH-возможностей, но ключ всё равно
может выполнять команды от имени пользователя деплоя. Храните его как production credential.

Пользователь из группы `docker` фактически имеет root-level возможности. Для staging допустимо
использовать текущего отдельного deploy-пользователя, но не добавляйте этот ключ личному или root
аккаунту без необходимости.

## 4. Получить и проверить SSH host key VPS

Workflow использует `StrictHostKeyChecking=yes`. Это защищает подключение от подмены сервера и требует
заранее сохранить настоящий host key.

На VPS покажите fingerprint:

```bash
sudo ssh-keygen -lf /etc/ssh/ssh_host_ed25519_key.pub
```

На доверенном компьютере получите публичную строку, подставив реальный host и SSH-порт:

```bash
ssh-keyscan -H -p 22 YOUR_VPS_HOST > go-cleany-staging-known-hosts
ssh-keygen -lf go-cleany-staging-known-hosts
```

Сравните fingerprint с результатом на VPS. Только после совпадения используйте содержимое файла
`go-cleany-staging-known-hosts` как GitHub secret. Один `ssh-keyscan` без проверки fingerprint не
подтверждает подлинность сервера.

## 5. Создать GitHub Environment

В GitHub откройте:

```text
Repository -> Settings -> Environments -> New environment
```

Создайте environment с точным именем:

```text
staging
```

Добавьте Environment secrets:

| Secret | Значение |
| --- | --- |
| `STAGING_SSH_PRIVATE_KEY` | Полное содержимое приватного `go-cleany-staging-actions` |
| `STAGING_SSH_KNOWN_HOSTS` | Проверенная строка или строки из `go-cleany-staging-known-hosts` |

Добавьте Environment variables:

| Variable | Пример |
| --- | --- |
| `STAGING_SSH_HOST` | `203.0.113.10` или hostname VPS |
| `STAGING_SSH_PORT` | `22` |
| `STAGING_SSH_USER` | пользователь-владелец `/opt/go-cleany` |

Отдельно откройте:

```text
Repository -> Settings -> Secrets and variables -> Actions -> Variables
```

Добавьте repository variables:

| Variable | Значение |
| --- | --- |
| `STAGING_URL` | `https://go-cleany-203-0-113-10.nip.io` или актуальный URL стенда |
| `STAGING_DEPLOY_ENABLED` | Пока не создавайте; значение `true` включит автоматические выкладки |

`STAGING_DEPLOY_ENABLED` намеренно является repository variable: условие job вычисляется до запуска
runner и до загрузки environment-level variables. Пока переменная отсутствует, `deploy_staging`
безопасно пропускается, поэтому workflow можно отправить в GitHub до завтрашней настройки сервера.

Если environment secrets недоступны на текущем GitHub-плане, создайте значения с теми же именами в
`Settings -> Secrets and variables -> Actions`. Workflow использует стандартные `secrets` и `vars`,
поэтому repository-level значения также подходят.

Для полностью автоматического staging не включайте required approval. Ограничьте deployment branch
веткой `main`, если такая настройка доступна. Для будущего production создайте отдельный environment с
ручным подтверждением.

## 6. Как работает workflow

В `.github/workflows/ci.yml` job `deploy_staging`:

1. выполняется только для `push` в `main`, но не для pull request;
2. зависит от `backend` и `frontend` через `needs`;
3. получает secrets только в staging environment;
4. проверяет формат приватного SSH-ключа;
5. подключается только к серверу из проверенного `known_hosts`;
6. вызывает `./deploy/scripts/release.sh "$GITHUB_SHA"`.

Передаётся точный SHA, прошедший CI. Даже если во время деплоя в `main` появится следующий commit,
сервер не переключится на непроверенную версию.

`concurrency` разрешает только одну staging-выкладку одновременно. Активный deploy не отменяется новым
push, поскольку прерывание между Liquibase, backup и Docker Compose может оставить неудобное для
диагностики состояние.

## 7. Первый автоматический запуск

После настройки и проверки всех secrets и variables создайте repository variable:

```text
STAGING_DEPLOY_ENABLED=true
```

Именно это включает автоматический deploy. Затем отправьте новый проверенный commit в `main` либо
сделайте обычное изменение приложения. В GitHub откройте:

```text
Repository -> Actions -> CI
```

Ожидаемый порядок:

```text
backend  --\
           -> deploy_staging
frontend --/
```

После зелёного deploy job проверьте:

```bash
cd /opt/go-cleany
./deploy/scripts/status.sh
cat .deploy-state/current-revision
git rev-parse HEAD
```

Последние две команды должны показать один commit. Затем выполните acceptance-flow через Telegram.

## 8. Откат и повторный запуск

Если application deploy прошёл, но обнаружена функциональная проблема:

```bash
cd /opt/go-cleany
./deploy/scripts/rollback.sh
```

Откат меняет application revision, но не откатывает Liquibase migrations. Миграции должны оставаться
совместимыми хотя бы с предыдущей версией приложения.

Если job упал из-за временной сетевой ошибки до запуска `release.sh`, исправьте причину и используйте
`Re-run failed jobs` в GitHub Actions. Повторное развёртывание того же SHA допустимо.

## 9. Диагностика

| Ошибка | Что проверить |
| --- | --- |
| `Host key verification failed` | `STAGING_SSH_KNOWN_HOSTS`, hostname и SSH-порт |
| `Permission denied (publickey)` | приватный Actions key, публичный ключ в `authorized_keys`, `STAGING_SSH_USER` |
| `git fetch` запрашивает пароль | HTTPS credentials либо read-only VPS deploy key |
| `Tracked files ... contain local changes` | не редактируйте отслеживаемые файлы непосредственно на VPS |
| `Another go-cleany deployment is already running` | дождитесь текущей выкладки и повторите job |
| frontend остаётся unhealthy | убедитесь, что deployed Compose проверяет `http://127.0.0.1/health` |
| Telegram long polling конфликтует | один bot token должен обслуживаться только одним backend instance |

Если SSH на VPS доступен только с фиксированного домашнего IP, GitHub-hosted runner не сможет
подключиться без дополнительной сети. Не отключайте проверку host key. Для такого случая отдельно
настройте VPN-overlay (например, Tailscale), постоянно обновляемый allowlist GitHub Actions или иной
pull-based deployment transport.

## 10. Полезные официальные материалы

- [Deployment environments](https://docs.github.com/en/actions/concepts/workflows-and-actions/deployment-environments)
- [Managing environments](https://docs.github.com/en/actions/how-tos/deploy/configure-and-manage-deployments/manage-environments)
- [Workflow syntax: needs, environment and concurrency](https://docs.github.com/en/actions/reference/workflows-and-actions/workflow-syntax)
- [Using secrets in GitHub Actions](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-secrets)
- [Using configuration variables](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-variables)
- [Docker post-install security warning](https://docs.docker.com/engine/install/linux-postinstall/)
