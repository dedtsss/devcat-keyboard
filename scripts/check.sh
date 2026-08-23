#!/usr/bin/env bash
set -euo pipefail

echo "== CatBoard project check =="

bash ./scripts/doctor.sh
bash ./scripts/test.sh
bash ./scripts/build.sh

echo "CatBoard check completed."
