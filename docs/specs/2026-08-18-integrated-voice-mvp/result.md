# Result — integrated voice MVP

Status: PARTIAL — engineering and exact-head CI complete; device/provider evidence deferred

## Verified alpha checkpoint

- Mission: Issue #3, implementation PR #4 (open, Draft, unmerged).
- Branch/head: `codex/catboard-alpha-mission` at
  `b0f4afc40daad4291734331f9d5645a4602e719b`.
- HeliBoard revision: `50d13c1bd6c3f4ee6d69644b3d422145cb928503`.
- Exact-head CI: push `32743738093` and PR `32743743021`, both green; both modules are built
  by explicit Gradle targets in the project check.
- Artifact: debug keyboard APK id `9526531713`, `310,357,002` bytes, SHA-256
  `143a5a462af90765b7a9cdac67bc59c271722efd51642144281ad76c28760ed5`.
- Architecture: CatBoard IME performs recorder/Silero VAD/GigaAM v3/sherpa offline ASR and
  commits the local transcript through `InputConnection`; optional cleanup is explicit Binder
  IPC to `cleaner-companion`, with local fallback on every failure.
- Boundary: keyboard has `RECORD_AUDIO` and no INTERNET; companion alone has INTERNET and the
  signature-protected `CLEAN_TRANSCRIPT` service. Full optional-cleanup alpha uses two APKs,
  installed companion-first then keyboard.
- Limits: minSdk 21; keyboard ABIs are armeabi-v7a, arm64-v8a, x86 and x86_64; bundled model/
  runtime footprint is reflected by the approximately 310 MB debug APK. Runtime performance,
  memory, latency and public distribution suitability are not yet measured.

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
