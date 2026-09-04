$ErrorActionPreference = "Stop"

if (-not (Test-Path ".\gradlew.bat" -PathType Leaf)) {
    throw "Missing Gradle wrapper: .\gradlew.bat"
}

Write-Host "== CatBoard baseline Android debug build =="
& .\gradlew.bat --no-daemon :app:assembleDebug
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
