$ErrorActionPreference = 'Stop'
$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$buildDrive = 'R:'
$studioJdk = 'C:\Program Files\Android\Android Studio\jbr'
$androidSdk = "$env:LOCALAPPDATA\Android\Sdk"

if (-not (Test-Path -LiteralPath $studioJdk)) {
    throw "Android Studio JDK not found: $studioJdk"
}
if (-not (Test-Path -LiteralPath $androidSdk)) {
    throw "Android SDK not found: $androidSdk"
}
if (Test-Path -LiteralPath "$buildDrive\") {
    throw "$buildDrive is already in use. Change buildDrive in scripts/build.ps1."
}

$env:JAVA_HOME = $studioJdk
$env:ANDROID_HOME = $androidSdk

try {
    & subst.exe $buildDrive $projectRoot
    if ($LASTEXITCODE -ne 0) { throw 'Unable to create the temporary ASCII build drive.' }

    Push-Location "$buildDrive\"
    powershell -ExecutionPolicy Bypass -File '.\scripts\verify-requirements.ps1'
    if ($LASTEXITCODE -ne 0) { throw 'Requirement verification failed.' }

    & '.\gradlew.bat' --no-daemon clean testDebugUnitTest assembleDebug
    if ($LASTEXITCODE -ne 0) { throw 'Gradle build failed.' }
}
finally {
    if ((Get-Location).Path.StartsWith($buildDrive, [System.StringComparison]::OrdinalIgnoreCase)) {
        Pop-Location
    }
    if (Test-Path -LiteralPath "$buildDrive\") {
        & subst.exe $buildDrive /d
    }
}

Write-Host 'Build complete: app\build\outputs\apk\debug\app-debug.apk'
