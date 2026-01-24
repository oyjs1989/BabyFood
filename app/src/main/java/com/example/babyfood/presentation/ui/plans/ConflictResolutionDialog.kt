package com.example.babyfood.presentation.ui.plans

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.babyfood.domain.model.ConflictResolution
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.PlanConflict

/**
 * 冲突处理对话框
 * @param conflicts 冲突列表
 * @param onDismiss 关闭对话框回调
 * @param onResolve 解决冲突回调，参数为冲突解决策略
 */
@Composable
fun ConflictResolutionDialog(
    conflicts: List<PlanConflict>,
    onDismiss: () -> Unit,
    onResolve: (ConflictResolution) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "检测到冲突",
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Column {
                Text(
                    text = "发现 ${conflicts.size} 个冲突，请选择处理方式：",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(conflicts) { conflict ->
                        ConflictItem(conflict)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onResolve(ConflictResolution.OVERWRITE_ALL) },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("覆盖所有")
            }
        },
        dismissButton = {
            Column {
                TextButton(onClick = { onResolve(ConflictResolution.SKIP_CONFLICTS) }) {
                    Text("跳过冲突")
                }
                TextButton(onClick = { onResolve(ConflictResolution.CANCEL) }) {
                    Text("取消")
                }
            }
        }
    )
}

/**
 * 冲突项显示
 */
@Composable
private fun ConflictItem(conflict: PlanConflict) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "冲突：${conflict.existingPlan.plannedDate.year}年${conflict.existingPlan.plannedDate.monthNumber}月${conflict.existingPlan.plannedDate.dayOfMonth}日 ${try { MealPeriod.valueOf(conflict.existingPlan.mealPeriod).displayName } catch (e: Exception) { conflict.existingPlan.mealPeriod }}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Row(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "现有：食谱ID ${conflict.existingPlan.recipeId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "推荐：${conflict.newPlan.recipeId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}