package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babyfood.presentation.theme.HighlightProtein
import com.example.babyfood.presentation.theme.HighlightCalcium
import com.example.babyfood.presentation.theme.HighlightIron
import com.example.babyfood.presentation.theme.HighlightBalanced
import com.example.babyfood.presentation.theme.HighlightLowCalorie
import com.example.babyfood.presentation.theme.HighlightVitamin
import com.example.babyfood.presentation.theme.HighlightDefault
import com.example.babyfood.presentation.theme.ScoreExcellent
import com.example.babyfood.presentation.theme.ScoreGood
import com.example.babyfood.presentation.theme.ScoreMedium
import com.example.babyfood.presentation.theme.ScoreFair
import com.example.babyfood.presentation.theme.ScorePoor
import com.example.babyfood.presentation.theme.Gray900
import com.example.babyfood.presentation.theme.RedContainer
import com.example.babyfood.presentation.theme.Red800

/**
 * 营养亮点徽章
 *
 * 标识食谱中的营养亮点（如高蛋白、高钙等）
 */
@Composable
fun NutritionHighlightBadge(
    highlight: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, icon) = when (highlight) {
        "蛋白质充足" -> Triple(HighlightProtein, Color.White, "💪")
        "钙充足" -> Triple(HighlightCalcium, Color.White, "🦴")
        "铁充足" -> Triple(HighlightIron, Color.White, "🩸")
        "营养均衡" -> Triple(HighlightBalanced, Color.White, "⚖️")
        "高蛋白" -> Triple(HighlightProtein, Color.White, "💪")
        "高钙" -> Triple(HighlightCalcium, Color.White, "🦴")
        "高铁" -> Triple(HighlightIron, Color.White, "🩸")
        "低热量" -> Triple(HighlightLowCalorie, Color.White, "🥗")
        "富含维生素" -> Triple(HighlightVitamin, Color.White, "🥬")
        else -> Triple(HighlightDefault, Color.White, "✨")
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = highlight,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 营养等级徽章
 *
 * 显示营养匹配度等级
 */
@Composable
fun NutritionGradeBadge(
    grade: String,
    score: Float,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, emoji) = when {
        score >= 90f -> Triple(ScoreExcellent, Color.White, "⭐⭐⭐⭐⭐")
        score >= 75f -> Triple(ScoreGood, Color.White, "⭐⭐⭐⭐")
        score >= 60f -> Triple(ScoreMedium, Gray900, "⭐⭐⭐")
        score >= 40f -> Triple(ScoreFair, Color.White, "⭐⭐")
        else -> Triple(ScorePoor, Color.White, "⭐")
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = grade,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.0f%%", score),
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

/**
 * 营养缺乏警告徽章
 */
@Composable
fun NutritionDeficiencyBadge(
    deficiency: String,
    modifier: Modifier = Modifier
) {
    val icon = when (deficiency) {
        "热量" -> "🍚"
        "蛋白质" -> "🥩"
        "钙" -> "🥛"
        "铁" -> "🥬"
        else -> "⚠️"
    }

    Box(
        modifier = modifier
            .background(
                color = RedContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "缺$deficiency",
                color = Red800,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 营养亮点列表
 *
 * 显示食谱的所有营养亮点
 */
@Composable
fun NutritionHighlightsList(
    highlights: List<String>,
    modifier: Modifier = Modifier
) {
    if (highlights.isEmpty()) return

    Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)
    ) {
        highlights.forEach { highlight ->
            NutritionHighlightBadge(highlight = highlight)
        }
    }
}

/**
 * 营养等级和亮点组合
 *
 * 显示营养等级和主要亮点
 */
@Composable
fun NutritionGradeAndHighlights(
    grade: String,
    score: Float,
    highlights: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        NutritionGradeBadge(
            grade = grade,
            score = score,
            modifier = Modifier.fillMaxWidth()
        )

        if (highlights.isNotEmpty()) {
            NutritionHighlightsList(
                highlights = highlights,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
