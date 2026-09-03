[CmdletBinding()]
param(
    [ValidateSet('smoke', 'rental-browse', 'image-burst', 'mixed-api', 'stress', 'all')]
    [string]$Scenario = 'smoke',
    [int]$Scale = 1,
    [long]$Seed = 42,
    [switch]$Reset,
    [switch]$SkipSeed,
    [switch]$Validation,
    [string]$BaseUrl = 'http://frontend',
    [string]$ApiBaseUrl = 'http://backend:8080'
)

$ErrorActionPreference = 'Stop'
$repositoryRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$composeFile = Join-Path $repositoryRoot 'performance\compose.perf.yaml'
$resultsDirectory = Join-Path $repositoryRoot 'performance\results'
$manifestPath = Join-Path $resultsDirectory 'manifest.json'
$backendPort = if ($env:PERF_BACKEND_PORT) { $env:PERF_BACKEND_PORT } else { '18080' }

function Assert-LocalTarget([string]$Name, [string]$Value) {
    try {
        $uri = [Uri]$Value
    } catch {
        throw "$Name is not a valid URL: $Value"
    }
    $allowed = @('127.0.0.1', 'localhost', 'host.docker.internal', 'frontend', 'backend')
    if ($uri.Scheme -notin @('http', 'https') -or $uri.Host -notin $allowed) {
        throw "$Name must target localhost or the internal loco-perf Compose network"
    }
}

function Invoke-Compose([string[]]$Arguments) {
    & docker compose -f $composeFile @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "docker compose failed: $($Arguments -join ' ')"
    }
}

Assert-LocalTarget 'BaseUrl' $BaseUrl
Assert-LocalTarget 'ApiBaseUrl' $ApiBaseUrl
New-Item -ItemType Directory -Force -Path $resultsDirectory | Out-Null

if ($Reset) {
    Write-Host 'Removing only the dedicated loco-perf containers and volume...'
    Invoke-Compose @('down', '--volumes', '--remove-orphans')
}

Invoke-Compose @('up', '--build', '-d', 'postgres', 'backend', 'frontend')

$healthUrl = "http://127.0.0.1:$backendPort/actuator/health"
$healthy = $false
for ($attempt = 1; $attempt -le 60; $attempt++) {
    try {
        $health = Invoke-RestMethod -Uri $healthUrl -TimeoutSec 2
        if ($health.status -eq 'UP') {
            $healthy = $true
            break
        }
    } catch {
        Start-Sleep -Seconds 1
    }
}
if (-not $healthy) {
    Invoke-Compose @('logs', '--tail=150', 'backend')
    throw "Performance backend did not become healthy at $healthUrl"
}

if (-not $SkipSeed) {
    $previousScale = $env:PERF_SCALE
    $previousSeed = $env:PERF_SEED
    $previousManifest = $env:PERF_MANIFEST
    try {
        $env:PERF_SCALE = $Scale.ToString()
        $env:PERF_SEED = $Seed.ToString()
        $env:PERF_MANIFEST = $manifestPath
        Push-Location (Join-Path $repositoryRoot 'backend')
        & .\gradlew performanceSeed
        if ($LASTEXITCODE -ne 0) {
            throw 'performanceSeed failed'
        }
    } finally {
        Pop-Location
        $env:PERF_SCALE = $previousScale
        $env:PERF_SEED = $previousSeed
        $env:PERF_MANIFEST = $previousManifest
    }
}

if (-not (Test-Path -LiteralPath $manifestPath)) {
    throw "Performance manifest is missing: $manifestPath"
}

$scenarios = if ($Scenario -eq 'all') {
    @('smoke', 'rental-browse', 'image-burst', 'mixed-api', 'stress')
} else {
    @($Scenario)
}

foreach ($currentScenario in $scenarios) {
    $timestamp = [DateTimeOffset]::UtcNow.ToString('yyyyMMdd-HHmmss')
    $arguments = @(
        '--profile', 'tools', 'run', '--rm',
        '-e', "BASE_URL=$BaseUrl",
        '-e', "API_BASE_URL=$ApiBaseUrl",
        '-e', 'PERF_MANIFEST=/results/manifest.json'
    )
    if ($Validation) {
        $arguments += @('-e', 'PERF_VALIDATION=true')
    }
    $arguments += @(
        'k6', 'run',
        '--summary-export', "/results/$currentScenario-$timestamp.json",
        "/scripts/$currentScenario.js"
    )
    Write-Host "Running k6 scenario: $currentScenario"
    Invoke-Compose $arguments
}
