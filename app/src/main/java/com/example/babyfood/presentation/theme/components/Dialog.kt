package com.example.babyfood.presentation.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.babyfood.presentation.theme.ButtonHeightMedium
import com.example.babyfood.presentation.theme.ButtonPrimaryShape
import com.example.babyfood.presentation.theme.ButtonSecondaryShape
import com.example.babyfood.presentation.theme.CardBackground
import com.example.babyfood.presentation.theme.DialogRadius
import com.example.babyfood.presentation.theme.DialogShape
import com.example.babyfood.presentation.theme.ElevationLevel2
import com.example.babyfood.presentation.theme.ElevationLevel3
import com.example.babyfood.presentation.theme.GradientEnd
import com.example.babyfood.presentation.theme.GradientStart
import com.example.babyfood.presentation.theme.Outline
import com.example.babyfood.presentation.theme.Primary
import com.example.babyfood.presentation.theme.SpacingMD
import com.example.babyfood.presentation.theme.SpacingSM
import com.example.babyfood.presentation.theme.SpacingXS
import com.example.babyfood.presentation.theme.TextPrimary
import com.example.babyfood.presentation.theme.TextSecondary
import com.example.babyfood.presentation.theme.TextTertiary

// ===== 全局对话框组件 - 28dp 圆角 =====
// 应用场景：全局弹窗、模态框、底部浮层
// 默认使用 Level 2 阴影（8dp 高度，16dp 模糊半径）
// 重要提示弹窗使用 Level 3 阴影（24dp 高度，32dp 模糊半径）

@Composable
fun BabyFoodDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    icon: ImageVector? = null,
    iconColor: Color = Primary,
    modifier: Modifier = Modifier,
    elevationLevel: androidx.compose.ui.unit.Dp = ElevationLevel2
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier
                        .padding(SpacingSM)
                        .width(40.dp)
                        .height(40.dp)
                )
            }
        } else null,
        title = title,
        text = text,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        modifier = modifier,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        ),
        shape = DialogShape,
        tonalElevation = elevationLevel,
        containerColor = CardBackground,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        iconContentColor = iconColor
    )
}

// ===== 自定义对话框组件 =====
// 提供更灵活的布局和样式

@Composable
fun BabyFoodCustomDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
    buttons: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(SpacingMD),
            shape = DialogShape,
            color = CardBackground,
            tonalElevation = ElevationLevel2,
            shadowElevation = ElevationLevel2
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingMD)
            ) {
                // 标题
                if (title != null) {
                    title()
                    Spacer(modifier = Modifier.height(SpacingSM))
                }

                // 内容
                content()

                Spacer(modifier = Modifier.height(SpacingMD))

                // 按钮
                buttons()
            }
        }
    }
}

// ===== 确认对话框 =====
// 标准的确认对话框样式
// 使用 Level 3 阴影（重要提示弹窗）

@Composable
fun BabyFoodConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "确认",
    dismissText: String = "取消",
    modifier: Modifier = Modifier
) {
    BabyFoodDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                shape = ButtonPrimaryShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = Color.White
                )
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = ButtonSecondaryShape
            ) {
                Text(dismissText, color = TextSecondary)
            }
        },
        modifier = modifier,
        elevationLevel = ElevationLevel3  // 确认对话框使用 Level 3 阴影
    )
}

// ===== 信息对话框 =====
// 仅显示信息，无操作按钮

@Composable
fun BabyFoodInfoDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    BabyFoodDialog(
        onDismissRequest = onDismiss,
        icon = icon,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = ButtonPrimaryShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = Color.White
                )
            ) {
                Text("确定")
            }
        },
        modifier = modifier
    )
}

// ===== 底部弹窗组件 =====
// 从底部弹出的对话框样式

@Composable
fun BabyFoodBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(SpacingMD),
            shape = RoundedCornerShape(
                topStart = DialogRadius,
                topEnd = DialogRadius,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            color = CardBackground,
            tonalElevation = ElevationLevel2,
            shadowElevation = ElevationLevel2
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingMD),
                content = content
            )
        }
    }
}

// ===== 加载对话框 =====
// 显示加载状态的对话框

@Composable
fun BabyFoodLoadingDialog(
    message: String = "加载中...",
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier.padding(SpacingMD),
            shape = DialogShape,
            color = CardBackground,
            tonalElevation = ElevationLevel2,
            shadowElevation = ElevationLevel2
        ) {
            Column(
                modifier = Modifier
                    .padding(SpacingMD)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpacingSM)
            ) {
                // 加载指示器
                androidx.compose.material3.CircularProgressIndicator(
                    color = Primary,
                    strokeWidth = 2.dp
                )

                // 加载文字
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}