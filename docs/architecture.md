# Architecture

Date: 2026-08-18  
Status: accepted direction; implementation pending

## Product shape

CatBoard is an Android Input Method Editor based on HeliBoard with an integrated offline Russian voice path and optional network-assisted text cleanup.

The architecture deliberately reuses mature keyboard/UI/clipboard code and adds the smallest new voice/post-processing layer needed.

## High-level components

```text
+---------------------------------------------------+
| CatBoard APK (no INTERNET permission preferred)   |
|                                                   |
|  HeliBoard UI / LatinIME / toolbar                |
|              |                                    |
|              v                                    |
|       VoiceController                             |
|          |        |                               |
|          |        +--> voice UI/state             |
|          v                                        |
|  Recorder + Silero VAD                            |
|          |                                        |
|          v                                        |
|  GigaAM v3 / sherpa-onnx                          |
|          |                                        |
|          v                                        |
|  Local dictionary/post-processing                 |
|          |                                        |
|          +--------------------------+             |
|                                     |             |
|                                     v             |
|                           InputConnection          |
+------------------|--------------------------------+
                   | optional explicit IPC
                   v
+---------------------------------------------------+
| CatBoard Network Companion (separate package)     |
| INTERNET permission                               |
|                                                   |
|  TextPostProcessor service                        |
|      -> provider adapter (initially one provider) |
|      -> bounded timeout / error mapping           |
+---------------------------------------------------+
```

## HeliBoard integration seam

Current upstream behavior uses the toolbar `VOICE` action to map to `KeyCode.VOICE_INPUT`; `LatinIME` then switches to a shortcut/external voice IME.

CatBoard should keep the existing toolbar button but replace only this behavior with an internal `VoiceController`.

This is intentionally narrower than redesigning toolbar/input architecture.

## VoiceController responsibility

Own the voice state machine and lifecycle, not ASR internals.

Suggested states:

```text
IDLE
-> RECORDING
-> TRANSCRIBING
-> [CLEANING]
-> COMMITTING
-> IDLE
```

Error/interruption always transitions through a recoverable path and must not silently discard a completed local transcript.

## Offline ASR module

Reuse/adapt the proven Govorun pieces:

- GigaAM model manager;
- sherpa-onnx recognizer wrapper (`OfflineTranscriber` concept);
- audio recorder;
- Silero VAD;
- local replacement dictionary where useful.

Do not port UI-specific Accessibility/overlay/dialog code.

## Online post-processing boundary

Define a small interface independent from a provider, conceptually:

```text
process(localTranscript, mode) -> cleanedText | failure
```

The first implementation should support one provider and a deterministic fallback. Do not build a general multi-provider AI framework until there is a real second provider requirement.

Preferred network design is a separate Android package/service so the keyboard APK itself can remain unable to access the Internet.

## Text commit

Final text is inserted through the current IME `InputConnection`.

Do not rely on Accessibility to locate/fill fields. The keyboard already owns the correct editor connection.

## Clipboard

Keep HeliBoard's existing clipboard database/history/view as the foundation. Clipboard improvements are downstream of the voice MVP and should not be coupled to ASR architecture.

## Dependency policy

- Preserve upstream licensing/attribution.
- Pin/document important native/model versions.
- Prefer reproducible dependency download/build procedures over committing opaque binaries.
- Measure APK/install/RAM impact before optimizing model packaging.

## Deliberate non-goals for first implementation

- rewrite HeliBoard;
- new gesture typing engine;
- local LLM integration;
- universal AI provider gateway;
- cloud audio ASR;
- new clipboard framework.
