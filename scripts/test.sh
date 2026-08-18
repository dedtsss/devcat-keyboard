#!/usr/bin/env bash
set -euo pipefail

if [[ -x "./gradlew" ]]; then
  echo "== CatBoard Android tests =="
  ./gradlew test
else
  echo "CatBoard Android source is not imported yet; Gradle tests skipped during repository bootstrap."
fi
