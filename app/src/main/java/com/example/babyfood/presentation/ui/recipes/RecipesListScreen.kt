package com.example.babyfood.presentation.ui.recipes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.example.babyfood.R
import com.example.babyfood.domain.model.Recipe
import com.example.babyfood.domain.model.RiskLevel
import com.example.babyfood.presentation.theme.PrimaryOrangeDark
import com.example.babyfood.presentation.theme.PrimaryOrange
import com.example.babyfood.presentation.theme.BackgroundWarm
import com.example.babyfood.presentation.theme.TextMain
import com.example.babyfood.presentation.theme.Coral
import com.example.babyfood.presentation.theme.Cream
import com.example.babyfood.presentation.theme.PrimaryOrangeLight
import com.example.babyfood.presentation.theme.Primary
import com.example.babyfood.presentation.theme.TextSub
import kotlinx.serialization.json.Json

@Composable
fun RecipesListScreen(
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: RecipesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // 分类筛选选项
    val categoryOptions = listOf(
        stringResource(R.string.recipes_category_all) to null,
        stringResource(R.string.recipes_category_vegetable) to "蔬菜",
        stringResource(R.string.recipes_category_fruit) to "水果",
        stringResource(R.string.recipes_category_staple) to "主食",
        stringResource(R.string.recipes_category_finger_food) to "手指食物",
        stringResource(R.string.recipes_category_protein) to "蛋白质"
    )

    // 月龄筛选选项
    val ageOptions = listOf(
        stringResource(R.string.recipes_age_6_8) to 6,
        stringResource(R.string.recipes_age_8_10) to 8,
        stringResource(R.string.recipes_age_10_12) to 10,
        stringResource(R.string.recipes_age_12_plus) to 12
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWarm)
    ) {
        // 搜索栏
        SearchBar(
            query = searchQuery,
            onQueryChange = { 
                searchQuery = it
                viewModel.searchRecipes(it)
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // 分类筛选标签
        FilterChipRow(
            options = categoryOptions,
            selectedOption = uiState.selectedCategory,
            onOptionSelected = { category ->
                if (category != null) {
                    viewModel.filterByCategory(category)
                } else {
                    viewModel.clearFilters()
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // 月龄筛选标签
        AgeFilterChipRow(
            options = ageOptions,
            selectedAge = uiState.selectedAge,
            onAgeSelected = { age ->
                if (age != null) {
                    viewModel.filterByAge(age)
                } else {
                    viewModel.clearFilters()
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 食谱列表
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.loading))
            }
        } else if (uiState.filteredRecipes.isEmpty()) {
            val isFiltering = searchQuery.isNotEmpty() || uiState.selectedAge != null || uiState.selectedCategory != null
            com.example.babyfood.presentation.theme.EmptyState(
                icon = if (isFiltering) Icons.Default.Search else Icons.Default.Restaurant,
                title = if (isFiltering) stringResource(R.string.recipes_empty_filter) else stringResource(R.string.recipes_empty_list),
                description = if (isFiltering) stringResource(R.string.recipes_filter_description) else stringResource(R.string.recipes_empty_description)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredRecipes) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onNavigateToDetail(recipe.id) },
                        onFavoriteClick = { /* TODO: 收藏功能 */ }
                    )
                }

                // 底部留白
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun RecipeListAppBar(
    onNavigateBack: () -> Unit = {},
    onFilterClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 返回按钮
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = PrimaryOrangeDark,
                modifier = Modifier.size(28.dp)
            )
        }

        // 标题
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryOrangeDark
        )

        // 筛选按钮
        IconButton(onClick = onFilterClick) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = stringResource(R.string.common_filter),
                tint = PrimaryOrangeDark,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Cream)
            .border(1.dp, PrimaryOrangeLight.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 搜索图标
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = PrimaryOrangeLight,
            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
        )

        // 输入框
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.recipes_search_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSub.copy(alpha = 0.6f)
                    )
                }
                innerTextField()
            }
        )
    }
}


@Composable
private fun FilterChipRow(
    options: List<Pair<String, String?>>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (label, value) ->
            val isSelected = selectedOption == value || (selectedOption == null && value == null)

            FilterChip(
                label = label,
                isSelected = isSelected,
                isPrimary = true,
                onClick = { onOptionSelected(value) }
            )
        }
    }
}

@Composable
private fun AgeFilterChipRow(
    options: List<Pair<String, Int>>,
    selectedAge: Int?,
    onAgeSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (label, age) ->
            val isSelected = selectedAge == age

            FilterChip(
                label = label,
                isSelected = isSelected,
                isPrimary = false,
                onClick = { 
                    onAgeSelected(if (isSelected) null else age)
                }
            )
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected && isPrimary -> PrimaryOrangeLight
        isSelected && !isPrimary -> PrimaryOrange.copy(alpha = 0.3f)
        else -> Cream
    }

    val textColor = when {
        isSelected && isPrimary -> TextMain
        isSelected && !isPrimary -> PrimaryOrangeDark
        else -> TextSub
    }

    val border = when {
        !isSelected -> BorderStroke(1.dp, PrimaryOrangeLight.copy(alpha = 0.3f))
        isSelected && !isPrimary -> BorderStroke(1.dp, PrimaryOrange.copy(alpha = 0.5f))
        else -> null
    }

    Box(
        modifier = Modifier
            .height(if (isPrimary) 36.dp else 28.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .then(
                if (border != null) {
                    Modifier.border(border, RoundedCornerShape(18.dp))
                } else Modifier
            )
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = if (isPrimary) {
                MaterialTheme.typography.labelLarge
            } else {
                MaterialTheme.typography.labelMedium
            },
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {}
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
            }.maxByOrNull { it.ordinal }
        } catch (e: Exception) {
            null
        }
    }

    // 是否已收藏（模拟）
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Cream),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 食谱图片
            RecipeImage(
                imageUrl = recipe.imageUrl,
                name = recipe.name,
                modifier = Modifier.size(88.dp)
            )

            // 食谱信息
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 标题
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextMain,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // 营养标签
                if (recipe.isIronRich) {
                    NutritionTag(
                        icon = "⚡",
                        text = stringResource(R.string.recipes_iron_rich),
                        backgroundColor = PrimaryOrange.copy(alpha = 0.2f),
                        textColor = PrimaryOrangeDark
                    )
                } else {
                    // 默认显示一个营养标签
                    val nutritionTag = when (recipe.category) {
                        "蔬菜" -> "富含维生素A" to "🥬"
                        "水果" -> "高钾" to "🍌"
                        "主食" -> "能量补充" to "⚡"
                        "蛋白质" -> "高蛋白" to "🥩"
                        else -> "健康选择" to "🌟"
                    }
                    NutritionTag(
                        icon = nutritionTag.second,
                        text = nutritionTag.first,
                        backgroundColor = PrimaryOrangeLight.copy(alpha = 0.3f),
                        textColor = TextSub
                    )
                }

                // 难度和时间
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 难度
                    val difficulty = when (recipe.minAgeMonths) {
                        in 0..8 -> stringResource(R.string.recipes_difficulty_easy)
                        in 9..11 -> stringResource(R.string.recipes_difficulty_medium)
                        else -> stringResource(R.string.recipes_difficulty_advanced)
                    }
                    Text(
                        text = difficulty,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSub,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "•",
                        color = PrimaryOrangeLight,
                        fontSize = 12.sp
                    )

                    // 时间
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = PrimaryOrangeLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.recipes_cooking_time_format, recipe.cookingTime ?: 15),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSub,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 收藏按钮
            IconButton(
                onClick = { 
                    isFavorite = !isFavorite
                    onFavoriteClick()
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.common_favorite),
                    tint = if (isFavorite) Coral else TextSub.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun RecipeImage(
    imageUrl: String?,
    name: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Cream),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // 使用首字母作为占位符
            Text(
                text = name.firstOrNull()?.toString() ?: "?",
                style = MaterialTheme.typography.headlineMedium,
                color = PrimaryOrangeLight,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun NutritionTag(
    icon: String,
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 12.sp
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}
