package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.dao.InventoryItemDao
import com.example.babyfood.data.local.database.entity.InventoryItemEntity
import com.example.babyfood.domain.model.ExpiryStatus
import com.example.babyfood.domain.model.InventoryItem
import com.example.babyfood.domain.model.StorageMethod
import com.example.babyfood.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepository @Inject constructor(
    private val inventoryItemDao: InventoryItemDao
) : SyncableRepository<InventoryItem, InventoryItemEntity, Long>() {

    // Note: InventoryItemDao implements SyncableDao methods implicitly
    // but doesn't extend the interface due to Room limitations

    override fun InventoryItemEntity.toDomainModel(): InventoryItem = InventoryItem(
        id = id,
        foodId = foodId,
        foodName = foodName,
        foodImageUrl = foodImageUrl,
        productionDate = kotlinx.datetime.LocalDate.parse(productionDate),
        expiryDate = kotlinx.datetime.LocalDate.parse(expiryDate),
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

    override fun InventoryItem.toEntity(): InventoryItemEntity = InventoryItemEntity(
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

    override fun getItemId(item: InventoryItem): Long = item.id

    // ============ CRUD Operations ============

    suspend fun getById(id: Long): InventoryItem? =
        inventoryItemDao.getById(id)?.toDomainModel()

    suspend fun insert(item: InventoryItem): Long {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val addedAt = item.addedAt.ifEmpty { currentTime.date.toString() }

        val entity = item.toEntity().copy(
            addedAt = addedAt
        ).prepareForInsert()
        return inventoryItemDao.insert(entity)
    }

    suspend fun update(item: InventoryItem) {
        val existing = inventoryItemDao.getById(item.id)
        if (existing != null) {
            inventoryItemDao.update(item.toEntity().prepareForUpdate(existing))
        }
    }

    suspend fun delete(item: InventoryItem) {
        inventoryItemDao.delete(item.toEntity())
    }

    // ============ Domain-Specific Query Methods ============

    fun getAllInventoryItems(): Flow<List<InventoryItem>> =
        inventoryItemDao.getAllInventoryItems().toDomainModels()

    fun getExpiredItems(): Flow<List<InventoryItem>> =
        inventoryItemDao.getExpiredItems().toDomainModels()

    fun getExpiringItems(): Flow<List<InventoryItem>> =
        inventoryItemDao.getExpiringItems().toDomainModels()

    fun getExpiringItemsWithin7Days(): Flow<List<InventoryItem>> =
        inventoryItemDao.getExpiringItemsWithin7Days().toDomainModels()

    fun getInventoryItemsByStorageMethod(storageMethod: StorageMethod): Flow<List<InventoryItem>> =
        inventoryItemDao.getInventoryItemsByStorageMethod(storageMethod.name).toDomainModels()

    fun searchInventoryItems(query: String): Flow<List<InventoryItem>> =
        inventoryItemDao.searchInventoryItems(query).toDomainModels()

    fun getInventoryItemsByExpiryStatus(status: ExpiryStatus): Flow<List<InventoryItem>> {
        return when (status) {
            ExpiryStatus.EXPIRED -> getExpiredItems()
            ExpiryStatus.URGENT -> getExpiringItems()
            ExpiryStatus.WARNING -> getExpiringItemsWithin7Days()
            ExpiryStatus.NORMAL -> getAllInventoryItems().map { items ->
                items.filter { it.getExpiryStatus() == ExpiryStatus.NORMAL }
            }
        }
    }

    suspend fun getAvailableIngredients(): List<String> = runCatching {
        inventoryItemDao.getAllInventoryItemsSync()
            .filter { !it.isDeleted }
            .map { it.toDomainModel() }
            .filter { !it.isExpired() }
            .map { it.foodName }
            .distinct()
    }.getOrDefault(emptyList())

    suspend fun getExpiringIngredients(): List<String> = runCatching {
        inventoryItemDao.getExpiringItemsSync()
            .filter { !it.isDeleted }
            .map { it.toDomainModel() }
            .filter { !it.isExpired() }
            .map { it.foodName }
            .distinct()
    }.getOrDefault(emptyList())
}