# CatBoard agent rules

Project: `dedtsss/devcat-keyboard`  
Product working name: **CatBoard**  
Platform: Android IME / Kotlin + Java + native dependencies inherited from HeliBoard/Govorun.

GitHub and this repository are authoritative. Chat and external memory are aids only.

Before substantial work:

1. Read the current task/Issue and this file.
2. Read only the specific entries/files needed for the current acceptance criterion or failure. Do not routinely replay all of `docs/decisions.md`, `docs/ideas.md`, `docs/status.md`, or every product document when those facts are unchanged.
3. For a new architecture/technology choice, consult the relevant accepted decision/standard and proportional upstream/prior-art evidence before inventing a workaround.
4. On Rework/recovery, start from current Git/PR/head and the concrete failure; do not reconstruct stable project context without a new reason.

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

`./scripts/check.sh` is the lightweight host-safe preflight. It must remain runnable on the worker host without installing Android SDK/NDK/emulator or performing the full Android Gradle build.

The authoritative Android build/test gate belongs in GitHub Actions (or another explicitly approved cloud runner) and may invoke dedicated Gradle-backed scripts such as `scripts/test.sh` / `scripts/build.sh`. A missing local Android toolchain is not a reason to move the full build onto CatVPS.

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
