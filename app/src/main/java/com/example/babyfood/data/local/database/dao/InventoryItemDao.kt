package com.example.babyfood.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.babyfood.data.local.database.entity.InventoryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryItemDao {
    @Query("SELECT * FROM inventory_items WHERE isDeleted = 0 ORDER BY expiryDate ASC")
    fun getAllInventoryItems(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items")
    suspend fun getAllInventoryItemsSync(): List<InventoryItemEntity>

    @Query("SELECT * FROM inventory_items WHERE id = :itemId AND isDeleted = 0")
    suspend fun getById(itemId: Long): InventoryItemEntity?

    @Query("SELECT * FROM inventory_items WHERE foodId = :foodId AND isDeleted = 0")
    fun getInventoryItemsByFoodId(foodId: Long): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE expiryDate < date('now') AND isDeleted = 0 ORDER BY expiryDate ASC")
    fun getExpiredItems(): Flow<List<InventoryItemEntity>>

    @Query("""
        SELECT * FROM inventory_items
        WHERE expiryDate >= date('now')
        AND expiryDate <= date('now', '+3 days')
        AND isDeleted = 0
        ORDER BY expiryDate ASC
    """)
    fun getExpiringItems(): Flow<List<InventoryItemEntity>>

    @Query("""
        SELECT * FROM inventory_items
        WHERE expiryDate >= date('now')
        AND expiryDate <= date('now', '+3 days')
        AND isDeleted = 0
        ORDER BY expiryDate ASC
    """)
    suspend fun getExpiringItemsSync(): List<InventoryItemEntity>

    @Query("""
        SELECT * FROM inventory_items
        WHERE expiryDate >= date('now')
        AND expiryDate <= date('now', '+7 days')
        AND isDeleted = 0
        ORDER BY expiryDate ASC
    """)
    fun getExpiringItemsWithin7Days(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE storageMethod = :storageMethod AND isDeleted = 0 ORDER BY expiryDate ASC")
    fun getInventoryItemsByStorageMethod(storageMethod: String): Flow<List<InventoryItemEntity>>

    @Query("""
        SELECT * FROM inventory_items
        WHERE foodName LIKE '%' || :query || '%'
        AND isDeleted = 0
        ORDER BY expiryDate ASC
    """)
    fun searchInventoryItems(query: String): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE cloudId = :cloudId")
    suspend fun getInventoryItemByCloudId(cloudId: String): InventoryItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InventoryItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InventoryItemEntity>): List<Long>

    @Update
    suspend fun update(item: InventoryItemEntity)

    @Update
    suspend fun updateAll(items: List<InventoryItemEntity>)

    @Delete
    suspend fun delete(item: InventoryItemEntity)

    @Query("DELETE FROM inventory_items WHERE id = :itemId")
    suspend fun deleteInventoryItemById(itemId: Long)

    @Query("UPDATE inventory_items SET isDeleted = 1, version = version + 1 WHERE id = :itemId")
    suspend fun softDeleteInventoryItem(itemId: Long)
}