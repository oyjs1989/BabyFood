package com.example.babyfood.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.babyfood.data.local.database.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: Long): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE minAgeMonths <= :ageMonths AND maxAgeMonths >= :ageMonths")
    fun getRecipesByAge(ageMonths: Int): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE category = :category")
    fun getRecipesByCategory(category: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isBuiltIn = 1")
    fun getBuiltInRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isBuiltIn = 0")
    fun getUserRecipes(): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: Long)
}