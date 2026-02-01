package com.example.babyfood.presentation.ui.recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.babyfood.domain.model.RiskLevel
import com.example.babyfood.presentation.ui.common.AppScaffold
import com.example.babyfood.presentation.ui.common.AppBarAction
import com.example.babyfood.presentation.ui.common.IronRichBadge
import com.example.babyfood.presentation.ui.common.SafetyWarningBadge
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesListScreen(
    onNavigateToDetail: (Long) -> Unit = {},
    viewModel: RecipesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAgeFilter by remember { mutableStateOf(false) }
    var showCategoryFilter by remember { mutableStateOf(false) }

    // 月份筛选选项
    val ageFilterOptions = listOf(
        "全部" to null,
        "6-8个月" to 6,
        "8-12个月" to 8,
        "12-24个月" to 12
    )

    // 分类筛选选项
    val categoryFilterOptions = listOf(
        "全部" to null,
        "主食" to "主食",
        "蔬菜" to "蔬菜",
        "水果" to "水果",
        "蛋白质" to "蛋白质"
    )

    AppScaffold(
        bottomActions = emptyList()
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 搜索框
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    viewModel.searchRecipes(query)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("搜索食谱名称、食材...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "搜索")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            viewModel.searchRecipes("")
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "清除")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { /* 隐藏键盘 */ }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 月龄筛选
            Column {
                Text(
                    text = "月龄",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    state = rememberLazyListState()
                ) {
                    items(ageFilterOptions) { (label, age) ->
                        FilterChip(
                            selected = uiState.selectedAge == age,
                            onClick = {
                                if (age != null) {
                                    viewModel.filterByAge(age)
                                } else {
                                    viewModel.clearFilters()
                                }
                            },
                            label = { Text(label) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 分类筛选
            Column {
                Text(
                    text = "分类",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    state = rememberLazyListState()
                ) {
                    items(categoryFilterOptions) { (label, category) ->
                        FilterChip(
                            selected = uiState.selectedCategory == category,
                            onClick = {
                                if (category != null) {
                                    viewModel.filterByCategory(category)
                                } else {
                                    viewModel.clearFilters()
                                }
                            },
                            label = { Text(label) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "加载中...")
                }
            } else if (uiState.filteredRecipes.isEmpty()) {
                val isFiltering = searchQuery.isNotEmpty() || uiState.selectedAge != null || uiState.selectedCategory != null
                com.example.babyfood.presentation.theme.EmptyState(
                    icon = if (isFiltering) Icons.Default.Search else Icons.Default.Restaurant,
                    title = if (isFiltering) "没有找到符合条件的食谱" else "还没有食谱数据",
                    description = if (isFiltering) "尝试调整搜索条件或筛选器" else "点击右下角 + 按钮添加食谱"
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
                ) {
                    items(uiState.filteredRecipes) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onNavigateToDetail(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: com.example.babyfood.domain.model.Recipe,
    onClick: () -> Unit = {}
) {
    // 解析风险等级列表并获取最高风险
    val highestRisk = recipe.riskLevelList?.let { riskList ->
        try {
            val riskLevels = Json.decodeFromString<List<String>>(riskList)
            riskLevels.mapNotNull { risk ->
                try {
                    RiskLevel.valueOf(risk)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }.maxByOrNull { riskLevel: RiskLevel -> riskLevel.ordinal }
        } catch (e: Exception) {
            null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outline
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 食谱略图
            Box(
                modifier = Modifier.size(88.dp)
            ) {
                if (recipe.imageUrl != null) {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = "食谱图片",
                        modifier = Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = recipe.name.firstOrNull()?.toString() ?: "?",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 食谱信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (recipe.isIronRich == true) {
                            IronRichBadge()
                        }
                        highestRisk?.let { risk ->
                            SafetyWarningBadge(riskLevel = risk)
                        }
                        if (recipe.isBuiltIn) {
                            Text(
                                text = "内置",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${recipe.minAgeMonths}-${recipe.maxAgeMonths}个月",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = recipe.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 食材列表（显示前3个）
                val ingredientsText = recipe.ingredients
                    .take(3)
                    .joinToString(", ") { "${it.name} ${it.amount}" } +
                        if (recipe.ingredients.size > 3) "..." else ""

                Text(
                    text = "食材：$ingredientsText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }
        }
    }
}
