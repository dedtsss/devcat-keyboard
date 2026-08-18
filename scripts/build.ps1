$ErrorActionPreference = "Stop"

if (Test-Path ".\gradlew.bat") {
    Write-Host "== CatBoard Android build =="
    & .\gradlew.bat assembleDebug
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
} else {
    Write-Host "CatBoard Android source is not imported yet; Gradle build skipped during repository bootstrap."
}
