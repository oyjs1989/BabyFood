# Quickstart Guide: 统一页面 Header 布局样式

**Feature**: 统一页面 Header 布局样式
**Branch**: `002-unified-header-body-footer-layout`
**Date**: 2026-01-31

## Overview

本指南帮助开发者快速在页面中集成统一的 Header 组件，确保所有页面具有一致的 Header 布局和交互行为。

## Prerequisites

- ✅ Jetpack Compose 已配置
- ✅ Material Design 3 已配置
- ✅ Hilt 依赖注入已配置
- ✅ AuthRepository 已实现

## Step 1: 创建 AppHeader 组件

在 `app/src/main/java/com/example/babyfood/presentation/ui/common/` 目录下创建 `AppHeader.kt`：

```kotlin
@Composable
fun AppHeader(
    config: AppHeaderConfig,
    authRepository: AuthRepository,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
    onSettings: () -> Unit = {},
    onSwitchBaby: () -> Unit = {}
) {
    val authState by authRepository.authState.collectAsState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .windowInsetsPadding(WindowInsets.systemBars),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: App name
            TextButton(
                onClick = config.onAppLogoClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "BabyFood",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Right: Login state
            when (config.currentRoute) {
                "login" -> RegisterButton()
                "register" -> LoginButton()
                else -> LoginStateIndicator(
                    authState = authState,
                    onLogout = onLogout,
                    onSettings = onSettings,
                    onSwitchBaby = onSwitchBaby
                )
            }
        }
    }
}
```

## Step 2: 创建辅助组件

创建 `LoginButton.kt`、`RegisterButton.kt` 和 `UserAvatarMenu.kt`：

```kotlin
// LoginButton.kt
@Composable
fun LoginButton(
    onLoginClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onLoginClick,
        modifier = modifier.semantics {
            contentDescription = "登录"
            role = Role.Button
        }
    ) {
        Text("登录")
    }
}

// RegisterButton.kt
@Composable
fun RegisterButton(
    onRegisterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onRegisterClick,
        modifier = modifier.semantics {
            contentDescription = "注册"
            role = Role.Button
        }
    ) {
        Text("注册")
    }
}
```

## Step 3: 在页面中集成 AppHeader

在任意页面的 Composable 函数中集成 AppHeader：

```kotlin
@Composable
fun TodayMenuScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val config = remember {
        AppHeaderConfig(
            currentRoute = "home",
            onAppLogoClick = {
                // 已在首页，可以什么都不做
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppHeader(
            config = config,
            authRepository = viewModel.authRepository,
            onLogout = {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            onSettings = {
                navController.navigate("settings")
            },
            onSwitchBaby = {
                // 显示宝宝选择对话框
            }
        )

        // Page content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            // Your page content here
        }
    }
}
```

## Step 4: 处理网络失败场景

在 `LoginStateIndicator` 组件中处理网络失败：

```kotlin
@Composable
fun LoginStateIndicator(
    authState: AuthState,
    hasError: Boolean,
    lastKnownState: AuthState?,
    onRetry: () -> Unit = {},
    onLogout: () -> Unit = {},
    onSettings: () -> Unit = {},
    onSwitchBaby: () -> Unit = {}
) {
    when {
        hasError && lastKnownState != null -> {
            // 显示最后已知状态 + 重试按钮
            Row {
                if (lastKnownState is AuthState.Authenticated) {
                    UserAvatarMenu(
                        user = lastKnownState.user,
                        onLogout = onLogout,
                        onSettings = onSettings,
                        onSwitchBaby = onSwitchBaby
                    )
                } else {
                    LoginButton()
                }
                IconButton(
                    onClick = onRetry,
                    modifier = Modifier.semantics {
                        contentDescription = "重试"
                        role = Role.Button
                    }
                ) {
                    Icon(Icons.Default.Refresh, "重试")
                }
            }
        }
        authState is AuthState.Authenticated -> {
            UserAvatarMenu(
                user = authState.user,
                onLogout = onLogout,
                onSettings = onSettings,
                onSwitchBaby = onSwitchBaby
            )
        }
        authState is AuthState.Unauthenticated -> {
            LoginButton()
        }
        authState is AuthState.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
```

## Step 5: 测试

运行应用，验证以下场景：

1. ✅ 所有页面都显示统一的 Header
2. ✅ Header 左侧显示 "BabyFood"
3. ✅ Header 右侧显示正确的登录状态
4. ✅ 点击应用名称跳转到首页
5. ✅ 点击登录按钮跳转到登录页面
6. ✅ 点击用户头像显示菜单
7. ✅ 用户菜单包含退出登录、个人设置、切换宝宝选项
8. ✅ 网络失败时显示重试按钮
9. ✅ 登录页面显示"注册"按钮
10. ✅ 注册页面显示"登录"按钮

## Common Issues

### Issue: Header 不显示

**Solution**: 确保页面使用 `Column` 布局，并且 `AppHeader` 是第一个子元素。

### Issue: 登录状态不更新

**Solution**: 确保 `AuthRepository` 的 `authState` 是 `StateFlow` 或 `LiveData`，并使用 `collectAsState()` 观察。

### Issue: Header 高度不正确

**Solution**: 确保 `Surface` 的 `height` 设置为 `56.dp`，并使用 `windowInsetsPadding(WindowInsets.systemBars)` 处理系统栏。

## Next Steps

1. 为所有页面集成 AppHeader 组件
2. 编写单元测试和 UI 测试
3. 验证无障碍访问功能
4. 在不同屏幕尺寸上测试响应式布局

## Additional Resources

- [Jetpack Compose Layout](https://developer.android.com/jetpack/compose/layouts)
- [Material Design 3](https://m3.material.io/)
- [Android Accessibility](https://developer.android.com/guide/topics/ui/accessibility)