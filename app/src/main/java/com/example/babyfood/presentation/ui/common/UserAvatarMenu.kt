package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.domain.model.AuthState

/**
 * 用户头像和菜单组件
 *
 * 显示用户头像和下拉菜单，包含：
 * - 退出登录
 * - 个人设置
 *
 * @param authRepository 认证仓库
 * @param onSettingsClick 个人设置点击回调
 * @param onLogoutClick 退出登录点击回调
 * @param modifier 修饰符
 */
@Composable
fun UserAvatarMenu(
    authRepository: AuthRepository,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val authState by authRepository.getAuthState().collectAsState(initial = AuthState.NotLoggedIn)

    // 使用 Box 包裹按钮和菜单，避免菜单展开时影响布局
    Box(modifier = modifier) {
        // 用户头像按钮
        androidx.compose.material3.IconButton(
            onClick = { expanded = true },
            modifier = Modifier.size(40.dp)
        ) {
            if (authState is AuthState.LoggedIn) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "用户头像",
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "用户头像",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // 用户菜单
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
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