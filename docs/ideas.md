# Ideas & Engineering Findings

Этот файл хранит идеи, prior art и результаты исследований, которые ещё не являются обязательными решениями.

Статусы: `candidate`, `research`, `accepted`, `implemented`, `rejected`.

## 2026-08-18 — Downloadable/importable GigaAM model pack
Status: candidate

Что найдено:
GigaAM заметно увеличивает размер приложения. Для внутреннего prototype проще bundled model, но для публичного релиза можно вынести модель в отдельную загрузку/import flow.

Чем полезно:
- меньше базовый APK;
- можно обновлять модель отдельно;
- пользователи без voice input не скачивают сотни мегабайт.

Следующий шаг:
Вернуться после стабильного bundled prototype и измерить реальный APK/install footprint.

## 2026-08-18 — Local LLM for offline cleanup
Status: research

Что найдено:
Ранее обсуждались небольшие Gemma/Qwen-class модели для локальной очистки диктовки. Основные риски — Android inference runtime, JNI/ABI, размер модели, RAM, latency и качество русского текста.

Чем полезно:
Может дать полностью офлайн цепочку `ASR -> intelligent cleanup` без сетевого companion.

Следующий шаг:
После voice MVP заново исследовать актуальные на тот момент small-model/runtime варианты на реальном целевом телефоне. Не фиксировать старый список моделей как обязательный.

## 2026-08-18 — Online cleanup provider abstraction
Status: candidate

Что найдено:
В `dedtsss/govorun-online-cleaner` уже есть GigaChat client и prompt modes. В CatBoard имеет смысл отделить общий `TextPostProcessor`/`OnlineCleaner` интерфейс от конкретного провайдера.

Чем полезно:
Позволит менять GigaChat/другой API без переписывания voice controller и UI.

Ограничение:
Не превращать MVP в универсальный AI-gateway. Сначала один рабочий provider.

## 2026-08-18 — Optional cloud ASR
Status: candidate

Что найдено:
Локальный GigaAM уже достаточно хорошо распознаёт русский, поэтому главная текущая ценность сети — cleanup. Однако позже можно сравнить облачное ASR как явный пользовательский режим для сложного шума/языков.

Ограничение:
Аудио нельзя отправлять в сеть скрыто. Cloud ASR должен быть отдельной явно включённой функцией с понятным privacy disclosure.

## 2026-08-18 — Personal local correction learning
Status: candidate

Source: `dedtsss/govorun-online-cleaner` Issue #10.

Идея:
Локально сохранять пары `raw ASR -> final corrected text` и предлагать пользователю словарные правила для частых имён, брендов, аббревиатур и ошибок.

Ограничение:
- не сохранять аудио без отдельной причины;
- не отправлять историю обучения на сервер;
- не принимать новые правила автоматически.

## 2026-08-18 — Clipboard UX improvements
Status: candidate

Идеи после voice MVP:
- быстрый поиск/filter;
- удобнее pinned/favorites;
- snippets/templates;
- более быстрые item actions;
- возможные группы/категории только если появится реальная потребность.

Основа остаётся HeliBoard clipboard; отдельный новый storage stack не нужен без доказанной причины.

## 2026-08-18 — Public distribution and donations
Status: candidate

Идея:
Если CatBoard станет действительно удобным продуктом, публиковать open-source сборки и принимать добровольные пожертвования на развитие/инфраструктуру.

Не является требованием MVP и не должно влиять на раннюю архитектуру сильнее, чем лицензии, reproducible build и privacy.

## 2026-08-18 — Upstream generic fixes where practical
Status: candidate

Если в ходе форка появятся исправления, полезные обычному HeliBoard и не завязанные на CatBoard-specific voice stack, рассматривать небольшой upstream PR вместо вечного локального патча.

Польза: меньше divergence и проще обновлять базу.
