#!/usr/bin/env bash
set -euo pipefail

echo "== CatBoard project check =="

bash ./scripts/doctor.sh
if [[ "${CATBOARD_RUN_ANDROID:-0}" == "1" ]]; then
    bash ./scripts/prepare-voice-runtime.sh
fi
bash ./scripts/test.sh
bash ./scripts/build.sh

echo "CatBoard check completed."
