# Quick Start Guide: 修复登录按钮加载动画

**Feature**: 001-fix-login-loading-animation
**Date**: 2026-01-25
**Phase**: Phase 1 - Design & Contracts

## Overview

本指南提供快速实现登录按钮加载动画修复的步骤说明。主要修改集中在 `LoginScreen.kt` 和 `LoginViewModel.kt` 两个文件。

## Prerequisites

- Android Studio Arctic Fox 或更高版本
- Kotlin 2.0.21
- Jetpack Compose 1.5.0+
- Material Design 3
- Hilt 依赖注入

## Implementation Steps

### Step 1: 更新 LoginScreen.kt

#### 1.1 添加 BackHandler 导入

```kotlin
import androidx.activity.compose.BackHandler
```

#### 1.2 添加返回键处理

在 `LoginScreen` Composable 函数内，添加 `BackHandler`：

```kotlin
// 拦截返回键（仅在加载状态下）
BackHandler(enabled = uiState.isLoading) {
    viewModel.cancelLogin()
}
```

#### 1.3 更新登录按钮的加载动画

找到登录按钮的 `Button` 组件，更新 `if (uiState.isLoading)` 分支：

```kotlin
Button(
    onClick = {
        Log.d("LoginScreen", "========== 登录按钮被点击 ==========")
        Log.d("LoginScreen", "当前状态: isLoading=${uiState.isLoading}, isFormValid=${uiState.isFormValid}")
        viewModel.login()
    },
    modifier = Modifier
        .fillMaxWidth()
        .height(52.dp),
    enabled = !uiState.isLoading && uiState.isFormValid,
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = Primary,
        contentColor = Color.White,
        disabledContainerColor = Outline,
        disabledContentColor = OnSurfaceVariant
    )
) {
    if (uiState.isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(24.dp)
                .semantics {
                    contentDescription = "登录中"
                },
            color = Color.White,
            strokeWidth = 2.dp
        )
    } else {
        Text(
            text = "登录",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
```

**关键变更**:
- 添加 `Modifier.semantics { contentDescription = "登录中" }` 以支持屏幕阅读器
- 确认 `CircularProgressIndicator` 的参数：`size(24.dp)`, `color = Color.White`, `strokeWidth = 2.dp`

### Step 2: 更新 LoginViewModel.kt

#### 2.1 添加 cancelLogin() 方法

在 `LoginViewModel` 类中添加取消登录的方法：

```kotlin
/**
 * 取消登录请求
 */
fun cancelLogin() {
    Log.d("LoginViewModel", "========== 取消登录请求 ==========")
    _uiState.update { it.copy(isLoading = false) }
    // TODO: 如果需要，可以在这里取消实际的协程任务
}
```

#### 2.2 更新 login() 方法的错误处理

在 `login()` 方法中，添加超时异常的捕获：

```kotlin
fun login() {
    Log.d("LoginViewModel", "========== 开始登录 ==========")
    Log.d("LoginViewModel", "账号: ${uiState.account}")

    _uiState.update { it.copy(isLoading = true, error = null) }

    viewModelScope.launch {
        try {
            // 调用登录 API
            val response = authRepository.login(
                account = uiState.account.trim(),
                password = uiState.password
            )

            when (response) {
                is LoginResult.Success -> {
                    Log.d("LoginViewModel", "✓ 登录成功")
                    _authState.value = AuthState.LoggedIn(response.user)
                    _uiState.update { it.copy(isLoading = false) }
                }
                is LoginResult.Error -> {
                    Log.d("LoginViewModel", "❌ 登录失败: ${response.message}")
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            }
        } catch (e: SocketTimeoutException) {
            Log.e("LoginViewModel", "❌ 连接超时: ${e.message}")
            _uiState.update { it.copy(isLoading = false, error = "请求超时，请检查网络后重试") }
        } catch (e: TimeoutException) {
            Log.e("LoginViewModel", "❌ 读取超时: ${e.message}")
            _uiState.update { it.copy(isLoading = false, error = "请求超时，请检查网络后重试") }
        } catch (e: IOException) {
            Log.e("LoginViewModel", "❌ 网络错误: ${e.message}")
            _uiState.update { it.copy(isLoading = false, error = "网络错误，请检查网络连接") }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "❌ 未知错误: ${e.message}", e)
            _uiState.update { it.copy(isLoading = false, error = "登录失败，请稍后重试") }
        }
    }

    Log.d("LoginViewModel", "========== 登录流程结束 ==========")
}
```

**关键变更**:
- 添加 `SocketTimeoutException` 和 `TimeoutException` 的捕获
- 为超时错误显示友好的用户提示

#### 2.3 添加必要的导入

```kotlin
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException
import java.io.IOException
```

### Step 3: 验证 NetworkModule.kt 配置

确认 `NetworkModule.kt` 中的 OkHttp 配置包含超时设置：

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .addInterceptor(loggingInterceptor)
    .build()
```

如果没有超时配置，请添加上述配置。

## Testing

### Manual Testing

1. **加载动画显示测试**:
   - 打开登录页面
   - 输入有效的账号和密码
   - 点击登录按钮
   - 验证按钮显示白色旋转圆形进度指示器（24dp）
   - 验证按钮处于禁用状态

2. **超时错误处理测试**:
   - 断开网络连接
   - 点击登录按钮
   - 验证显示超时错误提示
   - 验证按钮恢复可用状态

3. **返回键取消测试**:
   - 点击登录按钮
   - 在加载状态下按返回键
   - 验证登录请求被取消
   - 验证按钮恢复可用状态

4. **可访问性测试**:
   - 启用屏幕阅读器（如 TalkBack）
   - 点击登录按钮
   - 验证屏幕阅读器朗读"登录中"

### Automated Testing

#### Unit Tests

```kotlin
@Test
fun `login should set isLoading to true`() {
    // Given
    viewModel.onAccountChange("13800138000")
    viewModel.onPasswordChange("password123")

    // When
    viewModel.login()

    // Then
    assertTrue(viewModel.uiState.value.isLoading)
}

@Test
fun `cancelLogin should set isLoading to false`() {
    // Given
    viewModel.onAccountChange("13800138000")
    viewModel.onPasswordChange("password123")
    viewModel.login()

    // When
    viewModel.cancelLogin()

    // Then
    assertFalse(viewModel.uiState.value.isLoading)
}
```

#### UI Tests

```kotlin
@Test
fun `login button should show loading indicator when isLoading is true`() {
    // Given
    composeTestRule.setContent {
        LoginScreen()
    }

    // When
    composeTestRule.onNodeWithText("登录").performClick()

    // Then
    composeTestRule.onNodeWithContentDescription("登录中").assertIsDisplayed()
}
```

## Troubleshooting

### Issue: 加载动画仍然显示为白点

**Solution**:
- 确认 `CircularProgressIndicator` 的 `color` 参数设置为 `Color.White`
- 确认按钮的 `containerColor` 不是白色
- 检查主题配置中的 `Primary` 颜色是否正确

### Issue: 返回键无法取消登录

**Solution**:
- 确认 `BackHandler` 的 `enabled` 参数设置为 `uiState.isLoading`
- 确认 `cancelLogin()` 方法正确更新 `isLoading` 状态
- 检查是否有其他组件拦截了返回键

### Issue: 超时错误不显示

**Solution**:
- 确认 `NetworkModule.kt` 中配置了超时时间
- 确认 `login()` 方法中捕获了 `SocketTimeoutException` 和 `TimeoutException`
- 检查错误信息是否正确显示在 `ErrorDialog` 中

## Checklist

- [ ] 更新 `LoginScreen.kt` 添加 `BackHandler`
- [ ] 更新 `LoginScreen.kt` 的加载动画实现
- [ ] 添加 `contentDescription = "登录中"` 支持屏幕阅读器
- [ ] 更新 `LoginViewModel.kt` 添加 `cancelLogin()` 方法
- [ ] 更新 `LoginViewModel.kt` 添加超时异常捕获
- [ ] 验证 `NetworkModule.kt` 的超时配置
- [ ] 手动测试加载动画显示
- [ ] 手动测试超时错误处理
- [ ] 手动测试返回键取消
- [ ] 手动测试可访问性
- [ ] 编写单元测试
- [ ] 编写 UI 测试

## Next Steps

完成实现后，运行 `/speckit.tasks` 生成详细的任务分解和实施清单。

## Resources

- [Material Design 3 - Progress Indicators](https://m3.material.io/components/progress-indicators/overview)
- [Jetpack Compose - BackHandler](https://developer.android.com/reference/kotlin/androidx/activity/compose/BackHandler)
- [Compose Semantics - Accessibility](https://developer.android.com/jetpack/compose/accessibility)
- [OkHttp - Interceptors](https://square.github.io/okhttp/interceptors/)