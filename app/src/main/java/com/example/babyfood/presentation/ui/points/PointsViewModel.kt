package com.example.babyfood.presentation.ui.points

import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.remote.api.PointsApiService
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.domain.model.CheckInResponse
import com.example.babyfood.domain.model.PointsHistoryResponse
import com.example.babyfood.domain.model.PointsInfo
import com.example.babyfood.domain.model.PointsTransaction
import com.example.babyfood.presentation.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 积分系统 ViewModel
 * 处理签到、积分查询、积分历史等功能
 */
@HiltViewModel
class PointsViewModel @Inject constructor(
    private val pointsApiService: PointsApiService,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    override val logTag: String = "PointsViewModel"

    companion object {
        const val AI_RECOMMENDATION_COST = 50  // AI推荐消耗积分
    }

    // 积分信息状态
    private val _pointsInfo = MutableStateFlow<PointsInfo?>(null)
    val pointsInfo: StateFlow<PointsInfo?> = _pointsInfo.asStateFlow()

    // 签到响应状态
    private val _checkInResponse = MutableStateFlow<CheckInResponse?>(null)
    val checkInResponse: StateFlow<CheckInResponse?> = _checkInResponse.asStateFlow()

    // 积分历史状态
    private val _pointsHistory = MutableStateFlow<List<PointsTransaction>>(emptyList())
    val pointsHistory: StateFlow<List<PointsTransaction>> = _pointsHistory.asStateFlow()

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 错误消息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadPointsInfo()
    }

    /**
     * 加载积分信息
     */
    fun loadPointsInfo() {
        logMethodStart("加载积分信息")
        _isLoading.value = true
        _errorMessage.value = null

        safeLaunch(
            errorMessage = "加载积分信息",
            onError = {
                _errorMessage.value = "网络错误，请稍后重试"
                _isLoading.value = false
                logMethodEnd("加载积分信息")
            }
        ) {
            // 检查登录状态
            if (!authRepository.isLoggedIn()) {
                logError("未登录，无法获取积分信息")
                _errorMessage.value = "请先登录"
                _isLoading.value = false
                return@safeLaunch
            }

            val response = pointsApiService.getPointsInfo()
            if (response.success) {
                _pointsInfo.value = response
                logSuccess("积分信息加载成功")
                logD("当前积分: ${response.currentBalance}, 今日已签到: ${response.todayCheckedIn}")
            } else {
                logError("积分信息加载失败: ${response.errorMessage}")
                _errorMessage.value = response.errorMessage ?: "加载积分信息失败"
            }
            _isLoading.value = false
            logMethodEnd("加载积分信息")
        }
    }

    /**
     * 每日签到
     */
    fun dailyCheckIn() {
        logMethodStart("每日签到")
        _isLoading.value = true
        _errorMessage.value = null

        safeLaunch(
            errorMessage = "每日签到",
            onError = {
                _errorMessage.value = "网络错误，请稍后重试"
                _isLoading.value = false
                logMethodEnd("每日签到")
            }
        ) {
            // 检查登录状态
            if (!authRepository.isLoggedIn()) {
                logError("未登录，无法签到")
                _errorMessage.value = "请先登录"
                _isLoading.value = false
                return@safeLaunch
            }

            val response = pointsApiService.dailyCheckIn()
            _checkInResponse.value = response

            if (response.success) {
                logSuccess("签到成功")
                logD("获得积分: ${response.pointsEarned}, 当前积分: ${response.currentBalance}, 连续签到: ${response.consecutiveDays}")
                // 重新加载积分信息
                loadPointsInfo()
            } else {
                logError("签到失败: ${response.errorMessage}")
                _errorMessage.value = response.errorMessage ?: "签到失败"
            }
            _isLoading.value = false
            logMethodEnd("每日签到")
        }
    }

    /**
     * 加载积分历史
     * @param limit 每页数量
     * @param offset 偏移量
     */
    fun loadPointsHistory(limit: Int = 20, offset: Int = 0) {
        logMethodStart("加载积分历史")
        logD("限制: $limit, 偏移: $offset")
        _isLoading.value = true
        _errorMessage.value = null

        safeLaunch(
            errorMessage = "加载积分历史",
            onError = {
                _errorMessage.value = "网络错误，请稍后重试"
                _isLoading.value = false
                logMethodEnd("加载积分历史")
            }
        ) {
            // 检查登录状态
            if (!authRepository.isLoggedIn()) {
                logError("未登录，无法获取积分历史")
                _errorMessage.value = "请先登录"
                _isLoading.value = false
                return@safeLaunch
            }

            val response = pointsApiService.getPointsHistory(limit, offset)
            if (response.success) {
                _pointsHistory.value = response.transactions
                logSuccess("积分历史加载成功")
                logD("交易记录数: ${response.transactions.size}, 总数: ${response.total}")
            } else {
                logError("积分历史加载失败: ${response.errorMessage}")
                _errorMessage.value = response.errorMessage ?: "加载积分历史失败"
            }
            _isLoading.value = false
            logMethodEnd("加载积分历史")
        }
    }

    /**
     * 清除错误消息
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * 检查用户是否有足够的积分进行AI推荐
     * @return 是否有足够积分
     */
    fun hasEnoughPointsForRecommendation(): Boolean {
        val info = _pointsInfo.value ?: return false
        return info.currentBalance >= AI_RECOMMENDATION_COST
    }

    /**
     * 获取当前积分余额
     * @return 积分余额
     */
    fun getCurrentBalance(): Int {
        return _pointsInfo.value?.currentBalance ?: 0
    }

    /**
     * 检查今日是否已签到
     * @return 今日是否已签到
     */
    fun hasCheckedInToday(): Boolean {
        return _pointsInfo.value?.todayCheckedIn ?: false
    }

    /**
     * 刷新积分信息（供外部调用）
     */
    fun refreshPointsInfo() {
        loadPointsInfo()
    }
}
