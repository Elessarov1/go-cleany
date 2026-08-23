# Локальный запуск go-cleany и go-rent в Docker

Этот сценарий поднимает PostgreSQL, backend и frontend одной командой. Backend применяет Liquibase,
работает с Telegram Bot API через long polling и не требует webhook, домена или HTTPS.

## 1. Что понадобится

- Docker Desktop с Docker Compose v2;
- бот, созданный через `@BotFather` командой `/newbot`;
- токен этого бота;
- свободные локальные порты `5432`, `8080` и `5173`.

Для локального polling не вызывайте `setWebhook`. При каждом старте backend сам вызывает
[`deleteWebhook`](https://core.telegram.org/bots/api#deletewebhook), не удаляя ожидающие updates,
а затем получает `message` и `callback_query` через
[`getUpdates`](https://core.telegram.org/bots/api#getupdates).

## 2. Подготовить локальные переменные

Из корня репозитория:

```powershell
Set-Location E:\IdeaProjects\cleany
Copy-Item .env.docker.example .env.docker
notepad .env.docker
```

Вставьте полученный у BotFather токен в `TELEGRAM_BOT_TOKEN`. Файл `.env.docker` уже исключён из
Git. Никогда не добавляйте его в коммит и не отправляйте токен в чат или логи.

По умолчанию `DATA_RETENTION_DAYS=7`, а cleanup запускается ночью. Для длительной локальной отладки
его можно временно выключить через `DATA_CLEANUP_ENABLED=false` и пересоздать backend.
`DATA_CLEANUP_BATCH_SIZE` ограничивает число обрабатываемых записей в одной транзакции, а
`DATA_CLEANUP_MAX_BATCHES_PER_RUN` — число таких транзакций за один запуск.

Параметры `RENTAL_*` задают общую политику go-rent: минимальный и максимальный срок, скидку за
длительное проживание, горизонт бронирования и лимит активных броней. Суточная цена каждой квартиры
задаётся в админке и в `.env` не хранится.

Если свой числовой Telegram ID вы пока не знаете, оставьте эти начальные значения:

```dotenv
CLEANER_TELEGRAM_IDS=1
LOCAL_TELEGRAM_USER_ID=1
ADMIN_TELEGRAM_IDS=1
```

## 3. Первый запуск и получение Telegram ID

Запустите весь контур:

```powershell
docker compose --env-file .env.docker up --build -d
docker compose --env-file .env.docker ps
docker compose --env-file .env.docker logs -f backend
```

Backend готов к работе после строки `Telegram webhook is disabled; long polling is active`. Выйти
из просмотра логов можно через `Ctrl+C` — контейнеры продолжат работать.

Откройте диалог с ботом, нажмите **Start** и отправьте `/whoami`. Бот ответит вашим числовым ID даже
до добавления в список клинеров. Запишите этот ID во все три поля `.env.docker` для простого теста одним
Telegram-аккаунтом:

```dotenv
CLEANER_TELEGRAM_IDS=123456789
LOCAL_TELEGRAM_USER_ID=123456789
ADMIN_TELEGRAM_IDS=123456789
```

Примените новые значения только к backend:

```powershell
docker compose --env-file .env.docker up -d --force-recreate backend
docker compose --env-file .env.docker logs -f backend
```

Если клинеров несколько, перечислите их ID через запятую без пробелов. `LOCAL_TELEGRAM_USER_ID` —
это локальный покупатель, от имени которого обычный браузер обращается к API.
`ADMIN_TELEGRAM_IDS` — отдельный список администраторов; администратор не обязан быть клинером.

## 4. Проверить инфраструктуру

```powershell
docker compose --env-file .env.docker ps
(Invoke-RestMethod http://localhost:8080/actuator/health).status
Invoke-RestMethod http://localhost:5173/api/v1/cleaning/configuration
Invoke-RestMethod http://localhost:5173/api/v1/rental/configuration
Invoke-WebRequest http://localhost:5173/health -UseBasicParsing
```

Ожидаемый статус backend — `UP`, а все три контейнера в `docker compose ps` должны стать
`healthy`. Ответы обоих configuration endpoints через порт frontend дополнительно подтверждают,
что Nginx правильно проксирует backend.

Откройте приложение в браузере: <http://localhost:5173>. Контейнерный frontend использует реальный
HTTP API, а backend с профилем `local` подставляет пользователя из `LOCAL_TELEGRAM_USER_ID`.

## 5. Проверить полный пользовательский сценарий

1. Отправьте боту `/start`, чтобы Telegram разрешил боту писать этому аккаунту.
2. В браузере создайте новый заказ.
3. В Telegram должна появиться карточка заказа. Нажмите **Принять**.
4. Обновите страницу заказа в браузере и проверьте принятый статус.
5. В Telegram нажмите **Завершить уборку**.
6. Отправьте боту минимум одну фотографию и, при желании, отдельное текстовое сообщение как комментарий.
7. Нажмите **Отправить отчёт клиенту**.
8. Бот должен прислать покупателю заголовок отчёта, фотографии и комментарий. При проверке одним аккаунтом всё придёт в тот же чат.
9. Обновите заказ в браузере: итоговый статус должен быть `COMPLETED` / «Выполнен».

### Проверить go-rent

На чистой базе сначала создайте объект как администратор:

1. откройте <http://localhost:5173/admin/rent/properties> и создайте черновик;
2. заполните русское и английское названия/описания, slug, район, адрес, характеристики, цену и валюту;
3. загрузите хотя бы одну фотографию и опубликуйте квартиру;
4. при необходимости закройте тестовый диапазон в календаре квартиры;
5. откройте <http://localhost:5173/rent>, выберите свободный диапазон не короче `RENTAL_MIN_STAY_DAYS` и создайте бронь;
6. проверьте немедленный статус `CONFIRMED` в клиентской истории и в `/admin/rent/bookings`;
7. в админской карточке отмените бронь сначала с освобождением дат, а для другой брони — с флагом сохранения недоступности. Во втором случае календарь должен получить `OWNER_BLOCK`.

Цена и доступность в этом сценарии рассчитываются backend. Значения из frontend не являются
источником истины.

## 6. Проверить администрирование

Если `LOCAL_TELEGRAM_USER_ID` присутствует в `ADMIN_TELEGRAM_IDS`, в нижнем меню браузерного
приложения появится вкладка **Админ**. `/admin` открывает выбор сервиса, `/admin/cleaning` — статистику,
заказы и партнёров уборки, а `/admin/rent` — квартиры, календари и бронирования.

В чате того же бота отправьте:

```text
/admin
/stats
/orders
/order 1
```

Последнюю команду выполните с реальным номером заказа. Пользователь, чей ID отсутствует в
`ADMIN_TELEGRAM_IDS`, получит отказ и через HTTP API, и через бот.

В базе можно дополнительно посмотреть последние заказы:

```powershell
docker compose --env-file .env.docker exec postgres psql -U cleany -d cleany -c "select id, status, telegram_user_id, cleaner_telegram_user_id from cleaning_order order by id desc limit 10;"
```

Если вы изменили `POSTGRES_USER` или `POSTGRES_DB`, подставьте свои значения в команду.

## 7. Остановка и повторный запуск

Остановить контейнеры, сохранив PostgreSQL volume:

```powershell
docker compose --env-file .env.docker down
```

Повторно собрать изменённый код и запустить:

```powershell
docker compose --env-file .env.docker up --build -d
```

Полностью удалить локальную базу и начать с чистого состояния можно командой ниже. Она необратимо
удаляет данные PostgreSQL из Docker volume:

```powershell
docker compose --env-file .env.docker down -v
```

## Частые проблемы

- `TELEGRAM_BOT_TOKEN is required`: заполните токен в `.env.docker`.
- Telegram возвращает `409 Conflict`: одновременно работает другой `getUpdates` клиент. Остановите backend в IDEA, второй Compose-проект или другой экземпляр бота. Для long polling должен работать ровно один backend.
- Бот не присылает сообщения: сначала отправьте ему `/start`, затем проверьте `CLEANER_TELEGRAM_IDS` и логи backend.
- Нет вкладки **Админ** или команды возвращают отказ: проверьте `ADMIN_TELEGRAM_IDS`, затем пересоздайте backend.
- Backend долго не становится healthy: смотрите `docker compose --env-file .env.docker logs backend postgres`; обычно причина — параметры БД, занятый порт или ошибка Liquibase.
- Изменили `.env.docker`, но поведение прежнее: пересоздайте backend с `--force-recreate`.
- Порт занят: измените `POSTGRES_PORT`, `BACKEND_PORT` или `FRONTEND_PORT` в `.env.docker`. Если меняется `FRONTEND_PORT`, используйте новый порт в URL браузера.

Этот локальный режим проверяет браузерный frontend, backend, PostgreSQL и весь сценарий бота. Запуск
Mini App внутри мобильного Telegram требует доступного Telegram HTTPS URL; его следует настраивать
отдельно при подготовке staging/production, не возвращаясь к webhook для получения updates.
