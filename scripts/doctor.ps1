$ErrorActionPreference = "Stop"

Write-Host "Environment and project-standard check"
Write-Host "- Working directory: $(Get-Location)"

$requiredFiles = @(
    ".etalon-version",
    "README.md",
    "AGENTS.md",
    "CODEX.md",
    "docs/status.md",
    "docs/backlog.md",
    "docs/decisions.md",
    "docs/ideas.md",
    "docs/standards/index.yml",
    "docs/standards/project-lifecycle.md",
    "docs/specs/README.md"
)

foreach ($file in $requiredFiles) {
    if (-not (Test-Path -LiteralPath $file -PathType Leaf)) {
        throw "Missing required project-standard file: $file"
    }
}

$etalonVersion = (Get-Content -LiteralPath ".etalon-version" -Raw).Trim()
Write-Host "Required project-standard files are present (etalon $etalonVersion)."

$standardsIndex = "docs/standards/index.yml"
$standardFileCount = 0

foreach ($line in Get-Content -LiteralPath $standardsIndex) {
    if ($line -match "^\s*file:\s*(.+?)\s*$") {
        $standardFile = $Matches[1].Trim()
        $standardFile = $standardFile.Trim('"')
        $standardFile = $standardFile.Trim("'")

        if ([string]::IsNullOrWhiteSpace($standardFile)) {
            throw "Empty standard file reference in $standardsIndex"
        }

        if (-not (Test-Path -LiteralPath $standardFile -PathType Leaf)) {
            throw "Missing standard file referenced by ${standardsIndex}: $standardFile"
        }

        $standardFileCount += 1
    }
}

if ($standardFileCount -eq 0) {
    throw "No standard file references found in $standardsIndex"
}

Write-Host "Standards index references are valid."
