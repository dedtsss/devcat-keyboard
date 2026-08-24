# Result — integrated voice MVP

Status: PARTIAL — Stage F controller CI/artifact run pending; device/provider evidence deferred

## Historical Stage E checkpoint

- Mission: Issue #3, implementation PR #4 (open, Draft, unmerged).
- Branch/head: `codex/catboard-alpha-mission` at
  `b0f4afc40daad4291734331f9d5645a4602e719b`.
- HeliBoard revision: `50d13c1bd6c3f4ee6d69644b3d422145cb928503`.
- Historical exact-head CI: push `32743738093` and PR `32743743021`, both green.
- Historical artifact: debug keyboard APK id `9526531713`, `310,357,002` bytes, SHA-256
  `143a5a462af90765b7a9cdac67bc59c271722efd51642144281ad76c28760ed5`.
  This is keyboard-only evidence and is not the full two-APK Stage F alpha.
- Architecture: CatBoard IME performs recorder/Silero VAD/GigaAM v3/sherpa offline ASR and
  commits the local transcript through `InputConnection`; optional cleanup is explicit Binder
  IPC to `cleaner-companion`, with local fallback on every failure.
- Boundary: keyboard has `RECORD_AUDIO` and no INTERNET; companion alone has INTERNET and the
  signature-protected `CLEAN_TRANSCRIPT` service. Full optional-cleanup alpha uses two APKs,
  installed companion-first then keyboard.
- Limits: minSdk 21; keyboard ABIs are armeabi-v7a, arm64-v8a, x86 and x86_64; bundled model/
  runtime footprint is reflected by the approximately 310 MB debug APK. Runtime performance,
  memory, latency and public distribution suitability are not yet measured.

## Stage F controller evidence

The controller push and exact-head GitHub Actions run for the repaired workflow are pending. The
workflow is configured to publish one artifact containing both installable debug APKs:

- `app/build/outputs/apk/debug/CatBoard_*-debug.apk`;
- `cleaner-companion/build/outputs/apk/debug/cleaner-companion-debug.apk`.

Final exact-head Stage F CI/artifact success is not claimed until that run completes.

## Terminal evidence boundary

Physical device installation, mic start/stop, focus/IME hide/show, airplane-mode local dictation,
installed IPC smoke and live provider smoke remain unverified. The approved provider credential
is unavailable. These are deferred external evidence only; see `SETUP.md` for one checklist.

The original evidence template is retained below for historical completeness:

- Issue / PR;
- branch / commit;
- selected HeliBoard upstream revision;
- build result;
- dependency versions;
- device/Android tested;
- airplane-mode dictation evidence;
- regression smoke results;
- unverified boundaries;
- remaining risks/blockers.
