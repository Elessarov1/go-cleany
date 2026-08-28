# Локальный запуск Loco Place в Docker

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

Параметры `RENTAL_*` задают общую политику Loco Rent: минимальный и максимальный срок, скидку за
длительное проживание, горизонт бронирования и лимит активных броней. Суточная цена каждой квартиры
задаётся в админке и в `.env` не хранится.

Если свой числовой Telegram ID вы пока не знаете, оставьте эти начальные значения:

```dotenv
CLEANER_TELEGRAM_IDS=1
LOCAL_TELEGRAM_USER_ID=1
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
до добавления в список клинеров. Запишите этот ID в два поля `.env.docker` для простого теста одним
Telegram-аккаунтом:

```dotenv
CLEANER_TELEGRAM_IDS=123456789
LOCAL_TELEGRAM_USER_ID=123456789
```

Примените новые значения только к backend:

```powershell
docker compose --env-file .env.docker up -d --force-recreate backend
docker compose --env-file .env.docker logs -f backend
```

Если клинеров несколько, перечислите их ID через запятую без пробелов. `LOCAL_TELEGRAM_USER_ID` —
это локальный покупатель, от имени которого обычный браузер обращается к API.
Роль `ADMIN` больше не выдаётся по Telegram ID: её bootstrap выполняет только verified Google email из `ADMIN_GOOGLE_EMAILS`.

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
8. Бот должен прислать покупателю одно уведомление со ссылкой на отчёт в Loco Place. Сами фотографии в Telegram не дублируются.
9. Обновите заказ в браузере: итоговый статус должен быть `COMPLETED` / «Выполнен».

### Проверить Loco Rent

На чистой базе сначала создайте объект как администратор:

1. откройте <http://localhost:5173/admin/rent/properties> и создайте черновик;
2. заполните английское название и описание, при желании русское название, а также район, адрес, характеристики и цену — slug backend создаст автоматически;
3. загрузите хотя бы одну фотографию JPEG или PNG: backend сам исправит ориентацию, уменьшит и сожмёт изображение; затем опубликуйте квартиру;
4. при необходимости закройте тестовый диапазон в календаре квартиры;
5. откройте <http://localhost:5173/rent> и создайте две тестовые брони: одну по свободному диапазону дат, вторую — от даты начала на выбранное число полных месяцев;
6. проверьте немедленный статус `CONFIRMED`, рассчитанную backend цену и даты в клиентской истории и в `/admin/rent/bookings`;
7. если ADMIN-аккаунт связан с Telegram, боту разрешено писать и настройка включена, бот должен прислать ему сообщение о каждой созданной брони;
8. отмените одну бронь клиентом до заезда и проверьте отдельное сообщение администратору;
9. в админской карточке отмените другую бронь с флагом сохранения недоступности: календарь должен получить `OWNER_BLOCK`;
10. снимите опубликованную тестовую квартиру с публикации и убедитесь, что она исчезла из каталога, но история броней сохранилась.

Цена и доступность в этом сценарии рассчитываются backend. Значения из frontend не являются
источником истины.

## 6. Проверить администрирование

Для проверки админки включите Google login, добавьте verified email в `ADMIN_GOOGLE_EMAILS`, войдите через Google и откройте `/admin` вручную. `/admin` открывает выбор сервиса, `/admin/cleaning` — статистику,
заказы и партнёров уборки, а `/admin/rent` — квартиры, календари и бронирования.

В разделах администрирования Loco Rent доступен персональный переключатель Telegram-уведомлений о
бронях. Он изменяет настройку текущего `CustomerAccount` и хранится в PostgreSQL. Потенциальные получатели
определяются по сохранённой роли `ADMIN`, связанной Telegram identity и разрешению боту писать. Отдельная admin Telegram env-переменная не нужна.

В чате того же бота отправьте:

```text
/admin
/stats
/orders
/order 1
```

Последнюю команду выполните с реальным номером заказа. Telegram identity, не связанная с
тем же `CustomerAccount`, где сохранена роль `ADMIN`, получит отказ и через HTTP API, и через бот.

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
- Админка или bot-команды возвращают отказ: проверьте verified email в `ADMIN_GOOGLE_EMAILS`, сохранённую роль `ADMIN` и связь Telegram с тем же `CustomerAccount`.
- Backend долго не становится healthy: смотрите `docker compose --env-file .env.docker logs backend postgres`; обычно причина — параметры БД, занятый порт или ошибка Liquibase.
- Изменили `.env.docker`, но поведение прежнее: пересоздайте backend с `--force-recreate`.
- Порт занят: измените `POSTGRES_PORT`, `BACKEND_PORT` или `FRONTEND_PORT` в `.env.docker`. Если меняется `FRONTEND_PORT`, используйте новый порт в URL браузера.

Этот локальный режим проверяет браузерный frontend, backend, PostgreSQL и весь сценарий бота. Запуск
Mini App внутри мобильного Telegram требует доступного Telegram HTTPS URL; его следует настраивать
отдельно при подготовке staging/production, не возвращаясь к webhook для получения updates.
