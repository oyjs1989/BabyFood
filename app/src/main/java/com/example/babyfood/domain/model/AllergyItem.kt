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
 * 过敏食材项（带有效期和添加日期）
 */
@Serializable
data class AllergyItem(
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
        val expiry = LocalDate.parse(expiryDate)
        return today > expiry
    }

    companion object {
        /**
         * 从字符串列表创建 AllergyItem 列表（向后兼容）
         */
        fun fromStringList(list: List<String>): List<AllergyItem> {
            return list.map { ingredient ->
                AllergyItem(ingredient, null)
            }
        }

        /**
         * 创建新的过敏项（自动添加当前日期）
         */
        fun create(ingredient: String, expiryDays: Int? = null): AllergyItem {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
            val expiryDate = expiryDays?.let { days ->
                val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                currentDate.plus(days, kotlinx.datetime.DateTimeUnit.DAY).toString()
            }
            return AllergyItem(
                ingredient = ingredient,
                expiryDate = expiryDate,
                addedDate = today
            )
        }
    }
}