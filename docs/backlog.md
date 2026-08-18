# Backlog

High-level queue only. Concrete implementation work should live in GitHub Issues/specs.

## Bootstrap

- [x] Create repository from `dedtsss/etalon-project-template`.
- [x] Choose working product name CatBoard.
- [x] Record HeliBoard + Govorun/GigaAM architecture direction.
- [x] Record privacy/network boundary.
- [x] Create first integrated voice MVP spec.
- [ ] Import clean HeliBoard baseline into a focused implementation branch/PR.
- [ ] Verify untouched upstream baseline builds before CatBoard changes.

## Voice MVP

- [ ] Replace external voice-IME switch with internal voice controller.
- [ ] Port GigaAM v3 model management.
- [ ] Integrate sherpa-onnx runtime.
- [ ] Port recorder + Silero VAD path.
- [ ] Add microphone permission/lifecycle handling for IME.
- [ ] Tap mic: start/stop dictation.
- [ ] Hold mic: record-while-held if toolbar event plumbing supports it cleanly.
- [ ] Commit local transcript through `InputConnection`.
- [ ] Preserve transcript on failures/interruption.
- [ ] Verify local dictation in airplane mode.
- [ ] Regression-check typing, themes, emoji and clipboard.

## Online cleanup

- [ ] Define `TextPostProcessor`/cleaner interface.
- [ ] Prototype separate network companion package with signature-protected IPC.
- [ ] Port/adapt one working GigaChat cleanup provider from previous Govorun work.
- [ ] Add bounded timeout and automatic fallback to local transcript.
- [ ] Add explicit UI/settings describing what text is sent.
- [ ] Verify keyboard core still has no `INTERNET` permission.

## Clipboard

- [ ] Use current HeliBoard clipboard in daily testing before changing it.
- [ ] Identify actual UX pain points.
- [ ] Add search/filter improvements if still needed.
- [ ] Improve pinned/favorite workflow if justified.
- [ ] Consider snippets/groups only from real usage evidence.

## Later research

- [ ] Re-evaluate small local LLMs for Russian cleanup on target Android hardware.
- [ ] Evaluate model-pack/download flow for GigaAM.
- [ ] Decide public ABI/minSdk/device support.
- [ ] Evaluate optional cloud ASR separately from text cleanup.
- [ ] Decide final branding/icon/package name.
- [ ] Decide distribution channels and donation/support model if project becomes public-facing.
