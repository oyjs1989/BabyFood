package com.example.babyfood.presentation.ui.auth

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.babyfood.presentation.theme.Background
import com.example.babyfood.presentation.theme.Error
import com.example.babyfood.presentation.theme.ErrorContainer
import com.example.babyfood.presentation.theme.OnBackground
import com.example.babyfood.presentation.theme.OnSurface
import com.example.babyfood.presentation.theme.OnSurfaceVariant
import com.example.babyfood.presentation.theme.Outline
import com.example.babyfood.presentation.theme.Primary
import com.example.babyfood.presentation.theme.SurfaceVariant
import com.example.babyfood.presentation.ui.icons.AppIcons
import com.example.babyfood.presentation.theme.components.BabyFoodPrimaryButton

/**
 * 注册页面
 * 参考登录界面风格，支持手机号/邮箱 + 密码 + 验证码注册
 */
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onBackToLogin: () -> Unit = {},
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val authState by viewModel.authState.collectAsState()

    // 监听注册成功状态
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.LoggedIn -> {
                Log.d("RegisterScreen", "注册成功，导航到首页")
                onRegisterSuccess()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        CompositionLocalProvider(LocalContentColor provides OnSurface) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
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
                text = "创建账号",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 副标题
            Text(
                text = "加入 BabyFood，开启科学喂养之旅",
                fontSize = 14.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ========== 表单区域 ==========

            // 账号输入框（手机号或邮箱）
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
                        text = "请输入手机号或邮箱",
                        color = OnSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (uiState.account.contains("@")) {
                            AppIcons.Account
                        } else {
                            AppIcons.Phone
                        },
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
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (uiState.account.contains("@")) {
                        KeyboardType.Email
                    } else {
                        KeyboardType.Phone
                    }
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

            Spacer(modifier = Modifier.height(16.dp))

            // 验证码输入框
            OutlinedTextField(
                value = uiState.verificationCode,
                onValueChange = {
                    viewModel.onVerificationCodeChange(it)
                    viewModel.clearVerificationCodeError()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(
                        text = "验证码",
                        color = OnSurfaceVariant
                    )
                },
                placeholder = {
                    Text(
                        text = "请输入验证码",
                        color = OnSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = AppIcons.VerificationCode,
                        contentDescription = "验证码",
                        tint = OnSurfaceVariant
                    )
                },
                trailingIcon = {
                    TextButton(
                        onClick = { viewModel.sendVerificationCode() },
                        enabled = !uiState.isSendingCode &&
                                 uiState.countdown == 0 &&
                                 uiState.account.isNotEmpty(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (uiState.countdown > 0) {
                                OnSurfaceVariant.copy(alpha = 0.5f)
                            } else {
                                Primary
                            }
                        )
                    ) {
                        Text(
                            text = when {
                                uiState.countdown > 0 -> "${uiState.countdown}秒后重发"
                                uiState.isSendingCode -> "发送中..."
                                else -> "获取验证码"
                            },
                            fontSize = 14.sp
                        )
                    }
                },
                isError = uiState.verificationCodeError != null,
                supportingText = {
                    if (uiState.verificationCodeError != null) {
                        Text(
                            text = uiState.verificationCodeError!!,
                            color = Error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
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
                        text = "请输入密码（至少6位）",
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
                
                            Spacer(modifier = Modifier.height(16.dp))
                
                            // 确认密码输入框
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = {
                    viewModel.onConfirmPasswordChange(it)
                    viewModel.clearConfirmPasswordError()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(
                        text = "确认密码",
                        color = OnSurfaceVariant
                    )
                },
                placeholder = {
                    Text(
                        text = "请再次输入密码",
                        color = OnSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = AppIcons.Password,
                        contentDescription = "确认密码",
                        tint = OnSurfaceVariant
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                        Icon(
                            imageVector = if (uiState.isConfirmPasswordVisible) {
                                AppIcons.Visibility
                            } else {
                                AppIcons.VisibilityOff
                            },
                            contentDescription = if (uiState.isConfirmPasswordVisible) {
                                "隐藏密码"
                            } else {
                                "显示密码"
                            },
                            tint = OnSurfaceVariant
                        )
                    }
                },
                isError = uiState.confirmPasswordError != null,
                supportingText = {
                    if (uiState.confirmPasswordError != null) {
                        Text(
                            text = uiState.confirmPasswordError!!,
                            color = Error
                        )
                    }
                },
                visualTransformation = if (uiState.isConfirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                singleLine = true,
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
                
                            // 同意服务条款
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 自定义复选框
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (uiState.agreeToTerms) {
                                Primary
                            } else {
                                SurfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.agreeToTerms) {
                        Icon(
                            imageVector = AppIcons.VerificationCode,
                            contentDescription = "已同意",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = { viewModel.toggleAgreeToTerms() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = OnSurface
                    )
                ) {
                    Text(
                        text = "我已阅读并同意服务条款和隐私政策",
                        fontSize = 13.sp
                    )
                }
            }

            if (uiState.agreeToTermsError != null) {
                Text(
                    text = uiState.agreeToTermsError!!,
                    color = Error,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ========== 注册按钮 ==========
            BabyFoodPrimaryButton(
                text = "注册",
                onClick = { viewModel.register() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = uiState.isFormValid,
                loading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ========== 返回登录链接 ==========
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "已有账号？",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant
                )
                TextButton(
                    onClick = onBackToLogin,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Primary
                    )
                ) {
                    Text(
                        text = "立即登录",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
        }

        // ========== 错误提示（Toast样式）==========
        if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .background(
                        color = ErrorContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = uiState.error!!,
                    color = Error,
                    fontSize = 14.sp
                )
            }
        }
    }
}