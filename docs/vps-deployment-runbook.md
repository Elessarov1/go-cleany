# Деплой go-cleany на VPS

Этот сценарий рассчитан на один Ubuntu VPS, Docker Compose и Telegram long polling. Снаружи доступны
только Caddy на портах `80/443`; PostgreSQL, backend и frontend не публикуют свои порты. Liquibase
применяет миграции при старте backend.

## 1. Что понадобится

- Ubuntu VPS с публичным статическим IPv4;
- SSH-доступ с пользователем, имеющим `sudo`;
- Git-репозиторий проекта;
- токен Telegram-бота и ID клинеров/администраторов;
- подтверждённые production-цены;
- собственный домен либо временный staging-адрес через `nip.io`.

Для сборки Java и frontend непосредственно на VPS комфортнее использовать не менее 4 ГБ RAM.
На меньшем сервере заранее настройте swap или собирайте образы вне VPS.

## 2. HTTPS до покупки домена

Telegram Mini App должен открываться по публичному HTTPS. До покупки домена можно временно
использовать wildcard DNS `nip.io`. Для VPS с IP `203.0.113.10` адрес будет таким:

```text
go-cleany-203-0-113-10.nip.io
```

`nip.io` преобразует содержащийся в имени IP в DNS A-запись, после чего Caddy получает обычный
публичный сертификат. Это подходит для staging, но production лучше перевести на собственный домен:
сторонний wildcard DNS не находится под вашим контролем.

После покупки домена создайте A-запись на IP VPS, замените `APP_HOST` в `.env.production`, выполните
`./deploy/scripts/deploy.sh` и обновите URL в BotFather.

## 3. Получить код на сервере

Установите Git, создайте каталог и клонируйте репозиторий. URL будет известен после подключения
remote:

```bash
sudo apt-get update
sudo apt-get install -y git
sudo mkdir -p /opt/go-cleany
sudo chown "$USER":"$USER" /opt/go-cleany
git clone <REMOTE_REPOSITORY_URL> /opt/go-cleany
cd /opt/go-cleany
```

Для приватного репозитория используйте отдельный read-only deploy key на VPS. Не копируйте на
сервер личный SSH-ключ от основного GitHub-аккаунта.

## 4. Один раз подготовить Ubuntu

Скрипт подключает официальный Docker apt-репозиторий, устанавливает Docker Engine, Buildx и
Compose plugin, запускает Docker и добавляет указанного пользователя в группу `docker`:

```bash
sudo ./deploy/scripts/bootstrap-ubuntu.sh "$USER"
```

После этого завершите SSH-сессию и подключитесь повторно, затем проверьте:

```bash
docker version
docker compose version
```

Перед включением UFW обязательно сначала разрешите текущий SSH-доступ:

```bash
sudo ufw allow OpenSSH
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 443/udp
sudo ufw enable
sudo ufw status
```

Если у VPS-провайдера есть отдельный сетевой firewall, откройте в нём TCP `80/443` и UDP `443`.
PostgreSQL `5432` и backend `8080` открывать нельзя.

## 5. Заполнить production-конфигурацию

```bash
cd /opt/go-cleany
cp .env.production.example .env.production
chmod 600 .env.production
openssl rand -hex 32
nano .env.production
```

Вставьте сгенерированную строку в `POSTGRES_PASSWORD` и замените каждый `CHANGE_ME`.

Основные переменные:

- `APP_HOST` — hostname без `https://` и пути;
- `ACME_EMAIL` — email для уведомлений центра сертификации;
- `TELEGRAM_BOT_TOKEN` — секрет от BotFather;
- `CLEANER_TELEGRAM_IDS` — ID клинеров через запятую;
- `ADMIN_TELEGRAM_IDS` — ID администраторов через запятую;
- `CLEANING_PRICES_*` — утверждённые цены в TRY.

Production Compose намеренно не включает профиль `local`, `LOCAL_TELEGRAM_USER_ID` и тестовое имя
`Alex`. Клиент определяется только по проверенному Telegram `initData`.

## 6. Первый деплой

```bash
cd /opt/go-cleany
./deploy/scripts/deploy.sh
```

Скрипт последовательно:

1. проверяет `.env.production` и Compose;
2. собирает свежие backend/frontend images;
3. перед обновлением существующего контура делает PostgreSQL backup;
4. запускает контейнеры и ждёт их health checks;
5. проверяет `https://<APP_HOST>/healthz`.

Проверить состояние и логи:

```bash
./deploy/scripts/status.sh
docker compose --env-file .env.production -f compose.prod.yaml logs -f backend
docker compose --env-file .env.production -f compose.prod.yaml logs -f caddy
```

## 7. Подключить Telegram Mini App

После успешного HTTPS-деплоя откройте `@BotFather`:

1. выберите бота;
2. откройте **Bot Settings → Menu Button**;
3. задайте текст кнопки, например `Заказать уборку`;
4. укажите `https://<APP_HOST>`;
5. откройте бота, нажмите обновлённую кнопку и проверьте создание заказа.

Frontend подключает официальный `telegram-web-app.js`, отправляет raw `initData` в backend, а
backend проверяет подпись токеном бота. Обычное открытие production URL вне Telegram не создаёт
тестового пользователя и не даёт доступа к заказам.

## 8. Последующие релизы одной командой

На VPS не редактируйте отслеживаемые Git-файлы. Чтобы получить `origin/main` и развернуть его:

```bash
cd /opt/go-cleany
./deploy/scripts/release.sh main
```

Для зафиксированного тега или commit SHA:

```bash
./deploy/scripts/release.sh v0.1.0
./deploy/scripts/release.sh 0123456789abcdef
```

Скрипт откажется работать при локальных изменениях, использует только fast-forward для ветки и
после обновления вызывает обычный `deploy.sh`.

## 9. Бэкапы и откат

Перед каждым повторным деплоем backup создаётся автоматически в `/opt/go-cleany/backups`.
Создать его отдельно:

```bash
./deploy/scripts/backup.sh
ls -lh backups
```

Файлы имеют PostgreSQL custom format. Регулярно копируйте их на другое физическое хранилище: backup
на том же VPS не защищает от потери сервера. Автоматическое удаление старых копий намеренно не
включено — задайте retention только после подключения внешнего хранилища.

Вернуть предыдущую версию application-кода:

```bash
./deploy/scripts/rollback.sh
```

Или указать конкретный Git ref:

```bash
./deploy/scripts/rollback.sh <commit-or-tag>
```

Rollback пересобирает приложение и не откатывает схему PostgreSQL. Поэтому production-миграции
Liquibase должны оставаться совместимыми хотя бы с предыдущей версией приложения. Восстановление
базы из dump — отдельная аварийная операция, которую сначала необходимо отрепетировать на staging.

## 10. Приёмка перед открытием пользователям

Используйте разные Telegram-аккаунты клиента, клинера и администратора:

1. клиент открывает Mini App и создаёт заказ;
2. клинер получает карточку, принимает заказ и отправляет фотоотчёт;
3. клиент получает фотографии тем же ботом;
4. администратор видит заказ в `/admin` и выполняет `/stats`, `/orders`, `/order <id>`;
5. после перезапуска VPS заказ и история остаются в PostgreSQL;
6. backup копируется со страницы VPS на внешнее хранилище;
7. одновременно работает только один backend, выполняющий long polling этого bot token.
