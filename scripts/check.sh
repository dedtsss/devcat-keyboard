#!/usr/bin/env bash
set -euo pipefail

echo "== etalon-repository check =="

if [ -f "./scripts/doctor.sh" ]; then
  bash ./scripts/doctor.sh
fi

if [ -f "./scripts/test.sh" ]; then
  bash ./scripts/test.sh
fi

if [ -f "./scripts/build.sh" ]; then
  bash ./scripts/build.sh
fi

echo "Check completed."
