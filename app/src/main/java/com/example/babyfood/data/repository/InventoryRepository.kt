package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.dao.InventoryItemDao
import com.example.babyfood.data.local.database.entity.InventoryItemEntity
import com.example.babyfood.data.local.database.entity.toEntity
import com.example.babyfood.domain.model.ExpiryStatus
import com.example.babyfood.domain.model.InventoryItem
import com.example.babyfood.domain.model.StorageMethod
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
) {
    private fun Flow<List<InventoryItemEntity>>.toDomainModels(): Flow<List<InventoryItem>> =
        map { entities -> entities.map { it.toDomainModel() } }

    fun getAllInventoryItems(): Flow<List<InventoryItem>> =
        inventoryItemDao.getAllInventoryItems().toDomainModels()

    suspend fun getInventoryItemById(itemId: Long): InventoryItem? =
        inventoryItemDao.getInventoryItemById(itemId)?.toDomainModel()

    fun getInventoryItemsByFoodId(foodId: Long): Flow<List<InventoryItem>> =
        inventoryItemDao.getInventoryItemsByFoodId(foodId).toDomainModels()

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

    suspend fun insertInventoryItem(item: InventoryItem): Long {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val addedAt = item.addedAt.ifEmpty { currentTime.date.toString() }

        val entity = item.toEntity().copy(
            addedAt = addedAt
        ).prepareForInsert()
        return inventoryItemDao.insertInventoryItem(entity)
    }

    suspend fun updateInventoryItem(item: InventoryItem) {
        val existing = inventoryItemDao.getInventoryItemById(item.id)
        if (existing != null) {
            val entity = item.toEntity().prepareForUpdate(existing)
            inventoryItemDao.updateInventoryItem(entity)
        }
    }

    suspend fun deleteInventoryItem(item: InventoryItem) {
        val existing = inventoryItemDao.getInventoryItemById(item.id)
        if (existing != null) {
            val entity = item.toEntity().prepareForUpdate(existing, isDeleted = true)
            inventoryItemDao.updateInventoryItem(entity)
        }
    }

    suspend fun deleteInventoryItemById(itemId: Long) {
        getInventoryItemById(itemId)?.let { deleteInventoryItem(it) }
    }

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