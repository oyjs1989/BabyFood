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

/**
 * 主力模型策略
 * 负责在候选集合中做"规划"，生成一周饮食计划
 * 使用本地算法实现（可扩展为调用远程 LLM API）
 */
@Singleton
class MainModelStrategy @Inject constructor() {

    /**
     * 生成一周饮食计划
     * @param candidateSet 候选食谱集合
     * @param ageInMonths 宝宝年龄（月）
     * @param constraints 约束条件
     * @param startDate 开始日期
     * @return 一周饮食计划
     */
    suspend fun generateWeeklyPlan(
        candidateSet: CandidateRecipeSet,
        ageInMonths: Int,
        constraints: RecommendationConstraints,
        startDate: LocalDate
    ): WeeklyMealPlan {
        val dailyPlans = mutableListOf<DailyMealPlan>()
        // endDate 将在循环后通过最后一个日期计算

        // 统计约束使用情况
        var fishCount = 0
        var eggCount = 0

        // 生成7天的计划
        for (day in 0..6) {
            val currentDate = kotlinx.datetime.LocalDate.fromEpochDays(startDate.toEpochDays() + day)
            val meals = mutableListOf<PlannedMeal>()

            // 早餐
            if (candidateSet.breakfast.isNotEmpty()) {
                val breakfast = selectRecipe(
                    candidateSet.breakfast,
                    constraints,
                    usedRecipes = meals.map { it.recipe }
                )
                meals.add(
                    PlannedMeal(
                        mealPeriod = MealPeriod.BREAKFAST,
                        recipe = breakfast,
                        nutritionNotes = "提供优质蛋白质和碳水化合物，为一天提供能量",
                        childFriendlyText = "早餐有${breakfast.name}，香香的很好吃哦～"
                    )
                )
            }

            // 午餐
            if (candidateSet.lunch.isNotEmpty()) {
                val lunch = selectRecipe(
                    candidateSet.lunch,
                    constraints,
                    usedRecipes = meals.map { it.recipe }
                )
                meals.add(
                    PlannedMeal(
                        mealPeriod = MealPeriod.LUNCH,
                        recipe = lunch,
                        nutritionNotes = "提供丰富的蛋白质、维生素和矿物质，促进生长发育",
                        childFriendlyText = "午餐有${lunch.name}，营养满满！"
                    )
                )
            }

            // 晚餐
            if (candidateSet.dinner.isNotEmpty()) {
                val dinner = selectRecipe(
                    candidateSet.dinner,
                    constraints,
                    usedRecipes = meals.map { it.recipe }
                )
                meals.add(
                    PlannedMeal(
                        mealPeriod = MealPeriod.DINNER,
                        recipe = dinner,
                        nutritionNotes = "易消化，提供适量营养，有助于睡眠",
                        childFriendlyText = "晚餐有${dinner.name}，软软的很好消化～"
                    )
                )
            }

            // 点心（可选）
            if (candidateSet.snack.isNotEmpty() && day % 2 == 0) { // 隔天一次点心
                val snack = selectRecipe(
                    candidateSet.snack,
                    constraints,
                    usedRecipes = meals.map { it.recipe }
                )
                meals.add(
                    PlannedMeal(
                        mealPeriod = MealPeriod.SNACK,
                        recipe = snack,
                        nutritionNotes = "提供维生素和矿物质，作为两餐之间的补充",
                        childFriendlyText = "点心有${snack.name}，甜甜的很开心！"
                    )
                )
            }

            // 更新约束统计
            fishCount += meals.count { it.recipe.category.contains("鱼", ignoreCase = true) }
            eggCount += meals.count { it.recipe.category.contains("蛋", ignoreCase = true) }

            dailyPlans.add(DailyMealPlan(currentDate, meals))
        }

        // 计算营养摘要
        val nutritionSummary = calculateNutritionSummary(dailyPlans)

        // 生成替代方案
        val alternativeOptions = generateAlternatives(candidateSet, dailyPlans)

        return WeeklyMealPlan(
            startDate = startDate,
            endDate = kotlinx.datetime.LocalDate.fromEpochDays(startDate.toEpochDays() + 6),
            dailyPlans = dailyPlans,
            nutritionSummary = nutritionSummary,
            alternativeOptions = alternativeOptions
        )
    }

    /**
     * 选择食谱
     * 考虑约束条件和已使用的食谱
     */
    private fun selectRecipe(
        recipes: List<Recipe>,
        constraints: RecommendationConstraints,
        usedRecipes: List<Recipe>
    ): Recipe {
        // 过滤掉已使用的食谱
        val availableRecipes = recipes.filter { it !in usedRecipes }

        // 如果没有可用食谱，返回第一个
        if (availableRecipes.isEmpty()) {
            return recipes.random()
        }

        // 随机选择一个食谱
        return availableRecipes.random()
    }

    /**
     * 计算营养摘要
     */
    private fun calculateNutritionSummary(dailyPlans: List<DailyMealPlan>): com.example.babyfood.domain.model.NutritionSummary {
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

        // 生成营养亮点
        val highlights = mutableListOf<String>()
        if (dailyAverage.protein >= 25) {
            highlights.add("蛋白质摄入充足，有助于肌肉发育")
        }
        if (dailyAverage.calcium >= 400) {
            highlights.add("钙摄入充足，有助于骨骼发育")
        }
        if (dailyAverage.iron >= 10) {
            highlights.add("铁摄入充足，有助于预防贫血")
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

    /**
     * 生成替代方案
     */
    private fun generateAlternatives(
        candidateSet: CandidateRecipeSet,
        dailyPlans: List<DailyMealPlan>
    ): Map<MealPeriod, List<com.example.babyfood.domain.model.RecipeAlternative>> {
        val alternatives = mutableMapOf<MealPeriod, List<com.example.babyfood.domain.model.RecipeAlternative>>()

        // 为每个餐段时间段生成替代方案
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
                        reason = "同类替代方案",
                        nutritionDifference = "营养素相近，可互换"
                    )
                }
            }
        }

        return alternatives
    }
}