package com.example.babyfood.presentation.ui.recipes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import com.example.babyfood.presentation.theme.ANIMATION_DURATION_CARD_EXPAND
import com.example.babyfood.presentation.theme.EasingEaseOutBack
import com.example.babyfood.presentation.theme.Warning
import com.example.babyfood.presentation.theme.WarningContainer
import com.example.babyfood.presentation.theme.OnWarningContainer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.babyfood.data.service.SafetyRiskAnalyzer
import com.example.babyfood.data.service.FreshnessAdvisor
import com.example.babyfood.data.service.CookingMethodRecommender
import com.example.babyfood.data.service.NutritionMatcher
import com.example.babyfood.data.repository.NutritionGoalRepository
import com.example.babyfood.domain.model.RiskLevel
import com.example.babyfood.presentation.ui.common.AppScaffold
import com.example.babyfood.presentation.ui.common.FlavorNaturalBadge
import com.example.babyfood.presentation.ui.common.HandlingAdviceDialog
import com.example.babyfood.presentation.ui.common.SafetyWarningBadge
import com.example.babyfood.presentation.ui.common.SafetyWarningIcon
import com.example.babyfood.presentation.ui.common.CookingMethodBadge
import com.example.babyfood.presentation.ui.common.FreshnessSummaryCard
import com.example.babyfood.presentation.ui.common.CookingMethodCard
import com.example.babyfood.presentation.ui.common.FreshnessTipCard
import com.example.babyfood.presentation.ui.common.NutritionGradeAndHighlights
import com.example.babyfood.presentation.ui.common.NutritionHighlightBadge
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    onBack: () -> Unit = {},
    viewModel: RecipesViewModel = hiltViewModel(),
    safetyRiskAnalyzer: SafetyRiskAnalyzer? = null,
    freshnessAdvisor: FreshnessAdvisor? = null,
    cookingMethodRecommender: CookingMethodRecommender? = null,
    nutritionMatcher: NutritionMatcher? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var recipe by remember { mutableStateOf<com.example.babyfood.domain.model.Recipe?>(null) }
    var showAiTip by remember { mutableStateOf(true) }
    var showAllergyTip by remember { mutableStateOf(true) }
    var showSafetyRisk by remember { mutableStateOf(true) }
    var showHandlingAdviceDialog by remember { mutableStateOf(false) }
    var safetyAnalysis by remember { mutableStateOf<SafetyRiskAnalyzer.RecipeSafetyAnalysis?>(null) }
    var portions by remember { mutableStateOf(1f) }
    var babyAgeMonths by remember { mutableStateOf(12) } // 默认12个月

    LaunchedEffect(recipeId) {
        recipe = viewModel.getRecipeByIdAsync(recipeId)
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.clearSavedFlag()
            onBack()
        }
    }

    val currentRecipe = recipe

    AppScaffold(
        bottomActions = emptyList()
    ) {
        if (currentRecipe == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "加载中...")
            }
        } else {
            // 计算推荐数据（仅在 currentRecipe 非空时计算）
            val cookingRecommendation = remember(currentRecipe, babyAgeMonths) {
                cookingMethodRecommender?.recommendCookingMethod(currentRecipe, babyAgeMonths)
            }

            val freshnessAdvices = remember(currentRecipe, babyAgeMonths) {
                freshnessAdvisor?.analyzeRecipeFreshness(currentRecipe, babyAgeMonths) ?: emptyList()
            }

            val nutritionGoal = remember(babyAgeMonths) {
                com.example.babyfood.domain.model.NutritionGoal.calculateByAge(babyAgeMonths)
            }

            val nutritionMatch = remember(currentRecipe, nutritionGoal) {
                nutritionMatcher?.analyzeRecipeMatch(currentRecipe, nutritionGoal, portions)
            }

            val nutritionHighlights = remember(nutritionMatch) {
                nutritionMatch?.let { nutritionMatcher?.getHighlights(it) } ?: emptyList()
            }

            val nutritionGrade: String = remember(nutritionMatch) {
                nutritionMatch?.let { nutritionMatcher?.getNutritionGrade(it)?.displayName } ?: "N/A"
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 食谱图片
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        AsyncImage(
                            model = currentRecipe.imageUrl,
                            contentDescription = "食谱图片",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // 食谱名称
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentRecipe.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )

                            // 制作方式徽章
                            cookingRecommendation?.let { CookingMethodBadge(method = it.recommendedMethod) }

                            // 检查是否为原味食谱
                            val seasonings = listOf(
                                "盐", "糖", "酱油", "醋", "料酒", "味精", "鸡精",
                                "耗油", "豆瓣酱", "番茄酱", "沙拉酱"
                            )
                            val recipeIngredients = currentRecipe.ingredients.map { it.name.lowercase() }
                            val hasSeasoning = seasonings.any { seasoning ->
                                recipeIngredients.any { ingredient -> ingredient.contains(seasoning) }
                            }

                            if (!hasSeasoning) {
                                FlavorNaturalBadge()
                            }
                        }

                        // 制作方式推荐卡片
                        cookingRecommendation?.let { rec ->
                            Spacer(modifier = Modifier.height(8.dp))
                            CookingMethodCard(
                                recommendation = rec,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // 营养等级和亮点
                        Spacer(modifier = Modifier.height(8.dp))
                        NutritionGradeAndHighlights(
                            grade = nutritionGrade,
                            score = nutritionMatch?.overallScore ?: 0f,
                            highlights = nutritionHighlights,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 基础信息栏
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally)
                    ) {
                        InfoItemWithIcon(label = "准备", value = "${currentRecipe.cookingTime?.div(2) ?: 10}分钟")
                        InfoItemWithIcon(label = "烹饪", value = "${currentRecipe.cookingTime ?: 20}分钟")
                        InfoItemWithIcon(label = "份量", value = "1份")
                    }
                }

                // 质地信息卡片
                if (currentRecipe.textureType != null) {
                    item {
                        val textureType = try {
                            com.example.babyfood.domain.model.TextureType.valueOf(currentRecipe.textureType)
                        } catch (e: IllegalArgumentException) {
                            null
                        }

                        if (textureType != null) {
                            com.example.babyfood.presentation.ui.common.TextureInfoCard(
                                textureType = textureType,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                // 描述文案
                item {
                    Text(
                        text = "此食谱富含DHA和多种维生素，适合添加辅食中后期的宝宝。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // AI修正提示卡片
                item {
                    AnimatedVisibility(
                        visible = showAiTip,
                        enter = expandVertically(
                            animationSpec = tween(
                                durationMillis = ANIMATION_DURATION_CARD_EXPAND,
                                easing = EasingEaseOutBack
                            )
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = ANIMATION_DURATION_CARD_EXPAND,
                                easing = EasingEaseOutBack
                            )
                        ),
                        exit = shrinkVertically(
                            animationSpec = tween(
                                durationMillis = ANIMATION_DURATION_CARD_EXPAND,
                                easing = EasingEaseOutBack
                            )
                        ) + fadeOut(
                            animationSpec = tween(
                                durationMillis = ANIMATION_DURATION_CARD_EXPAND,
                                easing = EasingEaseOutBack
                            )
                        )
                    ) {
                        WarningCard(
                            index = "①",
                            label = "AI修正",
                            message = "AI已自动移除食谱中的盐和糖，以符合10月龄宝宝需求。",
                            onDismiss = { showAiTip = false }
                        )
                    }
                }

                // 过敏提示卡片
                item {
                    val allergens = currentRecipe.ingredients.filter { it.isAllergen }
                    if (allergens.isNotEmpty() && showAllergyTip) {
                        AnimatedVisibility(
                            visible = showAllergyTip,
                            enter = expandVertically(
                                animationSpec = tween(
                                    durationMillis = ANIMATION_DURATION_CARD_EXPAND,
                                    easing = EasingEaseOutBack
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = ANIMATION_DURATION_CARD_EXPAND,
                                    easing = EasingEaseOutBack
                                )
                            ),
                            exit = shrinkVertically(
                                animationSpec = tween(
                                    durationMillis = ANIMATION_DURATION_CARD_EXPAND,
                                    easing = EasingEaseOutBack
                                )
                            ) + fadeOut(
                                animationSpec = tween(
                                    durationMillis = ANIMATION_DURATION_CARD_EXPAND,
                                    easing = EasingEaseOutBack
                                )
                            )
                        ) {
                            WarningCard(
                                index = "②",
                                label = "过敏提示",
                                message = "包含${allergens.joinToString("、") { it.name }}（根据您的设置，请注意排敏）。",
                                onDismiss = { showAllergyTip = false }
                            )
                        }
                    }
                }

                // 安全风险提示卡片
                item {
                    if (currentRecipe.riskLevelList != null && showSafetyRisk) {
                        AnimatedVisibility(
                            visible = showSafetyRisk,
                            enter = expandVertically(
                                animationSpec = tween(
                                    durationMillis = ANIMATION_DURATION_CARD_EXPAND,
                                    easing = EasingEaseOutBack
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = ANIMATION_DURATION_CARD_EXPAND,
                                    easing = EasingEaseOutBack
                                )
                            ),
                            exit = shrinkVertically(
                                animationSpec = tween(
                                    durationMillis = ANIMATION_DURATION_CARD_EXPAND,
                                    easing = EasingEaseOutBack
                                )
                            ) + fadeOut(
                                animationSpec = tween(
                                    durationMillis = ANIMATION_DURATION_CARD_EXPAND,
                                    easing = EasingEaseOutBack
                                )
                            )
                        ) {
                            SafetyRiskCard(
                                recipe = currentRecipe,
                                onClick = {
                                    showHandlingAdviceDialog = true
                                    // TODO: 这里需要传入宝宝信息进行完整分析
                                    // 暂时使用默认分析
                                },
                                onDismiss = { showSafetyRisk = false }
                            )
                        }
                    }
                }

                // 食材清单模块
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "食材清单",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                TextButton(onClick = { }) {
                                    Text(
                                        text = "加入冰箱",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 调味品警告
                            val seasonings = listOf(
                                "盐", "糖", "酱油", "醋", "料酒", "味精", "鸡精",
                                "耗油", "豆瓣酱", "番茄酱", "沙拉酱"
                            )
                            val recipeIngredients = currentRecipe.ingredients.map { it.name }
                            val seasoningIngredients = recipeIngredients.filter { ingredient ->
                                seasonings.any { seasoning -> ingredient.contains(seasoning) }
                            }

                            if (seasoningIngredients.isNotEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        Warning
                                    ),
                                    colors = CardDefaults.cardColors(containerColor = WarningContainer)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "💡",
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "含调味品：${seasoningIngredients.joinToString("、")}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnWarningContainer
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // 新鲜度建议摘要
                            val freshnessSummary = remember(freshnessAdvices) {
                                freshnessAdvisor?.getStorageSummary(freshnessAdvices) ?: ""
                            }

                            FreshnessSummaryCard(
                                summary = freshnessSummary,
                                adviceCount = freshnessAdvices.size,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // 营养缺乏警告
                            if (nutritionMatch?.deficiencies?.isNotEmpty() == true || nutritionMatch?.excesses?.isNotEmpty() == true) {
                                androidx.compose.foundation.layout.Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
                                ) {
                                    nutritionMatch.deficiencies.forEach { deficiency ->
                                        com.example.babyfood.presentation.ui.common.NutritionDeficiencyBadge(
                                            deficiency = deficiency
                                        )
                                    }
                                    nutritionMatch.excesses.forEach { excess ->
                                        com.example.babyfood.presentation.ui.common.NutritionDeficiencyBadge(
                                            deficiency = excess
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // 数量选择器
                            Column {
                                Text(
                                    text = "做几顿？",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "1份",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Slider(
                                        value = portions,
                                        onValueChange = { portions = it },
                                        valueRange = 1f..5f,
                                        steps = 3,
                                        modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                                        colors = SliderDefaults.colors(
                                            thumbColor = MaterialTheme.colorScheme.primary,
                                            activeTrackColor = MaterialTheme.colorScheme.primary,
                                            inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                                        )
                                    )
                                    Text(
                                        text = "5份",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${portions.toInt()}份",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 食材列表
                            currentRecipe.ingredients.forEachIndexed { index, ingredient ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "${index + 1}. ${ingredient.name}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        if (ingredient.isAllergen) {
                                            Text(
                                                text = "⚠️",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${(ingredient.amount.toFloatOrNull() ?: 1f) * portions}${ingredient.amount.filter { it.isLetter() }}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (index < currentRecipe.ingredients.size - 1) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }

                // 新鲜度详细建议
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "新鲜度建议",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        freshnessAdvices.forEach { advice ->
                            FreshnessTipCard(
                                advice = advice,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                // 制作步骤
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "制作步骤",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            currentRecipe.steps.forEachIndexed { index, step ->
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (index < currentRecipe.steps.size - 1) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }

                // 营养成分
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "营养成分",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            currentRecipe.nutrition.calories?.let {
                                NutritionRow(label = "热量", value = "${it * portions} kcal")
                            }
                            currentRecipe.nutrition.protein?.let {
                                NutritionRow(label = "蛋白质", value = "${it * portions} g")
                            }
                            currentRecipe.nutrition.fat?.let {
                                NutritionRow(label = "脂肪", value = "${it * portions} g")
                            }
                            currentRecipe.nutrition.carbohydrates?.let {
                                NutritionRow(label = "碳水化合物", value = "${it * portions} g")
                            }
                            currentRecipe.nutrition.fiber?.let {
                                NutritionRow(label = "膳食纤维", value = "${it * portions} g")
                            }
                            currentRecipe.nutrition.calcium?.let {
                                NutritionRow(label = "钙", value = "${it * portions} mg")
                            }
                            currentRecipe.nutrition.iron?.let {
                                NutritionRow(label = "铁", value = "${it * portions} mg")
                            }
                        }
                    }
                }

                // 底部间距（为悬浮按钮留空间）
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // 安全风险处理建议对话框
            if (showHandlingAdviceDialog && safetyAnalysis != null) {
                HandlingAdviceDialog(
                    analysis = safetyAnalysis!!,
                    onDismiss = { showHandlingAdviceDialog = false },
                    onIgnoreWarning = { ingredientName ->
                        // TODO: 记录用户忽略警告
                        showHandlingAdviceDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun InfoItemWithIcon(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = when (label) {
                "准备" -> "⏱️"
                "烹饪" -> "🔥"
                "份量" -> "👶"
                else -> ""
            },
            fontSize = 20.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WarningCard(
    index: String,
    label: String,
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "关闭",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun NutritionRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

/**
 * 安全风险提示卡片
 */
@Composable
private fun SafetyRiskCard(
    recipe: com.example.babyfood.domain.model.Recipe,
    onClick: () -> Unit,
    onDismiss: () -> Unit
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

    if (highestRisk != null && highestRisk != RiskLevel.NORMAL) {
        val result = when (highestRisk) {
            RiskLevel.FORBIDDEN -> Triple(
                "🚫",
                "安全警告",
                "该食谱包含禁用食材，请勿使用"
            )
            RiskLevel.NOT_RECOMMENDED -> Triple(
                "⚠️",
                "安全提醒",
                "该食谱包含不推荐食材，建议替换"
            )
            RiskLevel.REQUIRES_SPECIAL_HANDLING -> Triple(
                "💡",
                "处理提示",
                "该食谱需特殊处理，请查看详情"
            )
            RiskLevel.CAUTIOUS_INTRODUCTION -> Triple(
                "⚠️",
                "谨慎食用",
                "该食谱含常见过敏原，请少量尝试"
            )
            RiskLevel.NORMAL -> Triple(
                "✅",
                "安全",
                "该食谱食材安全"
            )
        }
        val icon = result.first
        val label = result.second
        val message = result.third

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(onClick = onClick),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}