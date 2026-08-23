# Project Status

Date: 2026-08-23
Repository: `dedtsss/devcat-keyboard`  
Product working name: **CatBoard**  
Etalon baseline: v0.2.0  
Stage: `Stage B / HeliBoard baseline imported / voice implementation gated on CI`

## Current state

The repository was created from `dedtsss/etalon-project-template`.

Accepted direction:

- HeliBoard is the keyboard base; the selected upstream revision is now imported;
- Govorun/GigaAM voice recognition is embedded into the keyboard instead of running as a floating Accessibility overlay;
- the toolbar microphone becomes a native CatBoard action;
- local Russian ASR remains available fully offline;
- optional online text cleanup is planned after local ASR is stable;
- HeliBoard themes, emoji and clipboard are preserved and improved incrementally;
- swipe typing is not a priority.

Android product source is imported from HeliBoard revision `50d13c1bd6c3f4ee6d69644b3d422145cb928503`.
The imported upstream code remains GPL-3.0, with source attribution and license texts retained in
`LICENSE-HELIBOARD`; CatBoard-specific repository and harness files remain additive around that base.

The internal alpha identity is `devcat.catboard` (debug: `devcat.catboard.debug`) with the
`CatBoard` application label, allowing side-by-side installation with HeliBoard. The keyboard
package still has no `android.permission.INTERNET`; online cleanup remains a later companion boundary.

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

Next gated step is to prove this baseline in GitHub Actions and retain an installable debug APK
artifact from the checkpoint HEAD. Only after that gate passes may the first product Issue from the
integrated voice MVP spec be implemented:

1. import a clean current HeliBoard baseline while preserving licensing/history evidence;
2. make the upstream baseline build cleanly before voice changes;
3. identify and replace the current external voice-IME switch path;
4. port the minimum Govorun offline ASR stack;
5. produce the first reviewable Android build with internal offline dictation.

No public release or merge to `main` is authorized by this status document alone.
