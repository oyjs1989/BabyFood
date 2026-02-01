package com.example.babyfood.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.babyfood.data.local.database.entity.NutritionDataEntity

@Dao
interface NutritionDataDao {
    @Query("SELECT * FROM nutrition_data WHERE ingredientName = :ingredientName LIMIT 1")
    suspend fun getByIngredientName(ingredientName: String): NutritionDataEntity?

    @Query("SELECT * FROM nutrition_data WHERE ironContent >= :minIronContent")
    suspend fun getByIronContent(minIronContent: Double): List<NutritionDataEntity>

    @Query("SELECT * FROM nutrition_data")
    suspend fun getAll(): List<NutritionDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: NutritionDataEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<NutritionDataEntity>)

    @Query("DELETE FROM nutrition_data")
    suspend fun deleteAll()
}