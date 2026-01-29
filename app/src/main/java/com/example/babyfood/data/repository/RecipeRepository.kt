package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.dao.RecipeDao
import com.example.babyfood.data.local.database.entity.RecipeEntity
import com.example.babyfood.domain.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao
) {
    fun getAllRecipes(): Flow<List<Recipe>> =
        recipeDao.getAllRecipes().map { entities -> entities.map { entity -> entity.toDomainModel() } }

    suspend fun getRecipeById(recipeId: Long): Recipe? =
        recipeDao.getRecipeById(recipeId)?.toDomainModel()

    fun getRecipesByAge(ageMonths: Int): Flow<List<Recipe>> =
        recipeDao.getRecipesByAge(ageMonths).map { entities -> entities.map { entity -> entity.toDomainModel() } }

    fun getRecipesByCategory(category: String): Flow<List<Recipe>> =
        recipeDao.getRecipesByCategory(category).map { entities -> entities.map { entity -> entity.toDomainModel() } }

    fun getBuiltInRecipes(): Flow<List<Recipe>> =
        recipeDao.getBuiltInRecipes().map { entities -> entities.map { entity -> entity.toDomainModel() } }

    fun getUserRecipes(): Flow<List<Recipe>> =
        recipeDao.getUserRecipes().map { entities -> entities.map { entity -> entity.toDomainModel() } }

    suspend fun insertRecipe(recipe: Recipe): Long {
        val entity = recipe.toEntity().prepareForInsert()
        return recipeDao.insertRecipe(entity)
    }

    suspend fun insertRecipes(recipes: List<Recipe>) {
        val entities = recipes.map { it.toEntity().prepareForInsert() }
        recipeDao.insertRecipes(entities)
    }

    suspend fun updateRecipe(recipe: Recipe) {
        val existing = recipeDao.getRecipeById(recipe.id)
        if (existing != null) {
            val entity = recipe.toEntity().prepareForUpdate(existing)
            recipeDao.updateRecipe(entity)
        }
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        val existing = recipeDao.getRecipeById(recipe.id)
        if (existing != null) {
            // 软删除
            val entity = recipe.toEntity().prepareForUpdate(existing, isDeleted = true)
            recipeDao.updateRecipe(entity)
        }
    }

    suspend fun deleteRecipeById(recipeId: Long) {
        val recipe = getRecipeById(recipeId)
        if (recipe != null && !recipe.isBuiltIn) {
            deleteRecipe(recipe)
        }
    }

    private fun RecipeEntity.toDomainModel(): Recipe = Recipe(
        id = id,
        name = name,
        minAgeMonths = minAgeMonths,
        maxAgeMonths = maxAgeMonths,
        ingredients = ingredients,
        steps = steps,
        nutrition = nutrition,
        category = category,
        isBuiltIn = isBuiltIn,
        imageUrl = imageUrl
    )

    private fun Recipe.toEntity(): RecipeEntity = RecipeEntity(
        id = id,
        name = name,
        minAgeMonths = minAgeMonths,
        maxAgeMonths = maxAgeMonths,
        ingredients = ingredients,
        steps = steps,
        nutrition = nutrition,
        category = category,
        isBuiltIn = isBuiltIn,
        imageUrl = imageUrl,
        cloudId = null,
        syncStatus = "LOCAL_ONLY",
        lastSyncTime = null,
        version = 1,
        isDeleted = false
    )
}