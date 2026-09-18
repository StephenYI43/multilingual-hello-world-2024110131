# 多语言 HelloWorld

Android Studio 课程作业，作者：**2024110131 宋珺熠**。

## 作业功能

- 中文、English、Français 三种语言可在应用内即时切换
- 首次启动自动匹配系统语言，也可以手动覆盖并恢复“跟随系统”
- 中国、美国、法国三面国旗会同时显示在语言控制中心
- 启动图标会随当前语言切换为中、英、法三个版本
- 2026 风格深色玻璃拟态、霓虹科技 UI 与轻量动效
- UI 完全由 Kotlin 代码创建，不含 `res/layout` 布局文件
- 提供 ADB 安装、启动、查看根目录及模拟点击的脚本与说明

## 运行

1. 使用 Android Studio 打开项目。
2. 等待 Gradle Sync 完成。
3. 启动 API 23 或更高版本的模拟器。
4. 点击 **Run app**。

命令行构建（推荐，脚本会临时映射英文盘符，规避 Windows 下中文路径导致的 Gradle 单元测试类加载问题）：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\build.ps1
```

生成的 APK 位于 `app/build/outputs/apk/debug/app-debug.apk`。

## 项目结构

- `MainActivity.kt`：纯代码 UI 与交互逻辑
- `values*/strings.xml`：中、英、法三种语言资源
- `drawable-nodpi/flag_*.png`：中国、美国、法国国旗资源
- `mipmap-*`：三套可动态切换的自定义应用图标
- `scripts/adb-demo.ps1`：ADB 演示脚本

## 本机模拟器说明

已创建带完整可视界面的 API 35 标准模拟器 `HelloWorld_Visible_API35`。如果启动时提示缺少硬件加速，请以管理员身份运行 Android SDK 中的
`extras/google/Android_Emulator_Hypervisor_Driver/silent_install.bat`，然后重新启动模拟器。

三面国旗图片来自 FlagCDN：`cn.png`、`us.png`、`fr.png`。
