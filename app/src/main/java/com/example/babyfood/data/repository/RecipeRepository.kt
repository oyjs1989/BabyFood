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

    suspend fun insertRecipe(recipe: Recipe): Long =
        recipeDao.insertRecipe(recipe.toEntity())

    suspend fun insertRecipes(recipes: List<Recipe>) =
        recipeDao.insertRecipes(recipes.map { recipe -> recipe.toEntity() })

    suspend fun updateRecipe(recipe: Recipe) =
        recipeDao.updateRecipe(recipe.toEntity())

    suspend fun deleteRecipe(recipe: Recipe) =
        recipeDao.deleteRecipe(recipe.toEntity())

    suspend fun deleteRecipeById(recipeId: Long) =
        recipeDao.deleteRecipeById(recipeId)

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
        imageUrl = imageUrl
    )
}