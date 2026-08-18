# Tasks — integrated voice MVP

Status: planned

## A. Baseline

- [ ] Select and record exact HeliBoard upstream revision.
- [ ] Import baseline with license/attribution intact.
- [ ] Build untouched baseline.
- [ ] Adapt `scripts/build.*`, `scripts/test.*`, `scripts/check.*` and CI.
- [ ] Smoke-test baseline keyboard on target Android device.

## B. Voice integration seam

- [ ] Trace `VOICE -> VOICE_INPUT -> LatinIME` path in imported revision.
- [ ] Add internal `VoiceController` with explicit state ownership.
- [ ] Replace external shortcut-IME switch only for CatBoard voice action.
- [ ] Add basic in-keyboard recording/processing state indication.

## C. Govorun ASR port

- [ ] Verify exact licenses/versions for GigaAM, sherpa-onnx and Silero VAD.
- [ ] Decide model/runtime acquisition for prototype.
- [ ] Port model manager.
- [ ] Port/adapt recorder and VAD.
- [ ] Port/adapt offline transcriber.
- [ ] Add safe recognizer lifecycle/release behavior.
- [ ] Verify real minSdk requirement before changing HeliBoard compatibility.

## D. Editor commit and failure safety

- [ ] Commit transcript through active `InputConnection`.
- [ ] Handle permission denied.
- [ ] Handle empty/no-speech result.
- [ ] Handle IME/editor focus loss.
- [ ] Keep transcript recoverable after post-ASR failure.
- [ ] Avoid Accessibility-based insertion.

## E. Verification

- [ ] `scripts/check` passes.
- [ ] Debug APK builds from fresh checkout.
- [ ] Normal typing smoke passes.
- [ ] Russian dictation works in airplane mode.
- [ ] Mic does not switch IME.
- [ ] No floating overlay involved.
- [ ] Emoji smoke passes.
- [ ] Clipboard smoke passes.
- [ ] Theme switch smoke passes.
- [ ] Focus/IME hide interruption tested.
- [ ] Device/Android/version evidence recorded.

## F. Handoff

- [ ] Update `docs/status.md` with real implementation state.
- [ ] Record new decisions only if implementation forces them.
- [ ] Put experimental findings into `docs/ideas.md`.
- [ ] Add result/evidence to `result.md`.
- [ ] Link Issue and PR.
