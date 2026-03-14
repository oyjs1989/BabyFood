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
import androidx.compose.ui.res.stringResource
import com.example.babyfood.R
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
    var textureType by remember { mutableStateOf<com.example.babyfood.domain.model.TextureType?>(null) }
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
    val categories = listOf(
        stringResource(R.string.recipes_category_staple) to "主食",
        stringResource(R.string.recipes_category_vegetable) to "蔬菜",
        stringResource(R.string.recipes_category_fruit) to "水果",
        stringResource(R.string.recipes_category_protein) to "蛋白质"
    )

    // 保存函数
    val errorNameEmpty = stringResource(R.string.recipes_enter_name_error)
    val errorIngredientsEmpty = stringResource(R.string.recipes_at_least_one_ingredient)
    val errorStepsEmpty = stringResource(R.string.recipes_at_least_one_step)

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
                textureType = textureType?.name,
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
            errorMessage = errorNameEmpty
            showErrorDialog = true
        } else if (ingredients.isEmpty()) {
            errorMessage = errorIngredientsEmpty
            showErrorDialog = true
        } else if (steps.isEmpty()) {
            errorMessage = errorStepsEmpty
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
                textureType = recipe.textureType?.let { com.example.babyfood.domain.model.TextureType.valueOf(it) }
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
    val defaultError = stringResource(R.string.update_failed)
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            errorMessage = uiState.error ?: defaultError
            showErrorDialog = true
            viewModel.clearError()
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text(stringResource(R.string.common_tip)) },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text(stringResource(R.string.confirm))
                }
            }
        )
    }

    // 离开确认对话框
    if (showExitConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmationDialog = false },
            title = { Text(stringResource(R.string.recipes_unsaved_changes_title)) },
            text = { Text(stringResource(R.string.recipes_unsaved_changes_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        saveRecipe()
                        showExitConfirmationDialog = false
                    },
                    enabled = name.isNotBlank() && ingredients.isNotEmpty() && steps.isNotEmpty()
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExitConfirmationDialog = false
                        onBack()
                    }
                ) {
                    Text(stringResource(R.string.recipes_discard_changes))
                }
            }
        )
    }

    AppScaffold(
        bottomActions = listOf(
            AppBottomAction(
                icon = Icons.Default.Check,
                label = stringResource(R.string.save),
                contentDescription = stringResource(R.string.save),
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
                                contentDescription = null,
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
                                    text = stringResource(R.string.recipes_upload_image_hint),
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
                                contentDescription = stringResource(R.string.recipes_upload_image_action),
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
                            text = stringResource(R.string.recipes_basic_info_label),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(stringResource(R.string.recipes_name_label)) },
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
                                label = { Text(stringResource(R.string.recipes_min_age_label)) },
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
                                label = { Text(stringResource(R.string.recipes_max_age_label)) },
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
                            text = stringResource(R.string.recipes_texture_type_label),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            com.example.babyfood.domain.model.TextureType.entries.forEach { texture ->
                                androidx.compose.material3.FilterChip(
                                    selected = textureType == texture,
                                    onClick = { textureType = texture },
                                    label = { Text(texture.displayName) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(R.string.preferences),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { (label, cat) ->
                                androidx.compose.material3.FilterChip(
                                    selected = category == cat,
                                    onClick = { category = cat },
                                    label = { Text(label) }
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
                                text = stringResource(R.string.recipes_ingredients_list_label),
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = {
                                ingredients.add(IngredientFormItem("", "", false))
                            }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.common_add))
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
                                text = stringResource(R.string.recipes_steps_label),
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = { steps.add("") }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.common_add))
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
                            text = stringResource(R.string.recipes_nutrition_label),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        NutritionInputField(label = stringResource(R.string.recipes_calories_label), value = calories, unit = "kcal", onValueChange = { calories = it })
                        NutritionInputField(label = stringResource(R.string.recipes_protein_label), value = protein, unit = "g", onValueChange = { protein = it })
                        NutritionInputField(label = stringResource(R.string.recipes_fat_label), value = fat, unit = "g", onValueChange = { fat = it })
                        NutritionInputField(label = stringResource(R.string.recipes_carbohydrates_label), value = carbohydrates, unit = "g", onValueChange = { carbohydrates = it })
                        NutritionInputField(label = stringResource(R.string.recipes_fiber_label), value = fiber, unit = "g", onValueChange = { fiber = it })
                        NutritionInputField(label = stringResource(R.string.recipes_calcium_label), value = calcium, unit = "mg", onValueChange = { calcium = it })
                        NutritionInputField(label = stringResource(R.string.recipes_iron_label), value = iron, unit = "mg", onValueChange = { iron = it })
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
            label = { Text(stringResource(R.string.recipes_ingredients_label)) },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = ingredient.amount,
            onValueChange = onAmountChange,
            label = { Text(stringResource(R.string.recipes_amount_label)) },
            modifier = Modifier.width(100.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Checkbox(
            checked = ingredient.isAllergen,
            onCheckedChange = onAllergenChange
        )
        Text(
            text = stringResource(R.string.recipes_allergen_label),
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.back), // Reusing common_delete if exists, but back/delete icon desc
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
            placeholder = { Text(stringResource(R.string.recipes_step_placeholder)) },
            modifier = Modifier.weight(1f),
            minLines = 2
        )
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = null,
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