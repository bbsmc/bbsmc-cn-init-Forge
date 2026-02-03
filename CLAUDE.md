# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个 Minecraft Forge Mod 项目 (YtonGame-HostingMenu)，目标版本为 Minecraft 1.12.2，使用 Forge 14.23.5.2860 和 Mixin 技术。

## 常用命令

```bash
# 构建 Mod
./gradlew build

# 运行客户端测试
./gradlew runClient

# 运行服务端测试
./gradlew runServer

# 生成 IDE 运行配置
./gradlew genIntellijRuns    # IntelliJ IDEA
./gradlew genEclipseRuns     # Eclipse
```

## 架构

- **主类**: `moe.ytonidc.ytongame_hostingmenu.Ytongame_hostingmenu` - Mod 入口点，使用 `@Mod` 注解，MODID 为 `ytongame_hostingmenu`
- **配置**: `Config.java` - 使用 Forge Config 注解管理客户端配置，包含 `purchaseUrl`、`enableAds`、`chineseOnly` 配置项
- **Mixin**: 配置文件位于 `src/main/resources/ytongame_hostingmenu.mixins.json`，Mixin 类应放在 `moe.ytonidc.ytongame_hostingmenu.mixin` 包下

### 核心功能模块

- **client/HostingScreen.java** - 服务器套餐展示界面，继承 `GuiScreen`
- **client/HostingPackageList.java** - 套餐列表组件，继承 `GuiSlot`
- **client/HostingPackage.java** - 套餐数据类，支持从远程/本地 JSON 加载
- **client/MultiPlayerAdEntry.java** - 多人游戏服务器列表广告条目
- **client/RegionDetector.java** - 地区检测工具，判断是否显示广告
- **mixin/GuiCreateWorldMixin.java** - 注入创建世界界面，添加"联机开服"按钮
- **mixin/GuiWorldSelectionMixin.java** - 注入世界选择界面，添加"联机开服"按钮
- **mixin/ServerSelectionListMixin.java** - 注入服务器列表，添加广告条目

## 关键配置文件

- `gradle.properties` - Mod 版本等核心属性
- `src/main/resources/mcmod.info` - Mod 元数据
- `src/main/resources/hosting_packages.json` - 本地套餐数据

## 注意事项

- Java 版本: 8
- Gradle 版本: 8.5
- ForgeGradle 版本: 6.0+
- MCP Mappings: snapshot 20171003-1.12
- 构建输出会自动执行 reobfuscation (`reobfJar`)
- Mixin 需要在 JAR manifest 中配置 `TweakClass` 和 `MixinConfigs`
