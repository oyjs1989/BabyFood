package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.domain.model.AuthState
import kotlinx.coroutines.delay

/**
 * 登录状态指示器组件
 *
 * 显示当前登录状态：
 * - 已登录：显示用户头像
 * - 未登录：显示登录按钮
 * - 加载中：显示进度指示器
 * - 加载失败：显示最后已知状态 + 重试按钮（后续在 US3 中完善）
 *
 * @param authRepository 认证仓库
 * @param onLoginClick 登录按钮点击回调
 * @param onRegisterClick 注册按钮点击回调
 * @param currentRoute 当前路由（用于判断是否为登录/注册页面）
 * @param modifier 修饰符
 */
@Composable
fun LoginStateIndicator(
    authRepository: AuthRepository,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    val authState by authRepository.getAuthState().collectAsState(initial = AuthState.NotLoggedIn)

    // 首次加载时自动获取登录状态
    LaunchedEffect(Unit) {
        // 这里可以添加自动刷新逻辑（如果需要）
        delay(500)
    }

    when {
        isLoading -> {
            CircularProgressIndicator(
                modifier = modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        hasError -> {
            // 错误状态：显示最后已知状态 + 重试按钮（后续在 US3 中完善）
            Text(
                text = "加载失败",
                modifier = modifier,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        else -> {
            when (authState) {
                is AuthState.LoggedIn -> {
                    // 已登录：显示用户头像（后续在 US3 中实现完整功能）
                    Text(
                        text = "已登录",
                        modifier = modifier,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                is AuthState.NotLoggedIn -> {
                    // 未登录：显示登录按钮或注册按钮
                    if (currentRoute == "register") {
                        RegisterButton(onClick = onRegisterClick)
                    } else {
                        LoginButton(onClick = onLoginClick)
                    }
                }
                is AuthState.Loading -> {
                    CircularProgressIndicator(
                        modifier = modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is AuthState.Error -> {
                    LoginButton(onClick = onLoginClick)
                }
            }
        }
    }
}