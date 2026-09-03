# Development setup

## Baseline build

CatBoard currently carries the clean HeliBoard 4.1 Android baseline.

The host-safe repository preflight needs Bash and Git, but does not need Java,
Gradle, an Android SDK/NDK, or an emulator:

```bash
git clone https://github.com/dedtsss/devcat-keyboard.git
cd devcat-keyboard
./scripts/check.sh
```

GitHub Actions is the authoritative Android environment. Its `check` job installs
JDK 21, Android platform/build tools 36, and NDK 28.0.13004108, then runs:

```bash
./scripts/test.sh
./scripts/build.sh
```

To reproduce that Android build from a fresh checkout elsewhere, install those
same JDK/SDK/NDK versions (with accepted Android licenses) and run the two commands
above. They execute `:app:testRunTestsUnitTest` and `:app:assembleDebug` through the
checked-in Gradle wrapper. The `runTests` variant skips the network-only external
link checks so this baseline gate does not depend on third-party site availability.
No emulator is needed for this Stage 1 baseline.

The Gradle wrapper pins Gradle 8.14 (including its distribution checksum); the
project pins Android Gradle Plugin 8.13.2, compile/target SDK 36, and NDK
28.0.13004108. Build outputs and local SDK/cache files are ignored by Git.

See [`docs/upstream/heliboard.md`](docs/upstream/heliboard.md) for source provenance
and the exact import boundary.

## Voice-model dependencies

The first Govorun/GigaAM integration is expected to require:

- GigaAM v3 model files;
- sherpa-onnx Android runtime;
- Silero VAD model/runtime pieces.

Do not commit third-party binaries/models blindly. Before adding each dependency:

1. verify its exact license and redistribution terms;
2. record source/version/hash or reproducible download procedure;
3. decide whether it belongs in Git, GitHub Release assets, CI download, or a model-install flow;
4. never put API credentials into Gradle/resources/repository files.

## Target for first internal prototype

arm64-only is acceptable for the first integrated voice prototype. This does **not** define final public compatibility.

## Secrets

Online-cleanup credentials are not required for local ASR and must not be committed. Follow `docs/standards/secrets-policy.md`.
