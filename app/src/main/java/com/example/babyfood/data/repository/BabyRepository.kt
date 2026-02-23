package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.dao.BabyDao
import com.example.babyfood.data.local.database.entity.BabyEntity
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.NutritionGoal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BabyRepository @Inject constructor(
    private val babyDao: BabyDao
) : SyncableRepository<Baby, BabyEntity, Long>() {

    // Note: BabyDao implements SyncableDao methods implicitly
    // but doesn't extend the interface due to Room limitations

    override fun BabyEntity.toDomainModel(): Baby = Baby(
        id = id,
        name = name,
        birthDate = birthDate,
        allergies = allergies,
        weight = weight,
        height = height,
        preferences = preferences,
        nutritionGoal = nutritionGoal
    )

    override fun Baby.toEntity(): BabyEntity = BabyEntity(
        id = id,
        name = name,
        birthDate = birthDate,
        allergies = allergies,
        weight = weight,
        height = height,
        preferences = preferences,
        nutritionGoal = nutritionGoal
    )

    override fun getItemId(item: Baby): Long = item.id

    // ============ CRUD Operations ============

    suspend fun getById(id: Long): Baby? =
        babyDao.getById(id)?.toDomainModel()

    suspend fun insert(item: Baby): Long =
        babyDao.insert(item.toEntity().prepareForInsert())

    suspend fun update(item: Baby) {
        val existing = babyDao.getById(item.id)
        if (existing != null) {
            babyDao.update(item.toEntity().prepareForUpdate(existing))
        }
    }

    suspend fun delete(item: Baby) {
        babyDao.delete(item.toEntity())
    }

    // ============ Domain-Specific Query Methods ============

    fun getAllBabies(): Flow<List<Baby>> =
        babyDao.getAllBabies().toDomainModels()

    // ============ Custom Business Logic Methods ============

    suspend fun updateNutritionGoal(babyId: Long, nutritionGoal: NutritionGoal) {
        val baby = getById(babyId)
        if (baby != null) {
            update(baby.copy(nutritionGoal = nutritionGoal))
        }
    }

    /**
     * 添加过敏食材到宝宝档案
     */
    suspend fun addAllergies(babyId: Long, newAllergies: List<com.example.babyfood.domain.model.AllergyItem>) {
        val baby = getById(babyId)
        if (baby != null) {
            // 合并现有的过敏列表和新的过敏列表
            val existingIngredientNames = baby.allergies.map { it.ingredient }.toSet()
            val uniqueNewAllergies = newAllergies.filterNot { it.ingredient in existingIngredientNames }

            if (uniqueNewAllergies.isNotEmpty()) {
                val updatedAllergies = baby.allergies + uniqueNewAllergies
                update(baby.copy(allergies = updatedAllergies))
            }
        }
    }

    /**
     * 添加偏好食材到宝宝档案
     */
    suspend fun addPreferences(babyId: Long, newPreferences: List<com.example.babyfood.domain.model.PreferenceItem>) {
        val baby = getById(babyId)
        if (baby != null) {
            // 合并现有的偏好列表和新的偏好列表
            val existingIngredientNames = baby.preferences.map { it.ingredient }.toSet()
            val uniqueNewPreferences = newPreferences.filterNot { it.ingredient in existingIngredientNames }

            if (uniqueNewPreferences.isNotEmpty()) {
                val updatedPreferences = baby.preferences + uniqueNewPreferences
                update(baby.copy(preferences = updatedPreferences))
            }
        }
    }
}