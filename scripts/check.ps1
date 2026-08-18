$ErrorActionPreference = "Stop"

Write-Host "== etalon-repository check =="

if (Test-Path "./scripts/doctor.ps1") {
    ./scripts/doctor.ps1
}

if (Test-Path "./scripts/test.ps1") {
    ./scripts/test.ps1
}

if (Test-Path "./scripts/build.ps1") {
    ./scripts/build.ps1
}

Write-Host "Check completed."
