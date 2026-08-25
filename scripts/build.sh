#!/usr/bin/env bash
set -euo pipefail

if [[ ! -x "./gradlew" ]]; then
  echo "Missing executable Gradle wrapper: ./gradlew" >&2
  exit 1
fi

if [[ "${CATBOARD_RUN_ANDROID:-0}" != "1" ]]; then
  echo "Android build skipped on this host; set CATBOARD_RUN_ANDROID=1 in the CI build job."
  exit 0
fi

echo "== CatBoard Android debug build (GitHub Actions) =="
./gradlew --no-daemon :app:assembleDebug :cleaner-companion:assembleDebug
