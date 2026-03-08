package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babyfood.domain.model.RiskLevel
import com.example.babyfood.presentation.theme.Gray900
import com.example.babyfood.presentation.theme.RiskCautious
import com.example.babyfood.presentation.theme.RiskForbidden
import com.example.babyfood.presentation.theme.RiskNotRecommended
import com.example.babyfood.presentation.theme.RiskRequiresHandling

/**
 * 安全警告标识组件
 *
 * 根据风险等级显示不同颜色的警告标识：
 * - FORBIDDEN: 红色警告（禁用）
 * - NOT_RECOMMENDED: 黄色警告（不推荐）
 * - REQUIRES_SPECIAL_HANDLING: 蓝色提示（需特殊处理）
 * - CAUTIOUS_INTRODUCTION: 橙色提示（需谨慎引入）
 *
 * @param riskLevel 风险等级
 * @param modifier Modifier
 */
@Composable
fun SafetyWarningBadge(
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, labelText) = when (riskLevel) {
        RiskLevel.FORBIDDEN -> Triple(
            RiskForbidden,
            Color.White,
            "禁用"
        )
        RiskLevel.NOT_RECOMMENDED -> Triple(
            RiskNotRecommended,
            Gray900,
            "不推荐"
        )
        RiskLevel.REQUIRES_SPECIAL_HANDLING -> Triple(
            RiskRequiresHandling,
            Color.White,
            "需处理"
        )
        RiskLevel.CAUTIOUS_INTRODUCTION -> Triple(
            RiskCautious,
            Color.White,
            "谨慎"
        )
        RiskLevel.NORMAL -> Triple(
            Color.Transparent,
            Color.Transparent,
            ""
        )
    }

    // NORMAL 等级不显示标识
    if (riskLevel != RiskLevel.NORMAL) {
        Box(
            modifier = modifier
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = labelText,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 迷你版安全警告标识
 *
 * 用于空间受限的场景，只显示颜色圆点
 *
 * @param riskLevel 风险等级
 * @param modifier Modifier
 */
@Composable
fun MiniSafetyWarningBadge(
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier
) {
    val color = when (riskLevel) {
        RiskLevel.FORBIDDEN -> RiskForbidden
        RiskLevel.NOT_RECOMMENDED -> RiskNotRecommended
        RiskLevel.REQUIRES_SPECIAL_HANDLING -> RiskRequiresHandling
        RiskLevel.CAUTIOUS_INTRODUCTION -> RiskCautious
        RiskLevel.NORMAL -> Color.Transparent
    }

    // NORMAL 等级不显示标识
    if (riskLevel != RiskLevel.NORMAL) {
        Box(
            modifier = modifier
                .background(
                    color = color,
                    shape = RoundedCornerShape(50)
                )
        ) {
            // 空内容，只显示颜色圆点
        }
    }
}

/**
 * 安全警告图标
 *
 * 显示风险等级对应的图标
 *
 * @param riskLevel 风险等级
 * @param modifier Modifier
 */
@Composable
fun SafetyWarningIcon(
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier
) {
    val iconText = when (riskLevel) {
        RiskLevel.FORBIDDEN -> "🚫"
        RiskLevel.NOT_RECOMMENDED -> "⚠️"
        RiskLevel.REQUIRES_SPECIAL_HANDLING -> "💡"
        RiskLevel.CAUTIOUS_INTRODUCTION -> "⚠️"
        RiskLevel.NORMAL -> ""
    }

    // NORMAL 等级不显示图标
    if (riskLevel != RiskLevel.NORMAL) {
        Text(
            text = iconText,
            fontSize = 16.sp,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SafetyWarningBadgePreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("各种风险等级的标识：", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                SafetyWarningBadge(riskLevel = RiskLevel.FORBIDDEN)
                Text("  禁用（红色）", fontSize = 12.sp)
            }
            
            Row(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                SafetyWarningBadge(riskLevel = RiskLevel.NOT_RECOMMENDED)
                Text("  不推荐（黄色）", fontSize = 12.sp)
            }
            
            Row(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                SafetyWarningBadge(riskLevel = RiskLevel.REQUIRES_SPECIAL_HANDLING)
                Text("  需特殊处理（蓝色）", fontSize = 12.sp)
            }
            
            Row(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                SafetyWarningBadge(riskLevel = RiskLevel.CAUTIOUS_INTRODUCTION)
                Text("  需谨慎引入（橙色）", fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MiniSafetyWarningBadgePreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("迷你版标识：", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                MiniSafetyWarningBadge(riskLevel = RiskLevel.FORBIDDEN, modifier = Modifier.padding(4.dp))
                Text("  禁用", fontSize = 12.sp)
            }
            
            Row(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                MiniSafetyWarningBadge(riskLevel = RiskLevel.NOT_RECOMMENDED, modifier = Modifier.padding(4.dp))
                Text("  不推荐", fontSize = 12.sp)
            }
        }
    }
}
