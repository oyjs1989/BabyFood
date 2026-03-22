package com.example.babyfood.presentation.ui

import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.preferences.PreferencesManager
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.data.repository.HealthRecordRepository
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.NutritionGoal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主屏幕 ViewModel
 * 处理注销等全局操作，并维护当前选中的宝宝（供顶部 Header 显示）
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val babyRepository: BabyRepository,
    private val healthRecordRepository: HealthRecordRepository,
    private val preferencesManager: PreferencesManager
) : BaseViewModel() {

    override val logTag: String = "MainViewModel"

    private val _selectedBaby = MutableStateFlow<Baby?>(null)
    /** 当前选中的宝宝，供顶部 AvocadoHeader 等使用 */
    val selectedBaby: StateFlow<Baby?> = _selectedBaby.asStateFlow()
    val isLoggedIn: StateFlow<Boolean> = authRepository.observeLoginState()
    private val _isAuthCheckCompleted = MutableStateFlow(false)
    val isAuthCheckCompleted: StateFlow<Boolean> = _isAuthCheckCompleted.asStateFlow()

    init {
        bootstrapSession()
        refreshSelectedBaby()
    }

    /**
     * App 启动时恢复会话
     */
    private fun bootstrapSession() {
        viewModelScope.launch {
            try {
                authRepository.restoreSession()
            } finally {
                _isAuthCheckCompleted.value = true
            }
        }
    }

    /**
     * 根据 Preferences 中的选中 ID 刷新当前宝宝
     * 在进入主 Tab 或切换宝宝后调用，使顶部 Header 显示正确宝宝信息
     */
    fun refreshSelectedBaby() {
        viewModelScope.launch {
            val id = preferencesManager.getSelectedBabyId()
            _selectedBaby.value = if (id != -1L) babyRepository.getById(id) else null
        }
    }

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
     * 处理会话过期
     * token 过期或服务端返回 401 时，统一清理本地登录态
     */
    fun handleSessionExpired(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            authRepository.clearLocalSession()
            _selectedBaby.value = null
            _isAuthCheckCompleted.value = true
            onComplete()
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
