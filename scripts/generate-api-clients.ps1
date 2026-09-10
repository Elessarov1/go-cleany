$ErrorActionPreference = "Stop"
$repositoryRoot = Split-Path -Parent $PSScriptRoot
Push-Location $repositoryRoot
try {
    npx --yes '@openapitools/openapi-generator-cli@2.41.0' generate
    if ($LASTEXITCODE -ne 0) {
        throw "OpenAPI client generation failed with exit code $LASTEXITCODE"
    }
} finally {
    Pop-Location
}
