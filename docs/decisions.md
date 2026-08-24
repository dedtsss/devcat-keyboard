# Decisions

Этот файл хранит только принятые решения CatBoard. Общие процессные правила живут в `docs/standards/` и `AGENTS.md`.

Если решение меняется, старую запись не удалять: отметить как `superseded`, добавить новое решение, причину и evidence.

## 2026-08-18 — Working product name: CatBoard

Decision: использовать **CatBoard** как рабочее продуктовое название, а `dedtsss/devcat-keyboard` — как имя репозитория.

Reason:
- короткое имя для продукта;
- соответствует DevCat naming;
- не привязывает архитектуру к upstream-бренду HeliBoard.

Final branding может быть изменён позже без изменения технической базы.

## 2026-08-18 — Use HeliBoard as the keyboard base

Decision: строить CatBoard на базе `HeliBorg/HeliBoard`, а не форкать FUTO Keyboard и не писать IME с нуля.

Reason:
- HeliBoard уже закрывает обычный набор, раскладки, темы, emoji, toolbar и clipboard;
- проект open-source и подходит как инженерная база для собственного форка;
- swipe typing не является требованием CatBoard;
- это минимизирует объём ненужной реализации.

Changing the base keyboard requires a superseding decision backed by concrete evidence.

## 2026-08-18 — Voice input is native inside CatBoard

Decision: встроить голосовой тракт внутрь клавиатуры.

Target flow:

`toolbar mic -> internal recorder/VAD -> GigaAM -> post-processing -> InputConnection`.

Do not use as the normal path:
- switching to a separate voice IME;
- Accessibility overlay;
- floating Govorun bird/bubble;
- modal recognition dialog over the target app.

Reason: microphone must stay directly under the user's finger and voice typing must feel like one keyboard, not two applications layered together.

## 2026-08-18 — GigaAM remains the primary offline ASR

Decision: use the Govorun offline recognition stack as the first speech-to-text engine: GigaAM v3 + sherpa-onnx + Silero VAD.

Reason:
- it already works well enough for real Russian dictation;
- our previous Govorun work contains reusable integration code;
- replacing ASR before the integrated keyboard exists would add risk without demonstrated product value.

A future ASR engine can be evaluated later, but it is not a blocker for MVP.

## 2026-08-18 — Online cleanup follows local ASR

Decision: online intelligence is part of the intended product, but the first online stage is **text cleanup/post-processing after local ASR**, not mandatory cloud audio recognition.

Desired behavior:
- remove or normalize filler sounds/words and repetitions when appropriate;
- improve punctuation and casing;
- turn spoken phrasing into readable written text without inventing facts;
- preserve a recoverable local transcript;
- if network/API fails, fall back to the local result.

Reason: local GigaAM recognition is already useful, while the main remaining quality problem is spoken-text cleanup and structure.

Cloud ASR itself remains a later optional research item, not an MVP requirement.

## 2026-08-18 — Prefer a network-isolated companion for online cleanup

Decision: preferred architecture keeps the CatBoard keyboard package without `android.permission.INTERNET` and puts optional online cleanup into a separate companion/network package invoked explicitly from the keyboard.

The IPC boundary must not implicitly expose ordinary keystrokes, clipboard history, surrounding editor text or local learning data.

Reason:
- privacy becomes technically enforceable at the Android permission boundary;
- the core keyboard cannot silently access the network;
- users can keep a fully offline keyboard even if they never install/configure the companion.

Status: accepted architecture target, subject to implementation validation. If Android lifecycle/IPC complexity makes the split materially worse, reconsider through an explicit superseding decision rather than silently adding network permission to the keyboard.

## 2026-08-24 — Stage E companion boundary

Decision: implement the preferred split as a separate `cleaner-companion` Android package.
Only that package declares `android.permission.INTERNET`; the keyboard uses an explicit
component target and a signature-level `devcat.catboard.permission.CLEAN_TRANSCRIPT`
permission. The Binder payload is limited to a bounded dictated transcript and cleanup mode,
and the companion allowlists CatBoard package UIDs. Cleanup is opt-in and the local transcript
is always retained as the fallback.

Evidence: the existing Govorun GigaChat client/prompt concepts fit this narrow provider path;
the repository had no existing network or IPC module, so this is a minimal additive seam.

## 2026-08-18 — Reuse HeliBoard clipboard/emoji/theme foundations

Decision: do not rewrite clipboard, emoji, themes or general keyboard UI before the voice MVP.

Reason:
- HeliBoard already has persistent clipboard history, pinning/retention and dedicated UI;
- it already provides emoji and theme customization;
- improvements can be incremental after the integrated voice path works.

## 2026-08-18 — Local LLM is not an MVP dependency

Decision: do not block CatBoard on an on-device LLM for text cleanup.

Reason:
- mobile inference runtime/JNI/ABI integration, model size, RAM, speed and Russian cleanup quality need separate evidence;
- a small local LLM could become useful later, but the keyboard + GigaAM integration has higher immediate value.

Track local LLM candidates in `docs/ideas.md` and revisit after the voice MVP is stable.

## 2026-08-18 — First internal voice build may be arm64-only

Decision: arm64-only is acceptable for the first internal prototype if that materially reduces integration work with the existing Govorun/sherpa stack.

Reason: current target devices are arm64 and the purpose of the first build is architecture validation, not broad distribution.

Public ABI/device support is a separate release decision and must not be inferred from the prototype.
