# Repository fleet audit — 2026-08-16

Canonical baseline: `dedtsss/etalon-project-template` v0.2.0.

Goal: move active Bruce projects toward the common GitHub project contract without interfering with open work.

## Status meanings

- `COMPLIANT` — canonical template itself or project already aligned to required baseline.
- `MIGRATION_PR` — isolated baseline PR opened; no product code touched.
- `DEFERRED_ACTIVE` — open PR/recent work makes repository normalization unsafe right now.
- `MIGRATE_ON_TOUCH` — quiet/dormant project; bring to v0.2.0 before the next material development stage.
- `EXEMPT` — archived/superseded/special repository where full active-project baseline is not useful.
- `DEPRECATED` — must not be used as a source/template.

## Fleet

| Repository | Status | Reason / next action |
|---|---|---|
| `etalon-project-template` | COMPLIANT | Canonical GitHub Template Repository v0.2.0. |
| `etalon-repository` | DEPRECATED | README points to canonical template; never use for new projects. |
| `vps-k-setup` | MIGRATION_PR | Draft PR #21 adds `.etalon-version` and `docs/ideas.md` only. |
| `agent-dispatch` | DEFERRED_ACTIVE | Very active; multiple open PRs, including common-rule changes. Add template-creation rule after current common-rule work settles. |
| `ai-agent-bruce` | DEFERRED_ACTIVE | Multiple open documentation/agent-policy PRs; avoid overlapping project-rule edits. |
| `android-memory-bridge` | DEFERRED_ACTIVE | Open PR #1 and nonstandard active default branch. |
| `botogram-android` | DEFERRED_ACTIVE | Open implementation PR #1. |
| `cat-awg-tunnel` | DEFERRED_ACTIVE | Open product PR #1 and CAT UI PR #2. |
| `cat-awg-server` | DEFERRED_ACTIVE | Open product PR #1. |
| `crm-baza` | DEFERRED_ACTIVE | Recent production/UI work and open PR #2; has partial agent/docs baseline already. |
| `darkcat-camera-android` | DEFERRED_ACTIVE | Active PRs #1/#2/#5; `docs/IDEAS.md` and `docs/DECISIONS.md` already added on active product branch. Do not normalize root during current work. |
| `sudrf-research-tool` | DEFERRED_ACTIVE | PR #5 is open/draft with long-running research/runtime work. |
| `work-scripts-hub` | DEFERRED_ACTIVE | Commits on 2026-08-15; custom docs layout (`docs/DECISIONS.md`, specialized backlog files). Normalize later without renaming live documents underneath current work. |
| `battery-compatibility-map` | MIGRATE_ON_TOUCH | Quiet project; audit and align before next material implementation stage. |
| `bruce-agent-metrics` | MIGRATE_ON_TOUCH | Quiet support project; align before next material stage. |
| `bruce-agent-report-normalizer` | MIGRATE_ON_TOUCH | Small benchmark/support project; align when next used. |
| `bruce-memory` | MIGRATE_ON_TOUCH | Infrastructure project; inspect current memory-specific rules before adding baseline. |
| `dispatcher-android-inbox` | MIGRATE_ON_TOUCH | Quiet Android project; align before next material stage. |
| `distance-points-on-the-map` | MIGRATE_ON_TOUCH | Quiet project; align before next material stage. |
| `govorun-online-cleaner` | MIGRATE_ON_TOUCH | Quiet project; align before next material stage. |
| `hermes-canary-smoke` | MIGRATE_ON_TOUCH | Test/support repo; keep baseline lightweight if retained as active. |
| `tribe-companion-android` | MIGRATE_ON_TOUCH | Quiet Android project; align before next material stage. |
| `voice-inbox-bot` | MIGRATE_ON_TOUCH | Quiet bot project; align before next material stage. |
| `tenant-notifier` | EXEMPT | Superseded by CRM Baza; do not spend effort normalizing unless reactivated. |
| `botogram-android-legacy-2026` | EXEMPT | Archived legacy repository. |

## Safety rule used during this audit

Before writing to a repository, check open PRs and recent commits. Never modify an active feature/test branch for baseline work. Prefer a dedicated `chore/etalon-v0.2-baseline` branch and Draft PR. Product code, runtime configuration, deployments and secrets are out of scope for repository-standard migration.

## Required next work

1. Merge/review `vps-k-setup#21` when convenient.
2. Add the new-repository creation invariant to `agent-dispatch/AGENTS.md` after overlapping common-rule PRs settle: new coding project repositories must be created from `dedtsss/etalon-project-template` unless an explicit exception is recorded.
3. For each `DEFERRED_ACTIVE` repo, align only after current product PRs are merged/closed or after a conflict check shows the baseline diff is isolated.
4. For each `MIGRATE_ON_TOUCH` repo, make v0.2 baseline alignment the preflight step before the next material development task rather than creating noise in dormant repositories now.

This report is the fleet-level migration ledger; individual project repos remain authoritative for their own code/status/decisions.
