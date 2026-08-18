# etalon-project-template

Канонический шаблон и стандарт для новых и существующих Bruce-проектов.

Этот репозиторий — единственный эталонный template repository. Новые проекты должны создаваться через GitHub `Use this template`, а не из пустого репозитория вручную.

GitHub является источником истины: код, Issues, Pull Requests, Actions и проектные документы.

## Обязательный контур

```text
.etalon-version                 версия эталонного каркаса
README.md                       что это за проект и как его запустить
AGENTS.md                       короткие правила для AI-агентов
CODEX.md                        compatibility pointer
.github/                        Issue/PR templates и CI
docs/status.md                  текущее состояние и следующий шаг
docs/backlog.md                 обзорная очередь при необходимости
docs/decisions.md               принятые решения
docs/ideas.md                   идеи, prior art, найденные инженерные приёмы
docs/standards/                 общие стандарты
docs/specs/                     спецификации крупных задач
scripts/check.*                 единая проверка проекта
scripts/doctor.*                проверка обязательной структуры
```

## Главный стандарт проекта

`docs/standards/project-lifecycle.md`

Короткая лестница:

```text
IDEA
→ PRIOR ART
→ MVP + NON-GOALS + RISKS
→ CREATE FROM TEMPLATE
→ STATUS / AGENTS / CHECKS
→ ISSUE or SPEC
→ FOCUSED BRANCH / PR
→ TEST / REVIEW
→ MERGE / DEPLOY when authorized
→ UPDATE STATUS / DECISIONS / IDEAS
```

Перед крупной новой функцией или архитектурным изменением сначала проверяются `docs/decisions.md` и `docs/ideas.md`, затем релевантная профильная документация. Это предотвращает повторное изобретение уже принятых или исследованных решений.

## Ideas, Decisions, Issues

- `docs/ideas.md` — что найдено или потенциально полезно; не обязательство реализовывать.
- `docs/decisions.md` — что уже принято и почему.
- GitHub Issue/spec — что реально делаем и по каким acceptance criteria.

Принятая идея должна получить запись в decisions и, если нужна реализация, Issue/spec.

## Новый проект

1. `Use this template`.
2. Заполнить README и `docs/status.md` фактическими данными.
3. Адаптировать `AGENTS.md` и команды build/test/check под стек проекта.
4. Зафиксировать MVP, non-goals, риски и существенные решения.
5. Начинать существенную разработку через Issue/spec и focused PR.

## Старый проект

Приводить постепенно, без переписывания продукта. Минимум стандартизации определён в `docs/standards/project-lifecycle.md`.

## Контроль дрейфа

`.etalon-version` показывает версию принятого каркаса. `scripts/doctor.sh` и `scripts/doctor.ps1` проверяют наличие обязательных файлов и ссылок standards index.

## Версия

v0.2.0 — канонический project lifecycle, ideas/decisions split и контроль структуры.
