# Referral Financial Model v1

Реферальная модель Loco Cleaning реализована в backend и не зависит от канала, через который
пользователь открыл приложение. Telegram сейчас используется только для аутентификации и доставки
уведомлений. Referral-код можно передать вручную или через универсальный query parameter:

```text
https://APP_HOST/?ref=GC7K9P2M4Q
```

Такую ссылку можно отправлять в Telegram, браузере или будущем мобильном приложении.

## Финансовые правила

Для каждого заказа отдельно:

```text
commissionPool = basePrice × 15%
platformNet = commissionPool - customerDiscount - partnerPayout
platformNet >= 0
```

Итоговая скидка или выплата дополнительно ограничивается денежным cap и доступным остатком
комиссионного пула. По умолчанию все caps равны `2000 TRY`.

Заказ хранит неизменяемый snapshot: базовую цену, ставку и сумму комиссии, скидку клиенту,
партнёрскую выплату, итоговую цену клиента и доход платформы. Исторические заказы мигрируются как
`ORGANIC` без изменения прежней цены.

## Customer referral

Referral-код клиента становится доступен после первого заказа в статусе `COMPLETED`.
Приглашённый новый клиент получает скидку 15%. После завершения его первого referral-заказа
пригласившему создаётся отдельный reward 10% на один будущий заказ. Несколько rewards используются
по одному; reward резервируется заказом и возвращается при отмене.

Повторная first-order выгода после удаления аккаунта защищена отдельным псевдонимным ledger. Если у
удаляемого клиента была завершённая Cleaning, для каждого связанного Google / Apple / Telegram
identity сохраняется только HMAC-маркер со сроком один год. При quote и create customer- и
partner-referral проверяются одинаково; совпадение возвращает общий `referral_not_applicable`, не
раскрывая распознавание прежнего аккаунта. Полностью новый, ранее не связанный provider остаётся
допустимым ограничением v1; телефон, email, IP, device ID и payment fingerprint не используются.

При удалении клиентские коды деактивируются, `AVAILABLE` / `RESERVED` rewards переводятся в
`REVOKED` с очисткой reservation, а `REDEEMED` и финансовые snapshots сохраняются. Завершение уже
созданного referral-заказа не выдаёт новый reward referrer-аккаунту со статусом `DELETED`.

## Partner referral

Администратор создаёт партнёра в web-админке и передаёт ему универсальный код. Первый заказ нового
клиента получает скидку 5%. После перехода заказа в `COMPLETED` создаётся выплата партнёру 10% со
статусом `PAYABLE`. После ручного или пакетного перечисления администратор отмечает её как `PAID`.

До завершения заказа денежного обязательства перед партнёром в таблице выплат нет; плановая сумма
уже зафиксирована в финансовом snapshot заказа.

## Настройки

```text
REFERRAL_COMMISSION_RATE=0.15
REFERRAL_FRIEND_DISCOUNT_RATE=0.15
REFERRAL_FRIEND_MAX_DISCOUNT=2000
REFERRAL_REFERRER_REWARD_RATE=0.10
REFERRAL_REFERRER_MAX_DISCOUNT=2000
REFERRAL_PARTNER_CUSTOMER_DISCOUNT_RATE=0.05
REFERRAL_PARTNER_CUSTOMER_MAX_DISCOUNT=2000
REFERRAL_PARTNER_PAYOUT_RATE=0.10
REFERRAL_PARTNER_MAX_PAYOUT=2000
REFERRAL_ELIGIBILITY_HMAC_KEY=<32 random bytes encoded as Base64>
REFERRAL_ELIGIBILITY_MARKER_RETENTION=365d
```

Backend проверяет комбинацию ставок при запуске. Конфигурация, способная потратить больше 15%
базовой цены одного заказа, считается ошибочной и останавливает запуск приложения.
HMAC secret также обязателен для запуска, хранится только на backend и должен оставаться стабильным
не меньше максимального срока жизни marker.
