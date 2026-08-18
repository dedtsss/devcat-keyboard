# Template agent rules

This file is the canonical agent rule source for repositories created from this template. Keep it short and replace the project profile and commands when creating a real project.

- GitHub and repository files are authoritative. External memory and chat context are optional aids, never the source of truth.
- Read the task/Issue, this file, and only relevant project files. Use `docs/standards/index.yml` only when a standard is relevant to the task.
- For a new project or project-structure task, follow `docs/standards/project-lifecycle.md`.
- Before designing a new project, substantial feature, integration, architecture change, rewrite, non-trivial workaround, or technology choice, apply `docs/standards/engineering-principles.md`: reuse before build, prefer the simplest sufficient solution, keep the stack proportional to the task, and add complexity only for a demonstrated requirement.
- Before a major feature, architecture change, migration, or non-trivial workaround, review relevant entries in `docs/decisions.md` and `docs/ideas.md` plus the relevant project documentation. Do not reinvent an already settled decision without new evidence.
- Before inventing a non-trivial workaround, check prior art: official docs, upstream code, Issues, PRs, Discussions, changelog and relevant forks/projects.
- Make the smallest correct change on a focused branch; preserve unrelated work and avoid speculative features, broad refactors, or new frameworks.
- Do not expose or commit secrets, credentials, private data, or production configuration.
- Do not merge, deploy, change access/visibility, or make destructive changes without explicit authorization.
- Run the smallest relevant check and the repository's unified check when appropriate. Open a PR for reviewable changes and report changed files, verification, unverified boundaries and remaining risks.
- After a substantial stage, persist durable state in GitHub: update `docs/status.md`, `docs/decisions.md`, `docs/ideas.md`, Issue/PR or spec only where the facts changed. Important project knowledge must not exist only in chat.

`CODEX.md` is a compatibility pointer; task templates must contain task-specific context only.
