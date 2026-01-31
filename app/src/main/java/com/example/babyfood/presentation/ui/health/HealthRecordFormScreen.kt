package com.example.babyfood.presentation.ui.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.presentation.ui.common.AppScaffold
import com.example.babyfood.presentation.ui.common.AppBottomAction
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordFormScreen(
    babyId: Long,
    recordId: Long = 0,
    onBack: () -> Unit,
    onSave: () -> Unit,
    viewModel: HealthRecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var recordDate by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var headCircumference by remember { mutableStateOf("") }
    var ironLevel by remember { mutableStateOf("") }
    var calciumLevel by remember { mutableStateOf("") }
    var hemoglobin by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // AI 确认对话框状态
    var showAiConfirmDialog by remember { mutableStateOf(false) }
    var aiAnalysisResult by remember { mutableStateOf<String?>(null) }
    var editableAiAnalysis by remember { mutableStateOf("") }
    var expiryDays by remember { mutableStateOf(7) }
    var pendingRecordId by remember { mutableStateOf(0L) }

    // 未保存修改跟踪
    var hasUnsavedChanges by remember { mutableStateOf(false) }
    var showExitConfirmationDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val isEditing = recordId > 0

    // 返回键处理
    BackHandler(enabled = true) {
        if (hasUnsavedChanges) {
            showExitConfirmationDialog = true
        } else {
            onBack()
        }
    }

    LaunchedEffect(recordId) {
        if (isEditing) {
            val record = uiState.healthRecords.find { it.id == recordId }
            record?.let {
                recordDate = it.recordDate.toString()
                weight = it.weight?.toString() ?: ""
                height = it.height?.toString() ?: ""
                headCircumference = it.headCircumference?.toString() ?: ""
                ironLevel = it.ironLevel?.toString() ?: ""
                calciumLevel = it.calciumLevel?.toString() ?: ""
                hemoglobin = it.hemoglobin?.toString() ?: ""
                notes = it.notes ?: ""
            }
        } else {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            recordDate = today.toString()
        }
    }

    // 监听保存状态，显示 AI 确认对话框
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved && !isEditing && pendingRecordId == 0L) {
            // 查找最新保存的记录
            val latestRecord = uiState.healthRecords
                .filter { it.babyId == babyId }
                .maxByOrNull { it.id }
            
            latestRecord?.aiAnalysis?.let { analysis ->
                pendingRecordId = latestRecord.id
                aiAnalysisResult = analysis
                editableAiAnalysis = analysis
                showAiConfirmDialog = true
            }
        }
    }

    // 保存函数
    val saveRecord: () -> Unit = {
        val record = com.example.babyfood.domain.model.HealthRecord(
            id = recordId,
            babyId = babyId,
            recordDate = kotlinx.datetime.LocalDate.parse(recordDate),
            weight = weight.toFloatOrNull(),
            height = height.toFloatOrNull(),
            headCircumference = headCircumference.toFloatOrNull(),
            ironLevel = ironLevel.toFloatOrNull(),
            calciumLevel = calciumLevel.toFloatOrNull(),
            hemoglobin = hemoglobin.toFloatOrNull(),
            notes = notes.ifBlank { null },
            aiAnalysis = null,
            expiryDate = null
        )

        // 保存记录（会自动触发 AI 分析）
        viewModel.saveHealthRecord(record)
        hasUnsavedChanges = false
        onSave()
    }

    AppScaffold(
        bottomActions = listOf(
            AppBottomAction(
                icon = Icons.Default.Check,
                label = "保存",
                contentDescription = "保存体检记录",
                enabled = recordDate.isNotBlank(),
                onClick = saveRecord
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = recordDate,
                onValueChange = {
                    recordDate = it
                    hasUnsavedChanges = true
                },
                label = { Text("体检日期 (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = weight,
                onValueChange = {
                    weight = it
                    hasUnsavedChanges = true
                },
                label = { Text("体重 (kg)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = height,
                onValueChange = {
                    height = it
                    hasUnsavedChanges = true
                },
                label = { Text("身高 (cm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = headCircumference,
                onValueChange = {
                    headCircumference = it
                    hasUnsavedChanges = true
                },
                label = { Text("头围 (cm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = ironLevel,
                onValueChange = {
                    ironLevel = it
                    hasUnsavedChanges = true
                },
                label = { Text("铁 (mg/L)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = calciumLevel,
                onValueChange = {
                    calciumLevel = it
                    hasUnsavedChanges = true
                },
                label = { Text("钙 (mg/L)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = hemoglobin,
                onValueChange = {
                    hemoglobin = it
                    hasUnsavedChanges = true
                },
                label = { Text("血红蛋白 (g/L)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = {
                    notes = it
                    hasUnsavedChanges = true
                },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (uiState.error != null) {
                Text(
                    text = "错误: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // AI 确认对话框 - INSIDE AppScaffold content, OUTSIDE Column
        if (showAiConfirmDialog) {
            AiAnalysisConfirmDialog(
                aiAnalysis = editableAiAnalysis,
                onAnalysisChange = { editableAiAnalysis = it },
                expiryDays = expiryDays,
                onExpiryDaysChange = { expiryDays = it },
                onConfirm = {
                    // 计算有效期
                    val expiryDate = if (expiryDays > 0) {
                        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                        today.plus(kotlinx.datetime.DatePeriod(days = expiryDays))
                    } else {
                        null
                    }

                    // 更新记录
                    val updatedRecord = com.example.babyfood.domain.model.HealthRecord(
                        id = pendingRecordId,
                        babyId = babyId,
                        recordDate = kotlinx.datetime.LocalDate.parse(recordDate),
                        weight = weight.toFloatOrNull(),
                        height = height.toFloatOrNull(),
                        headCircumference = headCircumference.toFloatOrNull(),
                        ironLevel = ironLevel.toFloatOrNull(),
                        calciumLevel = calciumLevel.toFloatOrNull(),
                        hemoglobin = hemoglobin.toFloatOrNull(),
                        notes = notes.ifBlank { null },
                        aiAnalysis = editableAiAnalysis.ifBlank { null },
                        expiryDate = expiryDate,
                        isConfirmed = true
                    )
                    viewModel.saveHealthRecord(updatedRecord)
                    showAiConfirmDialog = false
                    pendingRecordId = 0L
                    onSave()
                },
                onDismiss = {
                    showAiConfirmDialog = false
                    pendingRecordId = 0L
                    onSave()
                }
            )
        }

        // 离开确认对话框 - INSIDE AppScaffold content, OUTSIDE Column
        if (showExitConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showExitConfirmationDialog = false },
                title = { Text("未保存的修改") },
                text = { Text("您有未保存的修改，是否要保存？") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            saveRecord()
                            showExitConfirmationDialog = false
                        },
                        enabled = recordDate.isNotBlank()
                    ) {
                        Text("保存")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitConfirmationDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
private fun AiAnalysisConfirmDialog(
    aiAnalysis: String,
    onAnalysisChange: (String) -> Unit,
    expiryDays: Int,
    onExpiryDaysChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI 分析结论") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "AI 已为您生成健康分析结论，您可以确认或修改：",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                OutlinedTextField(
                    value = aiAnalysis,
                    onValueChange = onAnalysisChange,
                    label = { Text("分析结论") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    minLines = 3
                )
                
                Text(
                    text = "数据有效期：",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = expiryDays == 7,
                        onClick = { onExpiryDaysChange(7) },
                        label = { Text("7天") }
                    )
                    FilterChip(
                        selected = expiryDays == 30,
                        onClick = { onExpiryDaysChange(30) },
                        label = { Text("30天") }
                    )
                    FilterChip(
                        selected = expiryDays == 90,
                        onClick = { onExpiryDaysChange(90) },
                        label = { Text("90天") }
                    )
                    FilterChip(
                        selected = expiryDays == 0,
                        onClick = { onExpiryDaysChange(0) },
                        label = { Text("永久") }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}