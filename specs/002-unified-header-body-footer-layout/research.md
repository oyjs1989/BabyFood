# Research Findings: 统一页面 Header 布局样式

**Feature**: 统一页面 Header 布局样式
**Date**: 2026-01-31
**Status**: Complete

## Research Questions & Decisions

### 1. Jetpack Compose 响应式布局最佳实践

**Decision**: 使用 `WindowInsets` 和 `Modifier.weight()` 实现响应式布局，结合 `BoxWithConstraints` 处理不同屏幕尺寸。

**Rationale**:
- `WindowInsets` 自动处理系统栏（状态栏、导航栏）的适配
- `Modifier.weight()` 确保内容区域占据剩余空间
- `BoxWithConstraints` 允许根据屏幕尺寸调整布局参数
- 符合 Material Design 3 响应式设计原则

**Alternatives Considered**:
- 固定尺寸布局：无法适配不同屏幕尺寸
- ConstraintLayout：过度复杂，不适合简单的 Header 布局

**Implementation Notes**:
```kotlin
@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.systemBars
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(windowInsets)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App name (left)
            Text(
                text = "BabyFood",
                modifier = Modifier.weight(1f)
            )

            // Login state (right)
            LoginStateIndicator()
        }
    }
}
```

---

### 2. Material Design 3 TopAppBar 组件使用

**Decision**: 使用自定义 `Row` + `Surface` 组合而非 `TopAppBar` 组件，以获得更大的布局灵活性。

**Rationale**:
- 标准的 `TopAppBar` 组件布局固定，难以满足"左侧应用名称、右侧登录状态"的需求
- 自定义组合可以精确控制布局和间距
- 仍然遵循 Material Design 3 的视觉规范（高度、颜色、字体）

**Alternatives Considered**:
- `TopAppBar` 组件：布局受限，难以满足自定义需求
- `LargeTopAppBar`：过大，不适合所有页面

**Implementation Notes**:
- 使用 `Surface` 作为容器，支持 elevation 和背景色
- 使用 `Row` 实现左右布局
- 使用 `Arrangement.SpaceBetween` 确保两端对齐
- Header 高度遵循 Material Design 3 标准：56dp

---

### 3. 用户认证系统集成

**Decision**: 通过 `AuthRepository` 观察 `AuthState`，使用 `StateFlow` 或 `LiveData` 实现响应式更新。

**Rationale**:
- 项目已有 `AuthRepository` 和 `AuthState` 模型（见 IFLOW.md）
- 响应式数据流确保 Header 自动更新登录状态
- 符合 MVVM 架构模式

**Alternatives Considered**:
- 直接调用 AuthRepository 同步方法：无法自动更新
- 使用 SharedPreferences：不支持响应式更新

**Implementation Notes**:
```kotlin
@Composable
fun AppHeader(
    authRepository: AuthRepository,
    modifier: Modifier = Modifier
) {
    val authState by authRepository.authState.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("BabyFood")
        when (authState) {
            is AuthState.Authenticated -> UserAvatarMenu(authState.user)
            is AuthState.Unauthenticated -> LoginButton()
            is AuthState.Loading -> CircularProgressIndicator()
        }
    }
}
```

---

### 4. 网络状态处理和重试机制

**Decision**: 使用 `rememberCoroutineScope` 和 `LaunchedEffect` 实现自动重试，同时提供手动重试按钮。

**Rationale**:
- `LaunchedEffect` 在首次加载时自动获取登录状态
- 显示最后已知状态避免用户困惑
- 手动重试按钮提供用户控制权
- 符合 Material Design 错误处理最佳实践

**Alternatives Considered**:
- 仅自动重试：用户无法控制
- 仅手动重试：用户体验差

**Implementation Notes**:
```kotlin
@Composable
fun LoginStateIndicator(
    authRepository: AuthRepository,
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(false) }
    var lastKnownState by remember { mutableStateOf<AuthState?>(null) }
    var hasError by remember { mutableStateOf(false) }

    val authState by authRepository.authState.collectAsState()

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            hasError = false
        } catch (e: Exception) {
            hasError = true
        } finally {
            isLoading = false
        }
    }

    Box(modifier = modifier) {
        when {
            isLoading -> CircularProgressIndicator()
            hasError && lastKnownState != null -> {
                Row {
                    // 显示最后已知状态
                    if (lastKnownState is AuthState.Authenticated) {
                        UserAvatar((lastKnownState as AuthState.Authenticated).user)
                    } else {
                        LoginButton()
                    }
                    // 重试按钮
                    IconButton(onClick = { /* retry */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "重试")
                    }
                }
            }
            else -> {
                when (authState) {
                    is AuthState.Authenticated -> {
                        lastKnownState = authState
                        UserAvatarMenu((authState as AuthState.Authenticated).user)
                    }
                    is AuthState.Unauthenticated -> {
                        lastKnownState = authState
                        LoginButton()
                    }
                    else -> CircularProgressIndicator()
                }
            }
        }
    }
}
```

---

### 5. 用户菜单实现模式

**Decision**: 使用 `DropdownMenu` 组件实现用户菜单，支持动态选项（根据宝宝数量显示/隐藏"切换宝宝"选项）。

**Rationale**:
- `DropdownMenu` 是 Material Design 3 标准组件
- 自动处理弹出位置和动画
- 支持动态内容更新

**Alternatives Considered**:
- 自定义 Dialog：不符合 Material Design 规范
- BottomSheet：过于复杂，不适合简单菜单

**Implementation Notes**:
```kotlin
@Composable
fun UserAvatarMenu(
    user: User,
    babies: List<Baby>,
    onLogout: () -> Unit,
    onSettings: () -> Unit,
    onSwitchBaby: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "用户头像",
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("退出登录") },
                onClick = {
                    expanded = false
                    onLogout()
                }
            )
            DropdownMenuItem(
                text = { Text("个人设置") },
                onClick = {
                    expanded = false
                    onSettings()
                }
            )
            DropdownMenuItem(
                text = { Text("切换宝宝") },
                onClick = {
                    expanded = false
                    if (babies.size > 1) {
                        onSwitchBaby()
                    } else {
                        // 显示提示：当前只有一个宝宝
                    }
                }
            )
        }
    }
}
```

---

### 6. 无障碍访问支持

**Decision**: 遵循 WCAG AA 标准，为所有交互元素提供语义化描述。

**Rationale**:
- 确保屏幕阅读器用户能够理解和使用 Header
- 符合 Android 无障碍访问指南
- 法规要求（部分地区的可访问性法规）

**Implementation Notes**:
```kotlin
// 为按钮提供 contentDescription
IconButton(
    onClick = { /* action */ },
    modifier = Modifier.semantics {
        contentDescription = "登录"
        role = Role.Button
    }
) {
    Icon(Icons.Default.Login, contentDescription = null)
}

// 支持键盘导航
Button(
    onClick = { /* action */ },
    modifier = Modifier.focusable()
) {
    Text("登录")
}

// 确保触摸目标最小尺寸为 48dp
Button(
    onClick = { /* action */ },
    modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
) {
    Text("登录")
}
```

---

### 7. 登录/注册页面特殊行为

**Decision**: 通过 `AppHeaderConfig` 参数传递当前路由信息，在登录/注册页面显示特殊按钮。

**Rationale**:
- 保持 Header 组件的通用性
- 通过配置参数控制行为，避免创建多个 Header 变体
- 符合开闭原则

**Implementation Notes**:
```kotlin
data class AppHeaderConfig(
    val currentRoute: String? = null,
    val onAppLogoClick: () -> Unit = {}
)

@Composable
fun AppHeader(
    config: AppHeaderConfig,
    authRepository: AuthRepository,
    modifier: Modifier = Modifier
) {
    val currentRoute = config.currentRoute ?: LocalNavGraph.currentEntry?.destination?.route

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // App name
        TextButton(onClick = config.onAppLogoClick) {
            Text("BabyFood")
        }

        // Login state
        when (currentRoute) {
            "login" -> RegisterButton()
            "register" -> LoginButton()
            else -> LoginStateIndicator(authRepository)
        }
    }
}
```

---

## Summary

所有研究问题已解决，技术方案已确定：

1. ✅ 响应式布局：使用 `WindowInsets` 和 `Modifier.weight()`
2. ✅ Material Design 3：自定义 `Row` + `Surface` 组合
3. ✅ 认证集成：通过 `AuthRepository` 观察 `AuthState`
4. ✅ 网络处理：显示最后已知状态 + 重试按钮
5. ✅ 用户菜单：使用 `DropdownMenu` 组件
6. ✅ 无障碍访问：遵循 WCAG AA 标准
7. ✅ 登录/注册页面：通过 `AppHeaderConfig` 控制行为

**Next Step**: Phase 1 - 设计数据模型和快速入门指南。