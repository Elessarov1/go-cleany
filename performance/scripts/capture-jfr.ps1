[CmdletBinding()]
param(
    [ValidatePattern('^[a-zA-Z0-9-]+$')]
    [string]$Name = 'profile',
    [ValidateRange(5, 3600)]
    [int]$DurationSeconds = 30
)

$ErrorActionPreference = 'Stop'
$repositoryRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$composeFile = Join-Path $repositoryRoot 'performance\compose.perf.yaml'
$resultsDirectory = Join-Path $repositoryRoot 'performance\results'
$timestamp = [DateTimeOffset]::UtcNow.ToString('yyyyMMdd-HHmmss')
$fileName = "$Name-$timestamp.jfr"
$containerPath = "/tmp/$fileName"
$localPath = Join-Path $resultsDirectory $fileName
$recordingName = "loco-$Name-$timestamp"

New-Item -ItemType Directory -Force -Path $resultsDirectory | Out-Null
$containerId = (& docker compose -f $composeFile ps -q backend).Trim()
if (-not $containerId) {
    throw 'The loco-perf backend container is not running'
}

$recordingStarted = $false
try {
    # Corretto 25 on the Docker Desktop overlay filesystem cannot copy a disk-backed
    # recording. An in-memory recording followed by an explicit stop is portable.
    & docker compose -f $composeFile exec -T backend jcmd 1 JFR.start `
        "name=$recordingName" settings=profile disk=false
    if ($LASTEXITCODE -ne 0) {
        throw 'Could not start JFR recording'
    }
    $recordingStarted = $true

    Start-Sleep -Seconds $DurationSeconds
    & docker compose -f $composeFile exec -T backend jcmd 1 JFR.stop `
        "name=$recordingName" "filename=$containerPath"
    if ($LASTEXITCODE -ne 0) {
        throw 'Could not stop JFR recording'
    }
    $recordingStarted = $false

    & docker compose -f $composeFile exec -T backend jfr summary $containerPath
    if ($LASTEXITCODE -ne 0) {
        throw 'JFR summary failed'
    }
    & docker cp "${containerId}:$containerPath" $localPath
    if ($LASTEXITCODE -ne 0) {
        throw 'Could not copy JFR recording'
    }
} finally {
    if ($recordingStarted) {
        & docker compose -f $composeFile exec -T backend jcmd 1 JFR.stop `
            "name=$recordingName" | Out-Null
    }
}
Write-Host "JFR recording saved to $localPath"
