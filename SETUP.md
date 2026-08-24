# Development setup

## Current alpha install/test notes

The historical Stage E keyboard-only debug artifact was built from commit
`b0f4afc40daad4291734331f9d5645a4602e719b`:

- GitHub Actions artifact `catboard-debug-b0f4afc40daad4291734331f9d5645a4602e719b`, id `9526531713`
  (keyboard APK only; not the full two-APK Stage F alpha);
- APK size `310,357,002` bytes, SHA-256
  `143a5a462af90765b7a9cdac67bc59c271722efd51642144281ad76c28760ed5`;
- push run `32743738093` and PR run `32743743021` passed; artifact expires 2026-11-22.

The Stage F controller push and exact-head Actions run for the two-APK artifact are pending; no
final Stage F CI or artifact result is claimed here. The workflow will publish one artifact named
`catboard-alpha-debug-${GITHUB_SHA}` containing:

- `app/build/outputs/apk/debug/CatBoard_*-debug.apk`;
- `cleaner-companion/build/outputs/apk/debug/cleaner-companion-debug.apk`.

Full alpha behavior uses two APKs. Install the companion first, then CatBoard, using APKs
from the same exact CI head and the same signing identity. Enable CatBoard in Android keyboard
settings, grant `RECORD_AUDIO` when prompted, and keep the companion installed only when the
explicit online cleanup option is desired. Local dictation does not require the companion or
network. The companion's provider key is configured separately and is not part of the artifact.

Static package checks enforce that the keyboard has no `INTERNET`, the companion is the only
network-capable package, and the signature-protected cleanup IPC plus microphone permission are
present. `scripts/check.sh` builds both `:app:assembleDebug` and `:cleaner-companion:assembleDebug`
in CI; Bruce must use the lightweight/static path and must not run local Android builds.

Device checklist (still outstanding): install both APKs; enable CatBoard and grant mic; verify
normal typing plus mic start/stop and focus/IME hide/show; dictate in airplane mode and confirm
the local transcript is inserted; optionally configure an approved provider key and verify
cleanup/fallback. No device or live-provider result is implied by CI.

## Historical bootstrap notes

Before the HeliBoard import, the meaningful bootstrap check was:

```bash
./scripts/check.sh
```

It validates the etalon structure and explicitly skips Android build/test while `gradlew` is absent.

## After HeliBoard baseline import

The implementation PR must preserve a standard Android/Gradle wrapper workflow so a fresh clone can run without local IDE-specific state.

The historical post-import command shape was:

```bash
./gradlew assembleDebug
./gradlew test
```

`scripts/build.sh`, `scripts/test.sh` and PowerShell equivalents should wrap the actual project commands once the source layout is known.

## Voice-model dependencies

The first Govorun/GigaAM integration is expected to require:

- GigaAM v3 model files;
- sherpa-onnx Android runtime;
- Silero VAD model/runtime pieces.

Do not commit third-party binaries/models blindly. Before adding each dependency:

1. verify its exact license and redistribution terms;
2. record source/version/hash or reproducible download procedure;
3. decide whether it belongs in Git, GitHub Release assets, CI download, or a model-install flow;
4. never put API credentials into Gradle/resources/repository files.

## Target for first internal prototype

arm64-only is acceptable for the first integrated voice prototype. This does **not** define final public compatibility.

## Secrets

Online-cleanup credentials are not required for local ASR and must not be committed. Follow `docs/standards/secrets-policy.md`.
## Alpha online-cleanup setup

Install `cleaner-companion-debug.apk` first and `CatBoard_*debug.apk` second from the same
build/signing identity. Open CatBoard settings, choose Online transcript cleanup, enable it,
and tap Cleanup mode to cycle Light, Normal, and Clean. Open CatBoard Cleaner from the
launcher to enter, Save, or Clear the provider Authorization Key; it remains in companion
private storage and is never passed to the keyboard.

With cleanup disabled, without the companion, or in airplane mode, local offline dictation
must still commit its transcript. The keyboard keeps no INTERNET permission. Remaining
physical checklist: install both APKs, verify mic start/stop and editor focus/hide/show,
airplane-mode dictation, cleanup timeout fallback, and (only with an approved key) live
provider cleanup. These are device/provider evidence, not local-build evidence.
