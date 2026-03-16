package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.dao.RecipeDao
import com.example.babyfood.data.local.database.entity.RecipeEntity
import com.example.babyfood.domain.model.Recipe
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao
) : SyncableRepository<Recipe, RecipeEntity, Long>() {

    // Note: RecipeDao implements SyncableDao methods implicitly
    // but doesn't extend the interface due to Room limitations

    // Convert Entity to Domain model
    override fun RecipeEntity.toDomainModel(): Recipe = Recipe(
        id = id,
        cloudId = cloudId,
        name = name,
        minAgeMonths = minAgeMonths,
        maxAgeMonths = maxAgeMonths,
        ingredients = ingredients,
        steps = steps,
        nutrition = nutrition,
        category = category,
        cookingTime = cookingTime,
        isBuiltIn = isBuiltIn,
        imageUrl = imageUrl,
        textureType = textureType,
        isIronRich = isIronRich,
        ironContent = ironContent,
        riskLevelList = riskLevelList,
        safetyAdvice = safetyAdvice
    )

    // Convert Domain model to Entity
    override fun Recipe.toEntity(): RecipeEntity = RecipeEntity(
        id = id,
        cloudId = cloudId,
        name = name,
        minAgeMonths = minAgeMonths,
        maxAgeMonths = maxAgeMonths,
        ingredients = ingredients,
        steps = steps,
        nutrition = nutrition,
        category = category,
        cookingTime = cookingTime,
        isBuiltIn = isBuiltIn,
        imageUrl = imageUrl,
        textureType = textureType,
        isIronRich = isIronRich,
        ironContent = ironContent,
        riskLevelList = riskLevelList,
        safetyAdvice = safetyAdvice
    )

    override fun getItemId(item: Recipe): Long = item.id

    // ============ CRUD Operations ============

    suspend fun getById(id: Long): Recipe? =
        recipeDao.getById(id)?.toDomainModel()

    /**
     * 根据云端 ID 查找食谱
     * 用于将 API 返回的 cloudId 映射到本地食谱
     */
    suspend fun findByCloudId(cloudId: String): Recipe? =
        recipeDao.findByCloudId(cloudId)?.toDomainModel()

    suspend fun insert(item: Recipe): Long =
        recipeDao.insert(item.toEntity().prepareForInsert())

    suspend fun update(item: Recipe) {
        val existing = recipeDao.getById(item.id)
        if (existing != null) {
            recipeDao.update(item.toEntity().prepareForUpdate(existing))
        }
    }

    suspend fun delete(item: Recipe) {
        recipeDao.delete(item.toEntity())
    }

    // ============ Domain-Specific Query Methods ============

    fun getAllRecipes(): Flow<List<Recipe>> =
        recipeDao.getAllRecipes().toDomainModels()

    fun getRecipesByAge(ageMonths: Int): Flow<List<Recipe>> =
        recipeDao.getRecipesByAge(ageMonths).toDomainModels()

    fun getRecipesByCategory(category: String): Flow<List<Recipe>> =
        recipeDao.getRecipesByCategory(category).toDomainModels()

    fun getBuiltInRecipes(): Flow<List<Recipe>> =
        recipeDao.getBuiltInRecipes().toDomainModels()

    fun getUserRecipes(): Flow<List<Recipe>> =
        recipeDao.getUserRecipes().toDomainModels()

    fun getByTextureType(textureType: com.example.babyfood.domain.model.TextureType): Flow<List<Recipe>> =
        recipeDao.getByTextureType(textureType.name).toDomainModels()

    suspend fun getByTextureTypeSync(textureType: com.example.babyfood.domain.model.TextureType): List<Recipe> =
        recipeDao.getByTextureTypeSync(textureType.name).map { it.toDomainModel() }

    suspend fun deleteRecipeById(recipeId: Long) {
        val recipe = getById(recipeId)
        if (recipe != null && !recipe.isBuiltIn) {
            delete(recipe)
        }
    }
}