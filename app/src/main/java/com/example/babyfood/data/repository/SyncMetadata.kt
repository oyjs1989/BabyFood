package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.entity.BabyEntity
import com.example.babyfood.data.local.database.entity.InventoryItemEntity
import com.example.babyfood.data.local.database.entity.PlanEntity
import com.example.babyfood.data.local.database.entity.RecipeEntity

/**
 * Entity preparation extension functions for synchronization
 *
 * These functions prepare entities for database insert/update operations by setting
 * appropriate sync metadata (cloudId, syncStatus, lastSyncTime, version, isDeleted).
 *
 * Pattern:
 * - prepareForInsert(): Sets metadata for new local records
 * - prepareForUpdate(): Increments version and marks for sync when updating existing records
 *
 * Note: Each entity has its own prepareForInsert/prepareForUpdate because some entities
 * have additional sync-related fields (e.g., PlanEntity has cloudBabyId and cloudRecipeId).
 */

// ============ BabyEntity ============

/**
 * Prepare BabyEntity for insert (new local record)
 * Sets: cloudId=null, syncStatus=PENDING_UPLOAD, lastSyncTime=null, version=1, isDeleted=false
 */
fun BabyEntity.prepareForInsert(): BabyEntity = this.copy(
    cloudId = null,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = null,
    version = 1,
    isDeleted = false
)

/**
 * Prepare BabyEntity for update
 * Preserves cloudId and lastSyncTime, increments version, marks as PENDING_UPLOAD
 * @param existing The existing entity in the database
 * @param isDeleted Whether to mark as deleted (soft delete)
 */
fun BabyEntity.prepareForUpdate(existing: BabyEntity, isDeleted: Boolean = false): BabyEntity = this.copy(
    cloudId = existing.cloudId,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = existing.lastSyncTime,
    version = existing.version + 1,
    isDeleted = isDeleted
)

// ============ PlanEntity ============

/**
 * Prepare PlanEntity for insert (new local record)
 * Sets: cloudId=null, cloudBabyId=null, cloudRecipeId=null, syncStatus=PENDING_UPLOAD,
 *       lastSyncTime=null, version=1, isDeleted=false
 */
fun PlanEntity.prepareForInsert(): PlanEntity = this.copy(
    cloudId = null,
    cloudBabyId = null,
    cloudRecipeId = null,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = null,
    version = 1,
    isDeleted = false
)

/**
 * Prepare PlanEntity for update
 * Preserves cloudId, cloudBabyId, cloudRecipeId and lastSyncTime,
 * increments version, marks as PENDING_UPLOAD
 * @param existing The existing entity in the database
 * @param isDeleted Whether to mark as deleted (soft delete)
 */
fun PlanEntity.prepareForUpdate(existing: PlanEntity, isDeleted: Boolean = false): PlanEntity = this.copy(
    cloudId = existing.cloudId,
    cloudBabyId = existing.cloudBabyId,
    cloudRecipeId = existing.cloudRecipeId,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = existing.lastSyncTime,
    version = existing.version + 1,
    isDeleted = isDeleted
)

// ============ RecipeEntity ============

/**
 * Prepare RecipeEntity for insert (new local record)
 * Sets: cloudId=null, syncStatus=PENDING_UPLOAD, lastSyncTime=null, version=1, isDeleted=false
 */
fun RecipeEntity.prepareForInsert(): RecipeEntity = this.copy(
    cloudId = null,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = null,
    version = 1,
    isDeleted = false
)

/**
 * Prepare RecipeEntity for update
 * Preserves cloudId and lastSyncTime, increments version, marks as PENDING_UPLOAD
 * @param existing The existing entity in the database
 * @param isDeleted Whether to mark as deleted (soft delete)
 */
fun RecipeEntity.prepareForUpdate(existing: RecipeEntity, isDeleted: Boolean = false): RecipeEntity = this.copy(
    cloudId = existing.cloudId,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = existing.lastSyncTime,
    version = existing.version + 1,
    isDeleted = isDeleted
)

// ============ InventoryItemEntity ============

/**
 * Prepare InventoryItemEntity for insert (new local record)
 * Sets: cloudId=null, syncStatus=PENDING_UPLOAD, lastSyncTime=null, version=1, isDeleted=false
 */
fun InventoryItemEntity.prepareForInsert(): InventoryItemEntity = this.copy(
    cloudId = null,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = null,
    version = 1,
    isDeleted = false
)

/**
 * Prepare InventoryItemEntity for update
 * Preserves cloudId and lastSyncTime, increments version, marks as PENDING_UPLOAD
 * @param existing The existing entity in the database
 * @param isDeleted Whether to mark as deleted (soft delete)
 */
fun InventoryItemEntity.prepareForUpdate(existing: InventoryItemEntity, isDeleted: Boolean = false): InventoryItemEntity = this.copy(
    cloudId = existing.cloudId,
    syncStatus = "PENDING_UPLOAD",
    lastSyncTime = existing.lastSyncTime,
    version = existing.version + 1,
    isDeleted = isDeleted
)