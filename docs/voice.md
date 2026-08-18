# Voice input

Date: 2026-08-18

## UX goal

Voice typing must feel like a built-in keyboard function.

No keyboard switch, no floating overlay, no modal recognition window.

Primary control: a permanently available microphone button in the CatBoard toolbar.

## Interaction

### Tap

- first tap -> start recording;
- second tap -> finish recording;
- useful for longer dictation.

### Hold

Target behavior:

- press and hold -> record;
- release -> stop and process;
- useful for short phrases.

Implement hold only if HeliBoard toolbar event handling can support press/release cleanly without invasive UI changes. Tap flow is sufficient for the first working build.

## State feedback

Voice state should be visible inside the keyboard area, preferably toolbar/suggestion-strip space.

Possible states:

- idle microphone;
- recording indicator/timer;
- transcribing;
- optional cleaning;
- recoverable error/fallback.

Do not cover the target application with a dialog for normal operation.

## ASR pipeline

```text
AudioRecord
 -> VAD / segmentation
 -> PCM 16 kHz mono
 -> sherpa-onnx
 -> GigaAM v3
 -> local transcript
 -> dictionary/local cleanup
```

The initial implementation should reuse the proven Govorun stack rather than changing speech models at the same time as keyboard integration.

## Online cleanup

After a valid local transcript exists, optional text cleanup may run:

```text
local transcript
 -> online TextPostProcessor
 -> cleaned text
```

The local transcript is the fallback and must remain available until the final commit succeeds.

Recommended modes eventually:

- `off` — local transcript only;
- `light` — punctuation/case/obvious ASR artifacts;
- `normal` — also remove repetitions/filler speech where safe;
- later stronger rewriting only if the user explicitly wants it.

The cleaner must not invent facts or materially change meaning.

## Commit behavior

Commit through the active `InputConnection`.

Before committing:

- verify the editor connection is still valid;
- handle app/focus change gracefully;
- do not commit into a new field if the original dictation context has clearly disappeared;
- retain/copy recoverable transcript on ambiguous failure rather than losing it.

## Lifecycle cases to test

- keyboard hidden while recording;
- user switches app;
- screen rotates/configuration changes;
- microphone permission denied/revoked;
- audio focus/interruption;
- recognizer initialization failure;
- very short/no speech;
- long pause;
- online cleanup timeout/error;
- IME destroyed during transcription;
- repeated rapid mic taps.

## Performance target

Do not invent hard latency numbers before measurement.

Practical target: after stop, local transcript should appear fast enough to feel like keyboard input; model/recognizer should be reused in memory where safe instead of reloading hundreds of MB for every phrase.

## Privacy

Local ASR does not require network. See `docs/privacy.md` for the network-cleanup boundary.
