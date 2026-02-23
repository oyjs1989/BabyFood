package com.example.babyfood.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * 用餐反馈选项
 */
enum class MealFeedbackOption(val displayName: String) {
    FINISHED("光盘"),
    HALF("吃了一半"),
    DISLIKED("吐了/不爱吃"),
    ALLERGY("出现过敏")
}

/**
 * 用餐反馈对话框
 * 参考设计：img_1.png
 */
@Composable
fun MealFeedbackDialog(
    selectedFeedback: MealFeedbackOption?,
    onDismiss: () -> Unit,
    onFeedbackSelected: (MealFeedbackOption) -> Unit,
    onConfirm: () -> Unit,
    onConfirmWithIngredients: (Set<String>) -> Unit = {},
    recipeIngredients: List<com.example.babyfood.domain.model.Ingredient> = emptyList(),
    modifier: Modifier = Modifier
) {
    var showIngredientSelection by remember { mutableStateOf(false) }

    // 当选择了需要食材选择的选项时，显示食材选择对话框
    if (showIngredientSelection && selectedFeedback != null) {
        val title = when (selectedFeedback) {
            MealFeedbackOption.DISLIKED -> "选择吐了/不爱吃的食材"
            MealFeedbackOption.ALLERGY -> "选择过敏的食材"
            else -> ""
        }

        IngredientSelectionDialog(
            title = title,
            ingredients = recipeIngredients,
            selectedIngredients = emptySet(),
            onDismiss = { showIngredientSelection = false },
            onConfirm = { ingredients ->
                showIngredientSelection = false
                onConfirmWithIngredients(ingredients)
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 标题栏
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 标题
                    Text(
                        text = "宝宝吃得怎么样？",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    // 关闭按钮
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭",
                            tint = Color(0xFF666666)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 反馈选项网格（2x2）
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 左侧列
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FeedbackOptionCard(
                            option = MealFeedbackOption.FINISHED,
                            isSelected = selectedFeedback == MealFeedbackOption.FINISHED,
                            onClick = { onFeedbackSelected(MealFeedbackOption.FINISHED) }
                        )
                        FeedbackOptionCard(
                            option = MealFeedbackOption.DISLIKED,
                            isSelected = selectedFeedback == MealFeedbackOption.DISLIKED,
                            onClick = { onFeedbackSelected(MealFeedbackOption.DISLIKED) }
                        )
                    }

                    // 右侧列
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FeedbackOptionCard(
                            option = MealFeedbackOption.HALF,
                            isSelected = selectedFeedback == MealFeedbackOption.HALF,
                            onClick = { onFeedbackSelected(MealFeedbackOption.HALF) }
                        )
                        FeedbackOptionCard(
                            option = MealFeedbackOption.ALLERGY,
                            isSelected = selectedFeedback == MealFeedbackOption.ALLERGY,
                            onClick = { onFeedbackSelected(MealFeedbackOption.ALLERGY) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 确认按钮
                Button(
                    onClick = {
                        // 如果选择了需要食材选择的选项，显示食材选择对话框
                        if (selectedFeedback == MealFeedbackOption.DISLIKED ||
                            selectedFeedback == MealFeedbackOption.ALLERGY) {
                            showIngredientSelection = true
                        } else {
                            // "光盘"、"吃了一半"直接确认
                            onConfirm()
                        }
                    },
                    enabled = selectedFeedback != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF7F3E),
                        disabledContainerColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    val buttonText = when (selectedFeedback) {
                        MealFeedbackOption.DISLIKED,
                        MealFeedbackOption.ALLERGY -> "选择食材"
                        else -> "确认"
                    }
                    Text(
                        text = buttonText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 取消按钮
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF666666)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDDDDDD))
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "取消",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

/**
 * 反馈选项卡片
 */
@Composable
private fun FeedbackOptionCard(
    option: MealFeedbackOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) {
        Color(0xFFFF7F3E)
    } else {
        Color(0xFFDDDDDD)
    }

    val borderWidth = if (isSelected) 2.dp else 1.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = borderWidth,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 图标占位符（灰色圆形）
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isSelected) {
                            Color(0xFFFF7F3E).copy(alpha = 0.1f)
                        } else {
                            Color(0xFFF0F0F0)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // 这里可以替换为实际的图标
                Icon(
                    imageVector = getFeedbackIcon(option),
                    contentDescription = option.displayName,
                    tint = if (isSelected) {
                        Color(0xFFFF7F3E)
                    } else {
                        Color(0xFF999999)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 选项文字
            Text(
                text = option.displayName,
                fontSize = 15.sp,
                fontWeight = if (isSelected) {
                    FontWeight.Medium
                } else {
                    FontWeight.Normal
                },
                color = if (isSelected) {
                    Color(0xFFFF7F3E)
                } else {
                    Color(0xFF333333)
                }
            )
        }
    }
}

/**
 * 获取反馈选项对应的图标
 */
@Composable
private fun getFeedbackIcon(option: MealFeedbackOption) = when (option) {
    MealFeedbackOption.FINISHED -> Icons.Default.CheckCircle
    MealFeedbackOption.HALF -> Icons.Default.RemoveCircle
    MealFeedbackOption.DISLIKED -> Icons.Default.RemoveCircle
    MealFeedbackOption.ALLERGY -> Icons.Default.Error
}