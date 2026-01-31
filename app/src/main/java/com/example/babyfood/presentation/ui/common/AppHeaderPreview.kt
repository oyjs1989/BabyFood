package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.babyfood.domain.model.AuthState
import com.example.babyfood.domain.model.User
import com.example.babyfood.presentation.theme.BabyFoodTheme
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * AppHeader Preview
 * 
 * 这个文件包含了 AppHeader 组件的预览，可以直接在 Android Studio 中查看，
 * 无需构建整个应用。
 * 
 * 使用方法：
 * 1. 打开这个文件
 * 2. 点击代码右上角的 "Design" 标签
 * 3. 或者在 @Preview 注解上点击 "Split" 或 "Design" 按钮
 */

/**
 * 未登录状态预览
 */
@Preview(
    name = "未登录状态",
    showBackground = true,
    widthDp = 400,
    heightDp = 100,
    group = "登录状态"
)
@Composable
fun AppHeaderPreview_NotLoggedIn() {
    var selectedOption by remember { mutableStateOf(0) }
    val options = listOf("未登录", "已登录", "加载中", "登录页面", "注册页面")
    
    BabyFoodTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            SimpleHeaderPreview(authState = when (selectedOption) {
                0 -> AuthState.NotLoggedIn
                1 -> AuthState.LoggedIn(
                    User(
                        id = 1,
                        phone = "13800138000",
                        email = "test@example.com",
                        nickname = "测试用户",
                        avatar = "",
                        createdAt = "",
                        updatedAt = "",
                        isEmailVerified = true,
                        isPhoneVerified = true
                    )
                )
                2 -> AuthState.Loading
                3 -> AuthState.NotLoggedIn
                else -> AuthState.NotLoggedIn
            }, currentRoute = when (selectedOption) {
                3 -> "login"
                4 -> "register"
                else -> "home"
            })
        }
    }
}

/**
 * 已登录状态预览
 */
@Preview(
    name = "已登录状态",
    showBackground = true,
    widthDp = 400,
    heightDp = 100,
    group = "登录状态"
)
@Composable
fun AppHeaderPreview_LoggedIn() {
    BabyFoodTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            SimpleHeaderPreview(
                authState = AuthState.LoggedIn(
                    User(
                        id = 1,
                        phone = "13800138000",
                        email = "test@example.com",
                        nickname = "测试用户",
                        avatar = "",
                        createdAt = "",
                        updatedAt = "",
                        isEmailVerified = true,
                        isPhoneVerified = true
                    )
                ),
                currentRoute = "home"
            )
        }
    }
}

/**
 * 登录页面预览
 */
@Preview(
    name = "登录页面",
    showBackground = true,
    widthDp = 400,
    heightDp = 100,
    group = "特殊页面"
)
@Composable
fun AppHeaderPreview_LoginPage() {
    BabyFoodTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            SimpleHeaderPreview(
                authState = AuthState.NotLoggedIn,
                currentRoute = "login"
            )
        }
    }
}

/**
 * 注册页面预览
 */
@Preview(
    name = "注册页面",
    showBackground = true,
    widthDp = 400,
    heightDp = 100,
    group = "特殊页面"
)
@Composable
fun AppHeaderPreview_RegisterPage() {
    BabyFoodTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            SimpleHeaderPreview(
                authState = AuthState.NotLoggedIn,
                currentRoute = "register"
            )
        }
    }
}

/**
 * 加载中状态预览
 */
@Preview(
    name = "加载中状态",
    showBackground = true,
    widthDp = 400,
    heightDp = 100,
    group = "加载状态"
)
@Composable
fun AppHeaderPreview_Loading() {
    BabyFoodTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            SimpleHeaderPreview(
                authState = AuthState.Loading,
                currentRoute = "home"
            )
        }
    }
}

/**
 * 小屏幕预览 (320dp)
 */
@Preview(
    name = "小屏幕 (320dp)",
    showBackground = true,
    widthDp = 320,
    heightDp = 100,
    group = "响应式布局"
)
@Composable
fun AppHeaderPreview_SmallScreen() {
    BabyFoodTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            SimpleHeaderPreview(
                authState = AuthState.NotLoggedIn,
                currentRoute = "home"
            )
        }
    }
}

/**
 * 中等屏幕预览 (400dp)
 */
@Preview(
    name = "中等屏幕 (400dp)",
    showBackground = true,
    widthDp = 400,
    heightDp = 100,
    group = "响应式布局"
)
@Composable
fun AppHeaderPreview_MediumScreen() {
    BabyFoodTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            SimpleHeaderPreview(
                authState = AuthState.NotLoggedIn,
                currentRoute = "home"
            )
        }
    }
}

/**
 * 大屏幕预览 (600dp)
 */
@Preview(
    name = "大屏幕 (600dp)",
    showBackground = true,
    widthDp = 600,
    heightDp = 100,
    group = "响应式布局"
)
@Composable
fun AppHeaderPreview_LargeScreen() {
    BabyFoodTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            SimpleHeaderPreview(
                authState = AuthState.NotLoggedIn,
                currentRoute = "home"
            )
        }
    }
}

// ============ 简化的预览组件 ============

/**
 * 简化的 Header 预览组件
 * 用于展示 UI 效果，无需完整的依赖注入
 */
@Composable
private fun SimpleHeaderPreview(
    authState: AuthState,
    currentRoute: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：应用名称
        androidx.compose.material3.TextButton(
            onClick = {},
            modifier = Modifier.weight(1f)
        ) {
            androidx.compose.material3.Text(
                text = "BabyFood",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }

        // 右侧：登录状态
        when (authState) {
            is AuthState.LoggedIn -> {
                // 已登录：显示用户图标
                androidx.compose.material3.IconButton(onClick = {}) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "用户头像",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            is AuthState.NotLoggedIn -> {
                // 未登录：显示登录/注册按钮
                if (currentRoute == "register") {
                    RegisterButton(onClick = {})
                } else {
                    LoginButton(onClick = {})
                }
            }
            is AuthState.Loading -> {
                // 加载中：显示进度指示器
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.padding(8.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                )
            }
            is AuthState.Error -> {
                // 错误：显示登录按钮
                LoginButton(onClick = {})
            }
        }
    }
}