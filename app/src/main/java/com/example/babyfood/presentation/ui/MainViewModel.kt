package com.example.babyfood.presentation.ui

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
) : BaseViewModel() {

    override val logTag: String = "MainViewModel"

    /**
     * 获取认证仓库（供 AppHeader 使用）
     */
    fun getAuthRepository(): AuthRepository = authRepository

    /**
     * 获取宝宝仓库（供 AppHeader 使用）
     */
    fun getBabyRepository(): BabyRepository = babyRepository

    /**
     * 用户注销
     * @param onSuccess 注销成功回调
     * @param onFailure 注销失败回调
     */
    fun logout(onSuccess: () -> Unit, onFailure: () -> Unit = {}) {
        logMethodStart("注销")
        viewModelScope.launch {
            val success = authRepository.logout()
            if (success) {
                logSuccess("注销成功")
                onSuccess()
            } else {
                logError("注销失败")
                onFailure()
            }
            logMethodEnd("注销")
        }
    }

    /**
     * 更新宝宝营养目标
     */
    fun updateBabyNutritionGoal(babyId: Long, goal: NutritionGoal) {
        logMethodStart("更新营养目标")
        logD("宝宝 ID: $babyId, 营养目标: $goal")

        safeLaunch("更新营养目标") {
            babyRepository.updateNutritionGoal(babyId, goal)
            logSuccess("营养目标更新成功")
            logMethodEnd("更新营养目标")
        }
    }

    /**
     * 生成营养目标推荐（结合体检数据）
     */
    suspend fun generateNutritionRecommendation(babyId: Long): NutritionGoal? {
        logMethodStart("生成营养目标推荐")
        logD("宝宝 ID: $babyId")

        return try {
            val baby = babyRepository.getById(babyId)
            if (baby == null) {
                logError("宝宝不存在")
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

            logSuccess("推荐生成成功: $recommendation")
            logMethodEnd("生成营养目标推荐")
            recommendation
        } catch (e: Exception) {
            logError("推荐生成失败: ${e.message}", e)
            logMethodEnd("生成营养目标推荐")
            null
        }
    }
}
