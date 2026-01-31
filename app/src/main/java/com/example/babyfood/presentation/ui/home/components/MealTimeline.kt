package com.example.babyfood.presentation.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.BreakfastDining
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.Cookie
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.presentation.theme.SecondaryContainer
import com.example.babyfood.presentation.theme.components.BabyFoodSmallCard
import com.example.babyfood.presentation.ui.home.PlanWithRecipe

@Composable
fun MealTimeline(
    plans: List<PlanWithRecipe>,
    onShuffle: (MealPeriod) -> Unit,
    onSelectRecipe: (MealPeriod) -> Unit,
    onViewRecipeDetail: (Long) -> Unit = {},
    onEditMealTime: (MealPeriod, String) -> Unit = { _, _ -> },
    onFeedback: (MealPeriod) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        MealPeriod.values().forEach { period ->
            val planWithRecipe = plans.find { it.plan.mealPeriod == period.name }
            val currentMealTime = planWithRecipe?.plan?.mealTime ?: getMealTime(period)
            MealPeriodCard(
                period = period,
                planWithRecipe = planWithRecipe,
                onShuffle = { onShuffle(period) },
                onSelectRecipe = { onSelectRecipe(period) },
                onViewRecipeDetail = onViewRecipeDetail,
                onEditMealTime = { _, _ -> onEditMealTime(period, currentMealTime) },
                onFeedback = { onFeedback(period) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MealPeriodCard(
    period: MealPeriod,
    planWithRecipe: PlanWithRecipe?,
    onShuffle: () -> Unit,
    onSelectRecipe: () -> Unit,
    onViewRecipeDetail: (Long) -> Unit,
    onEditMealTime: (MealPeriod, String) -> Unit,
    onFeedback: () -> Unit
) {
    val hasRecipe = planWithRecipe != null && planWithRecipe.recipe != null
    val feedbackStatus = planWithRecipe?.plan?.feedbackStatus
    val hasFeedback = feedbackStatus != null

    // 自定义卡片背景和边框
    val cardBackgroundColor = if (hasRecipe) {
        MaterialTheme.colorScheme.surface
    } else {
        SecondaryContainer
    }
    val cardBorder = if (!hasRecipe) {
        androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    } else {
        null
    }

    BabyFoodSmallCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBackgroundColor)
                .let { modifier ->
                    if (cardBorder != null) {
                        modifier.border(cardBorder)
                    } else {
                        modifier
                    }
                }
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧时间轴区域
            Row(
                modifier = Modifier.width(60.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 圆点（使用主色调）
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 时间文字（可点击编辑）
                    val mealTime = if (hasRecipe) {
                        planWithRecipe?.plan?.mealTime ?: getMealTime(period)
                    } else {
                        getMealTime(period)
                    }
                    Text(
                        text = mealTime,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (hasRecipe) Color(0xFFFF8C42) else Color(0xFF333333),
                        modifier = Modifier.clickable { onEditMealTime(period, mealTime) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 灰色竖线
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color(0xFFE0E0E0))
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 中部内容区域
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (hasRecipe) {
                    val recipe = planWithRecipe!!.recipe!!

                    Row(
                        modifier = Modifier.clickable { onViewRecipeDetail(recipe.id) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 圆形缩略图背景
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    color = Color(0xFFF8F8F8),
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFE5E5E5),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getMealIcon(period),
                                contentDescription = null,
                                tint = Color(0xFF999999),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            // 食谱标题
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF222222)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // 烹饪时长
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color(0xFF666666),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${recipe.cookingTime ?: 20}分钟",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF666666)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // 标签
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // 根据食谱类型生成标签
                                val tags = generateRecipeTags(recipe)
                                tags.forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = tag.color,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "#${tag.name}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.clickable { onSelectRecipe() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 空状态图标
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    color = Color(0xFFF8F8F8),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF999999),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "点击添加${period.displayName}食谱",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 右侧操作区域
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 换一换按钮
                Row(
                    modifier = Modifier.clickable(enabled = hasRecipe) { if (hasRecipe) onShuffle() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "换一换",
                        tint = if (hasRecipe) {
                            Color(0xFF666666)
                        } else {
                            Color(0xFF666666).copy(alpha = 0.5f)
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "换一换",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (hasRecipe) {
                            Color(0xFF666666)
                        } else {
                            Color(0xFF666666).copy(alpha = 0.5f)
                        }
                    )
                }

                // 反馈按钮
                if (hasRecipe) {
                    if (hasFeedback) {
                        // 已反馈状态
                        val feedbackText = getFeedbackDisplayName(feedbackStatus)
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val feedbackColor = getFeedbackBackgroundColor(feedbackStatus)
                        val backgroundColor = if (isPressed) feedbackColor.copy(alpha = 0.7f) else feedbackColor

                        Box(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) { onFeedback() }
                                .background(
                                    color = backgroundColor,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = feedbackText,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    } else {
                        // 未反馈状态
                        Row(
                            modifier = Modifier.clickable { onFeedback() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "反馈",
                                tint = Color(0xFF666666),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "反馈",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }
            }
        }
    }
    }
}

// 获取反馈显示名称
private fun getFeedbackDisplayName(feedbackStatus: String): String {
    return when (feedbackStatus) {
        "FINISHED" -> "光盘"
        "HALF" -> "吃了一半"
        "DISLIKED" -> "吐了/不爱吃"
        "ALLERGY" -> "出现过敏"
        else -> "已反馈"
    }
}

// 获取反馈背景颜色
private fun getFeedbackBackgroundColor(feedbackStatus: String): Color {
    return when (feedbackStatus) {
        "FINISHED" -> Color(0xFF2EC77C)  // 绿色
        "HALF" -> Color(0xFFFFB347)      // 橙色
        "DISLIKED" -> Color(0xFFFFB347)  // 橙色
        "ALLERGY" -> Color(0xFFE74C3C)   // 红色
        else -> Color(0xFF666666)        // 灰色
    }
}

// 辅助数据类：标签信息
data class RecipeTag(
    val name: String,
    val color: Color
)

// 生成食谱标签
private fun generateRecipeTags(recipe: com.example.babyfood.domain.model.Recipe): List<RecipeTag> {
    val tags = mutableListOf<RecipeTag>()
    
    // 根据烹饪时间添加"快手"标签
    if ((recipe.cookingTime ?: 0) <= 20) {
        tags.add(RecipeTag("快手", Color(0xFF2EC77C)))
    }
    
    // 根据营养均衡添加标签
    val protein = recipe.nutrition.protein ?: 0f
    val calories = recipe.nutrition.calories ?: 0f
    if (calories > 0 && protein / calories > 0.1f) {
        tags.add(RecipeTag("营养均衡", Color(0xFF2EB9A0)))
    }
    
    // 根据类别添加标签
    when (recipe.category) {
        "breakfast" -> tags.add(RecipeTag("早餐", Color(0xFFFFB347)))
        "lunch" -> tags.add(RecipeTag("午餐", Color(0xFF87CEEB)))
        "dinner" -> tags.add(RecipeTag("晚餐", Color(0xFFDDA0DD)))
        "snack" -> tags.add(RecipeTag("点心", Color(0xFF98FB98)))
    }
    
    // 限制最多显示3个标签
    return tags.take(3)
}

// 获取餐段时间
private fun getMealTime(period: MealPeriod): String = when (period) {
    MealPeriod.BREAKFAST -> "08:00"
    MealPeriod.LUNCH -> "12:00"
    MealPeriod.DINNER -> "18:00"
    MealPeriod.SNACK -> "15:00"
}

@Composable
private fun getMealIcon(period: MealPeriod) = when (period) {
    MealPeriod.BREAKFAST -> Icons.Outlined.BreakfastDining
    MealPeriod.LUNCH -> Icons.Outlined.LunchDining
    MealPeriod.DINNER -> Icons.Outlined.DinnerDining
    MealPeriod.SNACK -> Icons.Outlined.Cookie
}