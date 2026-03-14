package com.example.babyfood.data.ai.strategy

import com.example.babyfood.data.ai.CandidateRecipeSet
import com.example.babyfood.domain.model.DailyMealPlan
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.PlannedMeal
import com.example.babyfood.domain.model.Recipe
import com.example.babyfood.domain.model.RecommendationConstraints
import com.example.babyfood.domain.model.WeeklyMealPlan
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

import com.example.babyfood.domain.manager.LanguageManager
import com.example.babyfood.domain.model.AppLanguage

/**
 * 主力模型策略
 * 负责在候选集合中做"规划"，生成一周饮食计划
 * 使用本地算法实现（可扩展为调用远程 LLM API）
 */
@Singleton
class MainModelStrategy @Inject constructor(
    private val languageManager: LanguageManager
) {

    /**
     * 生成一周饮食计划
     */
    suspend fun generateWeeklyPlan(
        candidateSet: CandidateRecipeSet,
        ageInMonths: Int,
        constraints: RecommendationConstraints,
        startDate: LocalDate
    ): WeeklyMealPlan {
        val currentLang = languageManager.currentLanguage.value
        val dailyPlans = mutableListOf<DailyMealPlan>()

        // 生成7天的计划
        for (day in 0..6) {
            val currentDate = kotlinx.datetime.LocalDate.fromEpochDays(startDate.toEpochDays() + day)
            val meals = mutableListOf<PlannedMeal>()

            // 早餐
            if (candidateSet.breakfast.isNotEmpty()) {
                val breakfast = selectRecipe(candidateSet.breakfast, constraints, meals.map { it.recipe })
                val (notes, friendly) = getLocalizedMealTexts(MealPeriod.BREAKFAST, breakfast.name, currentLang)
                meals.add(PlannedMeal(MealPeriod.BREAKFAST, breakfast, notes, friendly))
            }

            // 午餐
            if (candidateSet.lunch.isNotEmpty()) {
                val lunch = selectRecipe(candidateSet.lunch, constraints, meals.map { it.recipe })
                val (notes, friendly) = getLocalizedMealTexts(MealPeriod.LUNCH, lunch.name, currentLang)
                meals.add(PlannedMeal(MealPeriod.LUNCH, lunch, notes, friendly))
            }

            // 晚餐
            if (candidateSet.dinner.isNotEmpty()) {
                val dinner = selectRecipe(candidateSet.dinner, constraints, meals.map { it.recipe })
                val (notes, friendly) = getLocalizedMealTexts(MealPeriod.DINNER, dinner.name, currentLang)
                meals.add(PlannedMeal(MealPeriod.DINNER, dinner, notes, friendly))
            }

            // 点心
            if (candidateSet.snack.isNotEmpty() && day % 2 == 0) {
                val snack = selectRecipe(candidateSet.snack, constraints, meals.map { it.recipe })
                val (notes, friendly) = getLocalizedMealTexts(MealPeriod.SNACK, snack.name, currentLang)
                meals.add(PlannedMeal(MealPeriod.SNACK, snack, notes, friendly))
            }
            
            dailyPlans.add(DailyMealPlan(currentDate, meals))
        }

        return WeeklyMealPlan(
            startDate = startDate,
            endDate = kotlinx.datetime.LocalDate.fromEpochDays(startDate.toEpochDays() + 6),
            dailyPlans = dailyPlans,
            nutritionSummary = calculateNutritionSummary(dailyPlans, currentLang),
            alternativeOptions = generateAlternatives(candidateSet, currentLang)
        )
    }

    private fun getLocalizedMealTexts(period: MealPeriod, recipeName: String, lang: AppLanguage): Pair<String, String> {
        val isZh = lang == AppLanguage.CHINESE || (lang == AppLanguage.FOLLOW_SYSTEM && java.util.Locale.getDefault().language == "zh")
        return if (isZh) {
            when (period) {
                MealPeriod.BREAKFAST -> "提供优质蛋白质和碳水化合物，为一天提供能量" to "早餐有${recipeName}，香香的很好吃哦～"
                MealPeriod.LUNCH -> "提供丰富的蛋白质、维生素和矿物质，促进生长发育" to "午餐有${recipeName}，营养满满！"
                MealPeriod.DINNER -> "易消化，提供适量营养，有助于睡眠" to "晚餐有${recipeName}，软软的很好消化～"
                MealPeriod.SNACK -> "提供维生素和矿物质，作为两餐之间的补充" to "点心有${recipeName}，甜甜的很开心！"
            }
        } else {
            when (period) {
                MealPeriod.BREAKFAST -> "Provides quality protein and carbohydrates for energy throughout the day." to "Breakfast has ${recipeName}, it's yummy!~"
                MealPeriod.LUNCH -> "Rich in protein, vitamins, and minerals to support growth and development." to "Lunch has ${recipeName}, full of nutrition!"
                MealPeriod.DINNER -> "Easy to digest, providing balanced nutrients for better sleep." to "Dinner has ${recipeName}, soft and easy to digest~"
                MealPeriod.SNACK -> "Supplements vitamins and minerals as a healthy snack between meals." to "Snack has ${recipeName}, so sweet and happy!"
            }
        }
    }

    private fun selectRecipe(
        recipes: List<Recipe>,
        constraints: RecommendationConstraints,
        usedRecipes: List<Recipe>
    ): Recipe {
        val availableRecipes = recipes.filter { it !in usedRecipes }
        if (availableRecipes.isEmpty()) {
            return recipes.random()
        }
        return availableRecipes.random()
    }

    private fun calculateNutritionSummary(dailyPlans: List<DailyMealPlan>, lang: AppLanguage): com.example.babyfood.domain.model.NutritionSummary {
        val totalCalories: Double = dailyPlans.sumOf { day ->
            day.meals.sumOf { (it.recipe.nutrition.calories ?: 0f).toDouble() }
        }
        val totalProtein: Double = dailyPlans.sumOf { day ->
            day.meals.sumOf { (it.recipe.nutrition.protein ?: 0f).toDouble() }
        }
        val totalCalcium: Double = dailyPlans.sumOf { day ->
            day.meals.sumOf { (it.recipe.nutrition.calcium ?: 0f).toDouble() }
        }
        val totalIron: Double = dailyPlans.sumOf { day ->
            day.meals.sumOf { (it.recipe.nutrition.iron ?: 0f).toDouble() }
        }

        val days = dailyPlans.size
        val dailyAverage = com.example.babyfood.domain.model.DailyNutritionAverage(
            calories = totalCalories / days,
            protein = totalProtein / days,
            calcium = totalCalcium / days,
            iron = totalIron / days
        )

        val highlights = mutableListOf<String>()
        val isZh = lang == AppLanguage.CHINESE || (lang == AppLanguage.FOLLOW_SYSTEM && java.util.Locale.getDefault().language == "zh")
        
        if (dailyAverage.protein >= 25) {
            highlights.add(if (isZh) "蛋白质摄入充足，有助于肌肉发育" else "Sufficient protein intake helps muscle development.")
        }
        if (dailyAverage.calcium >= 400) {
            highlights.add(if (isZh) "钙摄入充足，有助于骨骼发育" else "Sufficient calcium intake helps bone development.")
        }
        if (dailyAverage.iron >= 10) {
            highlights.add(if (isZh) "铁摄入充足，有助于预防贫血" else "Sufficient iron intake helps prevent anemia.")
        }

        return com.example.babyfood.domain.model.NutritionSummary(
            weeklyCalories = totalCalories,
            weeklyProtein = totalProtein,
            weeklyCalcium = totalCalcium,
            weeklyIron = totalIron,
            dailyAverage = dailyAverage,
            highlights = highlights
        )
    }

    private fun generateAlternatives(
        candidateSet: CandidateRecipeSet,
        lang: AppLanguage
    ): Map<MealPeriod, List<com.example.babyfood.domain.model.RecipeAlternative>> {
        val alternatives = mutableMapOf<MealPeriod, List<com.example.babyfood.domain.model.RecipeAlternative>>()
        val isZh = lang == AppLanguage.CHINESE || (lang == AppLanguage.FOLLOW_SYSTEM && java.util.Locale.getDefault().language == "zh")

        MealPeriod.entries.forEach { period ->
            val periodRecipes = when (period) {
                MealPeriod.BREAKFAST -> candidateSet.breakfast
                MealPeriod.LUNCH -> candidateSet.lunch
                MealPeriod.DINNER -> candidateSet.dinner
                MealPeriod.SNACK -> candidateSet.snack
            }

            if (periodRecipes.size > 1) {
                alternatives[period] = periodRecipes.take(3).map { recipe ->
                    com.example.babyfood.domain.model.RecipeAlternative(
                        recipe = recipe,
                        reason = if (isZh) "同类替代方案" else "Alternative of the same category",
                        nutritionDifference = if (isZh) "营养素相近，可互换" else "Similar nutrients, interchangeable"
                    )
                }
            }
        }

        return alternatives
    }
}
