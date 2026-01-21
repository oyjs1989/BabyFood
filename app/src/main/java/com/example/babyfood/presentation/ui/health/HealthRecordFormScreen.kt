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
    var recordDate by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var headCircumference by remember { mutableStateOf("") }
    var ironLevel by remember { mutableStateOf("") }
    var calciumLevel by remember { mutableStateOf("") }
    var hemoglobin by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

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
                    viewModel.saveHealthRecord(record)
                    onSave()
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
}