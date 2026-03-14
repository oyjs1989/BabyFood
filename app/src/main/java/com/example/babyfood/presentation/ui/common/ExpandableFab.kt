package com.example.babyfood.presentation.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 辅食碗悬浮按钮主题色
 */
private val BowlOrange = Color(0xFFFF9A3C)  // 碗体橙色
private val BowlCream = Color(0xFFFFF6E9)   // 碗内奶白色
private val SpoonWhite = Color(0xFFFFFBF5)  // 勺子米白色

/**
 * 展开式悬浮按钮的子项
 *
 * @param icon 图标
 * @param label 主标签
 * @param onClick 点击回调
 */
data class FabAction(
    val icon: ImageVector,
    val label: String,
    val description: String? = null,
    val onClick: () -> Unit
)

/**
 * 辅食碗微拟物悬浮按钮组件
 *
 * 设计特点：
 * - 收起状态：圆形辅食碗图标，视觉轻盈可爱
 * - 展开状态：向上弹出选项菜单
 * - 交互：点击主按钮展开/收起，点击空白区域收起
 *
 * @param actions 操作按钮列表
 * @param modifier 修饰符
 */
@Composable
fun ExpandableFab(
    actions: List<FabAction>,
    modifier: Modifier = Modifier,
    isExpanded: Boolean? = null,
    onExpandedChange: ((Boolean) -> Unit)? = null
) {
    var internalExpanded by remember { mutableStateOf(false) }
    val expanded = isExpanded ?: internalExpanded

    val onToggle: () -> Unit = {
        val newState = !expanded
        if (isExpanded == null) {
            internalExpanded = newState
        }
        onExpandedChange?.invoke(newState)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        // 半透明遮罩（展开时显示，点击收起）
        if (expanded) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onToggle() }
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            // 展开的子按钮
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + slideInVertically(
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    initialOffsetY = { it }
                ),
                exit = fadeOut() + slideOutVertically(
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    targetOffsetY = { it }
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 操作选项（逆序显示）
                    actions.reversed().forEach { action ->
                        FabActionItem(
                            action = action,
                            onDismiss = { onToggle() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 主按钮：辅食碗造型
            BowlFab(
                expanded = expanded,
                onClick = onToggle
            )
        }
    }
}

/**
 * 辅食碗造型的悬浮按钮
 */
@Composable
private fun BowlFab(
    expanded: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(BowlOrange)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // 辅食碗图标（简化版）
        FoodBowlIcon(
            modifier = Modifier.size(32.dp)
        )
    }
}

/**
 * 辅食碗图标
 * 简化的微拟物风格：碗体 + 勺子
 */
@Composable
private fun FoodBowlIcon(
    modifier: Modifier = Modifier
) {
    // 使用Canvas绘制简化的辅食碗
    androidx.compose.foundation.Canvas(
        modifier = modifier
    ) {
        val width = size.width
        val height = size.height
        
        // 碗内填充（奶白色）
        drawRoundRect(
            color = BowlCream,
            topLeft = androidx.compose.ui.geometry.Offset(width * 0.15f, height * 0.25f),
            size = androidx.compose.ui.geometry.Size(width * 0.7f, height * 0.45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(width * 0.15f, height * 0.15f)
        )
        
        // 碗体边缘（白色高光）
        drawRoundRect(
            color = SpoonWhite,
            topLeft = androidx.compose.ui.geometry.Offset(width * 0.1f, height * 0.35f),
            size = androidx.compose.ui.geometry.Size(width * 0.8f, height * 0.4f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(width * 0.2f, height * 0.2f)
        )
        
        // 勺子（简化的斜线）
        drawLine(
            color = SpoonWhite,
            start = androidx.compose.ui.geometry.Offset(width * 0.6f, height * 0.15f),
            end = androidx.compose.ui.geometry.Offset(width * 0.85f, height * 0.55f),
            strokeWidth = width * 0.08f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        
        // 勺子头（小圆）
        drawCircle(
            color = SpoonWhite,
            radius = width * 0.1f,
            center = androidx.compose.ui.geometry.Offset(width * 0.58f, height * 0.18f)
        )
    }
}

@Composable
private fun FabActionItem(
    action: FabAction,
    onDismiss: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            action.onClick()
            onDismiss()
        }
    ) {
        // 标签卡片
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 2.dp,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = BowlOrange,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = action.label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
            }
        }
    }
}