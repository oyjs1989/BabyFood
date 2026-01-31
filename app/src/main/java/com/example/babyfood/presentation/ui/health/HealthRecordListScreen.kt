package com.example.babyfood.presentation.ui.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.presentation.ui.common.AppScaffold
import com.example.babyfood.presentation.ui.common.AppBottomAction
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordListScreen(
    babyId: Long,
    onBack: () -> Unit,
    onAddRecord: () -> Unit,
    viewModel: HealthRecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showExpiredRecords by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadHealthRecords(babyId)
    }

    AppScaffold(
        bottomActions = listOf(
            AppBottomAction(
                icon = Icons.Default.Add,
                label = "添加",
                contentDescription = "添加体检记录",
                onClick = onAddRecord
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 过滤开关
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "显示过期记录",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = showExpiredRecords,
                    onCheckedChange = { showExpiredRecords = it }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val filteredRecords = if (showExpiredRecords) {
                    uiState.healthRecords
                } else {
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    uiState.healthRecords.filter { record ->
                        record.expiryDate == null || record.expiryDate >= today
                    }
                }

                if (filteredRecords.isEmpty()) {
                com.example.babyfood.presentation.theme.EmptyState(
                    icon = Icons.Default.MedicalInformation,
                    title = if (showExpiredRecords) "暂无体检记录" else "暂无有效体检记录",
                    description = if (showExpiredRecords) "点击右下角 + 按钮添加体检记录" else "开启\"显示过期记录\"查看所有记录\n\n或点击右下角 + 按钮添加体检记录"
                )
            } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredRecords) { record ->
                            HealthRecordCard(record = record)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthRecordCard(record: com.example.babyfood.domain.model.HealthRecord) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val isExpired = record.expiryDate != null && record.expiryDate < today

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (isExpired) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = record.recordDate.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                if (isExpired) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("已过期") }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            record.weight?.let { Text(text = "体重: ${it} kg") }
            record.height?.let { Text(text = "身高: ${it} cm") }
            record.headCircumference?.let { Text(text = "头围: ${it} cm") }
            record.ironLevel?.let { Text(text = "铁: ${it} mg/L") }
            record.calciumLevel?.let { Text(text = "钙: ${it} mg/L") }
            record.hemoglobin?.let { Text(text = "血红蛋白: ${it} g/L") }
            record.aiAnalysis?.let { 
                Text(
                    text = "AI分析: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isExpired) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
            if (record.expiryDate != null) {
                Text(
                    text = "有效期至: ${record.expiryDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}