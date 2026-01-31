package com.example.babyfood.presentation.ui.plans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDate

/**
 * 日期范围选择器对话框
 * @param onDismiss 关闭对话框回调
 * @param onConfirm 确认回调，参数为开始日期和天数
 */
@Composable
fun DateRangePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, Int) -> Unit
) {
    var startDate by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date) }
    var days by remember { mutableStateOf(7) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择日期范围") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 开始日期选择
                DatePicker(
                    label = "开始日期",
                    date = startDate,
                    onDateChange = { startDate = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 天数滑块
                Text(
                    text = "天数：${days}天",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = days.toFloat(),
                    onValueChange = { days = it.toInt().coerceIn(1, 14) },
                    valueRange = 1f..14f,
                    steps = 12
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 结束日期预览
                val endDate = kotlinx.datetime.LocalDate.fromEpochDays(startDate.toEpochDays() + (days - 1))
                Text(
                    text = "结束日期：${endDate.year}年${endDate.monthNumber}月${endDate.dayOfMonth}日",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(startDate, days) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 简单的日期选择器
 * @param label 标签
 * @param date 当前日期
 * @param onDateChange 日期变化回调
 */
@Composable
private fun DatePicker(
    label: String,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 前一天
                IconButton(
                    onClick = {
                        val newDate = kotlinx.datetime.LocalDate.fromEpochDays(date.toEpochDays() - 1)
                        onDateChange(newDate)
                    }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "前一天")
                }
                
                // 当前日期
                Text(
                    text = "${date.year}年${date.monthNumber}月${date.dayOfMonth}日",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // 后一天
                IconButton(
                    onClick = {
                        val newDate = kotlinx.datetime.LocalDate.fromEpochDays(date.toEpochDays() + 1)
                        onDateChange(newDate)
                    }
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "后一天")
                }
            }
        }
    }
}