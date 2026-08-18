# Agent Harness

Agent harness is the set of project files, scripts and checks that tells an AI agent when it is wrong.

The goal is to reduce repeated mistakes and manual correction.

## Layers

### 1. Instruction layer

Files:

- `AGENTS.md`
- `CODEX.md`

Use for:

- rules;
- commands;
- project structure;
- constraints;
- known traps.

### 2. Script layer

Files:

- `scripts/check.sh`
- `scripts/build.sh`
- `scripts/test.sh`
- `scripts/doctor.sh`
- PowerShell equivalents when needed.

Use for:

- repeatable local checks;
- environment checks;
- build/test commands;
- collecting logs.

### 3. CI layer

Files:

- `.github/workflows/check.yml`

Use for:

- automatic verification on push and pull request;
- preventing broken changes from merging;
- producing logs that agents can read.

### 4. Documentation layer

Files:

- `docs/status.md`
- `docs/backlog.md`
- `docs/decisions.md`

Use for:

- current state;
- next tasks;
- accepted decisions;
- context for future agents.

## Rule for repeated mistakes

When an agent makes a mistake:

1. Fix the immediate issue.
2. Decide why the mistake was possible.
3. Add a durable guard:
   - instruction;
   - script;
   - test;
   - CI check;
   - decision note.
4. Mention the harness update in the final report.

## Examples

| Mistake | Harness fix |
|---|---|
| Wrong build command | Add command to `AGENTS.md` and `scripts/build.sh` |
| Agent forgets tests | Add mandatory `scripts/check.sh` and CI |
| Agent edits unrelated files | Add PR checklist and surgical-change rule |
| Agent misses current project state | Require reading `docs/status.md` |
| Agent repeats bad architecture decision | Add note to `docs/decisions.md` |
