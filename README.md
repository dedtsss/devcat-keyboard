# CatBoard

**CatBoard** — рабочее название open-source клавиатуры для Android от DevCat.

Цель проекта: взять зрелую основу HeliBoard и сделать удобную повседневную клавиатуру с нативным голосовым вводом, хорошим буфером обмена и понятной privacy-моделью.

Repository: `dedtsss/devcat-keyboard`

## Что хотим получить

- обычную полноценную Android-клавиатуру на базе HeliBoard;
- встроенный офлайн-голосовой ввод для русского языка через GigaAM v3 + sherpa-onnx + Silero VAD;
- постоянную кнопку микрофона прямо в toolbar клавиатуры;
- без плавающего пузыря/Accessibility-overlay для основного голосового сценария;
- emoji, темы, раскладки и текущий clipboard-фундамент HeliBoard сохранить и развивать;
- опциональную онлайн-очистку уже распознанного текста: пунктуация, повторы, слова-паразиты, устная «каша» -> нормальный письменный текст;
- приватность по умолчанию: обычный набор и локальная диктовка не должны требовать сети.

## Базовый поток

```text
Микрофон
  -> локальная запись/VAD
  -> GigaAM v3 (offline ASR)
  -> словарь/локальная постобработка
  -> [опционально] online text cleanup
  -> InputConnection
  -> активное поле Android
```

Подробности: [`docs/architecture.md`](docs/architecture.md), [`docs/voice.md`](docs/voice.md), [`docs/privacy.md`](docs/privacy.md).

## Текущий статус

В репозиторий импортирован чистый исходный baseline HeliBoard 4.1. Исходники
приложения и Gradle-конфигурация соответствуют upstream tag `v4.1`, commit
`9f5bb635c2e8609dcd95dc7506c0c58fba82a52c`. CatBoard-specific voice/ASR-кода
в baseline нет.

Актуальный статус и следующий шаг: [`docs/status.md`](docs/status.md).
Точная граница импорта и лицензии: [`docs/upstream/heliboard.md`](docs/upstream/heliboard.md).

## MVP

Первый полезный build должен:

1. Собираться как HeliBoard-based Android IME.
2. Сохранять обычный набор, темы, emoji и clipboard HeliBoard.
3. Использовать существующую toolbar-кнопку микрофона как внутренний voice action.
4. Записывать речь внутри клавиатуры без переключения на внешний voice IME.
5. Распознавать русский локально через GigaAM.
6. Вставлять результат напрямую в активное поле через `InputConnection`.
7. Работать в авиарежиме для локальной диктовки.
8. Не показывать плавающую «птичку» Говоруна.

После стабильного local voice path добавляется опциональная online cleanup стадия.

## Не входит в первый MVP

- собственный swipe/glide typing;
- переписывание HeliBoard с нуля;
- новый clipboard с нуля;
- обязательная локальная LLM;
- облачная отправка всех нажатий или окружающего текста;
- автоматический публичный релиз/публикация без отдельного решения.

## Источники и reuse

Основные инженерные источники:

- `HeliBorg/HeliBoard` — основа клавиатуры;
- оригинальный Govorun / GigaAM — офлайн ASR-подход;
- `dedtsss/govorun-online-cleaner` — наш предыдущий эксперимент с GigaAM и online cleanup, из которого можно переиспользовать движковые части без старого overlay/dialog UX.

Перед переносом исходников и сторонних компонентов обязательно сохранять их лицензии, attribution и совместимость лицензий.

## Документы проекта

- [`docs/status.md`](docs/status.md) — текущее состояние;
- [`docs/decisions.md`](docs/decisions.md) — принятые решения;
- [`docs/ideas.md`](docs/ideas.md) — идеи/prior art, ещё не обязательства;
- [`docs/backlog.md`](docs/backlog.md) — обзорная очередь;
- [`docs/roadmap.md`](docs/roadmap.md) — этапы развития;
- [`docs/architecture.md`](docs/architecture.md) — архитектура;
- [`docs/voice.md`](docs/voice.md) — голосовой тракт и UX;
- [`docs/privacy.md`](docs/privacy.md) — privacy/network boundary;
- [`docs/clipboard.md`](docs/clipboard.md) — направление clipboard;
- [`AGENTS.md`](AGENTS.md) — правила для Codex/AI-агентов.

## Работа с проектом

GitHub — источник истины. Существенная разработка идёт через Issue/spec -> focused branch -> PR -> проверки -> review.

Единая проверка:

```bash
./scripts/check.sh
```

Команда выполняет быстрый source/provenance preflight без Android SDK и Gradle build.
Authoritative unit tests и debug build запускает job `check` в GitHub Actions через
закреплённый Gradle wrapper. Требования к чистому checkout описаны в [`SETUP.md`](SETUP.md).

## Название

`CatBoard` — рабочее продуктовое имя. Имя репозитория остаётся `devcat-keyboard`; окончательный брендинг можно изменить позже без архитектурной миграции.
