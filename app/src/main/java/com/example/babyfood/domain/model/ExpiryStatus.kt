package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

/**
 * 保质期状态枚举
 */
@Serializable
enum class ExpiryStatus {
    EXPIRED,    // 已过期（红色标签）
    URGENT,     // 紧急（3天内，红色标签）
    WARNING,    // 警告（7天内，橙色标签）
    NORMAL;     // 正常（灰色标签）

    /**
     * 获取显示文本
     */
    fun getDisplayText(): String = when (this) {
        EXPIRED -> "已过期"
        URGENT -> "即将过期"
        WARNING -> "临期"
        NORMAL -> "正常"
    }

    /**
     * 获取提示文本
     */
    fun getAlertText(): String? = when (this) {
        EXPIRED -> "今日到期，请尽快食用！"
        URGENT -> "3天内食用，建议尽快安排"
        WARNING -> "7天内食用，建议合理安排"
        NORMAL -> null
    }
}