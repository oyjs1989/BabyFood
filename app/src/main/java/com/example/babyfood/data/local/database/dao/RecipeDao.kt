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

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipesSync(): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getById(recipeId: Long): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE id IN (:recipeIds)")
    suspend fun getByIds(recipeIds: List<Long>): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE minAgeMonths <= :ageMonths AND maxAgeMonths >= :ageMonths")
    fun getRecipesByAge(ageMonths: Int): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE category = :category")
    fun getRecipesByCategory(category: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isBuiltIn = 1")
    fun getBuiltInRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isBuiltIn = 0")
    fun getUserRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE textureType = :textureType")
    fun getByTextureType(textureType: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE textureType = :textureType")
    suspend fun getByTextureTypeSync(textureType: String): List<RecipeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: RecipeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<RecipeEntity>): List<Long>

    @Update
    suspend fun update(recipe: RecipeEntity)

    @Update
    suspend fun updateAll(recipes: List<RecipeEntity>)

    @Delete
    suspend fun delete(recipe: RecipeEntity)

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: Long)
}