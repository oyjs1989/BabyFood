package com.example.babyfood.presentation.ui.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

    val scrollState = rememberScrollState()
    val isEditing = recordId > 0

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "编辑体检记录" else "添加体检记录") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = recordDate,
                onValueChange = { recordDate = it },
                label = { Text("体检日期 (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("体重 (kg)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("身高 (cm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = headCircumference,
                onValueChange = { headCircumference = it },
                label = { Text("头围 (cm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = ironLevel,
                onValueChange = { ironLevel = it },
                label = { Text("铁 (mg/L)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = calciumLevel,
                onValueChange = { calciumLevel = it },
                label = { Text("钙 (mg/L)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = hemoglobin,
                onValueChange = { hemoglobin = it },
                label = { Text("血红蛋白 (g/L)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
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
                    
                    // 延迟调用 onSave，等待 AI 分析完成
                    scope.launch {
                        kotlinx.coroutines.delay(500)
                        onSave()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = recordDate.isNotBlank()
            ) {
                Text("保存")
            }

            if (uiState.error != null) {
                Text(
                    text = "错误: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }

    // AI 确认对话框
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
            },
            onDismiss = {
                showAiConfirmDialog = false
                pendingRecordId = 0L
            }
        )
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