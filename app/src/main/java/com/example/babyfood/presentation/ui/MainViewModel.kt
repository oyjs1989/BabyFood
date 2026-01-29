package com.example.babyfood.presentation.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.data.repository.HealthRecordRepository
import com.example.babyfood.domain.model.NutritionGoal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主屏幕 ViewModel
 * 处理注销等全局操作
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val babyRepository: BabyRepository,
    private val healthRecordRepository: HealthRecordRepository
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    /**
     * 用户注销
     * @param onSuccess 注销成功回调
     * @param onFailure 注销失败回调
     */
    fun logout(onSuccess: () -> Unit, onFailure: () -> Unit = {}) {
        viewModelScope.launch {
            Log.d(TAG, "========== 开始注销 ==========")
            val success = authRepository.logout()
            if (success) {
                Log.d(TAG, "✓ 注销成功")
                onSuccess()
            } else {
                Log.e(TAG, "❌ 注销失败")
                onFailure()
            }
            Log.d(TAG, "========== 注销完成 ==========")
        }
    }

    /**
     * 更新宝宝营养目标
     */
    fun updateBabyNutritionGoal(babyId: Long, goal: NutritionGoal) {
        viewModelScope.launch {
            Log.d(TAG, "========== 更新营养目标 ==========")
            Log.d(TAG, "宝宝 ID: $babyId")
            Log.d(TAG, "营养目标: $goal")
            try {
                babyRepository.updateNutritionGoal(babyId, goal)
                Log.d(TAG, "✓ 营养目标更新成功")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 营养目标更新失败: ${e.message}")
            }
            Log.d(TAG, "========== 营养目标更新完成 ==========")
        }
    }

    /**
     * 生成营养目标推荐（结合体检数据）
     */
    suspend fun generateNutritionRecommendation(babyId: Long): NutritionGoal? {
        Log.d(TAG, "========== 生成营养目标推荐 ==========")
        Log.d(TAG, "宝宝 ID: $babyId")
        return try {
            val baby = babyRepository.getById(babyId)
            if (baby == null) {
                Log.e(TAG, "❌ 宝宝不存在")
                return null
            }

            // 获取最新体检数据
            val latestHealthRecord = healthRecordRepository.getLatestHealthRecord(babyId)

            // 生成智能推荐
            val recommendation = NutritionGoal.calculateWithHealthData(
                baby.ageInMonths,
                currentWeight = latestHealthRecord?.weight,
                currentHeight = latestHealthRecord?.height,
                hemoglobin = latestHealthRecord?.hemoglobin,
                ironLevel = latestHealthRecord?.ironLevel,
                calciumLevel = latestHealthRecord?.calciumLevel
            )

            Log.d(TAG, "✓ 推荐生成成功: $recommendation")
            Log.d(TAG, "========== 推荐生成完成 ==========")
            recommendation
        } catch (e: Exception) {
            Log.e(TAG, "❌ 推荐生成失败: ${e.message}")
            Log.e(TAG, "异常堆栈: ", e)
            Log.d(TAG, "========== 推荐生成完成 ==========")
            null
        }
    }
}