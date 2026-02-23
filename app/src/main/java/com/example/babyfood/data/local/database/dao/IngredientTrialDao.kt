package com.example.babyfood.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.babyfood.data.local.database.entity.IngredientTrialEntity

@Dao
interface IngredientTrialDao {
    @Query("SELECT * FROM ingredient_trials WHERE babyId = :babyId ORDER BY trialDate DESC")
    suspend fun getByBabyId(babyId: Long): List<IngredientTrialEntity>

    @Query("SELECT DISTINCT ingredientName FROM ingredient_trials WHERE babyId = :babyId AND isAllergic = 0")
    suspend fun getTriedIngredients(babyId: Long): List<String>

    @Query("SELECT COUNT(*) FROM ingredient_trials WHERE babyId = :babyId")
    suspend fun countByBabyId(babyId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trial: IngredientTrialEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trials: List<IngredientTrialEntity>)

    @Query("DELETE FROM ingredient_trials WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM ingredient_trials WHERE babyId = :babyId")
    suspend fun deleteByBabyId(babyId: Long)
}