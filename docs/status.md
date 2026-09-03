# Project Status

Date: 2026-09-04
Repository: `dedtsss/devcat-keyboard`  
Product working name: **CatBoard**  
Etalon baseline: v0.2.0  
Stage: `clean HeliBoard baseline imported / voice implementation not started`

## Current state

The repository now contains a clean HeliBoard 4.1 source baseline imported from
`HeliBorg/HeliBoard` tag `v4.1`, commit
`9f5bb635c2e8609dcd95dc7506c0c58fba82a52c`. The exact import and license boundary
is recorded in [`docs/upstream/heliboard.md`](upstream/heliboard.md).

The Android application, Gradle wrapper, resources, layouts, and native keyboard
sources remain identical to that upstream revision. No CatBoard voice, Govorun,
GigaAM, sherpa-onnx, or VAD implementation is present in this baseline.

`./scripts/check.sh` is the lightweight host-safe structure/provenance preflight.
GitHub Actions job `check` is the authoritative Android build environment: it sets
up JDK 17 and the pinned Android SDK/NDK, runs `:app:testRunTestsUnitTest`, and builds
`:app:assembleDebug`. No emulator is required for this stage.

Accepted direction:

- HeliBoard is the keyboard base;
- Govorun/GigaAM voice recognition is embedded into the keyboard instead of running as a floating Accessibility overlay;
- the toolbar microphone becomes a native CatBoard action;
- local Russian ASR remains available fully offline;
- optional online text cleanup is planned after local ASR is stable;
- HeliBoard themes, emoji and clipboard are preserved and improved incrementally;
- swipe typing is not a priority.

The accepted voice architecture below remains future scope; importing the baseline
does not implement or validate it.

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

After the Stage 1 baseline PR passes its authoritative GitHub Actions check and is
merged, plan the next separately authorized issue. Voice/ASR integration, microphone
behavior, `InputConnection` dictation, models, and device validation are not part of
this baseline stage.

No public release or merge to `main` is authorized by this status document alone.
