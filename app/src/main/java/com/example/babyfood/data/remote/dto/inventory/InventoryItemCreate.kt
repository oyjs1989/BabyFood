package com.example.babyfood.data.remote.dto.inventory

import kotlinx.serialization.Serializable

/**
 * 创建库存物品请求 DTO
 */
@Serializable
data class InventoryItemCreate(
    val name: String,
    val quantity: Double,
    val unit: String,
    val productionDate: String? = null,
    val expiryDate: String? = null,
    val storageMethod: String = "REFRIGERATOR",  // REFRIGERATOR, FREEZER, ROOM_TEMPERATURE
    val notes: String? = null
)
