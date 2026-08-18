# Result: Agent OS Adaptation Example

## Template

Use this structure for future spec-backed tasks: changed files, verification and remaining risks.

## Changed

- Standards layer: added `docs/standards/index.yml` and short topic standards.
- Specs layer: added `docs/specs/README.md` and this example spec folder.
- Existing docs updated: `AGENTS.md`, `CODEX.md`, `docs/status.md` and `docs/decisions.md`.

## Verification

- Command: `./scripts/check.ps1`
- Result: passed.
- Command: `C:\Program Files\Git\bin\bash.exe ./scripts/check.sh`
- Result: passed.
- Command: `git diff --check`
- Result: passed; Git reported line-ending normalization warnings for Markdown files.
- Command: forbidden-folder check for `agent-os/` and `.claude/commands/`
- Result: passed.

## Remaining Risks

- The standards index may need adjustment after the next real task.
- Specs can become stale if agents create them for small changes.
- Repository-specific checks still depend on each project adapting `scripts/check`.
