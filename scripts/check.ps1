$ErrorActionPreference = "Stop"

Write-Host "== CatBoard project check =="

& .\scripts\doctor.ps1
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& .\scripts\test.ps1
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& .\scripts\build.ps1
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "CatBoard check completed."
