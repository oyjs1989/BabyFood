package com.example.babyfood.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.babyfood.domain.model.NutritionGoal
import com.example.babyfood.domain.model.NutritionIntake
import com.example.babyfood.presentation.theme.NutritionCalories
import com.example.babyfood.presentation.theme.NutritionCalcium
import com.example.babyfood.presentation.theme.NutritionIron
import com.example.babyfood.presentation.theme.NutritionProtein
import com.example.babyfood.presentation.theme.Primary
import com.example.babyfood.presentation.theme.PrimaryLight
import com.example.babyfood.presentation.theme.ElevationLevel1

@Composable
fun NutritionGoalCard(
    nutritionGoal: NutritionGoal,
    nutritionIntake: NutritionIntake = NutritionIntake.empty(),
    onEdit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onEdit,
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = ElevationLevel1
        ),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Primary,
                            PrimaryLight
                        )
                    )
                )
                .padding(8.dp)
        ) {
            // 标题栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "今日营养目标",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.Black
                )

                IconButton(
                    onClick = onEdit
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "编辑营养目标",
                        tint = androidx.compose.ui.graphics.Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 计算营养进度
            val nutritionProgress = nutritionIntake.calculateProgress(nutritionGoal)

            // 营养数据网格（修改为环形进度条）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutritionProgressItem(
                    label = "热量",
                    targetValue = nutritionGoal.calories.toInt(),
                    currentValue = nutritionIntake.calories.toInt(),
                    unit = "kcal",
                    color = NutritionCalories,
                    progress = nutritionProgress.caloriesProgress
                )
                NutritionProgressItem(
                    label = "蛋白质",
                    targetValue = nutritionGoal.protein.toInt(),
                    currentValue = nutritionIntake.protein.toInt(),
                    unit = "g",
                    color = NutritionProtein,
                    progress = nutritionProgress.proteinProgress
                )
                NutritionProgressItem(
                    label = "钙",
                    targetValue = nutritionGoal.calcium.toInt(),
                    currentValue = nutritionIntake.calcium.toInt(),
                    unit = "mg",
                    color = NutritionCalcium,
                    progress = nutritionProgress.calciumProgress
                )
                NutritionProgressItem(
                    label = "铁",
                    targetValue = nutritionGoal.iron.toInt(),
                    currentValue = nutritionIntake.iron.toInt(),
                    unit = "mg",
                    color = NutritionIron,
                    progress = nutritionProgress.ironProgress
                )
            }
        }
    }
}

@Composable
private fun NutritionProgressItem(
    label: String,
    targetValue: Int,
    currentValue: Int,
    unit: String,
    color: androidx.compose.ui.graphics.Color,
    progress: Float
) {
    CircularProgressWithValue(
        progress = progress,
        value = "$currentValue/$targetValue",
        unit = unit,
        label = label,
        size = 80.dp,
        strokeWidth = 8.dp,
        progressColor = color
    )
}