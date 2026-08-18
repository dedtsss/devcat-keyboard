---
name: Migrate repository to standard
about: Bring an existing project to etalon-repository standard
title: "[Migration] Bring repository to etalon standard"
labels: migration, agent-harness
assignees: ""
---

## Goal

Bring this repository to the standard agent-ready structure.

## Required changes

- [ ] Add/update `README.md`.
- [ ] Add/update `AGENTS.md`.
- [ ] Add/update `CODEX.md`.
- [ ] Add/update `docs/status.md`.
- [ ] Add/update `docs/backlog.md`.
- [ ] Add/update `docs/decisions.md`.
- [ ] Add/update `docs/agent-harness.md`.
- [ ] Add/update `scripts/check`.
- [ ] Add/update GitHub Actions.
- [ ] Add/update issue templates.
- [ ] Add/update pull request template.

## Rules

- Do not rewrite the project.
- Do not break existing code.
- Preserve existing instructions.
- Add the standard around existing structure.
- First document current state, then make changes.

## Verification

- [ ] Repository has required files.
- [ ] `scripts/check` runs or clearly reports missing project-specific checks.
- [ ] GitHub Actions runs.
