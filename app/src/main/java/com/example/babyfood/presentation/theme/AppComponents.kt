package com.example.babyfood.presentation.theme

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ===== 增强型卡片组件 - Soft UI Evolution 风格 =====

/**
 * 基础应用卡片 - 增强版
 * @param modifier 修饰符
 * @param backgroundColor 背景颜色
 * @param elevation 阴影高度
 * @param content 内容
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 2.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = LargeRoundedCornerShape,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

/**
 * 可交互卡片 - Soft UI Evolution 风格
 * 添加悬停和点击效果
 * @param modifier 修饰符
 * @param onClick 点击事件
 * @param backgroundColor 背景颜色
 * @param content 内容
 */
@Composable
fun InteractiveCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 300f
        ),
        label = "card_scale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 2.dp,
        animationSpec = tween(durationMillis = 150),
        label = "card_elevation"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = LargeRoundedCornerShape,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

/**
 * 渐变卡片 - Soft UI Evolution 风格
 * @param modifier 修饰符
 * @param gradientColors 渐变色列表
 * @param content 内容
 */
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    gradientColors: List<Color>,
    content: @Composable () -> Unit
) {
    val gradient = Brush.horizontalGradient(
        colors = gradientColors
    )

    Card(
        modifier = modifier,
        shape = LargeRoundedCornerShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .background(gradient, LargeRoundedCornerShape)
                .padding(16.dp)
        ) {
            content()
        }
    }
}

/**
 * 信息提示卡片 - Soft UI Evolution 风格
 * @param title 标题
 * @param message 消息内容
 * @param type 提示类型
 * @param modifier 修饰符
 */
@Composable
fun InfoCard(
    title: String,
    message: String,
    type: InfoCardType = InfoCardType.INFO,
    modifier: Modifier = Modifier
) {
    val colors = when (type) {
        InfoCardType.INFO -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        InfoCardType.SUCCESS -> Pair(
            SuccessContainer,
            OnSuccessContainer
        )
        InfoCardType.WARNING -> Pair(
            WarningContainer,
            OnWarningContainer
        )
        InfoCardType.ERROR -> Pair(
            ErrorContainer,
            OnErrorContainer
        )
    }

    Card(
        modifier = modifier,
        shape = MediumRoundedCornerShape,
        colors = CardDefaults.cardColors(
            containerColor = colors.first
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = colors.second,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.second
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.second.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// 提示卡片类型
enum class InfoCardType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

// ===== 营养数据项 =====

/**
 * 营养数据显示项 - Soft UI Evolution 风格
 * @param label 标签
 * @param value 数值
 * @param unit 单位
 * @param color 显示颜色
 * @param modifier 修饰符
 */
@Composable
fun NutritionItem(
    label: String,
    value: String,
    unit: String,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = NutritionValueStyle,
            fontSize = 24.sp,
            color = color
        )
        Text(
            text = unit,
            style = NutritionLabelStyle,
            color = color.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = NutritionLabelStyle,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp
        )
    }
}

/**
 * 圆形营养数据项 - Soft UI Evolution 风格
 * @param label 标签
 * @param value 数值
 * @param unit 单位
 * @param color 显示颜色
 * @param size 圆形大小
 * @param modifier 修饰符
 */
@Composable
fun CircularNutritionItem(
    label: String,
    value: String,
    unit: String,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 80.dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(
                    color = color.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = value,
                    style = NutritionValueStyle,
                    fontSize = 20.sp,
                    color = color
                )
                Text(
                    text = unit,
                    style = NutritionLabelStyle,
                    color = color.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = NutritionLabelStyle,
            fontSize = 12.sp
        )
    }
}

// ===== 按钮组件 - Soft UI Evolution 风格 =====

/**
 * 主要按钮 - Soft UI Evolution 风格
 * @param text 按钮文本
 * @param onClick 点击事件
 * @param modifier 修饰符
 * @param enabled 是否启用
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 300f
        ),
        label = "button_scale"
    )

    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .scale(scale),
        enabled = enabled,
        interactionSource = interactionSource,
        shape = ButtonShape,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = text,
            style = ButtonTextStyle
        )
    }
}

/**
 * 次要按钮（轮廓按钮）- Soft UI Evolution 风格
 * @param text 按钮文本
 * @param onClick 点击事件
 * @param modifier 修饰符
 * @param enabled 是否启用
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 300f
        ),
        label = "button_scale"
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .scale(scale),
        enabled = enabled,
        interactionSource = interactionSource,
        shape = ButtonShape,
        border = BorderStroke(
            1.dp,
            if (enabled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
        )
    ) {
        Text(
            text = text,
            style = ButtonTextStyle,
            color = if (enabled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
        )
    }
}

/**
 * 文本按钮 - Soft UI Evolution 风格
 * @param text 按钮文本
 * @param onClick 点击事件
 * @param modifier 修饰符
 * @param enabled 是否启用
 */
@Composable
fun TextOnlyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 300f
        ),
        label = "button_scale"
    )

    TextButton(
        onClick = onClick,
        modifier = modifier.scale(scale),
        enabled = enabled,
        interactionSource = interactionSource,
        shape = ButtonShape
    ) {
        Text(
            text = text,
            style = ButtonTextStyle,
            color = if (enabled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
        )
    }
}

// ===== 标签组件 =====

/**
 * 基础标签 - Soft UI Evolution 风格
 * @param text 标签文本
 * @param color 标签颜色
 * @param modifier 修饰符
 */
@Composable
fun AppLabel(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 状态标签 - Soft UI Evolution 风格
 * @param text 标签文本
 * @param status 状态类型
 * @param modifier 修饰符
 */
@Composable
fun StatusLabel(
    text: String,
    status: LabelStatus,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        LabelStatus.SUCCESS -> Success
        LabelStatus.WARNING -> Warning
        LabelStatus.ERROR -> Error
        LabelStatus.INFO -> MaterialTheme.colorScheme.primary
        LabelStatus.DEFAULT -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    AppLabel(
        text = text,
        color = color,
        modifier = modifier
    )
}

// 标签状态类型
enum class LabelStatus {
    SUCCESS,
    WARNING,
    ERROR,
    INFO,
    DEFAULT
}

// ===== 空状态组件 =====

/**
 * 空状态提示 - Soft UI Evolution 风格
 * @param icon 图标
 * @param title 标题
 * @param description 描述文本
 * @param actionText 操作按钮文本（可选）
 * @param onAction 操作按钮点击事件（可选）
 * @param modifier 修饰符
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
    }
}

// ===== 分隔线组件 =====

/**
 * 水平分隔线 - Soft UI Evolution 风格
 * @param modifier 修饰符
 * @param thickness 厚度
 * @param color 颜色
 */
@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.outlineVariant
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}
