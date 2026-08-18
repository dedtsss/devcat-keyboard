# CatBoard integrated voice MVP

Date: 2026-08-18  
Status: planned

## Goal

Produce the first reviewable CatBoard Android build based on HeliBoard where the existing toolbar microphone runs local Govorun/GigaAM recognition **inside the keyboard** and commits Russian text into the active editor.

## User outcome

The user opens any normal text field, sees CatBoard, taps the microphone in the keyboard toolbar, dictates, stops, and receives recognized text without:

- switching to another IME;
- using a floating Govorun bird;
- requiring Accessibility overlay access;
- requiring Internet for recognition.

## Prior art / known code

### HeliBoard

Current relevant seam:

`ToolbarKey.VOICE -> KeyCode.VOICE_INPUT -> LatinIME.onEvent()`.

Upstream currently handles the voice action by switching to a shortcut/external IME. CatBoard should replace this narrow behavior with an internal controller.

### Govorun work

Reusable reference lives in `dedtsss/govorun-online-cleaner` and upstream Govorun:

- `OfflineTranscriber` concept;
- GigaAM v3 model handling;
- sherpa-onnx;
- `VadRecorder` / Silero VAD;
- dictionary/post-processing pieces.

Do not port Accessibility overlay/bubble/dialog UX.

## MVP scope

### 1. Baseline import

- import a clean selected HeliBoard revision;
- preserve license/attribution and record upstream revision;
- make baseline build before functional changes;
- adapt repository scripts/CI to real Gradle commands.

### 2. Internal voice action

- intercept existing voice toolbar action inside CatBoard;
- start an internal `VoiceController` instead of `switchToShortcutIme`;
- keep keyboard/editor context alive.

### 3. Audio + ASR

- request/manage microphone permission;
- record audio suitable for GigaAM;
- integrate VAD where it provides existing Govorun behavior;
- initialize/reuse sherpa recognizer safely;
- return local transcript.

### 4. Commit

- commit recognized text through current `InputConnection`;
- do not use Accessibility to fill fields;
- handle missing/stale editor connection without losing transcript.

### 5. Basic voice UI

- mic button visibly reflects recording/processing state;
- tap start/stop is required;
- hold-to-talk is optional in this MVP if it would require invasive toolbar changes.

## Non-goals

- online cleanup implementation;
- cloud audio ASR;
- local LLM;
- clipboard redesign;
- swipe typing;
- broad HeliBoard refactor;
- public release/signing/distribution.

## Acceptance criteria

1. Fresh checkout can build the documented debug target.
2. CatBoard can be enabled as an Android keyboard and type normally.
3. Existing toolbar microphone is visible/configurable as expected.
4. Pressing mic does not switch to a separate voice IME.
5. No Govorun floating overlay is involved.
6. Russian dictation is recognized locally through GigaAM.
7. Result is committed into the active editor via IME connection.
8. Local dictation works in airplane mode.
9. Emoji, themes and clipboard still open/work in a basic smoke test.
10. Permission denial and recognizer failure do not crash the IME.
11. If a transcript exists but final commit cannot safely occur, it is recoverable rather than silently discarded.
12. PR reports exact upstream HeliBoard revision, dependency versions, device/Android tested and unverified boundaries.

## Required investigation

Before changing compatibility settings:

- determine actual minimum Android version supported by the chosen sherpa-onnx/GigaAM runtime;
- do not assume the previous Govorun fork's `minSdk 33` is technically required;
- arm64-only is acceptable for the internal prototype if necessary;
- document model size/install/RAM observations rather than optimizing prematurely.

## Risks

- native runtime/ABI incompatibility;
- memory pressure from large ASR model inside IME process;
- microphone lifecycle interacting with IME destruction/focus changes;
- upstream divergence if changes spread outside a narrow voice seam;
- large APK/model distribution footprint.

## Verification evidence

The result should include:

- build command/output summary;
- manifest permission check;
- APK artifact or reproducible build location;
- device model + Android version;
- airplane-mode dictation test;
- at least one focus/keyboard-hide interruption test;
- regression smoke for typing/emoji/clipboard/themes;
- remaining risks/blockers.
