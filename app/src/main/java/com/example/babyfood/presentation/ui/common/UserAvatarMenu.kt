package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
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
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.domain.model.AuthState
import kotlinx.coroutines.launch

/**
 * 用户头像和菜单组件
 *
 * 显示用户头像和下拉菜单，包含：
 * - 退出登录
 * - 个人设置
 * - 切换宝宝
 *
 * @param authRepository 认证仓库
 * @param babyRepository 宝宝仓库
 * @param onSettingsClick 个人设置点击回调
 * @param onSwitchBabyClick 切换宝宝点击回调
 * @param onLogoutClick 退出登录点击回调
 * @param modifier 修饰符
 */
@Composable
fun UserAvatarMenu(
    authRepository: AuthRepository,
    babyRepository: BabyRepository,
    onSettingsClick: () -> Unit,
    onSwitchBabyClick: () -> Unit,
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var babyCount by remember { mutableStateOf(0) }
    var showSingleBabyToast by remember { mutableStateOf(false) }

    val authState by authRepository.getAuthState().collectAsState(initial = AuthState.NotLoggedIn)

    // 获取宝宝数量
    androidx.compose.runtime.LaunchedEffect(Unit) {
        babyRepository.getAllBabies().collect { babies ->
            babyCount = babies.size
        }
    }

    // 显示单宝宝提示
    if (showSingleBabyToast) {
        androidx.compose.material3.Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                androidx.compose.material3.TextButton(
                    onClick = { showSingleBabyToast = false }
                ) {
                    Text("确定")
                }
            }
        ) {
            Text("当前只有一个宝宝")
        }
    }

    // 用户头像按钮
    androidx.compose.material3.IconButton(
        onClick = { expanded = true },
        modifier = modifier.size(40.dp)
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

        // 切换宝宝
        DropdownMenuItem(
            text = { Text("切换宝宝") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "切换宝宝"
                )
            },
            onClick = {
                expanded = false
                if (babyCount > 1) {
                    onSwitchBabyClick()
                } else {
                    showSingleBabyToast = true
                }
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