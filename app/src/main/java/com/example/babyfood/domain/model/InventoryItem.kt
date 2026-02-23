package com.example.babyfood.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

/**
 * 仓库物品模型
 */
@Serializable
data class InventoryItem(
    val id: Long = 0,
    val foodId: Long,              // 关联后端食材ID
    val foodName: String,          // 食材名称（冗余字段，用于快速显示）
    val foodImageUrl: String?,     // 食材图片（从后端获取）
    val productionDate: LocalDate, // 生产日期
    val expiryDate: LocalDate,     // 保质期
    val storageMethod: StorageMethod, // 保存方式
    val quantity: Float,           // 数量
    val unit: String,              // 单位
    val addedAt: String,           // 添加时间（ISO 8601 格式）
    val notes: String? = null,     // 备注

    // 同步元数据字段
    val cloudId: String? = null,                    // 云端唯一标识
    val syncStatus: SyncStatus = SyncStatus.LOCAL_ONLY, // 同步状态
    val lastSyncTime: Long? = null,                 // 最后同步时间戳（毫秒）
    val version: Int = 1,                           // 版本号
    val isDeleted: Boolean = false                  // 软删除标记
) {
    /**
     * 检查是否已过期
     */
    fun isExpired(): Boolean {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return today > expiryDate
    }

    /**
     * 获取剩余天数
     */
    fun getRemainingDays(): Int {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val daysUntilExpiry = (expiryDate.toEpochDays() - today.toEpochDays()).toInt()
        return daysUntilExpiry
    }

    /**
     * 获取保质期状态
     */
    fun getExpiryStatus(): ExpiryStatus {
        val remainingDays = getRemainingDays()
        return when {
            remainingDays < 0 -> ExpiryStatus.EXPIRED
            remainingDays <= 3 -> ExpiryStatus.URGENT
            remainingDays <= 7 -> ExpiryStatus.WARNING
            else -> ExpiryStatus.NORMAL
        }
    }

    /**
     * 获取剩余天数显示文本
     */
    fun getRemainingDaysText(): String {
        val remainingDays = getRemainingDays()
        return when {
            remainingDays < 0 -> "已过期 ${-remainingDays} 天"
            remainingDays == 0 -> "今日到期"
            remainingDays == 1 -> "剩余 1 天"
            else -> "剩余 $remainingDays 天"
        }
    }

    /**
     * 获取保质期标签文本
     */
    fun getExpiryLabelText(): String {
        val remainingDays = getRemainingDays()
        return when {
            remainingDays < 0 -> "已过期"
            remainingDays == 0 -> "今日到期"
            remainingDays <= 3 -> "3天内到期"
            remainingDays <= 7 -> "7天内到期"
            else -> "${remainingDays}天后到期"
        }
    }
}