# HeliBoard upstream baseline

Imported: 2026-09-04

## Source revision

- Repository: [`HeliBorg/HeliBoard`](https://github.com/HeliBorg/HeliBoard)
- Release tag: [`v4.1`](https://github.com/HeliBorg/HeliBoard/releases/tag/v4.1)
- Commit: [`9f5bb635c2e8609dcd95dc7506c0c58fba82a52c`](https://github.com/HeliBorg/HeliBoard/commit/9f5bb635c2e8609dcd95dc7506c0c58fba82a52c)

The commit, rather than a moving branch, is the comparison point for future
upstream updates.

## Import boundary

The following paths were copied unchanged from that revision:

- `.editorconfig`, `.gitignore`, `AI_USAGE.md`, and `CONTRIBUTING.md`;
- `LICENSE`, `LICENSE-Apache-2.0`, and `LICENSE-CC-BY-SA-4.0`;
- `app/`, including application code, resources, tests, dictionaries, and native
  keyboard sources;
- `art/`, `fastlane/`, and `layouts.md`;
- `build.gradle.kts`, `settings.gradle`, `gradle.properties`, `gradle/`, `gradlew`,
  and `gradlew.bat`;
- `tools/`.

The upstream `.git` history, `.idea/`, `.github/`, and `README.md` were not copied.
This repository retains its own Git history, project documentation, GitHub workflow,
and README around the unchanged source baseline. The CatBoard build/test wrapper
scripts are repository glue and are not upstream HeliBoard files.

The import was verified by checking out tag `v4.1` and recursively comparing every
path listed above. No source or build-file differences were found.

## Licensing and attribution

HeliBoard identifies its application as GPL-3.0 licensed; the imported `LICENSE`
is preserved at the repository root. The upstream Apache 2.0 and CC BY-SA 4.0 license
texts are also preserved for files and assets to which those terms apply. Existing
file-level copyright and license headers remain unchanged.

No Govorun, GigaAM, sherpa-onnx, Silero VAD, model files, credentials, or
CatBoard-specific voice implementation are included in this Stage 1 import.
