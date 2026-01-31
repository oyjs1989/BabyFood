# Implementation Plan: 统一页面 Header 布局样式

**Branch**: `002-unified-header-body-footer-layout` | **Date**: 2026-01-31 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/002-unified-header-body-footer-layout/spec.md`

## Summary

为 BabyFood Android 应用的所有页面创建统一的 Header 布局组件，左侧显示应用名称 "BabyFood"，右侧显示登录状态（已登录显示用户头像，未登录显示登录按钮）。技术方案包括：创建可复用的 AppHeader 组件、支持响应式适配、完整的无障碍访问支持、网络失败处理和登录/注册页面的特殊行为。预期实现 Header 组件代码复用率 100%，维护成本降低 80%。

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Jetpack Compose (BOM), Material Design 3, AndroidX Core KTX 1.12.0, Coil (for image loading)
**Storage**: Room (现有，不涉及数据库变更)
**Testing**: JUnit 4.13.2, AndroidX Test Extensions 1.1.5, Espresso 3.5.1
**Target Platform**: Android 7.0+ (API 24+), minSdk 24, targetSdk 34, compileSdk 34
**Project Type**: Mobile (Android)
**Performance Goals**: Header 响应时间 < 0.5s，屏幕方向改变响应时间 < 0.5s
**Constraints**:
- 必须支持响应式布局，自动适配不同屏幕尺寸
- 必须支持完整的无障碍访问（屏幕阅读器、键盘导航、高对比度、动态字体大小、触摸目标最小 48dp）
- 必须确保 Header 固定在页面顶部，不随内容滚动
- 必须与现有导航系统和用户认证系统集成
- 必须处理网络不稳定时的登录状态加载失败
**Scale/Scope**: 约 10+ 个主要页面需要集成统一 Header（今日餐单、食谱管理、计划管理、宝宝档案、体检记录、生长曲线、仓库管理、AI 设置、登录、注册等）

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

当前项目 constitution 文件为模板，未定义具体原则。基于 Android 开发最佳实践，应用以下标准：

| Principle | Status | Notes |
|-----------|--------|-------|
| Material Design 3 遵循 | ✅ Pass | 统一 Header 将严格遵循 Material Design 3 规范 |
| 无障碍访问支持 | ✅ Pass | 已明确要求完全无障碍访问支持（FR-015 到 FR-020） |
| 代码复用性 | ✅ Pass | 核心目标是创建可复用 Header 组件（FR-014） |
| 向后兼容性 | ✅ Pass | 支持 Android 7.0+ (API 24+) |
| 测试覆盖 | ✅ Pass | 需要为 Header 组件编写单元测试和 UI 测试 |

**Gate Result (Phase 0)**: ✅ PASS - 无违规项，可以继续

**Gate Result (Phase 1)**: ✅ PASS - 设计已验证，符合所有原则

## Project Structure

### Documentation (this feature)

```
specs/002-unified-header-body-footer-layout/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (repository root)

```
app/src/main/java/com/example/babyfood/
├── presentation/
│   └── ui/
│       └── common/      # NEW: 统一布局组件目录
│           ├── AppHeader.kt                 # NEW: 统一 Header 组件
│           ├── AppHeaderConfig.kt           # NEW: Header 配置数据类
│           ├── LoginButton.kt               # NEW: 登录/注册按钮组件
│           ├── UserAvatarMenu.kt            # NEW: 用户头像和菜单组件
│           └── NetworkErrorBadge.kt         # NEW: 网络错误徽章组件
│
├── [existing pages to be updated]
│   ├── home/...              # UPDATE: 集成 AppHeader
│   ├── recipes/...           # UPDATE: 集成 AppHeader
│   ├── plans/...             # UPDATE: 集成 AppHeader
│   ├── baby/...              # UPDATE: 集成 AppHeader
│   ├── health/...            # UPDATE: 集成 AppHeader
│   ├── growth/...            # UPDATE: 集成 AppHeader
│   ├── inventory/...         # UPDATE: 集成 AppHeader
│   ├── ai/...                # UPDATE: 集成 AppHeader
│   ├── auth/...              # UPDATE: 集成 AppHeader（特殊行为）
│
├── domain/model/
│   └── AuthState.kt          # EXISTING: 认证状态模型
│
└── data/auth/
    └── AuthRepository.kt     # EXISTING: 认证仓库（用于获取登录状态）

app/src/test/java/com/example/babyfood/
└── presentation/
    └── ui/
        └── common/           # NEW: Header 组件单元测试
            ├── AppHeaderTest.kt
            ├── LoginButtonTest.kt
            └── UserAvatarMenuTest.kt

app/src/androidTest/java/com/example/babyfood/
└── presentation/
    └── ui/
        └── common/           # NEW: Header 组件 UI 测试
            └── AppHeaderUiTest.kt
```

**Structure Decision**: 在现有 Android 项目结构中添加 `presentation/ui/common/` 目录存放统一 Header 组件。所有现有页面将集成 `AppHeader` 组件。

## Complexity Tracking

*无违规项，无需填写*