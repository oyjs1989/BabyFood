package com.example.babyfood.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.babyfood.data.local.database.entity.NutritionGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionGoalDao {
    @Query("SELECT * FROM nutrition_goals WHERE babyId = :babyId LIMIT 1")
    suspend fun getByBabyId(babyId: Long): NutritionGoalEntity?

    @Query("SELECT * FROM nutrition_goals WHERE babyId = :babyId LIMIT 1")
    fun getByBabyIdFlow(babyId: Long): Flow<NutritionGoalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: NutritionGoalEntity): Long

    @Query("UPDATE nutrition_goals SET calories = :calories, protein = :protein, calcium = :calcium, iron = :iron, vitaminA = :vitaminA, vitaminC = :vitaminC WHERE babyId = :babyId")
    suspend fun update(
        babyId: Long,
        calories: Double,
        protein: Double,
        calcium: Double,
        iron: Double,
        vitaminA: Double,
        vitaminC: Double
    )

    @Query("DELETE FROM nutrition_goals WHERE babyId = :babyId")
    suspend fun deleteByBabyId(babyId: Long)
}