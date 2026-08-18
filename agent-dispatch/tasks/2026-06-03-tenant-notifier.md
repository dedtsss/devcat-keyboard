# Task: tenant-notifier

Status: blocked
Owner: Codex
Created: 2026-06-03
Updated: 2026-06-03

## Goal

Создать отдельный приватный репозиторий `tenant-notifier` и реализовать MVP веб-утилиты для SMS-уведомлений арендаторов.

## Target repository

- Repository: `dedtsss/tenant-notifier`
- Branch: `main` or task branch
- Expected report: `WORK_REPORT.md`

## Requirements

- Новый отдельный сервис, не встраивать в `tenant-cabinet`.
- Backend: Python 3.12+, FastAPI, SQLAlchemy, SQLite, APScheduler.
- Frontend: простой HTML/Jinja2.
- SMS provider abstraction: `FakeProvider`, `SmsRuProvider`.
- Управление арендаторами, шаблонами, ручной отправкой, отложенной отправкой, журналом.
- REST API для будущей интеграции.
- `.env.example`, README, тесты, Docker по возможности.
- Реальные SMS не отправлять без явного включения провайдера через env.

## Current external check result

На момент проверки через GitHub-коннектор:

- репозиторий `tenant-notifier` не найден;
- в `dedtsss/tenant-cabinet` следов задачи не найдено;
- PR по задаче не найден;
- `WORK_REPORT.md` по задаче не найден.

## Acceptance criteria

- [ ] Репозиторий `dedtsss/tenant-notifier` существует и доступен.
- [ ] Код залит в GitHub.
- [ ] Есть `WORK_REPORT.md`.
- [ ] Есть README с запуском.
- [ ] Есть `.env.example`.
- [ ] FakeProvider работает без внешних ключей.
- [ ] Реальная отправка SMS выключена по умолчанию.
- [ ] Тесты или ручные проверки перечислены в отчёте.

## Required next action for Codex

Если работа уже выполнена локально — залить её через доступный способ в GitHub и обновить статус/отчёт.

Если работа не выполнена — прямо написать это в статус и указать причину.

Запрещено отвечать `готово`, пока результат не виден в GitHub.
