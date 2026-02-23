package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.domain.model.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 统一 Header 组件
 *
 * 为所有页面提供统一的 Header 布局：
 * - 左侧：应用名称 "BabyFood"
 * - 右侧：登录状态（用户头像或登录按钮）
 *
 * 支持响应式布局，自动适配不同屏幕尺寸和屏幕方向
 * 支持完整的无障碍访问
 *
 * @param config Header 配置
 * @param authRepository 认证仓库
 * @param onLoginClick 登录按钮点击回调
 * @param onRegisterClick 注册按钮点击回调
 * @param onSettingsClick 个人设置点击回调
 * @param onLogoutClick 退出登录点击回调
 * @param modifier 修饰符（可选）
 * @param windowInsets 系统栏内边距（可选）
 */
@Composable
fun AppHeader(
    config: AppHeaderConfig,
    authRepository: AuthRepository,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets(0, 0, 0, 0)
) {
    val authState by authRepository.getAuthState().collectAsState(initial = AuthState.NotLoggedIn)
    val configuration = LocalConfiguration.current

    // 根据屏幕宽度调整 padding
    val horizontalPadding = when {
        configuration.screenWidthDp < 360 -> 12.dp  // 小屏幕
        configuration.screenWidthDp < 600 -> 16.dp  // 中等屏幕
        else -> 24.dp  // 大屏幕
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(windowInsets)
            .semantics { heading() },
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧：应用名称
            TextButton(
                onClick = config.onAppLogoClick,
                modifier = Modifier.semantics {
                    heading()
                    contentDescription = "BabyFood"
                }
            ) {
                Text(
                    text = "BabyFood",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                )
            }

            // 右侧：登录状态
            when (authState) {
                is AuthState.LoggedIn -> {
                    // 已登录：显示用户头像和菜单
                    UserAvatarMenu(
                        authRepository = authRepository,
                        currentBalance = config.pointsBalance,
                        onSettingsClick = onSettingsClick,
                        onLogoutClick = onLogoutClick,
                        onPointsClick = config.onPointsClick
                    )
                }
                is AuthState.NotLoggedIn -> {
                    // 未登录：显示登录按钮
                    Row(modifier = Modifier.widthIn(max = 120.dp)) {
                        if (config.currentRoute == "register") {
                            RegisterButton(onClick = onRegisterClick)
                        } else {
                            LoginButton(onClick = onLoginClick)
                        }
                    }
                }
                is AuthState.Loading -> {
                    // 加载中：显示进度指示器
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(8.dp)
                            .semantics { contentDescription = "加载中" },
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is AuthState.Error -> {
                    // 错误：显示登录按钮
                    Row(modifier = Modifier.widthIn(max = 120.dp)) {
                        LoginButton(onClick = onLoginClick)
                    }
                }
            }
        }
    }
}