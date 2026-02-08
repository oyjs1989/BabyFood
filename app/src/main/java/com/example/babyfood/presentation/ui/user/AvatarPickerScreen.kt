package com.example.babyfood.presentation.ui.user

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.presentation.theme.*

/**
 * 头像选择页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarPickerScreen(
    onBack: () -> Unit = {},
    currentAvatar: String = "",
    onAvatarSelected: (String) -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    android.util.Log.d("AvatarPickerScreen", "========== AvatarPickerScreen 开始渲染 ==========")

    val avatars = remember {
        listOf(
            "avatar_1",
            "avatar_2",
            "avatar_3",
            "avatar_4",
            "avatar_5",
            "avatar_6",
            "avatar_7",
            "avatar_8",
            "avatar_9",
            "avatar_10",
            "avatar_11",
            "avatar_12"
        )
    }

    var selectedAvatar by remember { mutableStateOf(currentAvatar) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "选择头像",
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
                    if (selectedAvatar.isNotEmpty() && selectedAvatar != currentAvatar) {
                        IconButton(
                            onClick = {
                                viewModel.updateAvatar(selectedAvatar)
                                onAvatarSelected(selectedAvatar)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "确认",
                                tint = Primary
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 提示信息
                Text(
                    text = "请选择一个头像",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // 头像网格
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(avatars.size) { index ->
                        AvatarItem(
                            avatar = avatars[index],
                            isSelected = selectedAvatar == avatars[index],
                            isCurrent = currentAvatar == avatars[index],
                            onClick = {
                                selectedAvatar = avatars[index]
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 头像项
 */
@Composable
private fun AvatarItem(
    avatar: String,
    isSelected: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> Primary
                    isCurrent -> SurfaceVariant
                    else -> CardBackground
                }
            )
            .border(
                width = if (isSelected || isCurrent) 2.dp else 1.dp,
                color = when {
                    isSelected -> Primary
                    isCurrent -> Outline
                    else -> Outline
                },
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // 显示头像首字母（这里简化处理，实际应该显示头像图片）
        Text(
            text = avatar.last().toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = when {
                isSelected -> Color.White
                isCurrent -> Primary
                else -> TextPrimary
            }
        )

        // 选中标记
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "已选择",
                    tint = Primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // 当前头像标记
        if (isCurrent && !isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "当前",
                    fontSize = 8.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}