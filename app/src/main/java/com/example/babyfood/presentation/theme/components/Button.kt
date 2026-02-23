package com.example.babyfood.presentation.theme.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.babyfood.presentation.theme.ANIMATION_SCALE_BUTTON_END
import com.example.babyfood.presentation.theme.ANIMATION_SCALE_BUTTON_START
import com.example.babyfood.presentation.theme.AnimationSpecButtonClick
import com.example.babyfood.presentation.theme.ButtonPrimaryShape
import com.example.babyfood.presentation.theme.ButtonSecondaryShape
import com.example.babyfood.presentation.theme.ButtonTextStyle
import com.example.babyfood.presentation.theme.ElevationHover
import com.example.babyfood.presentation.theme.ElevationLevel1
import com.example.babyfood.presentation.theme.ElevationPressed
import com.example.babyfood.presentation.theme.GradientEnd
import com.example.babyfood.presentation.theme.GradientStart
import com.example.babyfood.presentation.theme.Outline
import com.example.babyfood.presentation.theme.SpacingMD
import com.example.babyfood.presentation.theme.SpacingSM
import com.example.babyfood.presentation.theme.SpacingXS
import com.example.babyfood.presentation.theme.TextPrimary
import com.example.babyfood.presentation.theme.TextTertiary
import com.example.babyfood.presentation.theme.Typography

// ===== 主按钮 - 24dp 圆角，45度渐变 =====
// 应用场景：核心操作按钮、主要功能入口

@Composable
fun BabyFoodPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 按钮点击反馈动画：缩放 0.95→1.0，150ms 弹簧动画
    val scale by animateFloatAsState(
        targetValue = if (isPressed) ANIMATION_SCALE_BUTTON_START else ANIMATION_SCALE_BUTTON_END,
        animationSpec = AnimationSpecButtonClick,
        label = "button_scale"
    )

    // 主按钮颜色：45度渐变从 #FF9F69 到 #FFD4BD
    // 注意：Material3 Button不支持自定义Brush背景，使用单一颜色
    val gradientColors = ButtonDefaults.buttonColors(
        containerColor = GradientStart,
        contentColor = Color.White,
        disabledContainerColor = Outline,
        disabledContentColor = TextTertiary
    )

    Button(
        onClick = onClick,
        modifier = modifier.scale(scale),
        enabled = enabled && !loading,
        interactionSource = interactionSource,
        shape = ButtonPrimaryShape,
        colors = gradientColors,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = ElevationLevel1,
            pressedElevation = ElevationPressed,
            hoveredElevation = ElevationHover
        ),
        contentPadding = PaddingValues(
            horizontal = SpacingMD,
            vertical = SpacingSM
        )
    ) {
        if (loading) {
            // 加载状态显示旋转指示器
            Box(
                modifier = Modifier.size(20.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }
        } else {
            Text(
                text = text,
                style = ButtonTextStyle,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ===== 次要按钮 - 16dp 圆角，纯色 =====
// 应用场景：小型标签按钮、次要功能按钮

@Composable
fun BabyFoodSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 按钮点击反馈动画：缩放 0.95→1.0，150ms 弹簧动画
    val scale by animateFloatAsState(
        targetValue = if (isPressed) ANIMATION_SCALE_BUTTON_START else ANIMATION_SCALE_BUTTON_END,
        animationSpec = AnimationSpecButtonClick,
        label = "button_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier.scale(scale),
        enabled = enabled,
        interactionSource = interactionSource,
        shape = ButtonSecondaryShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Outline,
            contentColor = TextPrimary,
            disabledContainerColor = Outline,
            disabledContentColor = TextTertiary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,  // 次要按钮无阴影
            pressedElevation = ElevationPressed,
            hoveredElevation = ElevationLevel1
        ),
        contentPadding = PaddingValues(
            horizontal = SpacingSM,
            vertical = SpacingXS
        )
    ) {
        Text(
            text = text,
            style = Typography.labelMedium,
            textAlign = TextAlign.Center
        )
    }
}

// ===== 图标按钮 =====
// 应用场景：带有图标的按钮

@Composable
fun BabyFoodIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 按钮点击反馈动画
    val scale by animateFloatAsState(
        targetValue = if (isPressed) ANIMATION_SCALE_BUTTON_START else ANIMATION_SCALE_BUTTON_END,
        animationSpec = AnimationSpecButtonClick,
        label = "icon_button_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier.scale(scale).size(48.dp),
        enabled = enabled,
        interactionSource = interactionSource,
        shape = ButtonSecondaryShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = TextTertiary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            hoveredElevation = 0.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        icon()
    }
}

// ===== 禁用按钮 =====
// 无阴影，不可点击，文字置灰

@Composable
fun BabyFoodDisabledButton(
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { },  // 禁用按钮无点击事件
        modifier = modifier,
        enabled = false,
        shape = ButtonPrimaryShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Outline,
            contentColor = TextTertiary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,  // 禁用按钮无阴影
            pressedElevation = 0.dp,
            hoveredElevation = 0.dp
        ),
        contentPadding = PaddingValues(
            horizontal = SpacingMD,
            vertical = SpacingSM
        )
    ) {
        Text(
            text = text,
            style = ButtonTextStyle,
            color = TextTertiary
        )
    }
}