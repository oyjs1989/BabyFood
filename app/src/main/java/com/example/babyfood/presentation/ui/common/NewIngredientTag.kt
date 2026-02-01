package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

/**
 * 新食材标签组件
 *
 * 标注食谱中的新食材（宝宝首次尝试的食材）
 */
@Composable
fun NewIngredientTag(
    ingredientName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFF9C27B0),  // 紫色
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "✨",
                fontSize = 12.sp
            )
            Text(
                text = ingredientName,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 新食材列表组件
 *
 * 显示食谱中的所有新食材
 */
@Composable
fun NewIngredientsList(
    ingredients: List<String>,
    modifier: Modifier = Modifier
) {
    if (ingredients.isEmpty()) return

    Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = "新食材：",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ingredients.forEach { ingredient ->
            NewIngredientTag(ingredientName = ingredient)
        }
    }
}

/**
 * 食材种类统计组件
 *
 * 显示食谱中的食材种类数量
 */
@Composable
fun IngredientVarietySummary(
    totalIngredients: Int,
    newIngredients: Int,
    modifier: Modifier = Modifier
) {
    val varietyPercentage = if (totalIngredients > 0) {
        (newIngredients.toFloat() / totalIngredients.toFloat() * 100).toInt()
    } else {
        0
    }

    Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = "🌿",
            fontSize = 14.sp
        )
        Text(
            text = "食材种类：$totalIngredients",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "•",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "新食材：$newIngredients",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF9C27B0),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "•",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "多样性：$varietyPercentage%",
            style = MaterialTheme.typography.bodySmall,
            color = when {
                varietyPercentage >= 50 -> Color(0xFF4CAF50)  // 绿色
                varietyPercentage >= 30 -> Color(0xFFFF9800)  // 橙色
                varietyPercentage >= 10 -> Color(0xFFFFC107)  // 黄色
                else -> Color(0xFFF44336)  // 红色
            },
            fontWeight = FontWeight.Bold
        )
    }
}

// Preview function temporarily removed due to import resolution issues