package com.example.babyfood.presentation.ui.points

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.remote.api.PointsApiService
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.domain.model.CheckInResponse
import com.example.babyfood.domain.model.PointsHistoryResponse
import com.example.babyfood.domain.model.PointsInfo
import com.example.babyfood.domain.model.PointsTransaction
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
) : ViewModel() {

    companion object {
        private const val TAG = "PointsViewModel"
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
        viewModelScope.launch {
            Log.d(TAG, "========== 加载积分信息 ==========")
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // 检查是否已登录（通过同步方式）
                val isLoggedIn = authRepository.isLoggedIn()
                if (!isLoggedIn) {
                    Log.e(TAG, "❌ 未登录，无法获取积分信息")
                    _errorMessage.value = "请先登录"
                    _isLoading.value = false
                    return@launch
                }

                val response = pointsApiService.getPointsInfo()
                if (response.success) {
                    _pointsInfo.value = response
                    Log.d(TAG, "✓ 积分信息加载成功")
                    Log.d(TAG, "当前积分: ${response.currentBalance}")
                    Log.d(TAG, "今日已签到: ${response.todayCheckedIn}")
                } else {
                    Log.e(TAG, "❌ 积分信息加载失败: ${response.errorMessage}")
                    _errorMessage.value = response.errorMessage ?: "加载积分信息失败"
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ 加载积分信息异常: ${e.message}")
                Log.e(TAG, "异常堆栈: ", e)
                _errorMessage.value = "网络错误，请稍后重试"
            } finally {
                _isLoading.value = false
                Log.d(TAG, "========== 积分信息加载完成 ==========")
            }
        }
    }

    /**
     * 每日签到
     */
    fun dailyCheckIn() {
        viewModelScope.launch {
            Log.d(TAG, "========== 每日签到 ==========")
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // 检查是否已登录（通过同步方式）
                val isLoggedIn = authRepository.isLoggedIn()
                if (!isLoggedIn) {
                    Log.e(TAG, "❌ 未登录，无法签到")
                    _errorMessage.value = "请先登录"
                    _isLoading.value = false
                    return@launch
                }

                val response = pointsApiService.dailyCheckIn()
                _checkInResponse.value = response

                if (response.success) {
                    Log.d(TAG, "✓ 签到成功")
                    Log.d(TAG, "获得积分: ${response.pointsEarned}")
                    Log.d(TAG, "当前积分: ${response.currentBalance}")
                    Log.d(TAG, "连续签到天数: ${response.consecutiveDays}")
                    // 重新加载积分信息
                    loadPointsInfo()
                } else {
                    Log.e(TAG, "❌ 签到失败: ${response.errorMessage}")
                    _errorMessage.value = response.errorMessage ?: "签到失败"
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ 签到异常: ${e.message}")
                Log.e(TAG, "异常堆栈: ", e)
                _errorMessage.value = "网络错误，请稍后重试"
            } finally {
                _isLoading.value = false
                Log.d(TAG, "========== 签到完成 ==========")
            }
        }
    }

    /**
     * 加载积分历史
     * @param limit 每页数量
     * @param offset 偏移量
     */
    fun loadPointsHistory(limit: Int = 20, offset: Int = 0) {
        viewModelScope.launch {
            Log.d(TAG, "========== 加载积分历史 ==========")
            Log.d(TAG, "限制: $limit, 偏移: $offset")
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // 检查是否已登录（通过同步方式）
                val isLoggedIn = authRepository.isLoggedIn()
                if (!isLoggedIn) {
                    Log.e(TAG, "❌ 未登录，无法获取积分历史")
                    _errorMessage.value = "请先登录"
                    _isLoading.value = false
                    return@launch
                }

                val response = pointsApiService.getPointsHistory(limit, offset)
                if (response.success) {
                    _pointsHistory.value = response.transactions
                    Log.d(TAG, "✓ 积分历史加载成功")
                    Log.d(TAG, "交易记录数: ${response.transactions.size}")
                    Log.d(TAG, "总数: ${response.total}")
                } else {
                    Log.e(TAG, "❌ 积分历史加载失败: ${response.errorMessage}")
                    _errorMessage.value = response.errorMessage ?: "加载积分历史失败"
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ 加载积分历史异常: ${e.message}")
                Log.e(TAG, "异常堆栈: ", e)
                _errorMessage.value = "网络错误，请稍后重试"
            } finally {
                _isLoading.value = false
                Log.d(TAG, "========== 积分历史加载完成 ==========")
            }
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