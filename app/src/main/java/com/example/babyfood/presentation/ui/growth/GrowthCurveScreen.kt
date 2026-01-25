package com.example.babyfood.presentation.ui.growth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.presentation.ui.baby.BabyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthCurveScreen(
    babyId: Long,
    onBack: () -> Unit,
    viewModel: GrowthRecordViewModel = hiltViewModel(),
    babyViewModel: BabyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val babyUiState by babyViewModel.uiState.collectAsState()
    val baby = babyUiState.selectedBaby
    var selectedChartType by remember { mutableStateOf(ChartType.WEIGHT) }

    LaunchedEffect(Unit) {
        viewModel.loadGrowthRecords(babyId)
        babyViewModel.loadBaby(babyId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("生长曲线") },
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
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // 图表类型选择器
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = selectedChartType == ChartType.WEIGHT,
                            onClick = { selectedChartType = ChartType.WEIGHT },
                            label = { Text("体重") }
                        )
                        FilterChip(
                            selected = selectedChartType == ChartType.HEIGHT,
                            onClick = { selectedChartType = ChartType.HEIGHT },
                            label = { Text("身高") }
                        )
                        FilterChip(
                            selected = selectedChartType == ChartType.HEAD_CIRCUMFERENCE,
                            onClick = { selectedChartType = ChartType.HEAD_CIRCUMFERENCE },
                            label = { Text("头围") }
                        )
                    }
                }

                // 生长曲线图表 - 即使没有数据也显示标准曲线
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "生长曲线图",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        GrowthChart(
                            growthRecords = uiState.growthRecords,
                            isBoy = true,
                            chartType = selectedChartType,
                            birthDate = baby?.birthDate,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                        
                        // 图例
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            LegendItem(color = com.example.babyfood.presentation.theme.NutritionCalcium, text = "P3 (下界)")
                            LegendItem(color = com.example.babyfood.presentation.theme.NutritionCalories, text = "P50 (中位)")
                            LegendItem(color = com.example.babyfood.presentation.theme.Error, text = "P97 (上界)")
                            LegendItem(color = com.example.babyfood.presentation.theme.NutritionProtein, text = "实际数据")
                        }
                        
                        // 如果没有数据，显示提示
                        if (uiState.growthRecords.isEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "提示：添加体检记录后，您的宝宝数据将显示在图表中",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // 如果有数据，显示生长记录列表
                if (uiState.growthRecords.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "生长记录列表",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(uiState.growthRecords) { record ->
                                    GrowthRecordItem(record = record)
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
private fun LegendItem(color: Color, text: String) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = androidx.compose.foundation.shape.CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun GrowthRecordItem(record: com.example.babyfood.domain.model.GrowthRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = record.recordDate.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            record.weight?.let { Text(text = "体重: ${String.format("%.2f", it)} kg") }
            record.height?.let { Text(text = "身高: ${String.format("%.2f", it)} cm") }
            record.headCircumference?.let { Text(text = "头围: ${String.format("%.2f", it)} cm") }
        }
    }
}