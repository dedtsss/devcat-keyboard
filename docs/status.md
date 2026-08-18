# Project Status

Version: v0.2.0  
Date: 2026-08-16  
Repository: `dedtsss/etalon-project-template`

## Current state

Canonical GitHub template and project standard for new Bruce projects and for incremental standardization of existing repositories.

GitHub Template Repository mode is enabled (`is_template=true`).

## Purpose

Provide one repeatable project structure where:
- GitHub is the source of truth;
- AI agents have short operating rules;
- important ideas and accepted decisions survive chat/context changes;
- Issues/specs define work;
- Pull Requests and Actions provide review/evidence;
- scripts provide one-command checks;
- old projects can be brought to the same baseline incrementally.

## Required baseline

- `.etalon-version`
- `README.md`
- `AGENTS.md`
- `CODEX.md`
- `docs/status.md`
- `docs/backlog.md`
- `docs/decisions.md`
- `docs/ideas.md`
- `docs/standards/`
- `docs/specs/`
- `scripts/check.*`
- `scripts/doctor.*`
- `.github/`

## Canonical lifecycle

See `docs/standards/project-lifecycle.md`.

Core flow:

`IDEA -> PRIOR ART -> MVP/NON-GOALS/RISKS -> TEMPLATE -> ISSUE/SPEC -> PR -> TEST/REVIEW -> DURABLE HANDOFF`

## Known state of existing repositories

The standard has historically been applied inconsistently. Some repositories have strong project-specific documentation and/or `AGENTS.md`; others were created without the template baseline. Existing products must be audited and standardized incrementally rather than rewritten.

## Memory

External memory is optional. GitHub and repository documents remain authoritative. Important state must not exist only in chat or an external memory service.

## Next steps

1. Keep this repository as the single canonical template.
2. Mark the older `dedtsss/etalon-repository` as a deprecated pointer.
3. Audit active repositories against v0.2.0 and migrate them in focused non-product-changing PRs/tasks.
4. Future repositories: create via `Use this template` by default.
