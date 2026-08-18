# Specs

Specs capture context for large or ambiguous tasks. They do not replace GitHub Issues, Pull Requests, `docs/status.md`, `docs/backlog.md` or `docs/decisions.md`.

## When to create a spec

Create `docs/specs/{date-slug}/` when a task has unclear scope, multiple steps, meaningful tradeoffs or enough risk that future agents need a saved plan.

Do not create a spec for small, obvious edits. For those, a GitHub Issue and PR are enough.

## Location

Use one folder per task:

```text
docs/specs/2026-06-18-short-task-name/
```

## Minimal format

- `spec.md` - goal, scope, non-goals, relevant standards and readiness criteria.
- `tasks.md` - implementation steps, checks and expected PR contents.
- `result.md` - final report, completed checks and remaining risks.

## GitHub relationship

The Issue remains the task discussion and source of assignment. The spec stores the working plan when the task needs more structure. The PR links or references the spec and records the reviewable result.
