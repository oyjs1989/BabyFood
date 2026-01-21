package com.example.babyfood.presentation.ui.baby

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyFormScreen(
    babyId: Long = 0,
    onBack: () -> Unit,
    onSave: () -> Unit,
    viewModel: BabyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var preferences by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val isEditing = babyId > 0

    LaunchedEffect(babyId) {
        if (isEditing) {
            val baby = uiState.babies.find { it.id == babyId }
            baby?.let {
                name = it.name
                birthDate = it.birthDate.toString()
                weight = it.weight?.toString() ?: ""
                height = it.height?.toString() ?: ""
                allergies = it.getEffectiveAllergies().joinToString(", ")
                preferences = it.getEffectivePreferences().joinToString(", ")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "编辑宝宝" else "添加宝宝") },
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
            // 姓名
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("宝宝姓名") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 出生日期
            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("出生日期 (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("例如: 2024-01-01") }
            )

            // 体重
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("体重 (kg)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            // 身高
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("身高 (cm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            // 过敏食材
            OutlinedTextField(
                value = allergies,
                onValueChange = { allergies = it },
                label = { Text("过敏食材 (用逗号分隔)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("例如: 牛奶, 鸡蛋, 花生") },
                minLines = 2
            )

            // 偏好食材
            OutlinedTextField(
                value = preferences,
                onValueChange = { preferences = it },
                label = { Text("偏好食材 (用逗号分隔)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("例如: 苹果, 香蕉, 南瓜") },
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 保存按钮
            Button(
                onClick = {
                    val baby = com.example.babyfood.domain.model.Baby(
                        id = babyId,
                        name = name,
                        birthDate = kotlinx.datetime.LocalDate.parse(birthDate),
                        weight = weight.toFloatOrNull(),
                        height = height.toFloatOrNull(),
                        allergies = if (allergies.isBlank()) emptyList() 
                            else allergies.split(",").map { it.trim() }
                                .map { com.example.babyfood.domain.model.AllergyItem(it) },
                        preferences = if (preferences.isBlank()) emptyList() 
                            else preferences.split(",").map { it.trim() }
                                .map { com.example.babyfood.domain.model.PreferenceItem(it) }
                    )
                    viewModel.saveBaby(baby)
                    onSave()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && birthDate.isNotBlank()
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