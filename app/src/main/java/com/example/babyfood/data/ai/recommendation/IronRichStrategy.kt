package com.example.babyfood.data.ai.recommendation

import android.util.Log
import com.example.babyfood.data.local.database.dao.NutritionDataDao
import com.example.babyfood.domain.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 铁优先推荐策略
 * 基于铁含量对食谱进行评分和排序
 */
class IronRichStrategy(
    private val nutritionDataDao: NutritionDataDao
) {
    
    companion object {
        private const val TAG = "IronRichStrategy"
        private const val IRON_RICH_THRESHOLD = 2.0  // mg/100g，富铁阈值
        private const val IRON_EXCELLENT_THRESHOLD = 5.0  // mg/100g，优秀铁含量
    }
    
    /**
     * 计算食谱的铁含量评分（0-100）
     */
    suspend fun calculateIronScore(recipe: Recipe): Double = withContext(Dispatchers.IO) {
        Log.d(TAG, "========== 开始计算食谱铁评分 ==========")
        Log.d(TAG, "食谱ID: ${recipe.id}, 名称: ${recipe.name}")
        
        var totalIronContent = 0.0
        var ingredientCount = 0
        
        // 计算所有食材的铁含量总和
        recipe.ingredients.forEach { ingredient ->
            val nutritionData = nutritionDataDao.getByIngredientName(ingredient.name)
            if (nutritionData != null) {
                totalIronContent += nutritionData.ironContent
                ingredientCount++
                Log.d(TAG, "食材: ${ingredient.name}, 铁含量: ${nutritionData.ironContent} mg/100g")
            }
        }
        
        // 计算平均铁含量
        val averageIronContent = if (ingredientCount > 0) {
            totalIronContent / ingredientCount
        } else {
            0.0
        }
        
        Log.d(TAG, "总铁含量: $totalIronContent mg/100g, 食材数: $ingredientCount")
        Log.d(TAG, "平均铁含量: $averageIronContent mg/100g")
        
        // 计算评分（0-100）
        val score = when {
            averageIronContent >= IRON_EXCELLENT_THRESHOLD -> 100.0
            averageIronContent >= IRON_RICH_THRESHOLD -> {
                // 线性插值：2.0mg → 60分，5.0mg → 100分
                60.0 + (averageIronContent - IRON_RICH_THRESHOLD) / (IRON_EXCELLENT_THRESHOLD - IRON_RICH_THRESHOLD) * 40.0
            }
            averageIronContent > 0 -> {
                // 线性插值：0mg → 0分，2.0mg → 60分
                averageIronContent / IRON_RICH_THRESHOLD * 60.0
            }
            else -> 0.0
        }
        
        Log.d(TAG, "✓ 铁评分: $score")
        Log.d(TAG, "========== 铁评分计算完成 ==========")
        
        score
    }
    
    /**
     * 判断食谱是否为富铁食谱
     */
    suspend fun isIronRich(recipe: Recipe): Boolean = withContext(Dispatchers.IO) {
        Log.d(TAG, "========== 判断食谱是否富铁 ==========")
        Log.d(TAG, "食谱: ${recipe.name}")
        
        val score = calculateIronScore(recipe)
        val isRich = score >= 60.0  // 评分60分以上视为富铁
        
        Log.d(TAG, "评分: $score, 是否富铁: $isRich")
        Log.d(TAG, "========== 富铁判断完成 ==========")
        
        isRich
    }
    
    /**
     * 获取食谱的铁含量（mg/100g）
     */
    suspend fun getIronContent(recipe: Recipe): Double = withContext(Dispatchers.IO) {
        var totalIronContent = 0.0
        var ingredientCount = 0
        
        recipe.ingredients.forEach { ingredient ->
            val nutritionData = nutritionDataDao.getByIngredientName(ingredient.name)
            if (nutritionData != null) {
                totalIronContent += nutritionData.ironContent
                ingredientCount++
            }
        }
        
        if (ingredientCount > 0) {
            totalIronContent / ingredientCount
        } else {
            0.0
        }
    }
}