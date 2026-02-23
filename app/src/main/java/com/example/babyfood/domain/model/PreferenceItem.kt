package com.example.babyfood.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.plus
import kotlinx.serialization.Serializable

/**
 * 偏好食材项（带有效期和添加日期）
 */
@Serializable
data class PreferenceItem(
    val ingredient: String,           // 食材名称
    val expiryDate: String? = null,  // 有效期，null 表示永久（使用字符串存储以便序列化）
    val addedDate: String? = null    // 添加日期，记录何时添加的（ISO 8601 格式）
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

        /**
         * 创建新的偏好项（自动添加当前日期）
         */
        fun create(ingredient: String, expiryDays: Int? = null): PreferenceItem {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
            val expiryDate = expiryDays?.let { days ->
                val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                currentDate.plus(days, kotlinx.datetime.DateTimeUnit.DAY).toString()
            }
            return PreferenceItem(
                ingredient = ingredient,
                expiryDate = expiryDate,
                addedDate = today
            )
        }
    }
}