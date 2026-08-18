#!/usr/bin/env bash
set -euo pipefail

echo "Environment and project-standard check"
echo "- Working directory: $(pwd)"
echo "- Git available: $(command -v git || true)"
echo "- Bash version: ${BASH_VERSION:-unknown}"

required_files=(
  ".etalon-version"
  "README.md"
  "AGENTS.md"
  "CODEX.md"
  "docs/status.md"
  "docs/backlog.md"
  "docs/decisions.md"
  "docs/ideas.md"
  "docs/standards/index.yml"
  "docs/standards/project-lifecycle.md"
  "docs/specs/README.md"
)

for file in "${required_files[@]}"; do
  if [ ! -f "$file" ]; then
    echo "Missing required project-standard file: $file"
    exit 1
  fi
done

echo "Required project-standard files are present (etalon $(tr -d '\r\n' < .etalon-version))."

standards_index="docs/standards/index.yml"
standard_file_count=0

while IFS= read -r line; do
  trimmed="${line#"${line%%[![:space:]]*}"}"

  case "$trimmed" in
    file:*)
      standard_file="${trimmed#file:}"
      standard_file="${standard_file#"${standard_file%%[![:space:]]*}"}"
      standard_file="${standard_file%"${standard_file##*[![:space:]]}"}"
      standard_file="${standard_file%\"}"
      standard_file="${standard_file#\"}"
      standard_file="${standard_file%\'}"
      standard_file="${standard_file#\'}"

      if [ -z "$standard_file" ]; then
        echo "Empty standard file reference in $standards_index"
        exit 1
      fi

      if [ ! -f "$standard_file" ]; then
        echo "Missing standard file referenced by $standards_index: $standard_file"
        exit 1
      fi

      standard_file_count=$((standard_file_count + 1))
      ;;
  esac
done < "$standards_index"

if [ "$standard_file_count" -eq 0 ]; then
  echo "No standard file references found in $standards_index"
  exit 1
fi

echo "Standards index references are valid."
