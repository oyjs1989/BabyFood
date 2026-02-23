package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

/**
 * 保存方式枚举
 */
@Serializable
enum class StorageMethod(val displayName: String) {
    REFRIGERATOR("冰箱冷藏"),
    FREEZER("冰箱冷冻"),
    ROOM_TEMP("常温")
}