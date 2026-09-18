# ADB 调试步骤

以下命令覆盖作业要求的“查看根目录、安装并启动 APP、模拟触摸按钮”。运行前请在 Android Studio 的 Device Manager 中启动模拟器。

```powershell
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"

# 1. 确认设备并显示模拟器根目录
& $adb devices -l
& $adb shell ls /

# 2. 安装并启动应用
& $adb install -r .\app\build\outputs\apk\debug\app-debug.apk
& $adb shell am start -n com.songjunyi.multilingualhelloworld/.MainActivity

# 3. 模拟点击语言按钮（坐标按模拟器分辨率微调）
& $adb shell input tap 540 1250
```

也可以直接执行 `powershell -ExecutionPolicy Bypass -File .\scripts\adb-demo.ps1` 完成前两步。

