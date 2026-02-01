package com.example.babyfood.data.repository

import android.util.Log
import com.example.babyfood.data.local.database.dao.IngredientTrialDao
import com.example.babyfood.domain.model.IngredientTrial
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 食材尝试记录仓库
 *
 * 负责管理宝宝尝试过的食材记录
 */
@Singleton
class IngredientTrialRepository @Inject constructor(
    private val ingredientTrialDao: IngredientTrialDao
) {
    companion object {
        private const val TAG = "IngredientTrialRepository"
    }

    /**
     * 获取宝宝的所有食材尝试记录
     */
    suspend fun getTrialsByBabyId(babyId: Long): List<IngredientTrial> {
        val entities = ingredientTrialDao.getByBabyId(babyId)
        return entities.map { IngredientTrial.fromEntity(it) }
    }

    /**
     * 获取宝宝已尝试的食材列表（非过敏）
     */
    suspend fun getTriedIngredients(babyId: Long): List<String> {
        return ingredientTrialDao.getTriedIngredients(babyId)
    }

    /**
     * 获取食材尝试记录数量
     */
    suspend fun countByBabyId(babyId: Long): Int {
        return ingredientTrialDao.countByBabyId(babyId)
    }

    /**
     * 插入食材尝试记录
     */
    suspend fun insert(trial: IngredientTrial): Long {
        Log.d(TAG, "========== 插入食材尝试记录 ==========")
        Log.d(TAG, "宝宝ID: ${trial.babyId}")
        Log.d(TAG, "食材: ${trial.ingredientName}")
        Log.d(TAG, "过敏: ${trial.isAllergic}")
        Log.d(TAG, "偏好: ${trial.preference}")
        
        val id = ingredientTrialDao.insert(IngredientTrial.toEntity(trial))
        Log.d(TAG, "✓ 插入成功，ID: $id")
        Log.d(TAG, "========== 插入完成 ==========")
        return id
    }

    /**
     * 批量插入食材尝试记录
     */
    suspend fun insertAll(trials: List<IngredientTrial>) {
        Log.d(TAG, "========== 批量插入食材尝试记录 ==========")
        Log.d(TAG, "数量: ${trials.size}")
        
        val entities = trials.map { IngredientTrial.toEntity(it) }
        ingredientTrialDao.insertAll(entities)
        
        Log.d(TAG, "✓ 批量插入成功")
        Log.d(TAG, "========== 插入完成 ==========")
    }

    /**
     * 删除食材尝试记录
     */
    suspend fun deleteById(id: Long) {
        Log.d(TAG, "========== 删除食材尝试记录 ==========")
        Log.d(TAG, "记录ID: $id")
        
        ingredientTrialDao.deleteById(id)
        
        Log.d(TAG, "✓ 删除成功")
        Log.d(TAG, "========== 删除完成 ==========")
    }

    /**
     * 删除宝宝的所有食材尝试记录
     */
    suspend fun deleteByBabyId(babyId: Long) {
        Log.d(TAG, "========== 删除宝宝所有食材尝试记录 ==========")
        Log.d(TAG, "宝宝ID: $babyId")
        
        ingredientTrialDao.deleteByBabyId(babyId)
        
        Log.d(TAG, "✓ 删除成功")
        Log.d(TAG, "========== 删除完成 ==========")
    }

    /**
     * 检查食材是否已尝试过
     */
    suspend fun hasTriedIngredient(babyId: Long, ingredientName: String): Boolean {
        val triedIngredients = getTriedIngredients(babyId)
        return triedIngredients.contains(ingredientName)
    }
}