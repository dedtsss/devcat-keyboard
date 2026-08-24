# Project Status

Date: 2026-08-24
Repository: `dedtsss/devcat-keyboard`  
Product working name: **CatBoard**  
Etalon baseline: v0.2.0  
Stage: `Stage F / alpha delivery checkpoint / PARTIAL pending device evidence`

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

## Current Stage E checkpoint

An optional `cleaner-companion` Android package owns `INTERNET` and exposes only an
explicitly targeted, signature-protected transcript-cleaning Binder service. The keyboard
client sends one local transcript and a cleanup mode only, with a 1-second bind bound and
provider timeouts bounded inside the companion. The opt-in gate defaults off; every bind,
permission, package-allowlist, missing-key, provider and IPC failure returns the original
local transcript for commit. The companion stores its provider Authorization Key separately
under the existing Govorun preference name `gigachat_authorization_key`; no key is checked in.

Historical exact-head Stage E CI was green at `b0f4afc40daad4291734331f9d5645a4602e719b`: push run
`32743738093`, PR run `32743743021`, artifact `9526531713` (`310,357,002` bytes,
SHA-256 `143a5a462af90765b7a9cdac67bc59c271722efd51642144281ad76c28760ed5`). This historical
artifact contains only the keyboard debug APK; it is not the full two-APK Stage F alpha. The
full optional-cleanup alpha requires the `cleaner-companion` APK from the same head, installed
companion-first, then keyboard.

Stage F static checks record the package boundary: `app` has `RECORD_AUDIO` and no INTERNET;
`cleaner-companion` alone has INTERNET and owns the signature-level cleanup permission/service.
Both application modules are explicit targets of the CI build. The keyboard is minSdk 21 and
packages armeabi-v7a, arm64-v8a, x86 and x86_64; the bundled GigaAM v3/sherpa/VAD runtime is
large and the debug APK is about 310 MB. Model assets are downloaded and hash-verified in CI,
not committed. Runtime, memory, latency and public distribution limits remain unmeasured.

The Stage F controller push and exact-head Actions run/artifact publication are pending. The
workflow is configured to publish both installable debug APKs together, but this checkout does
not claim final exact-head CI or artifact evidence until that controller run completes.

No approved `gigachat_authorization_key` is available for live provider smoke. Physical-device
installation, airplane-mode dictation, mic/focus lifecycle and installed cross-package IPC are
also unverified. These are the only remaining evidence boundaries; engineering result is
terminal `PARTIAL`, with the concise checklist in `SETUP.md`.

This is a source/static and exact-head CI checkpoint only. Physical Android evidence is still
required before claiming end-user acceptance.

## Next step

The mission has no further engineering stage in this checkout. Controller push of the Stage F
checkpoint requires one final exact-head Actions run because the build target and static check
changed. No merge or release is authorized.

No public release or merge to `main` is authorized by this status document alone.
