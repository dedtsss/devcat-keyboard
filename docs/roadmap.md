# Roadmap

Roadmap is directional. Concrete work belongs in Issues/specs and may be reordered from evidence.

## Phase 0 — Repository/bootstrap

Goal: make GitHub the product source of truth before code divergence begins.

- project profile and durable decisions;
- architecture/privacy/voice docs;
- focused MVP spec;
- remove template-only example/history that does not belong to CatBoard.

Exit: repository clearly describes CatBoard and the next implementation task.

## Phase 1 — Clean HeliBoard baseline

Goal: prove we can build and maintain the chosen keyboard base before adding voice.

- import current selected HeliBoard baseline;
- preserve upstream license/attribution and record source revision;
- adapt Gradle/build/test/check scripts;
- produce an untouched/near-untouched debug APK;
- verify normal typing, Russian layout, emoji, themes and clipboard on target device.

Exit: reproducible baseline build and known upstream revision.

## Phase 2 — Embedded offline voice MVP

Goal: microphone works inside the keyboard with no external IME/overlay.

- internal `VoiceController`;
- microphone lifecycle;
- GigaAM v3 + sherpa-onnx;
- Silero VAD/recording;
- direct `InputConnection` commit;
- tap start/stop; hold-to-talk if low-cost;
- airplane-mode test;
- regression tests for keyboard basics.

Exit: CatBoard is usable daily with local Russian dictation.

## Phase 3 — Online text cleanup

Goal: turn spoken transcript into clean written text without weakening core privacy more than necessary.

- small `TextPostProcessor` interface;
- separate network companion prototype;
- one provider initially;
- cleanup modes;
- timeout/error/fallback;
- clear UI indication/settings;
- privacy verification.

Exit: optional cleanup is reliable and local transcript is never lost on network failure.

## Phase 4 — Daily-use polish

Goal: use CatBoard as a real primary keyboard and fix evidence-based friction.

Likely areas:
- voice state feedback/latency;
- punctuation/dictionary corrections;
- clipboard search/pinning UX;
- theme/toolbar defaults;
- model packaging/install experience;
- stability across apps/Android lifecycle.

Exit: stable internal beta suitable for broader testers.

## Phase 5 — Distribution decisions

Only after product quality justifies it:

- final branding/package ID;
- supported Android versions/ABIs;
- model delivery strategy;
- reproducible/signing/release pipeline;
- privacy documentation for users;
- distribution channels;
- donation/support model if wanted.

## Research lane (non-blocking)

- small local LLM for offline cleanup;
- optional cloud ASR;
- alternative ASR models/runtimes;
- local correction learning;
- generic patches worth upstreaming to HeliBoard.
