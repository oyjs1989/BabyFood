package com.example.babyfood.presentation.ui.baby

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllergyManagementScreen(
    babyId: Long,
    onBack: () -> Unit,
    viewModel: BabyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val baby = uiState.babies.find { it.id == babyId }
    
    var newAllergy by remember { mutableStateOf("") }
    var expiryDays by remember { mutableStateOf("30") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("过敏食材管理") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (baby != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 添加过敏食材
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "添加过敏食材",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = newAllergy,
                            onValueChange = { newAllergy = it },
                            label = { Text("食材名称") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = expiryDays,
                            onValueChange = { expiryDays = it },
                            label = { Text("有效期（天，留空表示永久）") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Button(
                            onClick = {
                                if (newAllergy.isNotBlank()) {
                                    val expiryDate = if (expiryDays.isNotBlank()) {
                                        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                                        (today + kotlinx.datetime.DatePeriod(days = expiryDays.toInt())).toString()
                                    } else null
                                    
                                    val updatedBaby = baby.copy(
                                        allergies = baby.allergies.toMutableList().apply {
                                            add(com.example.babyfood.domain.model.AllergyItem(newAllergy, expiryDate))
                                        }
                                    )
                                    viewModel.saveBaby(updatedBaby)
                                    newAllergy = ""
                                    expiryDays = "30"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = newAllergy.isNotBlank()
                        ) {
                            Text("添加")
                        }
                    }
                }
                
                // 过敏食材列表
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "过敏食材列表",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (baby.allergies.isEmpty()) {
                            Text(
                                text = "暂无过敏食材",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(baby.allergies) { allergy ->
                                    AllergyItemRow(
                                        allergy = allergy,
                                        onDelete = {
                                            val updatedBaby = baby.copy(
                                                allergies = baby.allergies.toMutableList().apply { remove(allergy) }
                                            )
                                            viewModel.saveBaby(updatedBaby)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AllergyItemRow(
    allergy: com.example.babyfood.domain.model.AllergyItem,
    onDelete: () -> Unit
) {
    val isExpired = allergy.isExpired()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpired) 
                MaterialTheme.colorScheme.surfaceVariant 
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = allergy.ingredient,
                    style = MaterialTheme.typography.bodyLarge
                )
                allergy.expiryDate?.let {
                    Text(
                        text = "有效期至: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isExpired) 
                            MaterialTheme.colorScheme.error 
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "删除")
            }
        }
    }
}