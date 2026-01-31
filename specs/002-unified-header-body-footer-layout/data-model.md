# Data Model: 统一页面 Header 布局样式

**Feature**: 统一页面 Header 布局样式
**Date**: 2026-01-31
**Status**: Complete

## Overview

本功能主要创建可复用的 Header 组件，涉及的数据模型包括 Header 配置、认证状态（已存在）、用户菜单选项等。不涉及数据库变更。

## Data Entities

### 1. AppHeaderConfig

**Purpose**: 定义 Header 的配置参数，控制不同页面的行为

**Fields**:
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `currentRoute` | `String?` | 当前页面路由，用于判断是否为登录/注册页面 | 可选 |
| `onAppLogoClick` | `() -> Unit` | 应用名称点击回调 | 必填 |
| `showBackButton` | `Boolean` | 是否显示返回按钮 | 默认 false |

**Relationships**:
- 无直接关系
- 依赖导航系统获取 `currentRoute`

**State Transitions**:
- 无状态，纯配置数据类

---

### 2. LoginState

**Purpose**: Header 右侧登录状态的显示状态

**Fields**:
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `isAuthenticated` | `Boolean` | 用户是否已登录 | 必填 |
| `user` | `User?` | 用户信息（已登录时） | 可选 |
| `isLoading` | `Boolean` | 是否正在加载登录状态 | 默认 false |
| `hasError` | `Boolean` | 是否加载失败 | 默认 false |
| `lastKnownState` | `LoginState?` | 最后已知的登录状态 | 可选 |

**Relationships**:
- 关联 `User` 实体（已存在）
- 派生自 `AuthState`（已存在）

**State Transitions**:
```
Loading → Authenticated / Unauthenticated
Authenticated → Loading (重试时)
Unauthenticated → Loading (重试时)
Any → Error (网络失败)
Error → Loading (重试)
```

---

### 3. UserMenuItem

**Purpose**: 用户菜单中的菜单项

**Fields**:
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | `String` | 菜单项唯一标识 | 必填，非空 |
| `title` | `String` | 菜单项标题 | 必填，非空 |
| `icon` | `ImageVector` | 菜单项图标 | 必填 |
| `onClick` | `() -> Unit` | 点击回调 | 必填 |
| `enabled` | `Boolean` | 是否启用 | 默认 true |
| `visible` | `Boolean` | 是否可见 | 默认 true |

**Relationships**:
- 无直接关系

**State Transitions**:
- 无状态，纯数据类

---

### 4. UserMenuConfig

**Purpose**: 用户菜单的配置

**Fields**:
| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `menuItems` | `List<UserMenuItem>` | 菜单项列表 | 必填，非空 |
| `showAvatar` | `Boolean` | 是否显示用户头像 | 默认 true |
| `onAvatarClick` | `(() -> Unit)?` | 头像点击回调 | 可选 |

**Relationships**:
- 包含多个 `UserMenuItem`

**State Transitions**:
- 无状态，纯配置数据类

---

## Existing Entities Used

### 1. User (已存在)

**Purpose**: 用户信息

**Fields**:
- `id`: 用户 ID
- `username`: 用户名
- `email`: 邮箱
- `avatarUrl`: 头像 URL

**Usage**: 在 Header 中显示用户头像和用户菜单

---

### 2. AuthState (已存在)

**Purpose**: 认证状态

**Fields**:
- `Authenticated`: 已登录状态，包含 `User`
- `Unauthenticated`: 未登录状态
- `Loading`: 加载中状态

**Usage**: Header 根据 `AuthState` 显示不同的登录状态

---

### 3. Baby (已存在)

**Purpose**: 宝宝信息

**Fields**:
- `id`: 宝宝 ID
- `name`: 宝宝姓名
- `avatarUrl`: 头像 URL

**Usage**: 用于判断是否显示"切换宝宝"选项

---

## Validation Rules

### AppHeaderConfig
- `onAppLogoClick` 必须非空
- `currentRoute` 可以为 null（首页或其他页面）

### LoginState
- 如果 `isAuthenticated` 为 true，`user` 必须非空
- 如果 `isLoading` 为 true，其他状态字段被忽略

### UserMenuItem
- `id` 必须唯一
- `title` 不能为空
- `icon` 不能为 null

### UserMenuConfig
- `menuItems` 不能为空列表

---

## Business Rules

1. **登录状态显示**:
   - 已登录：显示用户头像
   - 未登录：显示"登录"按钮
   - 加载中：显示加载指示器
   - 加载失败：显示最后已知状态 + 重试按钮

2. **用户菜单选项**:
   - 始终显示：退出登录、个人设置、切换宝宝
   - 点击"切换宝宝"时：
     - 如果有多个宝宝：显示宝宝选择对话框
     - 如果只有一个宝宝：显示提示"当前只有一个宝宝"

3. **登录/注册页面特殊行为**:
   - 登录页面：Header 右侧显示"注册"按钮
   - 注册页面：Header 右侧显示"登录"按钮
   - 其他页面：Header 右侧显示正常登录状态

4. **应用名称点击**:
   - 所有页面点击应用名称跳转到首页（今日餐单页面）

---

## No Database Changes

本功能不涉及数据库变更，所有数据模型均为内存中的配置和状态对象。

---

## Summary

- 新增配置数据类：`AppHeaderConfig`, `UserMenuItem`, `UserMenuConfig`
- 新增状态模型：`LoginState`（可选，可直接使用 `AuthState`）
- 复用现有实体：`User`, `AuthState`, `Baby`
- 无数据库变更
- 无 API 契约变更

**Next Step**: Phase 1 - 生成快速入门指南和更新 Agent 上下文。