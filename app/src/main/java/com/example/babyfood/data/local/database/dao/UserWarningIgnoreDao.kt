package com.example.babyfood.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.babyfood.data.local.database.entity.UserWarningIgnoreEntity

@Dao
interface UserWarningIgnoreDao {
    @Query("SELECT * FROM user_warning_ignores WHERE userId = :userId AND ingredientName = :ingredientName ORDER BY ignoreDate DESC LIMIT 1")
    suspend fun getByUserAndIngredient(userId: Long, ingredientName: String): UserWarningIgnoreEntity?

    @Query("SELECT * FROM user_warning_ignores WHERE userId = :userId AND warningType = :warningType AND ingredientName = :ingredientName ORDER BY ignoreDate DESC LIMIT 1")
    suspend fun getByUserAndWarningTypeAndIngredient(userId: Long, warningType: String, ingredientName: String): UserWarningIgnoreEntity?

    @Query("SELECT COUNT(*) FROM user_warning_ignores WHERE userId = :userId AND ingredientName = :ingredientName")
    suspend fun countByUserAndIngredient(userId: Long, ingredientName: String): Int

    @Query("SELECT COALESCE(ignoreCount, 0) FROM user_warning_ignores WHERE userId = :userId AND warningType = :warningType AND ingredientName = :ingredientName")
    suspend fun getIgnoreCount(userId: Long, warningType: String, ingredientName: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ignore: UserWarningIgnoreEntity): Long

    @Query("DELETE FROM user_warning_ignores WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM user_warning_ignores WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long)
}