package com.example.babyfood.presentation.ui.inventory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.babyfood.domain.model.ExpiryStatus
import com.example.babyfood.domain.model.InventoryItem
import com.example.babyfood.presentation.theme.OnWarningContainer
import com.example.babyfood.presentation.theme.Warning

/**
 * 库存物品卡片组件
 * 显示食材图片、名称、数量、保质期状态等信息
 */
@Composable
fun InventoryItemCard(
    item: InventoryItem,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val expiryStatus = item.getExpiryStatus()
    val statusColor = getStatusColor(
        expiryStatus,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.onError,
        MaterialTheme.colorScheme.surfaceVariant
    )
    val statusTextColor = getStatusTextColor(
        expiryStatus,
        MaterialTheme.colorScheme.onError,
        MaterialTheme.colorScheme.onSurfaceVariant
    )
    val statusText = getStatusText(expiryStatus, item)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 食材图片
            FoodImage(item = item)

            // 食材信息
            androidx.compose.foundation.layout.Row(
                modifier = androidx.compose.ui.Modifier.weight(1f)
            ) {
                FoodInfo(
                    item = item,
                    expiryStatus = expiryStatus,
                    statusColor = statusColor,
                    statusTextColor = statusTextColor,
                    statusText = statusText
                )
            }

            // 操作按钮
            ActionButtons(onEdit = onEdit, onDelete = onDelete)
        }
    }
}

@Composable
private fun FoodImage(item: InventoryItem) {
    Box(modifier = Modifier.size(88.dp)) {
        if (item.foodImageUrl != null) {
            AsyncImage(
                model = item.foodImageUrl,
                contentDescription = "食材图片",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.foodName.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun FoodInfo(
    item: InventoryItem,
    expiryStatus: ExpiryStatus,
    statusColor: androidx.compose.ui.graphics.Color,
    statusTextColor: androidx.compose.ui.graphics.Color,
    statusText: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.foodName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${item.quantity}${item.unit}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 保质期标签
        Text(
            text = statusText,
            style = MaterialTheme.typography.labelSmall,
            color = statusTextColor,
            modifier = Modifier
                .background(statusColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )

        // 提示文字
        ExpiryHint(expiryStatus = expiryStatus)
    }
}

@Composable
private fun ExpiryHint(expiryStatus: ExpiryStatus) {
    if (expiryStatus == ExpiryStatus.EXPIRED) {
        Text(
            text = "今日到期，请尽快食用！",
            style = MaterialTheme.typography.bodySmall,
            color = Warning,
            modifier = Modifier.padding(top = 2.dp)
        )
    } else if (expiryStatus == ExpiryStatus.URGENT) {
        Text(
            text = "3天内食用，建议尽快安排",
            style = MaterialTheme.typography.bodySmall,
            color = Warning,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun ActionButtons(onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "编辑",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "删除",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun getStatusColor(
    expiryStatus: ExpiryStatus,
    errorColor: androidx.compose.ui.graphics.Color,
    onErrorColor: androidx.compose.ui.graphics.Color,
    surfaceVariantColor: androidx.compose.ui.graphics.Color
): androidx.compose.ui.graphics.Color {
    return when (expiryStatus) {
        ExpiryStatus.EXPIRED -> errorColor
        ExpiryStatus.URGENT -> errorColor
        ExpiryStatus.WARNING -> Warning
        ExpiryStatus.NORMAL -> surfaceVariantColor
    }
}

private fun getStatusTextColor(
    expiryStatus: ExpiryStatus,
    onErrorColor: androidx.compose.ui.graphics.Color,
    onSurfaceVariantColor: androidx.compose.ui.graphics.Color
): androidx.compose.ui.graphics.Color {
    return when (expiryStatus) {
        ExpiryStatus.EXPIRED -> onErrorColor
        ExpiryStatus.URGENT -> onErrorColor
        ExpiryStatus.WARNING -> OnWarningContainer
        ExpiryStatus.NORMAL -> onSurfaceVariantColor
    }
}

private fun getStatusText(expiryStatus: ExpiryStatus, item: InventoryItem): String {
    return when (expiryStatus) {
        ExpiryStatus.EXPIRED -> "已过期"
        ExpiryStatus.URGENT -> "已过期"
        ExpiryStatus.WARNING -> "3天内食用"
        ExpiryStatus.NORMAL -> "${item.getRemainingDays()}天后到期"
    }
}