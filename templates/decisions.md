# Decisions

## 2026-05-27 — Use GitHub as the project state center

Decision: Every coding project should treat GitHub as the source of state.

Reason:

- Code, issues, pull requests, checks and history stay in one place.
- ChatGPT, Codex, Claude Code and other agents can refer to repository state.
- Manual copying of screenshots, logs and errors should be reduced.

## 2026-05-27 — Keep agent instructions short and practical

Decision: Do not use long role-playing prompts in repository agent files.

Reason:

- Agents need commands, constraints and checks, not abstract persona text.
- Long instructions become stale and are ignored.
- Repeated mistakes should become rules, scripts or tests.

## 2026-05-27 — Use harness engineering

Decision: Every repeatable agent mistake should become a repository improvement.

Possible improvements:

- `AGENTS.md` / `CODEX.md` rule;
- script in `scripts/`;
- automated test;
- GitHub Actions check;
- note in `docs/decisions.md`;
- current-state update in `docs/status.md`.

## 2026-05-27 — Public template must contain no private data

Decision: The template may be public if it contains no secrets or internal project data.

Reason:

- Public template is easier for tools and agents to access.
- Private repositories can cause permission friction.
- Real projects can still be private.
