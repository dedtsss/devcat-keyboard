$ErrorActionPreference = "Stop"

if (-not (Test-Path ".\gradlew.bat" -PathType Leaf)) {
    throw "Missing Gradle wrapper: .\gradlew.bat"
}

Write-Host "== CatBoard baseline Android unit tests =="
& .\gradlew.bat --no-daemon :app:testRunTestsUnitTest
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
