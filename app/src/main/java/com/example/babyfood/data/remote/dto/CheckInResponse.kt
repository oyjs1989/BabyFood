package com.example.babyfood.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 每日签到响应
 */
@Serializable
data class CheckInResponse(
    val success: Boolean,
    
    @SerialName("errorMessage")
    val errorMessage: String? = null,
    
    @SerialName("pointsEarned")
    val pointsEarned: Int,
    
    @SerialName("currentBalance")
    val currentBalance: Int,
    
    @SerialName("consecutiveDays")
    val consecutiveDays: Int,
    
    @SerialName("todayCheckedIn")
    val todayCheckedIn: Boolean
)
