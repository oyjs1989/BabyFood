package com.example.babyfood.data.remote.dto.inventory

import kotlinx.serialization.Serializable

/**
 * 库存物品响应 DTO
 */
@Serializable
data class InventoryItemResponse(
    val id: Int,
    val cloudId: String,
    val userId: Int,
    val name: String,
    val quantity: Double,
    val unit: String,
    val productionDate: String? = null,
    val expiryDate: String? = null,
    val storageMethod: String,  // REFRIGERATOR, FREEZER, ROOM_TEMPERATURE
    val notes: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)
