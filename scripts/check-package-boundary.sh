#!/usr/bin/env bash
set -euo pipefail

echo "== CatBoard package boundary check =="

settings="$(<settings.gradle)"
grep -Fq "include ':app'" <<<"$settings"
grep -Fq "include ':cleaner-companion'" <<<"$settings"

app_manifest="app/src/main/AndroidManifest.xml"
companion_manifest="cleaner-companion/src/main/AndroidManifest.xml"

if grep -RIn --include='AndroidManifest.xml' 'android.permission.INTERNET' . \
    | grep -v '^./cleaner-companion/src/main/AndroidManifest.xml:'; then
  echo "INTERNET must be declared only by cleaner-companion" >&2
  exit 1
fi
grep -Fq 'android.permission.INTERNET' "$companion_manifest"
grep -Fq 'android.permission.RECORD_AUDIO' "$app_manifest"
grep -Fq 'devcat.catboard.permission.CLEAN_TRANSCRIPT' "$app_manifest"
grep -Fq 'android:protectionLevel="signature"' "$companion_manifest"
grep -Fq 'android:permission="devcat.catboard.permission.CLEAN_TRANSCRIPT"' "$companion_manifest"
grep -Fq 'android:exported="true"' "$companion_manifest"

if grep -Fq 'android.permission.INTERNET' "$app_manifest"; then
  echo "Keyboard manifest must not request INTERNET" >&2
  exit 1
fi

grep -Fq 'minSdk = 21' app/build.gradle.kts
grep -Fq 'minSdk = 21' cleaner-companion/build.gradle.kts
for abi in armeabi-v7a arm64-v8a x86 x86_64; do
  grep -Fq "\"$abi\"" app/build.gradle.kts
done

echo "Package boundary OK: app has RECORD_AUDIO/no INTERNET; companion owns INTERNET/signature IPC; both modules are included."
