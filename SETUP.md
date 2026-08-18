# Development setup

## Current bootstrap state

Android source has not yet been imported into this repository. During bootstrap the meaningful repository check is:

```bash
./scripts/check.sh
```

It validates the etalon structure and explicitly skips Android build/test while `gradlew` is absent.

## After HeliBoard baseline import

The implementation PR must preserve a standard Android/Gradle wrapper workflow so a fresh clone can run without local IDE-specific state.

Expected commands after import:

```bash
./gradlew assembleDebug
./gradlew test
```

`scripts/build.sh`, `scripts/test.sh` and PowerShell equivalents should wrap the actual project commands once the source layout is known.

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
