# Testing Standard

## Rule

Run the smallest relevant verification before claiming a task is complete, preferably `scripts/check`.

## Details

- Use `./scripts/check.sh` or `./scripts/check.ps1` when available.
- If a narrower test is more appropriate, run it and explain why.
- If no real tests exist yet, report that clearly instead of implying coverage.
- CI should stay aligned with the repository check script.
- For bug fixes, reproduce or describe the failing case when practical.
