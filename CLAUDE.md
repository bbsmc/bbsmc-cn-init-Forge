# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个 Minecraft Forge Mod 项目 (YtonGame-HostingMenu)，目标版本为 Minecraft 1.20.1，使用 Forge 47.4.0 和 Mixin 技术。

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

# 数据生成
./gradlew runData
```

## 架构

- **主类**: `moe.ytonidc.ytongame_hostingmenu.Ytongame_hostingmenu` - Mod 入口点，MODID 为 `ytongame_hostingmenu`
- **配置**: `Config.java` - 使用 ForgeConfigSpec 管理客户端配置，包含 `purchaseUrl` 购买链接配置项
- **Mixin**: 配置文件位于 `src/main/resources/ytongame_hostingmenu.mixins.json`，Mixin 类应放在 `moe.ytonidc.ytongame_hostingmenu.mixin` 包下

### 核心功能模块

- **client/HostingTab.java** - 服务器套餐展示标签页，实现 `Tab` 接口，在创建世界界面显示
- **client/HostingPackage.java** - 套餐数据类，定义5种服务器套餐（入门型、标准型、灵活型、悦享型、曜石型）
- **mixin/CreateWorldScreenMixin.java** - 注入创建世界界面，处理 HostingTab 的渲染
- **mixin/TabNavigationBarBuilderMixin.java** - 拦截标签栏构建，动态添加 Hosting 标签页

## 关键配置文件

- `gradle.properties` - Mod 版本、Minecraft/Forge 版本等核心属性
- `src/main/resources/META-INF/mods.toml` - Mod 元数据（使用 gradle.properties 中的变量）

## 注意事项

- Java 版本: 17
- 构建输出会自动执行 reobfuscation (`reobfJar`)
- 数据生成资源输出到 `src/generated/resources/`
