package com.example.babyfood.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.BreakfastDining
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.Cookie
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.presentation.theme.AvocadoDark
import com.example.babyfood.presentation.theme.AvocadoPrimary
import com.example.babyfood.presentation.theme.Charcoal
import com.example.babyfood.presentation.theme.Coral
import com.example.babyfood.presentation.theme.Cream
import com.example.babyfood.presentation.theme.Peach
import com.example.babyfood.presentation.theme.Primary
import com.example.babyfood.presentation.theme.SoftBrown
import com.example.babyfood.presentation.ui.home.PlanWithRecipe

/**
 * 任务卡片样式的时间轴 - 采用新主题色
 */
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
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MealPeriod.values().forEach { period ->
            val planWithRecipe = plans.find { it.plan.mealPeriod == period.name }
            val currentMealTime = planWithRecipe?.plan?.mealTime ?: getMealTime(period)
            TaskCard(
                period = period,
                planWithRecipe = planWithRecipe,
                onShuffle = { onShuffle(period) },
                onSelectRecipe = { onSelectRecipe(period) },
                onViewRecipeDetail = onViewRecipeDetail,
                onEditMealTime = { _, _ -> onEditMealTime(period, currentMealTime) },
                onFeedback = { onFeedback(period) }
            )
        }
    }
}

@Composable
private fun TaskCard(
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (hasRecipe) onViewRecipeDetail(planWithRecipe!!.recipe!!.id) else onSelectRecipe() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 左侧图片区域 - 使用新主题色
            Box(
                modifier = Modifier
                    .size(width = 90.dp, height = 90.dp)
                    .background(
                        color = if (hasRecipe) {
                            when (period) {
                                MealPeriod.BREAKFAST -> Peach.copy(alpha = 0.3f)
                                MealPeriod.LUNCH -> AvocadoPrimary.copy(alpha = 0.3f)
                                MealPeriod.DINNER -> Cream
                                MealPeriod.SNACK -> Coral.copy(alpha = 0.3f)
                            }
                        } else {
                            Color(0xFFF8F9FA)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (hasRecipe) {
                    // 有食谱时显示图标
                    Icon(
                        imageVector = getMealIcon(period),
                        contentDescription = null,
                        tint = if (period == MealPeriod.DINNER) SoftBrown else AvocadoDark,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    // 空状态显示添加图标
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = SoftBrown.copy(alpha = 0.5f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // 右侧内容区域
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // 时间和更多按钮行
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 时间标签 - 使用Peach背景
                        val mealTime = if (hasRecipe) {
                            planWithRecipe?.plan?.mealTime ?: getMealTime(period)
                        } else {
                            getMealTime(period)
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (hasRecipe) Peach else Cream,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .clickable { onEditMealTime(period, mealTime) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mealTime,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (hasRecipe) Charcoal else SoftBrown,
                                letterSpacing = 0.3.sp
                            )
                        }

                        // 更多操作按钮
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "更多",
                            tint = SoftBrown.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 标题
                    if (hasRecipe) {
                        val recipe = planWithRecipe!!.recipe!!
                        Text(
                            text = recipe.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Charcoal
                        )

                        Spacer(modifier = Modifier.height(1.dp))

                        // 副标题/描述
                        Text(
                            text = "${recipe.cookingTime ?: 20}分钟 · ${period.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftBrown
                        )
                    } else {
                        Text(
                            text = "添加${period.displayName}食谱",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = SoftBrown
                        )
                    }
                }

                // 底部操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasRecipe) {
                        if (hasFeedback) {
                            // 已反馈状态 - 显示反馈标签
                            val feedbackText = getFeedbackDisplayName(feedbackStatus)
                            val feedbackColor = getFeedbackBackgroundColor(feedbackStatus)

                            Box(
                                modifier = Modifier
                                    .clickable { onFeedback() }
                                    .background(
                                        color = feedbackColor,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = feedbackText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        } else {
                            // 未反馈状态 - 显示完成按钮
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 换一换按钮 - Cream背景
                                Box(
                                    modifier = Modifier
                                        .clickable { onShuffle() }
                                        .background(
                                            color = Cream,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            tint = Charcoal,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "换一换",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Charcoal
                                        )
                                    }
                                }

                                // 完成按钮 - Primary绿色背景
                                Box(
                                    modifier = Modifier
                                        .clickable { onFeedback() }
                                        .background(
                                            color = AvocadoPrimary,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "完成",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AvocadoDark
                                    )
                                }
                            }
                        }
                    } else {
                        // 空状态 - 显示添加按钮
                        Box(
                            modifier = Modifier
                                .clickable { onSelectRecipe() }
                                .background(
                                    color = Cream,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "选择食谱",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Charcoal
                            )
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