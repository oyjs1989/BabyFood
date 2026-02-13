package com.example.babyfood.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 积分信息响应
 */
@Serializable
data class PointsInfo(
    override val success: Boolean = true,
    @SerialName("errorMessage")
    override val errorMessage: String? = null,
    @SerialName("currentBalance")
    val currentBalance: Int = 0,
    @SerialName("lastCheckInDate")
    val lastCheckInDate: Long? = null,
    @SerialName("todayCheckedIn")
    val todayCheckedIn: Boolean = false
) : ApiResponse

/**
 * 签到响应
 */
@Serializable
data class CheckInResponse(
    override val success: Boolean = true,
    @SerialName("errorMessage")
    override val errorMessage: String? = null,
    @SerialName("pointsEarned")
    val pointsEarned: Int = 0,
    @SerialName("currentBalance")
    val currentBalance: Int = 0,
    @SerialName("consecutiveDays")
    val consecutiveDays: Int = 0,
    @SerialName("todayCheckedIn")
    val todayCheckedIn: Boolean = false
) : ApiResponse

/**
 * 积分交易记录
 */
@Serializable
data class PointsTransaction(
    val id: Int = 0,
    @SerialName("transaction_type")
    val transactionType: String = "",
    @SerialName("points_change")
    val pointsChange: Int = 0,
    @SerialName("balance_after")
    val balanceAfter: Int = 0,
    val reason: String? = null,
    @SerialName("reference_id")
    val referenceId: String? = null,
    @SerialName("created_at")
    val createdAt: Long = 0L
)

/**
 * 积分历史响应
 */
@Serializable
data class PointsHistoryResponse(
    override val success: Boolean = true,
    @SerialName("errorMessage")
    override val errorMessage: String? = null,
    val transactions: List<PointsTransaction> = emptyList(),
    val total: Int = 0
) : ApiResponse

/**
 * 积分交易类型枚举
 */
enum class TransactionType(val displayName: String) {
    EARN("获得"),
    CONSUME("消耗"),
    REFUND("退还"),
    ADMIN_ADJUST("系统调整");

    companion object {
        fun fromString(value: String): TransactionType {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: ADMIN_ADJUST
        }
    }
}