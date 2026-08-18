# Spec: Agent OS Adaptation Example

## Goal

Add a lightweight standards and specs layer inspired by Agent OS without installing Agent OS as a separate system.

## Scope

- Add `docs/standards/index.yml`.
- Add short standards in `docs/standards/`.
- Add `docs/specs/README.md`.
- Add this example spec folder.
- Update `AGENTS.md`, `CODEX.md`, `docs/status.md` and `docs/decisions.md`.

## Non-goals

- Do not add `agent-os/`.
- Do not add `.claude/commands/`.
- Do not replace `docs/status.md`, `docs/backlog.md` or `docs/decisions.md`.
- Do not add an external Agent OS dependency.

## Relevant Standards

- `docs/standards/github-workflow.md`
- `docs/standards/codex-runtime.md`
- `docs/standards/pr-review.md`
- `docs/standards/testing.md`

## Readiness Criteria

- The new layer is documented as a supplement to the existing template.
- Standards are short and topic-specific.
- Specs are required only for large or ambiguous tasks.
- Existing project state documents keep their current roles.
- Verification has been run or explicitly reported as unavailable.
