# Project Status

Version: v0.1.0  
Date: 2026-05-27  
Repository: etalon-repository

## Current state

This repository is the base template for future projects and for upgrading existing projects to a standard agent-ready structure.

## Purpose

Create a repeatable project structure where:

- GitHub is the center of project state.
- AI agents have clear operating rules.
- Issues and pull requests hold task history.
- GitHub Actions verifies changes.
- Scripts provide one-command checks.
- Documentation records status, backlog and decisions.

## Current files

- `README.md`
- `AGENTS.md`
- `CODEX.md`
- `docs/status.md`
- `docs/backlog.md`
- `docs/decisions.md`
- `docs/agent-harness.md`
- `scripts/`
- `.github/`

## Known limitations

- This is a generic template. Each real project must adapt scripts and checks to its actual stack.
- Template repository mode must be enabled manually in GitHub settings.
- The repository should not contain secrets or private project data.

## Next step

Enable GitHub setting:

```text
Settings -> General -> Template repository
```
