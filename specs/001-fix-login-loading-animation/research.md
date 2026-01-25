# Research: 修复登录按钮加载动画

**Feature**: 001-fix-login-loading-animation
**Date**: 2026-01-25
**Phase**: Phase 0 - Outline & Research

## Research Task 1: Material Design 3 CircularProgressIndicator

### Decision
使用 Material Design 3 标准的 `CircularProgressIndicator` 组件，配置为 24dp 大小、白色、2dp stroke width。

### Rationale
- Material Design 3 提供了标准的 `CircularProgressIndicator` 组件，无需自定义实现
- 24dp 是 Material Design 推荐的标准尺寸，适合按钮内部使用
- 白色与主题色 (Primary) 背景形成最佳对比
- 2dp stroke width 是默认值，视觉效果清晰且不过于厚重

### Alternatives Considered
1. **自定义 Canvas 绘制**: 复杂度高，维护成本大，不符合 Material Design 规范
2. **LinearProgressIndicator**: 不适合按钮内部，视觉上不如圆形进度指示器清晰
3. **第三方库**: 增加依赖，标准组件已满足需求

### Implementation Details
```kotlin
CircularProgressIndicator(
    modifier = Modifier.size(24.dp),
    color = Color.White,
    strokeWidth = 2.dp
)
```

---

## Research Task 2: Compose 按钮状态管理

### Decision
使用 `enabled` 属性控制按钮禁用状态，结合 ViewModel 的 `isLoading` 状态进行管理。

### Rationale
- Compose Button 组件提供 `enabled` 属性，自动处理禁用状态的视觉反馈
- ViewModel 的 `isLoading` 状态作为单一数据源，确保状态一致性
- 防止重复点击通过禁用按钮实现，无需额外的防抖逻辑

### Alternatives Considered
1. **手动防抖逻辑**: 增加复杂度，禁用按钮更简洁直接
2. **使用 LaunchedEffect 防抖**: 过度设计，不符合需求
3. **点击计数器**: 不必要的状态管理，禁用按钮已足够

### Implementation Details
```kotlin
Button(
    onClick = { viewModel.login() },
    enabled = !uiState.isLoading && uiState.isFormValid,
    // ...
) {
    if (uiState.isLoading) {
        CircularProgressIndicator(...)
    } else {
        Text("登录")
    }
}
```

---

## Research Task 3: Compose 可访问性 (Accessibility)

### Decision
使用 `Modifier.semantics` 和 `contentDescription` 为加载动画添加辅助文本。

### Rationale
- Compose 提供了 `semantics` API，可以为组件添加语义信息
- `contentDescription` 属性会被屏幕阅读器朗读
- 辅助文本"登录中"简洁明了，符合用户预期

### Alternatives Considered
1. **使用 Text 组件**: 不适合加载动画，会增加视觉干扰
2. **自定义 AccessibilityDelegate**: 过度设计，标准 API 已满足需求
3. **忽略可访问性**: 不符合无障碍设计原则

### Implementation Details
```kotlin
CircularProgressIndicator(
    modifier = Modifier
        .size(24.dp)
        .semantics {
            contentDescription = "登录中"
        },
    color = Color.White,
    strokeWidth = 2.dp
)
```

---

## Research Task 4: 网络请求超时处理

### Decision
在 Retrofit 配置中设置超时时间（连接超时 10 秒，读取超时 30 秒），在 ViewModel 中捕获超时异常并显示错误提示。

### Rationale
- OkHttp 提供了标准的超时配置，无需额外实现
- 10 秒连接超时和 30 秒读取超时是行业最佳实践
- 捕获 `SocketTimeoutException` 和 `TimeoutException` 并显示友好错误提示

### Alternatives Considered
1. **使用协程 withTimeout**: 需要在每个请求中添加，不如全局配置统一
2. **忽略超时**: 用户体验差，可能导致应用无响应
3. **自动重试**: 增加复杂度，用户可能不期望自动重试

### Implementation Details
```kotlin
// NetworkModule.kt
val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

// LoginViewModel.kt
try {
    // 登录请求
} catch (e: SocketTimeoutException) {
    _uiState.update { it.copy(error = "请求超时，请检查网络后重试", isLoading = false) }
} catch (e: TimeoutException) {
    _uiState.update { it.copy(error = "请求超时，请检查网络后重试", isLoading = false) }
}
```

---

## Research Task 5: 返回键拦截

### Decision
使用 Compose 的 `BackHandler` API，在加载状态下拦截返回键并取消登录请求。

### Rationale
- Compose 提供了 `BackHandler` API，专门用于拦截返回键
- 条件拦截（仅在 `isLoading = true` 时拦截）符合用户预期
- 取消登录请求后自动恢复按钮状态

### Alternatives Considered
1. **使用 Activity.onBackPressed()**: 已废弃，不符合 Compose 最佳实践
2. **使用 OnBackPressedDispatcher**: 需要手动管理生命周期，不如 BackHandler 简洁
3. **忽略返回键**: 用户体验差，用户无法取消操作

### Implementation Details
```kotlin
// LoginScreen.kt
BackHandler(enabled = uiState.isLoading) {
    viewModel.cancelLogin()
}

// LoginViewModel.kt
fun cancelLogin() {
    // 取消登录请求
    _uiState.update { it.copy(isLoading = false) }
}
```

---

## Summary

所有研究任务已完成，关键决策如下：

1. **加载动画**: 使用 Material Design 3 标准 `CircularProgressIndicator` (24dp, 白色)
2. **按钮状态**: 使用 `enabled` 属性 + ViewModel `isLoading` 状态管理
3. **可访问性**: 使用 `Modifier.semantics` + `contentDescription = "登录中"`
4. **超时处理**: OkHttp 全局超时配置 + ViewModel 异常捕获
5. **返回键拦截**: 使用 `BackHandler` API 条件拦截

无未解决的 NEEDS CLARIFICATION，可以进入 Phase 1 设计阶段。