package com.example.babyfood.presentation.ui.plans

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDate

/**
 * 日期范围选择器对话框
 * 
 * 使用Material3 DateRangePicker提供更好的用户体验
 * 
 * @param onDismiss 关闭对话框回调
 * @param onConfirm 确认回调，参数为开始日期和结束日期
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, LocalDate) -> Unit
) {
    Log.d("DateRangePickerDialog", "========== 开始选择日期范围 ==========")
    
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    
    // 开始日期选择器状态
    var startDate by remember { mutableStateOf<LocalDate?>(today) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = today.toEpochDays().toLong() * 24 * 60 * 60 * 1000
    )
    
    // 结束日期选择器状态 - 默认7天后
    var endDate by remember { mutableStateOf<LocalDate?>(LocalDate.fromEpochDays(today.toEpochDays() + 6)) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = LocalDate.fromEpochDays(today.toEpochDays() + 6).toEpochDays().toLong() * 24 * 60 * 60 * 1000
    )
    
    // 开始日期选择器
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        startDatePickerState.selectedDateMillis?.let { millis ->
                            startDate = LocalDate.fromEpochDays((millis / (24 * 60 * 60 * 1000)).toInt())
                            Log.d("DateRangePickerDialog", "✓ 选择开始日期: $startDate")
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(
                state = startDatePickerState,
                title = { Text("选择开始日期") }
            )
        }
    }
    
    // 结束日期选择器
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        endDatePickerState.selectedDateMillis?.let { millis ->
                            val selectedEndDate = LocalDate.fromEpochDays((millis / (24 * 60 * 60 * 1000)).toInt())
                            
                            // 验证结束日期 >= 开始日期
                            val startValue = startDate
                            if (startValue != null && selectedEndDate < startValue) {
                                Log.d("DateRangePickerDialog", "⚠️ 结束日期不能早于开始日期")
                                endDate = startValue
                            } else {
                                endDate = selectedEndDate
                            }
                            Log.d("DateRangePickerDialog", "✓ 选择结束日期: $endDate")
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(
                state = endDatePickerState,
                title = { Text("选择结束日期") }
            )
        }
    }
    
    // 主对话框
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择日期范围") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 开始日期选择
                DateSelectRow(
                    label = "开始日期",
                    date = startDate,
                    onClick = { showStartDatePicker = true }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 结束日期选择
                DateSelectRow(
                    label = "结束日期",
                    date = endDate,
                    onClick = { showEndDatePicker = true }
                )
                
                // 显示总天数
                val startValue = startDate
                val endValue = endDate
                if (startValue != null && endValue != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    val days = (endValue.toEpochDays() - startValue.toEpochDays() + 1).toInt()
                    Text(
                        text = "共 $days 天",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val start = startDate
                    val end = endDate
                    if (start != null && end != null && end >= start) {
                        Log.d("DateRangePickerDialog", "✓ 确认日期范围: $start ~ $end")
                        Log.d("DateRangePickerDialog", "========== 日期范围选择完成 ==========")
                        onConfirm(start, end)
                    } else {
                        Log.d("DateRangePickerDialog", "⚠️ 日期范围无效")
                    }
                },
                enabled = startDate != null && endDate != null && endDate!! >= startDate!!
            ) {
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
 * 日期选择行组件
 * 
 * @param label 标签
 * @param date 选中的日期
 * @param onClick 点击回调
 */
@Composable
private fun DateSelectRow(
    label: String,
    date: LocalDate?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = label,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
        )
        
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (date != null) {
                    "${date!!.year}年${date!!.monthNumber}月${date!!.dayOfMonth}日"
                } else {
                    "选择日期"
                }
            )
        }
    }
}