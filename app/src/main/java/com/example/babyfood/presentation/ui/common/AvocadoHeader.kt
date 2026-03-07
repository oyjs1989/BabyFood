package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babyfood.presentation.theme.Charcoal
import com.example.babyfood.presentation.theme.Cream
import com.example.babyfood.presentation.theme.Peach
import com.example.babyfood.presentation.theme.SoftBrown

/**
 * 全局顶部 Header 组件
 * 设计风格：Peach 半透明背景 + backdrop blur 模糊效果
 */
@Composable
fun AvocadoHeader(
    babyName: String = "未选择宝宝",
    babyAge: String = "",
    onNotificationsClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Peach.copy(alpha = 0.8f)
            )
            .then(
                // 模拟 backdrop blur 效果的渐变叠加
                Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Baby Profile Image - 圆形头像，Cream 背景，白色边框
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Cream)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // 宝宝头像占位符
                    Text("👶", fontSize = 20.sp)
                }

                Column {
                    // "GROW HEALTHY" 标签 - SoftBrown 色，大写，粗体
                    Text(
                        text = "GROW HEALTHY",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = SoftBrown
                    )
                    // 宝宝名字行
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = babyName,
                            style = androidx.compose.ui.text.TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Charcoal
                            )
                        )
                        if (babyAge.isNotBlank()) {
                            Text(
                                text = " ($babyAge)",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Charcoal.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                            )
                        }
                    }
                }
            }

            // Notification Button - 圆形白色按钮，带阴影
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onNotificationsClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "通知",
                        tint = Charcoal,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
