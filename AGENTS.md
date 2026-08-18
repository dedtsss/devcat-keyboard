# CatBoard agent rules

Project: `dedtsss/devcat-keyboard`  
Product working name: **CatBoard**  
Platform: Android IME / Kotlin + Java + native dependencies inherited from HeliBoard/Govorun.

GitHub and this repository are authoritative. Chat and external memory are aids only.

Before substantial work:

1. Read the task/Issue and this file.
2. Read relevant entries in `docs/decisions.md` and `docs/ideas.md`.
3. Read `docs/status.md` and the relevant product document (`architecture`, `voice`, `privacy`, `clipboard`).
4. For architecture/technology choices apply `docs/standards/engineering-principles.md` and check upstream/prior art before inventing a workaround.

## Product boundaries

- HeliBoard is the selected keyboard base. Do not replace it with FUTO/another keyboard without an explicit superseding decision.
- Preserve normal HeliBoard typing, layouts, themes, emoji and clipboard behavior unless a task explicitly changes them.
- Swipe/glide typing is not a project priority.
- The primary voice path is internal to CatBoard: toolbar mic -> recorder/VAD -> GigaAM -> post-processing -> `InputConnection`.
- Do not reintroduce the Govorun floating bubble or Accessibility overlay for the normal keyboard voice path.
- Offline voice recognition must remain usable without network.
- Ordinary keystrokes, clipboard history, surrounding editor text and learned personal data must not be sent to network implicitly.
- Preferred online-cleanup architecture keeps the keyboard package without `INTERNET` permission and uses a separate explicitly invoked companion/network package. Reconsider only with a recorded decision and evidence.
- On online-cleanup failure, preserve/fall back to the local transcript; never silently lose dictated text.
- Reuse the existing HeliBoard clipboard foundation before adding custom storage/frameworks.
- A local LLM is optional research, not a blocker for the first working keyboard.

## Upstream and licensing

- Preserve upstream copyright/license headers and required attribution.
- Before copying code from HeliBoard, Govorun, sherpa-onnx, GigaAM or another project, verify the exact source/license boundary.
- Keep upstream-derived changes focused so future rebases/updates remain practical.

## Workflow

- Work from a GitHub Issue or spec for substantial changes.
- Use a focused branch and reviewable PR.
- Do not merge, publish releases, deploy, change repository visibility, or make destructive changes without explicit authorization.
- Do not commit secrets/API keys/tokens/model credentials.
- Avoid broad rewrites and speculative features.

## Verification

Current unified check:

```bash
./scripts/check.sh
```

After HeliBoard source import, adapt the repository scripts to real Gradle build/test/lint commands and keep `scripts/check.sh` as the single entry point.

For voice changes, report at minimum:

- build result;
- device/Android version tested;
- mic start/stop behavior;
- focus/IME hide/show behavior;
- airplane-mode local dictation result;
- whether transcript was preserved on errors;
- any network traffic/permission change.

After a substantial stage, update only the durable facts that changed in `docs/status.md`, `docs/decisions.md`, `docs/ideas.md`, relevant spec and Issue/PR.

`CODEX.md` is only a compatibility pointer to this file.
