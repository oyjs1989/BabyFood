package com.example.babyfood.presentation.ui.auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.activity.compose.BackHandler
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import com.example.babyfood.presentation.theme.components.BabyFoodPrimaryButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.LocalContentColor
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.domain.model.AuthState
import com.example.babyfood.presentation.theme.BackgroundLight
import com.example.babyfood.presentation.theme.Error
import com.example.babyfood.presentation.theme.OnBackground
import com.example.babyfood.presentation.theme.OnSurface
import com.example.babyfood.presentation.theme.OnSurfaceVariant
import com.example.babyfood.presentation.theme.Outline
import com.example.babyfood.presentation.theme.Primary
import com.example.babyfood.presentation.theme.Surface
import com.example.babyfood.presentation.theme.SurfaceVariant
import com.example.babyfood.presentation.ui.icons.AppIcons
import com.example.babyfood.presentation.ui.common.AppScaffold
import com.example.babyfood.presentation.ui.common.AppBarConfig
import com.example.babyfood.presentation.ui.common.AppBottomAction
import com.example.babyfood.presentation.ui.components.ErrorDialog

/**
 * 登录页面
 * 参考设计图：支持手机号和邮箱登录
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val authState by viewModel.authState.collectAsState()

    // 初始化日志
    LaunchedEffect(Unit) {
        Log.d("LoginScreen", "========== LoginScreen 初始化 ==========")
        Log.d("LoginScreen", "当前表单状态:")
        Log.d("LoginScreen", "  账号: '${uiState.account}'")
        Log.d("LoginScreen", "  密码长度: ${uiState.password.length}")
        Log.d("LoginScreen", "  表单有效: ${uiState.isFormValid}")
        Log.d("LoginScreen", "  加载中: ${uiState.isLoading}")
    }

    // 监听登录成功状态
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.LoggedIn -> {
                Log.d("LoginScreen", "登录成功，导航到首页")
                onLoginSuccess()
            }
            else -> {}
        }
    }

    // 监听表单状态变化
    LaunchedEffect(uiState.isFormValid) {
        Log.d("LoginScreen", "表单有效状态变化: ${uiState.isFormValid}")
    }

    // 拦截返回键（仅在加载状态下）
    BackHandler(enabled = uiState.isLoading) {
        viewModel.cancelLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        CompositionLocalProvider(LocalContentColor provides OnSurface) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
            // ========== 品牌区域 ==========
            Spacer(modifier = Modifier.height(40.dp))

            // 品牌图标（无背景色）
            AppIcons.AppLogo(
                size = 120.dp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 标题
            Text(
                text = "BabyFood",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 副标题
            Text(
                text = "智能餐单推荐 · 科学营养搭配",
                fontSize = 14.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ========== 表单区域 ==========
            // 账号输入框
            OutlinedTextField(
                value = uiState.account,
                onValueChange = {
                    viewModel.onAccountChange(it)
                    viewModel.clearAccountError()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(
                        text = "手机号或邮箱",
                        color = OnSurfaceVariant
                    )
                },
                placeholder = {
                    Text(
                        text = "请输入账号或邮箱",
                        color = OnSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = AppIcons.Account,
                        contentDescription = "账号",
                        tint = OnSurfaceVariant
                    )
                },
                isError = uiState.accountError != null,
                supportingText = {
                    if (uiState.accountError != null) {
                        Text(
                            text = uiState.accountError!!,
                            color = Error
                        )
                    }
                },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = OnSurface),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Outline,
                    errorBorderColor = Error,
                    cursorColor = Primary,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 密码输入框
            OutlinedTextField(
                value = uiState.password,
                onValueChange = {
                    viewModel.onPasswordChange(it)
                    viewModel.clearPasswordError()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(
                        text = "密码",
                        color = OnSurfaceVariant
                    )
                },
                placeholder = {
                    Text(
                        text = "请输入密码",
                        color = OnSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = AppIcons.Password,
                        contentDescription = "密码",
                        tint = OnSurfaceVariant
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) {
                                AppIcons.Visibility
                            } else {
                                AppIcons.VisibilityOff
                            },
                            contentDescription = if (uiState.isPasswordVisible) {
                                "隐藏密码"
                            } else {
                                "显示密码"
                            },
                            tint = OnSurfaceVariant
                        )
                    }
                },
                isError = uiState.passwordError != null,
                supportingText = {
                    if (uiState.passwordError != null) {
                        Text(
                            text = uiState.passwordError!!,
                            color = Error
                        )
                    }
                },
                visualTransformation = if (uiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = OnSurface),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Outline,
                    errorBorderColor = Error,
                    cursorColor = Primary,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 记住我 + 忘记密码
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 记住我（带勾选框）
                Row(
                    modifier = Modifier.clickable { viewModel.toggleRememberMe() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 自定义勾选框
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (uiState.rememberMe) {
                                    Primary
                                } else {
                                    SurfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.rememberMe) {
                            Icon(
                                imageVector = AppIcons.VerificationCode,
                                contentDescription = "已记住",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "记住我",
                        fontSize = 14.sp,
                        color = OnSurface
                    )
                }

                // 忘记密码
                TextButton(
                    onClick = onForgotPasswordClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Primary
                    )
                ) {
                    Text(
                        text = "忘记密码？",
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ========== 登录按钮 ==========
            BabyFoodPrimaryButton(
                text = "登录",
                onClick = {
                    Log.d("LoginScreen", "========== 登录按钮被点击 ==========")
                    Log.d("LoginScreen", "当前状态: isLoading=${uiState.isLoading}, isFormValid=${uiState.isFormValid}")
                    viewModel.login()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = uiState.isFormValid,
                loading = uiState.isLoading
            )

            // 表单验证状态提示（调试用）
            if (!uiState.isFormValid && !uiState.isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                val validationMessages = mutableListOf<String>()
                if (uiState.account.isEmpty()) validationMessages.add("请输入账号")
                else if (!Regex("^1[3-9]\\d{9}$").matches(uiState.account.trim()) &&
                        !Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(uiState.account.trim())) {
                    validationMessages.add("账号格式不正确（手机号或邮箱）")
                }
                if (uiState.password.length < 6) validationMessages.add("密码至少6位")

                if (validationMessages.isNotEmpty()) {
                    Text(
                        text = "提示: ${validationMessages.joinToString(", ")}",
                        fontSize = 12.sp,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ========== 第三方登录 ==========
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 分隔线
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Outline)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "或",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Outline)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 第三方登录按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 微信
                ThirdPartyLoginButton(
                    icon = "💬",
                    onClick = { /* TODO: 微信登录 */ }
                )

                // 手机验证码登录
                ThirdPartyLoginButton(
                    icon = "📱",
                    onClick = { /* TODO: 手机验证码登录 */ },
                    isSelected = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ========== 注册链接 ==========
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "还没有账号？",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant
                )
                TextButton(
                    onClick = onRegisterClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Primary
                    )
                ) {
                    Text(
                        text = "立即注册",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========== 底部法律声明 ==========
            Text(
                text = "登录即表示同意服务条款和隐私政策",
                fontSize = 12.sp,
                color = OnSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
        }

        // ========== 错误提示弹窗 ==========
        if (uiState.error != null) {
            ErrorDialog(
                errorMessage = uiState.error!!,
                onDismiss = { viewModel.clearError() }
            )
        }
    }
}

/**
 * 第三方登录按钮
 */
@Composable
private fun ThirdPartyLoginButton(
    icon: String,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    Primary.copy(alpha = 0.1f)
                } else {
                    SurfaceVariant
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
    }
}