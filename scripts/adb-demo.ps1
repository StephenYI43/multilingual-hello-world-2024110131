param(
    [string]$Adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$apk = Join-Path $projectRoot 'app\build\outputs\apk\debug\app-debug.apk'

if (-not (Test-Path -LiteralPath $Adb)) { throw "找不到 adb：$Adb" }
if (-not (Test-Path -LiteralPath $apk)) { throw "请先构建 APK：$apk" }

& $Adb devices -l
& $Adb shell ls /
& $Adb install -r $apk
& $Adb shell am force-stop com.songjunyi.multilingualhelloworld
& $Adb shell am start -n com.songjunyi.multilingualhelloworld/.MainActivity

Write-Host '应用已安装并启动。可用以下命令模拟点击：'
Write-Host "  $Adb shell input tap <x> <y>"

