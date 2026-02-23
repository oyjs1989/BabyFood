package com.example.babyfood.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.babyfood.presentation.theme.Error
import com.example.babyfood.presentation.theme.OnSurface

/**
 * 全局错误提示弹窗
 * 用于显示用户操作失败的错误信息
 *
 * @param errorMessage 错误信息
 * @param onDismiss 关闭弹窗回调
 * @param properties 对话框属性（默认为 dismissOnClickOutside = true）
 */
@Composable
fun ErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit,
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true
    )
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = properties,
        icon = {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "错误",
                tint = Error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        },
        title = {
            Text(
                text = "操作失败",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "确定",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        iconContentColor = Error
    )
}

/**
 * 简化版错误提示弹窗（带自动关闭）
 *
 * @param errorMessage 错误信息
 * @param isVisible 是否显示
 * @param onDismiss 关闭弹窗回调
 * @param autoDismissDelay 自动关闭延迟时间（毫秒），默认 5000ms
 */
@Composable
fun AutoDismissErrorDialog(
    errorMessage: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    autoDismissDelay: Long = 5000
) {
    if (isVisible) {
        androidx.compose.runtime.LaunchedEffect(isVisible) {
            kotlinx.coroutines.delay(autoDismissDelay)
            onDismiss()
        }

        ErrorDialog(
            errorMessage = errorMessage,
            onDismiss = onDismiss
        )
    }
}