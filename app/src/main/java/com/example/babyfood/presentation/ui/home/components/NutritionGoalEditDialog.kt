package com.example.babyfood.presentation.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.babyfood.domain.model.NutritionGoal

@Composable
fun NutritionGoalEditDialog(
    currentGoal: NutritionGoal,
    ageInMonths: Int,
    onDismiss: () -> Unit,
    onSave: (NutritionGoal) -> Unit,
    onRecommend: (() -> NutritionGoal?)? = null  // AI 推荐回调
) {
    // 输入状态
    var calories by remember { mutableStateOf(currentGoal.calories.toString()) }
    var protein by remember { mutableStateOf(currentGoal.protein.toString()) }
    var calcium by remember { mutableStateOf(currentGoal.calcium.toString()) }
    var iron by remember { mutableStateOf(currentGoal.iron.toString()) }

    // 错误状态
    var caloriesError by remember { mutableStateOf<String?>(null) }
    var proteinError by remember { mutableStateOf<String?>(null) }
    var calciumError by remember { mutableStateOf<String?>(null) }
    var ironError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 标题
                Text(
                    text = "编辑营养目标",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // AI 推荐按钮
                Button(
                    onClick = {
                        val recommended = if (onRecommend != null) {
                            onRecommend()
                        } else {
                            NutritionGoal.calculateByAge(ageInMonths)
                        }
                        recommended?.let {
                            calories = it.calories.toString()
                            protein = it.protein.toString()
                            calcium = it.calcium.toString()
                            iron = it.iron.toString()
                            // 清除错误
                            caloriesError = null
                            proteinError = null
                            calciumError = null
                            ironError = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "AI 智能推荐（结合体检数据）",
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 热量输入
                NutritionInputField(
                    label = "热量",
                    value = calories,
                    unit = "kcal",
                    error = caloriesError,
                    onValueChange = { newValue ->
                        calories = newValue
                        caloriesError = validatePositiveNumber(newValue)
                    }
                )

                // 蛋白质输入
                NutritionInputField(
                    label = "蛋白质",
                    value = protein,
                    unit = "g",
                    error = proteinError,
                    onValueChange = { newValue ->
                        protein = newValue
                        proteinError = validatePositiveNumber(newValue)
                    }
                )

                // 钙输入
                NutritionInputField(
                    label = "钙",
                    value = calcium,
                    unit = "mg",
                    error = calciumError,
                    onValueChange = { newValue ->
                        calcium = newValue
                        calciumError = validatePositiveNumber(newValue)
                    }
                )

                // 铁输入
                NutritionInputField(
                    label = "铁",
                    value = iron,
                    unit = "mg",
                    error = ironError,
                    onValueChange = { newValue ->
                        iron = newValue
                        ironError = validatePositiveNumber(newValue)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 按钮行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 取消按钮
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("取消")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 保存按钮
                    Button(
                        onClick = {
                            val c = calories.toFloatOrNull()
                            val p = protein.toFloatOrNull()
                            val ca = calcium.toFloatOrNull()
                            val i = iron.toFloatOrNull()

                            if (c != null && p != null && ca != null && i != null) {
                                if (c > 0 && p > 0 && ca > 0 && i > 0) {
                                    onSave(
                                        NutritionGoal(
                                            calories = c,
                                            protein = p,
                                            calcium = ca,
                                            iron = i
                                        )
                                    )
                                }
                            }
                        },
                        enabled = caloriesError == null &&
                                proteinError == null &&
                                calciumError == null &&
                                ironError == null &&
                                calories.isNotBlank() &&
                                protein.isNotBlank() &&
                                calcium.isNotBlank() &&
                                iron.isNotBlank()
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}

@Composable
private fun NutritionInputField(
    label: String,
    value: String,
    unit: String,
    error: String?,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            trailingIcon = {
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Validates that the input is a positive number
 * @param input The string input to validate
 * @return Error message if invalid, null if valid or empty
 */
private fun validatePositiveNumber(input: String): String? {
    return if (input.isBlank()) null else {
        input.toFloatOrNull()?.let {
            if (it > 0) null else "必须大于0"
        } ?: "请输入有效数字"
    }
}