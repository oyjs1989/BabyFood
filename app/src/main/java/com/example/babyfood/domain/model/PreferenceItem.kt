package com.example.babyfood.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

/**
 * 偏好食材项（带有效期）
 */
@Serializable
data class PreferenceItem(
    val ingredient: String,           // 食材名称
    val expiryDate: String? = null  // 有效期，null 表示永久（使用字符串存储以便序列化）
) {
    /**
     * 检查是否已过期
     */
    fun isExpired(): Boolean {
        if (expiryDate == null) return false
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val expiry = kotlinx.datetime.LocalDate.parse(expiryDate)
        return today > expiry
    }

    companion object {
        /**
         * 从字符串列表创建 PreferenceItem 列表（向后兼容）
         */
        fun fromStringList(list: List<String>): List<PreferenceItem> {
            return list.map { ingredient ->
                PreferenceItem(ingredient, null)
            }
        }
    }
}