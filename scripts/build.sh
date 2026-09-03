#!/usr/bin/env bash
set -euo pipefail

if [[ ! -x "./gradlew" ]]; then
  echo "Missing executable Gradle wrapper: ./gradlew" >&2
  exit 1
fi

echo "== CatBoard baseline Android debug build =="
./gradlew --no-daemon :app:assembleDebug
