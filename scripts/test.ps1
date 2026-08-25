$ErrorActionPreference = "Stop"

if (Test-Path ".\gradlew.bat") {
    Write-Host "== CatBoard Android tests =="
    & .\gradlew.bat testRunTestsUnitTest
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
} else {
    Write-Host "CatBoard Android source is not imported yet; Gradle tests skipped during repository bootstrap."
}
