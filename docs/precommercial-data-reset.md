# Предкоммерческая очистка данных Loco Place

## Назначение

`deploy/scripts/reset-precommercial-data.sh` — одноразовый ручной инструмент для удаления тестовой
операционной истории непосредственно перед коммерческим запуском. Это не миграция Liquibase и не
часть CI/CD. Без `--execute` скрипт всегда работает как read-only dry-run.

Скрипт сохраняет административный доступ: `CustomerAccount` с persisted-ролью `ADMIN`, его Google /
Telegram identities, роли и настройки административных уведомлений остаются в базе. Обычные аккаунты
удаляются. После очистки все web-сессии, включая административные, сбрасываются — администратору нужно
войти повторно.

## Карта данных

Классификация построена по текущим Liquibase FK и ownership-связям.

### PURGE

- analytics outcomes: `customer_acquisition`, `acquisition_campaign_entry`;
- customer history: `customer_notification`, `customer_identity_link_request`, Spring Session;
- обычные `customer_account` вместе с identities, roles и preferences по `ON DELETE CASCADE`;
- customer-owned `referral_code`, все `referral_reward` и `partner_payout`;
- все Cleaning orders, events, completion photos, onsite issues и их platform media;
- все Rental bookings, только occupancy типа `BOOKING`, booking-derived cleaning benefits;
- все Transfer bookings и связанные с ними notification/analytics outcomes;
- media assets/provider references, не принадлежащие `rental_property_media`.

Перед удалением скрипт явно разрывает reservation-ссылки `CleaningOrder → ReferralReward` и
`CleaningOrder → RentalCleaningBenefit`. Затем удаляет дочерние строки раньше orders/bookings. Он не
использует `TRUNCATE ... CASCADE`.

### PRESERVE

- Liquibase changelog и техническая конфигурация;
- `platform_service_state` и deployment/application configuration;
- ADMIN accounts, identities, roles и admin notification preferences;
- `rental_property`, amenities, catalog media и соответствующие `media_asset`;
- occupancy `OWNER_BLOCK`, `MAINTENANCE`, `EXTERNAL_BOOKING` и любые другие не-`BOOKING` типы;
- `referral_partner` и partner-owned referral codes;
- `acquisition_campaign`, включая неизменные `public_code` уже напечатанных QR.
- Transfer airports, vehicle types, fixed rates, drivers and verified driver Telegram configuration;

Скрипт сравнивает количество сохранённых записей до и после транзакции. Sequence сбрасываются только
для таблиц, которые гарантированно очищаются полностью; sequence campaigns, partners, properties,
accounts, referral codes, media и occupancy не меняются.

## Предварительные условия

1. На VPS актуальная ветка развернута в `/opt/go-cleany`.
2. PostgreSQL из `compose.prod.yaml` запущен и healthy.
3. В `.env.production` нет `CHANGE_ME`, настроен writable `GO_CLEANY_BACKUP_DIR` или используется
   стандартный `/opt/go-cleany/backups`.
4. Проверено, что ADMIN действительно имеет persisted-роль `ADMIN` и может войти через Google.
5. Для destructive запуска используется интерактивная SSH-сессия. Pipe, cron и GitHub Actions
   намеренно не поддерживаются.

## Dry-run

Dry-run разрешён при закрытом reset lock и ничего не меняет:

```bash
cd /opt/go-cleany
./deploy/scripts/reset-precommercial-data.sh
```

Проверьте database/host, все строки `Will delete` и контрольные строки `Will preserve`. Особенно
проверьте ненулевое ожидаемое количество ADMIN accounts, Rental properties, partners и campaigns.

## Выполнение

Откройте reset-окно только вручную в защищённом `.env.production`:

```bash
cd /opt/go-cleany
nano .env.production
# PRECOMMERCIAL_DATA_RESET_ALLOWED=true
chmod 600 .env.production
./deploy/scripts/reset-precommercial-data.sh --execute
```

Введите точную строку:

```text
RESET LOCO PLACE PRECOMMERCIAL DATA
```

После подтверждения скрипт:

1. вызывает существующий `deploy/scripts/backup.sh`;
2. проверяет, что dump существует и не пуст;
3. останавливает backend, если он был запущен;
4. выполняет весь DELETE/reset sequences одной PostgreSQL-транзакцией;
5. проверяет нулевые operational counts и неизменные preserved counts;
6. устанавливает `PRECOMMERCIAL_DATA_RESET_ALLOWED=false`;
7. возвращает backend в запущенное состояние.

Ошибка backup отменяет reset. При SQL-ошибке PostgreSQL откатывает транзакцию; trap возвращает ранее
работавший backend.

## Проверка результата

В финальном выводе должны быть:

- `Pre-commercial data reset completed successfully`;
- путь к непустому backup dump;
- подтверждение закрытого reset lock.

После повторного входа откройте `/admin/analytics`: коммерческие показатели должны быть нулевыми.
Убедитесь, что `/admin/rent` по-прежнему содержит каталог, partner codes работают, а старые URL вида
`https://loco-place.com/a/<public_code>` ведут на настроенный target.

## Коммерческая точка отсчёта

Сразу после успешной очистки задайте точное время старта, например:

```text
COMMERCIAL_LAUNCH_AT=2026-10-01T00:00:00+03:00
ANALYTICS_ZONE_ID=Europe/Istanbul
PRECOMMERCIAL_DATA_RESET_ALLOWED=false
```

`COMMERCIAL_LAUNCH_AT` можно хранить как GitHub Environment variable и применить обычным deploy.
Analytics API не включает события раньше этой точки даже при восстановлении старого backup.
Не открывайте `PRECOMMERCIAL_DATA_RESET_ALLOWED` повторно после начала реальных операций.
