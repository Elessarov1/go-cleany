#!/usr/bin/env bash
set -euo pipefail

repository_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repository_root"
npx --yes '@openapitools/openapi-generator-cli@2.41.0' generate
