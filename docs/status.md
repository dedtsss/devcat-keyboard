# Project Status

Date: 2026-08-24
Repository: `dedtsss/devcat-keyboard`  
Product working name: **CatBoard**  
Etalon baseline: v0.2.0  
Stage: `Stage D / usable voice UX and reliability checkpoint / CI and device evidence pending`

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

## Current Stage C checkpoint

The internal microphone route now owns the tap lifecycle: `AudioRecord` captures 16 kHz
mono PCM, Silero VAD detects speech, sherpa-onnx runs the pinned GigaAM v3 RNNT model,
local whitespace cleanup runs before the transcript is committed through the active
`InputConnection`. Permission, no-speech, editor loss, runtime failure, cancellation and
recoverable commit errors have explicit paths. Runtime/model assets are reproducibly
prepared in CI by `scripts/prepare-voice-runtime.sh`; the large ignored artifacts are not
checked into Git. The keyboard manifest retains `RECORD_AUDIO` and no `INTERNET` permission.

Stage D source work adds compact suggestion-strip recording/transcribing/error feedback,
cancels voice sessions when the editor or keyboard view changes, ignores rapid re-starts while
AudioRecord is still releasing, handles recorder-construction failures, and releases the native
recognizer/coroutine scope when the IME service is destroyed. Tap start/stop remains supported;
hold-to-talk is deferred because the existing toolbar plumbing has no low-risk press/release seam.

This is a source/static checkpoint only. Actions must still build the prepared APK, and a
physical Android test must provide the required airplane-mode dictation, focus/lifecycle,
mic start/stop and transcript evidence before Stage D is accepted.

## Next step

Next gated step is to prove this Stage D checkpoint in GitHub Actions from a fresh checkout.
Physical-device evidence for airplane-mode dictation, mic start/stop, focus changes and error
recovery remains deferred until all engineering/CI stages are complete by mission contract.

No public release or merge to `main` is authorized by this status document alone.
