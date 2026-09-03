---
title: Loco Place Local Stress Test Runbook
type: operations-runbook
status: active
scope: platform
updated: 2026-09-04
---

# Локальный stress-тест Loco Place

Этот runbook нужен для повторной проверки производительности после изменений в горячих API, работе с БД, медиа или загрузке frontend. Контур запускается только локально и не использует VPS, staging, production или GitHub Actions runner.

Полная техническая справка находится в [`performance/README.md`](../../performance/README.md), а последний проверенный baseline — в [`performance/baseline.md`](../../performance/baseline.md).

## Что потребуется

- запущенный Docker Desktop с доступным Linux Engine;
- JDK 25 в `JAVA_HOME`;
- PowerShell 7+;
- свободные локальные порты `15173`, `15432` и `18080`;
- запуск из корня репозитория.

Проверьте Docker перед началом:

```powershell
docker version
```

Команда должна показать и `Client`, и `Server`. Performance-контур использует отдельный Compose project `loco-perf`, отдельную БД `loco_performance` и отдельный Docker volume.

## Обычный запуск

Сначала пересоздайте контур и выполните короткую проверку:

```powershell
cd E:\IdeaProjects\cleany
$env:PERF_ANCHOR_DATE = '2026-09-04'
.\performance\scripts\run-local.ps1 -Reset -Scenario smoke -Validation
```

Для сравнения разных изменений используйте одну и ту же anchor date. Затем на уже прогретом backend и том же dataset запустите stress:

```powershell
.\performance\scripts\run-local.ps1 -ReuseStack -SkipSeed -Scenario stress
```

Обычный stress-профиль постепенно поднимает нагрузку до 100 VU и занимает примерно 3 минуты 15 секунд. По умолчанию он обращается к Compose-сервису `frontend`: это Caddy, который отдаёт Vite SPA и проксирует API напрямую в backend. Такой маршрут воспроизводит production topology. Прямой `API_BASE_URL=http://backend:8080` используйте только как явно подписанную компонентную диагностику.

Если нужна одна команда с полной пересборкой, пересозданием данных и stress-тестом:

```powershell
.\performance\scripts\run-local.ps1 -Reset -Scenario stress
```

## Результат

k6 печатает итог в терминал и записывает JSON в:

```text
performance/results/stress-<timestamp>.json
```

Смотрите в первую очередь:

- `http_reqs` / rate — фактический RPS;
- `http_req_duration` p50, p95 и p99;
- `http_req_failed` и `checks`;
- максимальную задержку;
- объём принятых и отправленных данных.

При поиске причины дополнительно проверяйте локальные метрики:

```powershell
Invoke-RestMethod http://127.0.0.1:18080/actuator/metrics/hikaricp.connections.active
Invoke-RestMethod http://127.0.0.1:18080/actuator/metrics/hikaricp.connections.pending
Invoke-RestMethod http://127.0.0.1:18080/actuator/metrics/jvm.memory.used
Invoke-RestMethod http://127.0.0.1:18080/actuator/metrics/hibernate.queries.executions
```

Доступные названия метрик можно получить через:

```powershell
Invoke-RestMethod http://127.0.0.1:18080/actuator/metrics
```

## Сравнение до и после

Для честного сравнения сохраняйте одинаковыми:

- Git commit исходного состояния;
- `PERF_SEED` — по умолчанию `42`;
- `PERF_SCALE` — по умолчанию `1`;
- `PERF_ANCHOR_DATE`;
- Docker CPU/RAM settings;
- сценарий и его параметры;
- состояние scheduler jobs;
- отсутствие тяжёлых фоновых задач на компьютере.

Сделайте один warm-up, затем один измеряемый запуск до изменения и один после. Дополнительный повтор нужен только при заметно нестабильных результатах. В [`performance/baseline.md`](../../performance/baseline.md) записывайте вывод и агрегированные цифры; полные k6/JFR-файлы не коммитьте.

Не считайте увеличение Hikari pool исправлением, если запрос удерживает одно соединение и пытается получить второе через вложенный `REQUIRES_NEW`. Не добавляйте Redis, CDN, новый storage, JVM flags или индексы без измеренной причины и повторного before/after.

## JFR при необходимости

JFR полезен, если stress показывает рост CPU, allocations, GC, locks или непонятную деградацию. В первом терминале начните запись чуть длиннее stress-сценария:

```powershell
.\performance\scripts\capture-jfr.ps1 -Name stress -DurationSeconds 210
```

Сразу после этого во втором терминале запустите:

```powershell
.\performance\scripts\run-local.ps1 -ReuseStack -SkipSeed -Scenario stress
```

Запись сохраняется в `performance/results/` и игнорируется Git.

## Завершение и очистка

Остановить контейнеры, сохранив synthetic database для следующего запуска:

```powershell
docker compose -p loco-perf -f performance/compose.perf.yaml down
```

Остановить контур и освободить место, удалив только его синтетический volume:

```powershell
docker compose -p loco-perf -f performance/compose.perf.yaml down --volumes
```

Записанный в документации baseline и локальные файлы `performance/results/` при этом сохраняются.

## Частые проблемы

### Docker environment не найден

Убедитесь, что Docker Desktop запущен и `docker version` показывает Server. На Windows должен использоваться Linux context Docker Desktop.

### Backend не стал healthy

Посмотрите последние логи:

```powershell
docker compose -p loco-perf -f performance/compose.perf.yaml logs --tail=200 backend
```

### Remote target rejected

Это штатная защита. Скрипты принимают только localhost и внутренние имена `loco-perf`. Не ослабляйте allowlist и не подставляйте адрес VPS или домен.

### Через Caddy появляются массовые 502

Сначала проверьте `docker compose ... logs frontend backend` и повторите короткий `smoke -Validation`. Не обходите проблему переключением baseline на прямой backend: production-shaped сценарий должен проходить через Caddy. Direct-backend запуск допустим только для локализации причины. Не добавляйте второй proxy/web-server и не увеличивайте системные лимиты без измеренного подтверждения.
