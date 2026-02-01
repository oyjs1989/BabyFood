package com.example.babyfood.data.repository

import android.util.Log
import com.example.babyfood.data.local.database.dao.NutritionGoalDao
import com.example.babyfood.data.local.database.entity.NutritionGoalEntity
import com.example.babyfood.domain.model.NutritionGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 营养目标仓库
 *
 * 负责管理宝宝的营养目标设置
 */
@Singleton
class NutritionGoalRepository @Inject constructor(
    private val nutritionGoalDao: NutritionGoalDao
) {
    companion object {
        private const val TAG = "NutritionGoalRepository"
    }

    /**
     * 获取宝宝的营养目标
     */
    fun getNutritionGoalByBabyId(babyId: Long): Flow<NutritionGoal?> {
        return nutritionGoalDao.getByBabyIdFlow(babyId).map { entity ->
            entity?.toDomainModel()
        }
    }

    /**
     * 获取宝宝的营养目标（同步版本）
     */
    suspend fun getNutritionGoalByBabyIdSync(babyId: Long): NutritionGoal? {
        return nutritionGoalDao.getByBabyId(babyId)?.toDomainModel()
    }

    /**
     * 保存营养目标
     */
    suspend fun saveNutritionGoal(babyId: Long, goal: NutritionGoal) {
        Log.d(TAG, "========== 保存营养目标 ==========")
        Log.d(TAG, "宝宝ID: $babyId")
        Log.d(TAG, "热量: ${goal.calories} kcal")
        Log.d(TAG, "蛋白质: ${goal.protein} g")
        Log.d(TAG, "钙: ${goal.calcium} mg")
        Log.d(TAG, "铁: ${goal.iron} mg")

        val existingGoal = nutritionGoalDao.getByBabyId(babyId)
        if (existingGoal == null) {
            val id = nutritionGoalDao.insert(
                NutritionGoalEntity(
                    babyId = babyId,
                    calories = goal.calories.toDouble(),
                    protein = goal.protein.toDouble(),
                    calcium = goal.calcium.toDouble(),
                    iron = goal.iron.toDouble(),
                    vitaminA = 0.0,
                    vitaminC = 0.0
                )
            )
            Log.d(TAG, "✓ 插入成功，ID: $id")
        } else {
            nutritionGoalDao.update(
                babyId = babyId,
                calories = goal.calories.toDouble(),
                protein = goal.protein.toDouble(),
                calcium = goal.calcium.toDouble(),
                iron = goal.iron.toDouble(),
                vitaminA = 0.0,
                vitaminC = 0.0
            )
            Log.d(TAG, "✓ 更新成功")
        }

        Log.d(TAG, "========== 保存完成 ==========")
    }

    /**
     * 删除宝宝的营养目标
     */
    suspend fun deleteNutritionGoal(babyId: Long) {
        Log.d(TAG, "========== 删除营养目标 ==========")
        Log.d(TAG, "宝宝ID: $babyId")

        nutritionGoalDao.deleteByBabyId(babyId)

        Log.d(TAG, "✓ 删除成功")
        Log.d(TAG, "========== 删除完成 ==========")
    }

    /**
     * 重置为年龄推荐值
     */
    suspend fun resetToRecommended(babyId: Long, ageInMonths: Int) {
        Log.d(TAG, "========== 重置为推荐值 ==========")
        Log.d(TAG, "宝宝ID: $babyId")
        Log.d(TAG, "月龄: $ageInMonths")

        val recommendedGoal = NutritionGoal.calculateByAge(ageInMonths)
        saveNutritionGoal(babyId, recommendedGoal)

        Log.d(TAG, "✓ 重置完成: $recommendedGoal")
        Log.d(TAG, "========== 重置完成 ==========")
    }
}

/**
 * NutritionGoalEntity 扩展函数
 */
private fun NutritionGoalEntity.toDomainModel(): NutritionGoal {
    return NutritionGoal(
        calories = calories.toFloat(),
        protein = protein.toFloat(),
        calcium = calcium.toFloat(),
        iron = iron.toFloat()
    )
}