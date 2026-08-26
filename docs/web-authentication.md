# Web-аутентификация и platform ADMIN

## Архитектура

Standalone web использует прямой Google OpenID Connect через Spring Security OAuth2 Client.
Google подтверждает внешнюю личность, а приложение владеет `CustomerAccount`, ролями, бизнес-данными
и серверной сессией. Сессии хранятся в существующем PostgreSQL через Spring Session JDBC; отдельные
Keycloak, Firebase Auth, Google Identity Platform, Redis и собственный OAuth/OIDC server не нужны.

```text
Google OIDC sub
→ CustomerExternalIdentity(provider = GOOGLE)
→ CustomerAccount
→ PostgreSQL-backed web session
```

Токены Google не возвращаются frontend и не сохраняются в `localStorage`. Production-cookie имеет
`HttpOnly`, `Secure` и `SameSite=Lax`. Изменяющие web-запросы используют Spring CSRF token; Telegram
Mini App продолжает передавать явный `Authorization: tma ...` и не создаёт browser session.

## Google OAuth Console

В Google Cloud Console создайте OAuth 2.0 Client ID типа **Web application**. Для публичного хоста
`https://example.com` укажите:

```text
Authorized JavaScript origin:
https://example.com

Authorized redirect URI:
https://example.com/login/oauth2/code/google
```

Для staging добавьте его HTTPS-host и такой же callback отдельно. Значения должны в точности
совпадать с внешним URL, включая схему, host и отсутствие лишнего завершающего `/`. Frontend,
OAuth callback и `/api` работают на одном origin; nginx проксирует OAuth-маршруты в backend.

## Runtime-конфигурация

```env
GOOGLE_AUTH_ENABLED=true
GOOGLE_CLIENT_ID=<backend secret>
GOOGLE_CLIENT_SECRET=<backend secret>
ADMIN_GOOGLE_EMAILS=admin@example.com
WEB_SESSION_TIMEOUT=12h
```

`GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` и `ADMIN_GOOGLE_EMAILS` хранятся в GitHub Environment
Secrets либо в защищённом `chmod 600` файле `.env.production` на VPS. Они не должны иметь префикс
`VITE_`, попадать в repository Variables, логи или собранный frontend.

Для автоматического staging-деплоя добавьте в environment `staging`:

- Secrets: `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `ADMIN_GOOGLE_EMAILS`;
- Variables: `GOOGLE_AUTH_ENABLED=true`, при необходимости `WEB_SESSION_TIMEOUT=12h`.

Если `GOOGLE_AUTH_ENABLED=false`, реальные Google credentials не требуются. Backend валидирует
наличие обеих credentials при включённом входе.

## ADMIN bootstrap и отзыв доступа

`ADMIN` хранится в `customer_role` и принадлежит `CustomerAccount`, а не Telegram или Google.
`ADMIN_TELEGRAM_IDS` и `ADMIN_GOOGLE_EMAILS` — только bootstrap allowlists: после успешной
аутентификации совпавшему аккаунту идемпотентно добавляется роль. Для Google email должен быть
присутствующим и `email_verified=true`.

Чтобы отозвать роль в pilot:

1. удалите Telegram ID или Google email из соответствующего deployment secret/configuration;
2. передеплойте backend;
3. найдите нужный `customer_id` через `customer_external_identity`;
4. удалите только его роль:

```sql
delete from customer_role
where customer_id = <CUSTOMER_ID>
  and role = 'ADMIN';
```

Если не убрать identity из bootstrap allowlist, следующий успешный вход вернёт роль.

## Вход и маршруты

Публичный web не показывает ссылку на админку. Администратор открывает `/admin` вручную. Без сессии
страница предлагает **Продолжить через Google**; обычный authenticated customer получает нейтральную
страницу «не найдено». Backend всё равно отдельно защищает каждый `/api/v1/admin/**` запрос.

Выход выполняется через `POST /api/v1/auth/logout`, инвалидирует JDBC session и удаляет session/CSRF
cookies. Текущая сессия доступна через `GET /api/v1/auth/me`; DTO содержит только внутренний customer,
display name, provider и platform roles.

## Отложенное связывание аккаунтов

Автоматического слияния Telegram и Google нет. Совпадение email, телефона, имени или username не
считается доказательством владения. Проверенное связывание нескольких external identities с одним
`CustomerAccount` — отдельный следующий этап; текущая модель `CustomerExternalIdentity →
CustomerAccount` сохраняет такую возможность.
