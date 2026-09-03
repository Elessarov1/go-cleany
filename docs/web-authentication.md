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

В Google Cloud Console создайте OAuth 2.0 Client ID типа **Web application**. Для текущего
канонического хоста `https://loco-place.com` укажите:

```text
Authorized JavaScript origin:
https://loco-place.com

Authorized redirect URI:
https://loco-place.com/login/oauth2/code/google
```

Для staging добавьте его HTTPS-host и такой же callback отдельно. Значения должны в точности
совпадать с внешним URL, включая схему, host и отсутствие лишнего завершающего `/`. Frontend,
OAuth callback и `/api` работают на одном origin; Caddy обслуживает Vite SPA и проксирует
OAuth/API-маршруты напрямую в backend.

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
`ADMIN_GOOGLE_EMAILS` — единственный external bootstrap allowlist: после успешной Google-
аутентификации совпавшему `CustomerAccount` идемпотентно добавляется роль. Email должен быть
присутствующим и `email_verified=true`.

Чтобы отозвать роль в pilot:

1. удалите Google email из `ADMIN_GOOGLE_EMAILS`;
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
display name, provider, platform roles и признак доступности login provider без его credentials.

## Явное связывание Google и Telegram

Автоматического слияния Telegram и Google нет. Совпадение email, телефона, имени или username не
считается доказательством владения. Authenticated Google-пользователь создаёт в `/account` одноразовую ссылку,
затем владение Telegram подтверждается подписанным TMA init data и явным действием. Raw token содержит 256 бит
случайности, живёт 10 минут, одноразовый, а в PostgreSQL хранится только SHA-256 hash.

Каноническая цель — инициировавший Google `CustomerAccount`. При слиянии переносятся владение бизнес-данными,
объединяются роли, сохраняется более ранний `createdAt`, а телефон копируется только в пустой profile. Два разных
непустых телефона или две разные identity одного provider дают явный conflict. Unlinking в этом этапе не реализован.

## Активация постоянного домена и Google Login

До покупки домена оставляйте `GOOGLE_AUTH_ENABLED=false`: backend запускается без Google credentials,
`GET /api/v1/auth/me` сообщает `loginProviders.google.available=false`, а frontend не показывает
неработающую кнопку входа. Значение `APP_HOST` остаётся единственной настройкой публичного hostname;
маршруты `/api/**`, `/oauth2/**` и `/login/oauth2/**` уже направляются Caddy в backend до SPA fallback.

После покупки домена выполните этот чек-лист:

1. Создайте DNS A-запись домена на публичный IP VPS и дождитесь её разрешения.
2. Установите `APP_HOST=loco-place.com` в GitHub Environment `staging`; deployment workflow
   безопасно синхронизирует это значение с защищённым `.env.production` на VPS.
3. Выполните деплой и убедитесь, что Caddy получил корректный HTTPS-сертификат.
4. В Google Cloud Console создайте OAuth 2.0 Client ID типа **Web application**.
5. Добавьте authorized origin `https://loco-place.com`.
6. Добавьте redirect URI `https://loco-place.com/login/oauth2/code/google`.
7. Запишите `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `ADMIN_GOOGLE_EMAILS` в GitHub Environment
   Secrets либо в защищённый `.env.production` на VPS.
8. Установите `GOOGLE_AUTH_ENABLED=true` и выполните деплой. Скрипт остановится до сборки, если
   обязательные Google-настройки отсутствуют.
9. Проверьте маршрут `/`: вход через Google, callback и возврат на исходную страницу.
10. Проверьте `GET /api/v1/auth/me`, затем logout и повторный анонимный ответ.
11. Откройте `/admin` напрямую и проверьте вход allowlisted Google-аккаунтом с ролью `ADMIN`.

Для этой активации не требуется изменение приложения или отдельный auth-сервис. Связывание Telegram
и Google identities в этот чек-лист не входит и остаётся отдельным подтверждаемым пользовательским flow.
