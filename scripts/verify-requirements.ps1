$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot

if (Test-Path -LiteralPath (Join-Path $projectRoot 'app\src\main\res\layout')) {
    throw 'A layout directory exists, but this project requires a code-only UI.'
}

$requiredFiles = @(
    'app\src\main\res\values\strings.xml',
    'app\src\main\res\values-en\strings.xml',
    'app\src\main\res\values-ja\strings.xml',
    'app\src\main\res\drawable\flag_japan.png',
    'app\src\main\res\mipmap-anydpi-v26\ic_launcher.xml'
)

foreach ($file in $requiredFiles) {
    if (-not (Test-Path -LiteralPath (Join-Path $projectRoot $file))) {
        throw "Missing required file: $file"
    }
}

$source = Get-Content -LiteralPath (Join-Path $projectRoot 'app\src\main\java\com\songjunyi\multilingualhelloworld\MainActivity.kt') -Raw
foreach ($marker in @('AppLanguage.CHINESE', 'AppLanguage.JAPANESE')) {
    if (-not $source.Contains($marker)) {
        throw "Source is missing required marker: $marker"
    }
}

$defaultStrings = Get-Content -LiteralPath (Join-Path $projectRoot 'app\src\main\res\values\strings.xml') -Raw -Encoding utf8
if (-not $defaultStrings.Contains('2024110131')) {
    throw 'Student identity is missing from default string resources.'
}

Write-Host 'PASS: languages, flag, icon, code-only UI, and student identity are ready.'
