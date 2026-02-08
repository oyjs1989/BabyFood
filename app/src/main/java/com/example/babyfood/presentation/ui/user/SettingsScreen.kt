package com.example.babyfood.presentation.ui.user

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.domain.model.User
import com.example.babyfood.presentation.theme.*

/**
 * 个人设置主页面
 * iOS 风格列表式设置页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onEditNickname: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onSelectAvatar: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    android.util.Log.d("SettingsScreen", "========== SettingsScreen 开始渲染 ==========")

    val uiState by viewModel.uiState.collectAsState()
    val operationState by viewModel.operationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 显示操作结果
    LaunchedEffect(operationState) {
        when (val state = operationState) {
            is OperationState.Success -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetOperationState()
            }
            is OperationState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetOperationState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "个人设置",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "返回",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PageBackground)
                .padding(paddingValues)
        ) {
            when (uiState) {
                is SettingsUiState.Loading -> {
                    LoadingView()
                }
                is SettingsUiState.NotLoggedIn -> {
                    NotLoggedInView()
                }
                is SettingsUiState.Success -> {
                    SettingsContent(
                        user = (uiState as SettingsUiState.Success).user,
                        onEditNickname = onEditNickname,
                        onChangePassword = onChangePassword,
                        onSelectAvatar = onSelectAvatar,
                        onLogout = {
                            viewModel.logout {
                                onLogout()
                            }
                        },
                        onThemeChange = { theme ->
                            viewModel.updateTheme(theme)
                        }
                    )
                }
            }
        }
    }
}

/**
 * 设置页面内容
 */
@Composable
private fun SettingsContent(
    user: User,
    onEditNickname: () -> Unit,
    onChangePassword: () -> Unit,
    onSelectAvatar: () -> Unit,
    onLogout: () -> Unit,
    onThemeChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 用户信息卡片
        UserCard(
            user = user,
            onSelectAvatar = onSelectAvatar,
            onEditNickname = onEditNickname
        )

        // 账号安全分组
        SettingsGroup(title = "账号安全") {
            SettingsItem(
                icon = null,
                title = "修改密码",
                subtitle = null,
                value = null,
                onClick = onChangePassword,
                showArrow = true
            )
        }

        // 偏好设置分组
        SettingsGroup(title = "偏好设置") {
            ThemeSettingsItem(
                currentTheme = user.theme,
                onThemeChange = onThemeChange
            )
        }

        // 退出登录按钮
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SurfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "退出登录",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * 用户信息卡片
 */
@Composable
private fun UserCard(
    user: User,
    onSelectAvatar: () -> Unit,
    onEditNickname: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 头像
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(SurfaceVariant)
                    .clickable(onClick = onSelectAvatar),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (user.nickname.isNotEmpty()) user.nickname[0].toString() else "U",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }

            // 用户信息
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = user.nickname.ifEmpty { "未设置昵称" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = user.phone ?: user.email ?: "未绑定手机号或邮箱",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            // 编辑按钮
            IconButton(onClick = onEditNickname) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "编辑",
                    tint = TextSecondary
                )
            }
        }
    }
}

/**
 * 设置分组
 */
@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 分组标题
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // 分组内容
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * 设置项
 */
@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    title: String,
    subtitle: String?,
    value: String?,
    onClick: () -> Unit,
    showArrow: Boolean = true
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 图标（如果有）
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 标题和副标题
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // 值（如果有）
            if (value != null) {
                Text(
                    text = value,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 箭头（如果有）
            if (showArrow) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * 主题设置项
 */
@Composable
private fun ThemeSettingsItem(
    currentTheme: String,
    onThemeChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        SettingsItem(
            icon = null,
            title = "主题设置",
            subtitle = null,
            value = when (currentTheme) {
                "light" -> "浅色"
                "dark" -> "深色"
                "auto" -> "跟随系统"
                else -> "浅色"
            },
            onClick = { expanded = true },
            showArrow = true
        )

        // 下拉菜单
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(200.dp)
        ) {
            ThemeMenuItem(
                title = "浅色",
                value = "light",
                currentTheme = currentTheme,
                onClick = {
                    onThemeChange("light")
                    expanded = false
                }
            )
            ThemeMenuItem(
                title = "深色",
                value = "dark",
                currentTheme = currentTheme,
                onClick = {
                    onThemeChange("dark")
                    expanded = false
                }
            )
            ThemeMenuItem(
                title = "跟随系统",
                value = "auto",
                currentTheme = currentTheme,
                onClick = {
                    onThemeChange("auto")
                    expanded = false
                }
            )
        }
    }
}

/**
 * 主题菜单项
 */
@Composable
private fun ThemeMenuItem(
    title: String,
    value: String,
    currentTheme: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = title,
                color = if (currentTheme == value) Primary else TextPrimary
            )
        },
        onClick = onClick,
        trailingIcon = if (currentTheme == value) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Primary
                )
            }
        } else null
    )
}

/**
 * 加载视图
 */
@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Primary
        )
    }
}

/**
 * 未登录视图
 */
@Composable
private fun NotLoggedInView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "请先登录",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
    }
}