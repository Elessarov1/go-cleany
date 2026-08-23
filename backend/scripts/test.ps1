[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$proxyImage = 'alpine/socat@sha256:3d9e7966201dd3a065df591020a09fd3c70845de7e7086e3531ea69db774406b'
$proxyName = "go-cleany-testcontainers-proxy-$PID"
$backendPath = Split-Path -Parent $PSScriptRoot
$previousDockerHost = [Environment]::GetEnvironmentVariable('DOCKER_HOST', 'Process')
$proxyStarted = $false
$testExitCode = 1

Push-Location $backendPath
try {
    & docker version --format '{{.Server.Version}}' | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw 'Docker Desktop is unavailable. Start it and wait until docker version shows the Server section.'
    }

    $existingProxy = & docker ps -a --filter "name=^/$proxyName$" --format '{{.Names}}'
    if ($existingProxy) {
        throw "Unexpected existing Docker proxy container: $existingProxy"
    }

    & docker run `
        --detach `
        --rm `
        --name $proxyName `
        --publish '127.0.0.1::2375' `
        --volume '/var/run/docker.sock:/var/run/docker.sock' `
        $proxyImage `
        -dd `
        'TCP-LISTEN:2375,fork,reuseaddr' `
        'UNIX-CONNECT:/var/run/docker.sock' | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw 'Failed to start the temporary Testcontainers Docker proxy.'
    }
    $proxyStarted = $true

    $publishedEndpoint = (& docker port $proxyName '2375/tcp').Trim()
    $portMatch = [regex]::Match($publishedEndpoint, ':(\d+)$')
    if (-not $portMatch.Success) {
        throw "Cannot determine the temporary Docker proxy port from: $publishedEndpoint"
    }

    $env:DOCKER_HOST = "tcp://127.0.0.1:$($portMatch.Groups[1].Value)"
    $proxyReady = $false
    for ($attempt = 0; $attempt -lt 20; $attempt++) {
        & docker version --format '{{.Server.Version}}' 2>$null | Out-Null
        if ($LASTEXITCODE -eq 0) {
            $proxyReady = $true
            break
        }
        Start-Sleep -Milliseconds 100
    }
    if (-not $proxyReady) {
        throw 'The temporary Testcontainers Docker proxy did not become ready.'
    }

    & .\gradlew.bat test
    $testExitCode = $LASTEXITCODE
} finally {
    if ([string]::IsNullOrEmpty($previousDockerHost)) {
        Remove-Item Env:DOCKER_HOST -ErrorAction SilentlyContinue
    } else {
        $env:DOCKER_HOST = $previousDockerHost
    }

    if ($proxyStarted) {
        & docker rm --force $proxyName | Out-Null
    }
    Pop-Location
}

exit $testExitCode
