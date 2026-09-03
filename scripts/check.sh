#!/usr/bin/env bash
set -euo pipefail

echo "== CatBoard project check =="

bash ./scripts/doctor.sh

required_baseline_files=(
  "docs/upstream/heliboard.md"
  "LICENSE"
  "LICENSE-Apache-2.0"
  "LICENSE-CC-BY-SA-4.0"
  "build.gradle.kts"
  "settings.gradle"
  "gradlew"
  "gradle/wrapper/gradle-wrapper.jar"
  "gradle/wrapper/gradle-wrapper.properties"
  "app/build.gradle.kts"
  "app/src/main/AndroidManifest.xml"
  "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
)

for file in "${required_baseline_files[@]}"; do
  if [[ ! -f "$file" ]]; then
    echo "Missing HeliBoard baseline file: $file" >&2
    exit 1
  fi
done

if [[ ! -x ./gradlew ]]; then
  echo "Gradle wrapper is not executable: ./gradlew" >&2
  exit 1
fi

upstream_revision="9f5bb635c2e8609dcd95dc7506c0c58fba82a52c"
if ! grep -Fq "$upstream_revision" docs/upstream/heliboard.md; then
  echo "HeliBoard provenance does not record the selected revision." >&2
  exit 1
fi

if ! grep -Fq "distributionSha256Sum=" gradle/wrapper/gradle-wrapper.properties; then
  echo "Gradle distribution checksum is not pinned." >&2
  exit 1
fi

for script in scripts/*.sh; do
  bash -n "$script"
done

if ! grep -Fq 'run: ./scripts/test.sh' .github/workflows/check.yml ||
   ! grep -Fq 'run: ./scripts/build.sh' .github/workflows/check.yml; then
  echo "GitHub Actions check must run the real Android test and build scripts." >&2
  exit 1
fi

echo "HeliBoard baseline structure, provenance, wrapper pin, and shell syntax are valid."
echo "CatBoard lightweight check completed (Android build/test run in GitHub Actions)."
