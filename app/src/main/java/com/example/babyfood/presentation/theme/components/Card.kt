package com.example.babyfood.presentation.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.babyfood.presentation.theme.CardBackground
import com.example.babyfood.presentation.theme.CardGradientEnd
import com.example.babyfood.presentation.theme.CardGradientStart
import com.example.babyfood.presentation.theme.CardLargeRadius
import com.example.babyfood.presentation.theme.CardLargeShape
import com.example.babyfood.presentation.theme.CardSmallRadius
import com.example.babyfood.presentation.theme.CardSmallShape
import com.example.babyfood.presentation.theme.ElevationLevel1
import com.example.babyfood.presentation.theme.PageBackground
import com.example.babyfood.presentation.theme.SpacingMD
import com.example.babyfood.presentation.theme.SpacingSM

// ===== 大卡片组件 - 24dp 圆角 =====
// 应用场景：首页餐单大卡片、食谱详情大卡片
// 背景：垂直渐变从 #FFFFFF 到 #FDFBF8

@Composable
fun BabyFoodLargeCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    // 卡片底纹渐变：垂直（90°）渐变从 #FFFFFF 到 #FDFBF8
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            CardGradientStart,
            CardGradientEnd
        )
    )

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = CardLargeShape,
            elevation = CardDefaults.cardElevation(
                defaultElevation = ElevationLevel1
            ),
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradientBackground)
                    .padding(SpacingMD)
            ) {
                content()
            }
        }
    } else {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = CardLargeShape,
            color = CardBackground,
            shadowElevation = ElevationLevel1
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradientBackground)
                    .padding(SpacingMD)
            ) {
                content()
            }
        }
    }
}

// ===== 小卡片组件 - 20dp 圆角 =====
// 应用场景：食谱列表小卡片、库存条目卡片

@Composable
fun BabyFoodSmallCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = CardSmallShape,
            elevation = CardDefaults.cardElevation(
                defaultElevation = ElevationLevel1
            ),
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
            )
        ) {
            Column(
                modifier = Modifier.padding(SpacingSM)
            ) {
                content()
            }
        }
    } else {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = CardSmallShape,
            color = CardBackground,
            shadowElevation = ElevationLevel1
        ) {
            Column(
                modifier = Modifier.padding(SpacingSM)
            ) {
                content()
            }
        }
    }
}

// ===== 标签卡片组件 - 16dp 圆角 =====
// 应用场景：营养标签、状态标签

@Composable
fun BabyFoodLabelCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = com.example.babyfood.presentation.theme.PageBackground,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        color = backgroundColor,
        shadowElevation = 0.dp  // 标签卡片无阴影
    ) {
        Column(
            modifier = Modifier.padding(SpacingSM)
        ) {
            content()
        }
    }
}