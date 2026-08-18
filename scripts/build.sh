#!/usr/bin/env bash
set -euo pipefail

if [[ -x "./gradlew" ]]; then
  echo "== CatBoard Android build =="
  ./gradlew assembleDebug
else
  echo "CatBoard Android source is not imported yet; Gradle build skipped during repository bootstrap."
fi
