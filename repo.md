# Baritone

## Repo简介

Baritone 是一个 Minecraft Java 模组的路径查找机器人系统。它是一个高性能的路径规划算法实现，用于在 Minecraft 世界中自动导航、挖掘、建造等任务。

**主要功能：**
- 高性能路径查找算法（比 MineBot 快 30 倍以上）
- 支持多种 Minecraft 版本（1.12.2 到 1.21.5）
- 支持 Forge、Fabric 和 NeoForge 模组加载器
- 提供完整的 API 供其他模组集成
- 支持多种导航模式：挖掘、建造、探索、跟随等

**技术栈：**
- Java
- Gradle 构建系统
- Minecraft Forge/Fabric API
- Mixin 技术用于代码注入

**项目结构：**
- `src/api/java/baritone/api/` - 公共 API 接口
- `src/main/java/baritone/` - 核心实现代码
- `src/launch/java/baritone/launch/` - 启动和 Mixin 相关代码
- `forge/` - Forge 模组实现
- `fabric/` - Fabric 模组实现
- `buildSrc/` - Gradle 构建脚本

**核心组件：**
- PathingBehavior - 路径查找行为
- LookBehavior - 视角控制行为
- InventoryBehavior - 物品栏管理行为
- 各种 Process（MineProcess、BuilderProcess、ExploreProcess 等）- 不同的功能处理流程

## 题目Prompt

I would like to add a new feature to this minecraft java mod, baritone. I want to add two settings options. Found in ./src/api/java/bairtone/api/settings.java. These will be #assume_horse and #assume_boat. the idea is that baritone will be able to navigate accounting for horse and boat travel. Additionally i'll ask that you create separate scripts for logic, navigation on boat, and on horse.

## PR链接

https://github.com/ncepudlgc/b1298_pre/pull/1
