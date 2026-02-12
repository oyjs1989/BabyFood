package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.domain.model.AuthState

/**
 * 用户头像和菜单组件
 *
 * 显示用户头像和下拉菜单，包含：
 * - 积分余额显示（独立显示，不在头像上）
 * - 个人设置
 * - 退出登录
 *
 * @param authRepository 认证仓库
 * @param currentBalance 当前积分余额
 * @param onSettingsClick 个人设置点击回调
 * @param onLogoutClick 退出登录点击回调
 * @param onPointsClick 积分页面点击回调
 * @param modifier 修饰符
 */
@Composable
fun UserAvatarMenu(
    authRepository: AuthRepository,
    currentBalance: Int = 0,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit = {},
    onPointsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val authState by authRepository.getAuthState().collectAsState(initial = AuthState.NotLoggedIn)

    // 使用Box作为根容器，确保DropdownMenu锚点正确
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        // 水平排列：积分显示 + 用户头像
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 积分显示（独立组件：icon + 数字）
            if (authState is AuthState.LoggedIn) {
                androidx.compose.material3.Surface(
                    onClick = onPointsClick,
                    modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "积分",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentBalance.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            // 用户头像按钮（无badge）
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "用户头像",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 用户菜单 - 添加offset使其显示在正确的位置
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = androidx.compose.ui.unit.DpOffset(x = 0.dp, y = 4.dp)
        ) {
            // 积分
            DropdownMenuItem(
                text = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("积分")
                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )
                        Text(
                            text = "$currentBalance",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "积分"
                    )
                },
                onClick = {
                    expanded = false
                    onPointsClick()
                }
            )

            // 个人设置
            DropdownMenuItem(
                text = { Text("个人设置") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "个人设置"
                    )
                },
                onClick = {
                    expanded = false
                    onSettingsClick()
                }
            )

            // 退出登录
            DropdownMenuItem(
                text = { Text("退出登录") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "退出登录"
                    )
                },
                onClick = {
                    expanded = false
                    onLogoutClick()
                }
            )
        }
    }
}