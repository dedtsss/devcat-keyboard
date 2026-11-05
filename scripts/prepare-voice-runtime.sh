#!/usr/bin/env bash
set -euo pipefail

# Reproducible CI-only preparation for the embedded offline voice path.
# The large AAR/models stay out of Git and are verified before Gradle sees them.
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
mkdir -p "$ROOT/app/libs" "$ROOT/app/src/main/assets/models/gigaam-v3"

SHERPA_VERSION="1.13.0"
SHERPA_URL="https://github.com/k2-fsa/sherpa-onnx/releases/download/v${SHERPA_VERSION}/sherpa-onnx-${SHERPA_VERSION}.aar"
SHERPA_SHA256="28e9b348736d84b610361daf161f5904440d6d516531e896bf9ee8b0ad02764e"
download_verified() {
  local url="$1" dest="$2" expected="$3"
  if [[ -f "$dest" ]] && [[ "$(sha256sum "$dest" | awk '{print $1}')" == "$expected" ]]; then
    return
  fi
  curl -fL --retry 5 --retry-delay 3 -o "$dest.tmp" "$url"
  local actual
  actual="$(sha256sum "$dest.tmp" | awk '{print $1}')"
  [[ "$actual" == "$expected" ]] || { echo "SHA-256 mismatch: $dest" >&2; rm -f "$dest.tmp"; exit 1; }
  mv "$dest.tmp" "$dest"
}

# The AAR digest is checked after download. If the upstream release changes,
# update this pin only with a reviewed provenance change.
download_verified "$SHERPA_URL" "$ROOT/app/libs/sherpa-onnx.aar" "$SHERPA_SHA256"

VAD_URL="https://raw.githubusercontent.com/dedtsss/govorun-online-cleaner/663af0b6283fe642c68c260749d5d15c654ec4c1/app/src/main/assets/silero_vad.onnx"
VAD_SHA256="9e2449e1087496d8d4caba907f23e0bd3f78d91fa552479bb9c23ac09cbb1fd6"
download_verified "$VAD_URL" "$ROOT/app/src/main/assets/silero_vad.onnx" "$VAD_SHA256"

MODEL_TAG="model-gigaam-v3"
MODEL_BASE="https://github.com/amidexe/govorun-lite/releases/download/${MODEL_TAG}"
declare -A MODEL_SHA256=(
  [gigaam_v3_e2e_rnnt_decoder.onnx]="781971998e6a355d6a714f6932a30eab295e7ba0d14fd7e0f78c83b87e811860"
  [gigaam_v3_e2e_rnnt_encoder_int8.onnx]="2cac62d0c270bd128f898f2be1a2d34780d524a6e9483888ebac7b00f97410f1"
  [gigaam_v3_e2e_rnnt_joint.onnx]="602ff7017a93311aad34df1437c8d7f49911353c13d6eae7a6ee7b041339465c"
  [gigaam_v3_e2e_rnnt_tokens.txt]="7ddf22514c42c531358182c81446a8159771e9921019f09ae743ea622d40221d"
)
for name in "${!MODEL_SHA256[@]}"; do
  download_verified "$MODEL_BASE/$name" "$ROOT/app/src/main/assets/models/gigaam-v3/$name" "${MODEL_SHA256[$name]}"
done
echo "Offline voice runtime and model assets prepared (sherpa-onnx ${SHERPA_VERSION}, GigaAM v3)."
