package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.entity.BabyEntity
import com.example.babyfood.data.local.database.entity.InventoryItemEntity
import com.example.babyfood.data.local.database.entity.PlanEntity
import com.example.babyfood.data.local.database.entity.RecipeEntity

/**
 * 同步元数据接口
 * 所有需要同步的实体都应该实现此接口
 */
interface SyncMetadata {
    val cloudId: String?
    val syncStatus: String
    val lastSyncTime: Long?
    val version: Int
    val isDeleted: Boolean
}

/**
 * 准备用于插入的实体（新记录）
 * 设置 syncStatus = PENDING_UPLOAD, lastSyncTime = null, version = 1, isDeleted = false
 */
fun BabyEntity.prepareForInsert(): BabyEntity = this.copy(
    cloudId = null,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = null,
    version = 1,
    isDeleted = false
)

fun PlanEntity.prepareForInsert(): PlanEntity = this.copy(
    cloudId = null,
    cloudBabyId = null,
    cloudRecipeId = null,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = null,
    version = 1,
    isDeleted = false
)

fun RecipeEntity.prepareForInsert(): RecipeEntity = this.copy(
    cloudId = null,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = null,
    version = 1,
    isDeleted = false
)

/**
 * 准备用于更新的实体
 * 保留现有的 cloudId, lastSyncTime，设置 syncStatus = PENDING_UPLOAD, version + 1
 */
fun BabyEntity.prepareForUpdate(existing: BabyEntity, isDeleted: Boolean = false): BabyEntity = this.copy(
    cloudId = existing.cloudId,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = existing.lastSyncTime,
    version = existing.version + 1,
    isDeleted = isDeleted
)

fun PlanEntity.prepareForUpdate(existing: PlanEntity, isDeleted: Boolean = false): PlanEntity = this.copy(
    cloudId = existing.cloudId,
    cloudBabyId = existing.cloudBabyId,
    cloudRecipeId = existing.cloudRecipeId,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = existing.lastSyncTime,
    version = existing.version + 1,
    isDeleted = isDeleted
)

fun RecipeEntity.prepareForUpdate(existing: RecipeEntity, isDeleted: Boolean = false): RecipeEntity = this.copy(
    cloudId = existing.cloudId,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = existing.lastSyncTime,
    version = existing.version + 1,
    isDeleted = isDeleted
)

/**
 * InventoryItemEntity 扩展函数
 */
fun InventoryItemEntity.prepareForInsert(): InventoryItemEntity = this.copy(
    cloudId = null,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = null,
    version = 1,
    isDeleted = false
)

fun InventoryItemEntity.prepareForUpdate(existing: InventoryItemEntity, isDeleted: Boolean = false): InventoryItemEntity = this.copy(
    cloudId = existing.cloudId,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = existing.lastSyncTime,
    version = existing.version + 1,
    isDeleted = isDeleted
)