# Project Status

Date: 2026-09-04
Repository: `dedtsss/devcat-keyboard`  
Product working name: **CatBoard**  
Etalon baseline: v0.2.0  
Stage: `Stage 2A internal voice route implemented / recording and ASR not started`

## Current state

The repository now contains a clean HeliBoard 4.1 source baseline imported from
`HeliBorg/HeliBoard` tag `v4.1`, commit
`9f5bb635c2e8609dcd95dc7506c0c58fba82a52c`. The exact import and license boundary
is recorded in [`docs/upstream/heliboard.md`](upstream/heliboard.md).

CatBoard now intercepts the existing toolbar microphone action in `LatinIME` and
routes it to an internal `VoiceController` instead of switching to an external
shortcut IME. The controller owns bounded idle, permission-request,
placeholder-ready, and error states. A non-exported activity requests
`RECORD_AUDIO`, and denial or cancellation returns the controller to idle.

Stage 2A does not add recording, recognition, GigaAM, sherpa-onnx, Silero VAD,
model assets, or network access. A narrow future transcript-delivery method commits
through the current `InputConnection` and retains text when that commit fails.

`./scripts/check.sh` is the lightweight host-safe structure/provenance preflight.
GitHub Actions job `check` is the authoritative Android build environment: it sets
up JDK 21 (required by Robolectric when testing against target SDK 36) and the pinned
Android SDK/NDK, runs `:app:testRunTestsUnitTest`, and builds `:app:assembleDebug`.
No emulator is required for this stage.

Accepted direction:

- HeliBoard is the keyboard base;
- Govorun/GigaAM voice recognition is embedded into the keyboard instead of running as a floating Accessibility overlay;
- the toolbar microphone becomes a native CatBoard action;
- local Russian ASR remains available fully offline;
- optional online text cleanup is planned after local ASR is stable;
- HeliBoard themes, emoji and clipboard are preserved and improved incrementally;
- swipe typing is not a priority.

Only the controller/permission/editor boundary of the accepted voice architecture
is implemented. The audio and recognition portions remain future scope.

## Accepted first architecture

```text
HeliBoard IME / CatBoard UI
        |
        v
internal VoiceController
        |
        +-> recorder + Silero VAD
        |
        +-> GigaAM v3 + sherpa-onnx (offline)
        |
        +-> local dictionary/post-processing
        |
        +-> optional online cleanup companion
        |
        v
InputConnection -> active Android editor
```

See:

- `docs/architecture.md`
- `docs/voice.md`
- `docs/privacy.md`
- `docs/specs/2026-08-18-integrated-voice-mvp/`

## Known reusable work

`dedtsss/govorun-online-cleaner` contains reusable reference/implementation pieces:

- `OfflineTranscriber`;
- GigaAM model management;
- sherpa-onnx integration;
- VAD/recording logic;
- dictionary ideas;
- GigaChat client/prompt cleaner work.

The old Accessibility overlay, floating bird and modal recognition-dialog UX are not target architecture.

## Known risks to verify

1. HeliBoard currently targets a wider Android/ABI range than the existing Govorun fork. Determine the real minimum supported by sherpa-onnx/GigaAM before changing project minSdk.
2. First internal voice build can be arm64-only, but public ABI policy remains undecided.
3. GigaAM model size may make a public APK too large; model-pack/download/import design is later work.
4. `InputMethodService` microphone lifecycle must be tested across focus changes, IME hide/show, app switching and interruptions.
5. Online cleanup must have a bounded timeout and a safe local-transcript fallback.
6. Upstream HeliBoard updates should remain reasonably mergeable; avoid unnecessary invasive rewrites.

## Next step

After the Stage 2A PR passes its authoritative GitHub Actions check and is merged,
plan the next separately authorized stage. Audio capture, ASR integration, models,
airplane-mode dictation, and device validation are not part of Stage 2A.

No public release or merge to `main` is authorized by this status document alone.
