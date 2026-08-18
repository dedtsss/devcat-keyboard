# Decisions

Этот файл хранит только уже принятые решения. Идеи и prior art живут в `docs/ideas.md`.

Если решение меняется, старую запись не удалять: отметить/описать как superseded и добавить новое решение с причиной.

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

## 2026-06-10 — AutoMem as required external memory layer

Status: `superseded` by the 2026-08-16 decision below.

Historical decision: every project based on this template was required to use `AutoMem_Bruce` for recall and post-task memory updates.

Reason for superseding: external memory availability and tooling change over time; requiring it made the project contract depend on infrastructure outside the repository and conflicted with the current `AGENTS.md` rule that GitHub is authoritative.

## 2026-06-18 — Add lightweight standards and specs layer

Decision: Do not adopt Agent OS as a separate system. Instead, keep a lightweight standards and specs layer inside `docs/`.

Reason:
- The existing `etalon-project-template` structure remains the source of truth.
- Agents can choose relevant task context from `docs/standards/index.yml` before work.
- Large or ambiguous changes can keep a saved spec/task/result in `docs/specs/`.
- `docs/status.md`, `docs/backlog.md` and `docs/decisions.md` keep distinct roles.

## 2026-08-16 — One canonical project template and standard

Decision: `dedtsss/etalon-project-template` is the single canonical template and project-standard repository.

New repositories must be created through GitHub `Use this template` unless there is a concrete technical reason not to. Existing repositories are upgraded incrementally without breaking product code.

Reason:
- two competing “etalon” repositories create ambiguity;
- recent repositories were not consistently receiving the required structure;
- a single canonical template makes project startup repeatable and auditable.

Implementation:
- `.etalon-version`;
- `docs/standards/project-lifecycle.md`;
- `scripts/doctor.*` structural checks.

## 2026-08-16 — Split ideas from accepted decisions

Decision: every standardized project keeps both `docs/ideas.md` and `docs/decisions.md`.

`ideas.md` stores candidate ideas, prior art, research, rejected approaches and implemented references. `decisions.md` stores only accepted/superseded project decisions.

Reason: an interesting idea from another project must not silently become an architectural requirement, while useful research must not disappear in chat history.

## 2026-08-16 — External memory is optional; GitHub is authoritative

Decision: external memory systems may be used when useful, but they are not mandatory project infrastructure.

Important decisions, status and implementation evidence must be persisted in the repository, Issue, PR or Actions. Chat/memory cannot be the only durable copy.

Reason: this keeps projects portable across ChatGPT, Codex, Manus and future agents and removes dependence on one memory backend.

## 2026-08-16 — Migrate existing repositories without disrupting active work

Decision: repository-standard migration is subordinate to product-work safety.

Before baseline changes, check open PRs and recent commits. Repositories with active or overlapping work are marked `DEFERRED_ACTIVE`; do not edit their active feature/test branches for standardization. Quiet repositories may be aligned in a dedicated `chore/etalon-v0.2-baseline` branch and Draft PR. Dormant projects may use `MIGRATE_ON_TOUCH`, meaning baseline alignment becomes the preflight step before the next material development stage.

Archived or explicitly superseded repositories may be `EXEMPT` rather than receiving meaningless maintenance work.

Reason:
- mass normalization must not create conflicts with current agents or long-running branches;
- the standard exists to reduce project risk, not to create repository churn;
- old projects can converge safely and incrementally while all new projects start correctly from the template.

Fleet-level evidence: `docs/audits/2026-08-16-repository-fleet.md`.

## 2026-08-16 — Canonical engineering principles govern solution choice

Decision: substantial project design and implementation should follow `docs/standards/engineering-principles.md`.

Core defaults:
- reuse before build;
- prefer the simplest sufficient solution;
- go from configuration/component/adapter to custom infrastructure only when a demonstrated requirement forces it;
- keep engineering effort proportional to product value, risk and expected lifetime;
- prefer MVP, small reversible changes and evidence before rewrite/optimization;
- treat deletion of unnecessary complexity as a successful engineering outcome.

Reason: prior-art checks and simplicity rules previously existed in several places but were fragmented. A dedicated standard makes the decision model explicit and reusable across new projects, existing projects and agents.

Implementation:
- `docs/standards/engineering-principles.md`;
- indexed in `docs/standards/index.yml`;
- referenced from template `AGENTS.md` before substantial design/technology choices;
- new-project routing in `dedtsss/agent-dispatch/AGENTS.md` points to this standard.
