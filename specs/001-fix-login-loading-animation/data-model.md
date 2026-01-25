# Data Model: 修复登录按钮加载动画

**Feature**: 001-fix-login-loading-animation
**Date**: 2026-01-25
**Phase**: Phase 1 - Design & Contracts

## Overview

本功能主要涉及 UI 状态管理，不引入新的数据库实体或持久化数据模型。所有状态变更均在 ViewModel 的 `LoginUiState` 中管理。

## Entities

### LoginUiState (UI State)

登录页面的 UI 状态，由 `LoginViewModel` 管理。

**Fields**:

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `account` | String | 用户输入的账号（手机号或邮箱） | 非空，符合手机号或邮箱格式 |
| `password` | String | 用户输入的密码 | 非空，长度 ≥ 6 |
| `isPasswordVisible` | Boolean | 密码是否可见 | - |
| `rememberMe` | Boolean | 是否记住登录状态 | - |
| `isLoading` | Boolean | 登录请求是否正在进行中 | - |
| `isFormValid` | Boolean | 表单是否有效（账号和密码都有效） | - |
| `accountError` | String? | 账号错误信息（如果有） | - |
| `passwordError` | String? | 密码错误信息（如果有） | - |
| `error` | String? | 通用错误信息（如果有） | - |

**State Transitions**:

```
Initial (isLoading=false)
    ↓
[User clicks login]
    ↓
Loading (isLoading=true, button disabled)
    ↓
[Success/Failure/Timeout/Cancelled]
    ↓
Completed (isLoading=false, button enabled)
```

**Derived State**:

- `isFormValid`: 根据 `account` 和 `password` 的验证结果计算得出
- 按钮的 `enabled` 状态: `!isLoading && isFormValid`

## Validation Rules

### Account Validation

- **非空检查**: 账号不能为空
- **格式检查**: 必须符合以下任一格式
  - 手机号: `^1[3-9]\d{9}$` (中国大陆手机号)
  - 邮箱: `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$`

### Password Validation

- **非空检查**: 密码不能为空
- **长度检查**: 密码长度必须 ≥ 6 位

## Relationships

本功能不涉及实体之间的关系，所有状态封装在 `LoginUiState` 中。

## Persistence

本功能不涉及数据持久化，所有状态在内存中管理。

## API Contracts

本功能不涉及新的 API 接口，使用现有的登录 API。

### Existing Login API

**Endpoint**: `POST /api/auth/login`

**Request**:
```json
{
  "account": "string",
  "password": "string"
}
```

**Success Response** (200):
```json
{
  "success": true,
  "token": "string",
  "user": {
    "id": 1,
    "account": "string",
    "name": "string"
  }
}
```

**Error Response** (400/401/500):
```json
{
  "success": false,
  "error": "string"
}
```

**Timeout**: 超时时间由 OkHttp 配置（连接超时 10 秒，读取超时 30 秒）

## Error Handling

### Error Types

| Error Type | Description | User Message |
|------------|-------------|--------------|
| `SocketTimeoutException` | 网络连接超时 | "请求超时，请检查网络后重试" |
| `TimeoutException` | 请求读取超时 | "请求超时，请检查网络后重试" |
| `IOException` | 网络错误 | "网络错误，请检查网络连接" |
| `HttpException` | HTTP 错误 (4xx/5xx) | 根据状态码显示具体错误 |
| `UnknownHostException` | 无法解析主机名 | "网络错误，请检查网络连接" |

### Error Display

- 使用 `ErrorDialog` 组件显示错误信息
- 错误信息显示后，用户点击确认后清除错误状态
- 错误状态清除后，按钮恢复可用状态

## Accessibility

### Screen Reader Support

- 加载状态: `contentDescription = "登录中"`
- 登录按钮: `contentDescription = "登录按钮"`
- 密码可见性切换: `contentDescription = "显示密码" / "隐藏密码"`
- 记住我: `contentDescription = "记住我，已选中" / "记住我，未选中"`

### Color Contrast

- 加载动画颜色: 白色 (#FFFFFF)
- 按钮背景色: 主题色 (Primary)
- 对比度: 符合 WCAG AA 标准（至少 4.5:1）

## Performance Considerations

### Loading Animation Performance

- 使用 Compose 的 `CircularProgressIndicator`，性能优化良好
- 动画帧率: 60 fps（由 Compose 自动管理）
- 内存占用: 最小（标准组件）

### State Management Performance

- 使用 `StateFlow` 进行状态管理，自动优化重组
- 仅在状态变化时触发 UI 更新
- 避免不必要的重组

## Security Considerations

### Password Handling

- 密码在内存中存储，不在日志中输出
- 密码传输使用 HTTPS 加密
- 登录成功后，密码立即从内存中清除

### Request Cancellation

- 用户按返回键时，取消正在进行的登录请求
- 避免内存泄漏和资源浪费

## Testing Strategy

### Unit Tests

- 测试 `LoginUiState` 的状态转换
- 测试表单验证逻辑
- 测试错误处理逻辑

### UI Tests

- 测试加载动画显示
- 测试按钮禁用状态
- 测试超时错误处理
- 测试返回键取消登录
- 测试可访问性（屏幕阅读器）

### Integration Tests

- 测试登录请求的完整流程
- 测试网络异常情况的处理