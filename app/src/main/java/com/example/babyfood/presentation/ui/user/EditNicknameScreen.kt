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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.presentation.theme.*

/**
 * 修改昵称页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNicknameScreen(
    onBack: () -> Unit = {},
    currentNickname: String = "",
    onSaveSuccess: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    android.util.Log.d("EditNicknameScreen", "========== EditNicknameScreen 开始渲染 ==========")

    var nickname by remember { mutableStateOf(currentNickname) }
    var isSaving by remember { mutableStateOf(false) }
    val isValid = nickname.trim().isNotEmpty() && nickname.trim().length <= 20

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "修改昵称",
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
                            if (isValid && !isSaving) {
                                isSaving = true
                                viewModel.updateNickname(nickname.trim())
                                onSaveSuccess()
                            }
                        },
                        enabled = isValid && !isSaving
                    ) {
                        if (isSaving) {
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
                        text = "昵称将显示在您的个人资料中，长度不超过20个字符",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                // 昵称输入框
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
                        value = nickname,
                        onValueChange = {
                            if (it.length <= 20) {
                                nickname = it
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = {
                            Text(
                                text = "请输入昵称",
                                color = TextTertiary
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        supportingText = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (nickname.length > 20) "已超过最大长度" else "",
                                    color = if (nickname.length > 20) Color.Red else TextSecondary,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${nickname.length}/20",
                                    color = if (nickname.length > 20) Color.Red else TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}