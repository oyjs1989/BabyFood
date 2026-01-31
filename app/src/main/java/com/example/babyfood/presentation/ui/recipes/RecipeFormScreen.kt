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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.babyfood.presentation.ui.common.AppScaffold
import com.example.babyfood.presentation.ui.common.AppBottomAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeFormScreen(
    recipeId: Long? = null,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    viewModel: RecipesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 表单状态
    var name by remember { mutableStateOf("") }
    var minAgeMonths by remember { mutableIntStateOf(6) }
    var maxAgeMonths by remember { mutableIntStateOf(24) }
    var category by remember { mutableStateOf("主食") }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var ingredients = remember { mutableStateListOf<IngredientFormItem>() }
    var steps = remember { mutableStateListOf<String>() }

    // 营养成分
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var carbohydrates by remember { mutableStateOf("") }
    var fiber by remember { mutableStateOf("") }
    var calcium by remember { mutableStateOf("") }
    var iron by remember { mutableStateOf("") }

    // 对话框状态
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // 未保存修改跟踪
    var hasUnsavedChanges by remember { mutableStateOf(false) }
    var showExitConfirmationDialog by remember { mutableStateOf(false) }

    // 分类选项
    val categories = listOf("主食", "蔬菜", "水果", "蛋白质")

    // 保存函数
    val saveRecipe = {
        // 验证表单
        if (name.isNotBlank() && ingredients.isNotEmpty() && steps.isNotEmpty()) {
            // 创建食谱对象
            val recipe = com.example.babyfood.domain.model.Recipe(
                id = recipeId ?: 0,
                name = name,
                minAgeMonths = minAgeMonths,
                maxAgeMonths = maxAgeMonths,
                ingredients = ingredients.map {
                    com.example.babyfood.domain.model.Ingredient(
                        name = it.name,
                        amount = it.amount,
                        isAllergen = it.isAllergen
                    )
                },
                steps = steps.toList(),
                nutrition = com.example.babyfood.domain.model.Nutrition(
                    calories = calories.toFloatOrNull(),
                    protein = protein.toFloatOrNull(),
                    fat = fat.toFloatOrNull(),
                    carbohydrates = carbohydrates.toFloatOrNull(),
                    fiber = fiber.toFloatOrNull(),
                    calcium = calcium.toFloatOrNull(),
                    iron = iron.toFloatOrNull()
                ),
                category = category,
                isBuiltIn = false,
                imageUrl = imageUrl
            )

            // 保存
            if (recipeId != null && recipeId > 0) {
                viewModel.updateRecipe(recipe)
            } else {
                viewModel.addRecipe(recipe)
            }
            hasUnsavedChanges = false
        } else if (name.isBlank()) {
            errorMessage = "请输入食谱名称"
            showErrorDialog = true
        } else if (ingredients.isEmpty()) {
            errorMessage = "请至少添加一种食材"
            showErrorDialog = true
        } else if (steps.isEmpty()) {
            errorMessage = "请至少添加一个制作步骤"
            showErrorDialog = true
        }
    }

    // 加载现有食谱数据（编辑模式）
    LaunchedEffect(recipeId) {
        if (recipeId != null && recipeId > 0) {
            val recipe = viewModel.getRecipeByIdAsync(recipeId)
            if (recipe != null && !recipe.isBuiltIn) {
                name = recipe.name
                minAgeMonths = recipe.minAgeMonths
                maxAgeMonths = recipe.maxAgeMonths
                category = recipe.category
                imageUrl = recipe.imageUrl
                ingredients.clear()
                recipe.ingredients.forEach { ingredient ->
                    ingredients.add(
                        IngredientFormItem(
                            name = ingredient.name,
                            amount = ingredient.amount,
                            isAllergen = ingredient.isAllergen
                        )
                    )
                }
                steps.clear()
                steps.addAll(recipe.steps)
                calories = recipe.nutrition.calories?.toString() ?: ""
                protein = recipe.nutrition.protein?.toString() ?: ""
                fat = recipe.nutrition.fat?.toString() ?: ""
                carbohydrates = recipe.nutrition.carbohydrates?.toString() ?: ""
                fiber = recipe.nutrition.fiber?.toString() ?: ""
                calcium = recipe.nutrition.calcium?.toString() ?: ""
                iron = recipe.nutrition.iron?.toString() ?: ""
            }
        }
    }

    // 监听保存成功
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.clearSavedFlag()
            onSave()
        }
    }

    // 错误处理
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            errorMessage = uiState.error ?: "操作失败"
            showErrorDialog = true
            viewModel.clearError()
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("提示") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("确定")
                }
            }
        )
    }

    // 离开确认对话框
    if (showExitConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmationDialog = false },
            title = { Text("未保存的修改") },
            text = { Text("您有未保存的修改，是否要保存？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        saveRecipe()
                        showExitConfirmationDialog = false
                    },
                    enabled = name.isNotBlank() && ingredients.isNotEmpty() && steps.isNotEmpty()
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExitConfirmationDialog = false
                        onBack()
                    }
                ) {
                    Text("放弃修改")
                }
            }
        )
    }

    AppScaffold(
        bottomActions = listOf(
            AppBottomAction(
                icon = Icons.Default.Check,
                label = "保存",
                contentDescription = "保存食谱",
                onClick = saveRecipe
            )
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 图片上传区域
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUrl != null) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "食谱图片",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "点击上传食谱图片",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        // 上传按钮
                        FloatingActionButton(
                            onClick = { /* TODO: 实现图片选择功能 */ },
                            modifier = Modifier
                                .size(56.dp)
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "上传图片",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // 基本信息
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "基本信息",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("食谱名称") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = minAgeMonths.toString(),
                                onValueChange = {
                                    minAgeMonths = it.toIntOrNull() ?: 6
                                },
                                label = { Text("最小月龄") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                )
                            )
                            OutlinedTextField(
                                value = maxAgeMonths.toString(),
                                onValueChange = {
                                    maxAgeMonths = it.toIntOrNull() ?: 24
                                },
                                label = { Text("最大月龄") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "分类",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { cat ->
                                androidx.compose.material3.FilterChip(
                                    selected = category == cat,
                                    onClick = { category = cat },
                                    label = { Text(cat) }
                                )
                            }
                        }
                    }
                }
            }

            // 食材列表
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "食材清单",
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = {
                                ingredients.add(IngredientFormItem("", "", false))
                            }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("添加")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ingredients.forEachIndexed { index, ingredient ->
                            IngredientItem(
                                ingredient = ingredient,
                                onNameChange = { ingredients[index] = ingredient.copy(name = it) },
                                onAmountChange = { ingredients[index] = ingredient.copy(amount = it) },
                                onAllergenChange = { ingredients[index] = ingredient.copy(isAllergen = it) },
                                onDelete = { ingredients.removeAt(index) }
                            )
                            if (index < ingredients.size - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            // 制作步骤
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "制作步骤",
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = { steps.add("") }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("添加")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        steps.forEachIndexed { index, step ->
                            StepItem(
                                step = step,
                                index = index,
                                onStepChange = { steps[index] = it },
                                onDelete = { steps.removeAt(index) }
                            )
                            if (index < steps.size - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            // 营养成分
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "营养成分（可选）",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        NutritionInputField(label = "热量", value = calories, unit = "kcal", onValueChange = { calories = it })
                        NutritionInputField(label = "蛋白质", value = protein, unit = "g", onValueChange = { protein = it })
                        NutritionInputField(label = "脂肪", value = fat, unit = "g", onValueChange = { fat = it })
                        NutritionInputField(label = "碳水化合物", value = carbohydrates, unit = "g", onValueChange = { carbohydrates = it })
                        NutritionInputField(label = "膳食纤维", value = fiber, unit = "g", onValueChange = { fiber = it })
                        NutritionInputField(label = "钙", value = calcium, unit = "mg", onValueChange = { calcium = it })
                        NutritionInputField(label = "铁", value = iron, unit = "mg", onValueChange = { iron = it })
                    }
                }
            }

            // 底部间距
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun IngredientItem(
    ingredient: IngredientFormItem,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onAllergenChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = ingredient.name,
            onValueChange = onNameChange,
            label = { Text("食材") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = ingredient.amount,
            onValueChange = onAmountChange,
            label = { Text("用量") },
            modifier = Modifier.width(100.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Checkbox(
            checked = ingredient.isAllergen,
            onCheckedChange = onAllergenChange
        )
        Text(
            text = "过敏原",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "删除",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun StepItem(
    step: String,
    index: Int,
    onStepChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${index + 1}.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(32.dp)
        )
        OutlinedTextField(
            value = step,
            onValueChange = onStepChange,
            placeholder = { Text("输入步骤描述") },
            modifier = Modifier.weight(1f),
            minLines = 2
        )
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "删除",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun NutritionInputField(
    label: String,
    value: String,
    unit: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label：",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(100.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(40.dp)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

data class IngredientFormItem(
    val name: String,
    val amount: String,
    val isAllergen: Boolean
)