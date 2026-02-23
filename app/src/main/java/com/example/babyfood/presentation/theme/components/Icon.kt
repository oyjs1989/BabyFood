package com.example.babyfood.presentation.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.babyfood.presentation.theme.CardBackground
import com.example.babyfood.presentation.theme.ElevationLevel1
import com.example.babyfood.presentation.theme.IconMediumRadius
import com.example.babyfood.presentation.theme.IconMediumShape
import com.example.babyfood.presentation.theme.IconSizeLarge
import com.example.babyfood.presentation.theme.IconSizeMedium
import com.example.babyfood.presentation.theme.IconSizeSmall
import com.example.babyfood.presentation.theme.IconSmallRadius
import com.example.babyfood.presentation.theme.IconSmallShape
import com.example.babyfood.presentation.theme.Outline
import com.example.babyfood.presentation.theme.Primary
import com.example.babyfood.presentation.theme.SpacingXS

// ===== 图标容器组件 =====
// 应用场景：小型图标外容器（12dp 圆角）、中型图标外容器（16dp 圆角）

// 小型图标容器 - 12dp 圆角
@Composable
fun BabyFoodSmallIconContainer(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    iconColor: Color = LocalContentColor.current,
    backgroundColor: Color = CardBackground,
    onClick: (() -> Unit)? = null
) {
    if (onClick != null) {
        IconButton(
            onClick = onClick,
            modifier = modifier.size(IconSizeSmall),
            content = {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconColor,
                    modifier = Modifier.size(IconSizeSmall * 0.6f)
                )
            }
        )
    } else {
        Surface(
            modifier = modifier.size(IconSizeSmall),
            shape = IconSmallShape,
            color = backgroundColor,
            tonalElevation = ElevationLevel1,
            shadowElevation = ElevationLevel1
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconColor,
                    modifier = Modifier.size(IconSizeSmall * 0.6f)
                )
            }
        }
    }
}

// 中型图标容器 - 16dp 圆角
@Composable
fun BabyFoodMediumIconContainer(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    iconColor: Color = LocalContentColor.current,
    backgroundColor: Color = CardBackground,
    onClick: (() -> Unit)? = null
) {
    if (onClick != null) {
        IconButton(
            onClick = onClick,
            modifier = modifier.size(IconSizeMedium),
            content = {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconColor,
                    modifier = Modifier.size(IconSizeMedium * 0.6f)
                )
            }
        )
    } else {
        Surface(
            modifier = modifier.size(IconSizeMedium),
            shape = IconMediumShape,
            color = backgroundColor,
            tonalElevation = ElevationLevel1,
            shadowElevation = ElevationLevel1
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconColor,
                    modifier = Modifier.size(IconSizeMedium * 0.6f)
                )
            }
        }
    }
}

// 大型图标容器 - 48dp，16dp 圆角
@Composable
fun BabyFoodLargeIconContainer(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    iconColor: Color = LocalContentColor.current,
    backgroundColor: Color = CardBackground
) {
    Surface(
        modifier = modifier.size(IconSizeLarge),
        shape = IconMediumShape,
        color = backgroundColor,
        tonalElevation = ElevationLevel1,
        shadowElevation = ElevationLevel1
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(IconSizeLarge * 0.6f)
            )
        }
    }
}

// ===== 状态图标容器 =====
// 用于显示不同状态的图标（成功、警告、错误）

@Composable
fun BabyFoodStatusIconContainer(
    icon: ImageVector,
    status: StatusType,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val (backgroundColor, iconColor) = when (status) {
        StatusType.SUCCESS -> Color(0xFFE8F5E9) to Color(0xFF52C41A)
        StatusType.WARNING -> Color(0xFFFFF3E0) to Color(0xFFFAAD14)
        StatusType.ERROR -> Color(0xFFFFEBEE) to Color(0xFFF5222D)
        StatusType.INFO -> Color(0xFFE3F2FD) to Color(0xFF5AC8FA)
    }

    Surface(
        modifier = modifier.size(IconSizeMedium),
        shape = IconMediumShape,
        color = backgroundColor,
        tonalElevation = ElevationLevel1,
        shadowElevation = ElevationLevel1
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(IconSizeMedium * 0.6f)
            )
        }
    }
}

// ===== 圆形图标按钮 =====
// 用于主要操作的圆形图标按钮

@Composable
fun BabyFoodCircularIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    iconColor: Color = Color.White,
    backgroundColor: Color = Primary
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(IconSizeMedium)
    ) {
        Box(
            modifier = Modifier
                .size(IconSizeMedium)
                .background(
                    color = backgroundColor,
                    shape = androidx.compose.foundation.shape.CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(IconSizeMedium * 0.5f)
            )
        }
    }
}

// ===== 品牌图标容器 =====
// 使用品牌色（主暖橙）的图标容器

@Composable
fun BabyFoodBrandIconContainer(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: IconSize = IconSize.MEDIUM
) {
    val iconSize = when (size) {
        IconSize.SMALL -> IconSizeSmall
        IconSize.MEDIUM -> IconSizeMedium
        IconSize.LARGE -> IconSizeLarge
    }

    Surface(
        modifier = modifier.size(iconSize),
        shape = IconMediumShape,
        color = Primary.copy(alpha = 0.1f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Primary,
                modifier = Modifier.size(iconSize * 0.6f)
            )
        }
    }
}

// ===== 辅助类型 =====

enum class StatusType {
    SUCCESS,
    WARNING,
    ERROR,
    INFO
}

enum class IconSize {
    SMALL,
    MEDIUM,
    LARGE
}