package com.example.babyfood.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.babyfood.domain.model.InventoryItem
import com.example.babyfood.domain.model.StorageMethod
import com.example.babyfood.domain.model.SyncStatus
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "inventory_items",
    indices = [
        Index(value = ["foodId"]),
        Index(value = ["expiryDate"]),
        Index(value = ["cloudId"])
    ]
)
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "foodId")
    val foodId: Long,
    @ColumnInfo(name = "foodName")
    val foodName: String,
    @ColumnInfo(name = "foodImageUrl")
    val foodImageUrl: String?,
    @ColumnInfo(name = "productionDate")
    val productionDate: String,
    @ColumnInfo(name = "expiryDate")
    val expiryDate: String,
    @ColumnInfo(name = "storageMethod")
    val storageMethod: String,
    @ColumnInfo(name = "quantity")
    val quantity: Float,
    @ColumnInfo(name = "unit")
    val unit: String,
    @ColumnInfo(name = "addedAt")
    val addedAt: String,
    @ColumnInfo(name = "notes")
    val notes: String?,
    @ColumnInfo(name = "cloudId")
    override val cloudId: String? = null,
    @ColumnInfo(name = "syncStatus", defaultValue = "LOCAL_ONLY")
    override val syncStatus: String = "LOCAL_ONLY",
    @ColumnInfo(name = "lastSyncTime")
    override val lastSyncTime: Long? = null,
    @ColumnInfo(name = "version", defaultValue = "1")
    override val version: Int = 1,
    @ColumnInfo(name = "isDeleted", defaultValue = "0")
    override val isDeleted: Boolean = false
) : SyncableEntity {
    fun toDomainModel(): InventoryItem = InventoryItem(
        id = id,
        foodId = foodId,
        foodName = foodName,
        foodImageUrl = foodImageUrl,
        productionDate = LocalDate.parse(productionDate),
        expiryDate = LocalDate.parse(expiryDate),
        storageMethod = StorageMethod.valueOf(storageMethod),
        quantity = quantity,
        unit = unit,
        addedAt = addedAt,
        notes = notes,
        cloudId = cloudId,
        syncStatus = SyncStatus.valueOf(syncStatus),
        lastSyncTime = lastSyncTime,
        version = version,
        isDeleted = isDeleted
    )
}

fun InventoryItem.toEntity(): InventoryItemEntity = InventoryItemEntity(
    id = id,
    foodId = foodId,
    foodName = foodName,
    foodImageUrl = foodImageUrl,
    productionDate = productionDate.toString(),
    expiryDate = expiryDate.toString(),
    storageMethod = storageMethod.name,
    quantity = quantity,
    unit = unit,
    addedAt = addedAt,
    notes = notes,
    cloudId = cloudId,
    syncStatus = syncStatus.name,
    lastSyncTime = lastSyncTime,
    version = version,
    isDeleted = isDeleted
)