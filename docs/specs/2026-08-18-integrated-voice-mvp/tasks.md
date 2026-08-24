# Tasks — integrated voice MVP

Status: Stage C implementation checkpoint; CI/device acceptance remains open

## A. Baseline

- [x] Select and record exact HeliBoard upstream revision: `50d13c1bd6c3f4ee6d69644b3d422145cb928503`.
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

- [x] Verify exact licenses/versions for GigaAM, sherpa-onnx and Silero VAD.
- [x] Decide model/runtime acquisition for prototype.
- [x] Port model manager.
- [x] Port/adapt recorder and VAD.
- [x] Port/adapt offline transcriber.
- [x] Add safe recognizer lifecycle/release behavior.
- [ ] Verify real minSdk requirement before changing HeliBoard compatibility.

## D. Editor commit and failure safety

- [x] Commit transcript through active `InputConnection`.
- [x] Handle permission denied.
- [x] Handle empty/no-speech result.
- [x] Handle IME/editor focus loss.
- [x] Keep transcript recoverable after post-ASR failure.
- [x] Avoid Accessibility-based insertion.

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
