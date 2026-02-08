package com.example.babyfood.presentation.ui.user

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.presentation.theme.*

/**
 * 修改密码页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit = {},
    onChangeSuccess: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    android.util.Log.d("ChangePasswordScreen", "========== ChangePasswordScreen 开始渲染 ==========")

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isChanging by remember { mutableStateOf(false) }
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isValid = validatePasswordForm(oldPassword, newPassword, confirmPassword)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "修改密码",
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
                actions = {
                    IconButton(
                        onClick = {
                            if (isValid && !isChanging) {
                                isChanging = true
                                viewModel.changePassword(oldPassword, newPassword)
                                onChangeSuccess()
                            }
                        },
                        enabled = isValid && !isChanging
                    ) {
                        if (isChanging) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "保存",
                                tint = if (isValid) Primary else TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PageBackground)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 提示信息
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "为了您的账号安全，修改密码需要先验证旧密码",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                // 旧密码输入框
                PasswordInputCard(
                    label = "旧密码",
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    visible = oldPasswordVisible,
                    onVisibleChange = { oldPasswordVisible = it },
                    isError = false,
                    supportingText = null
                )

                // 新密码输入框
                PasswordInputCard(
                    label = "新密码",
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    visible = newPasswordVisible,
                    onVisibleChange = { newPasswordVisible = it },
                    isError = newPassword.isNotEmpty() && newPassword.length < 6,
                    supportingText = if (newPassword.isNotEmpty() && newPassword.length < 6) {
                        "密码至少需要6个字符"
                    } else {
                        "密码至少需要6个字符"
                    }
                )

                // 确认密码输入框
                PasswordInputCard(
                    label = "确认新密码",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    visible = confirmPasswordVisible,
                    onVisibleChange = { confirmPasswordVisible = it },
                    isError = confirmPassword.isNotEmpty() && confirmPassword != newPassword,
                    supportingText = if (confirmPassword.isNotEmpty() && confirmPassword != newPassword) {
                        "两次输入的密码不一致"
                    } else {
                        "请再次输入新密码"
                    }
                )
            }
        }
    }
}

/**
 * 密码输入卡片
 */
@Composable
private fun PasswordInputCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    isError: Boolean,
    supportingText: String?
) {
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
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = {
                Text(
                    text = label,
                    color = TextSecondary
                )
            },
            placeholder = {
                Text(
                    text = "请输入${label}",
                    color = TextTertiary
                )
            },
            singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onVisibleChange(!visible) }) {
                    Icon(
                        imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (visible) "隐藏密码" else "显示密码",
                        tint = TextSecondary
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(),
            isError = isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            supportingText = {
                if (supportingText != null) {
                    Text(
                        text = supportingText,
                        color = if (isError) Color.Red else TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        )
    }
}

/**
 * 验证密码表单
 */
private fun validatePasswordForm(
    oldPassword: String,
    newPassword: String,
    confirmPassword: String
): Boolean {
    // 旧密码不能为空
    if (oldPassword.isEmpty()) return false

    // 新密码不能为空
    if (newPassword.isEmpty()) return false

    // 新密码至少需要6个字符
    if (newPassword.length < 6) return false

    // 确认密码不能为空
    if (confirmPassword.isEmpty()) return false

    // 两次输入的密码必须一致
    if (newPassword != confirmPassword) return false

    // 新密码不能与旧密码相同
    if (oldPassword == newPassword) return false

    return true
}