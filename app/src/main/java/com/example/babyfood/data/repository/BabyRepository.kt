package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.dao.BabyDao
import com.example.babyfood.data.local.database.entity.BabyEntity
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.NutritionGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BabyRepository @Inject constructor(
    private val babyDao: BabyDao
) {
    fun getAllBabies(): Flow<List<Baby>> =
        babyDao.getAllBabies().map { entities -> entities.map { entity -> entity.toDomainModel() } }

    suspend fun getBabyById(babyId: Long): Baby? =
        babyDao.getBabyById(babyId)?.toDomainModel()

    suspend fun insertBaby(baby: Baby): Long {
        val entity = baby.toEntity().prepareForInsert()
        return babyDao.insertBaby(entity)
    }

    suspend fun updateBaby(baby: Baby) {
        val existing = babyDao.getBabyById(baby.id)
        if (existing != null) {
            val entity = baby.toEntity().prepareForUpdate(existing)
            babyDao.updateBaby(entity)
        }
    }

    suspend fun deleteBaby(baby: Baby) {
        val existing = babyDao.getBabyById(baby.id)
        if (existing != null) {
            // 软删除
            val entity = baby.toEntity().prepareForUpdate(existing, isDeleted = true)
            babyDao.updateBaby(entity)
        }
    }

    suspend fun deleteBabyById(babyId: Long) {
        val baby = getBabyById(babyId)
        if (baby != null) {
            deleteBaby(baby)
        }
    }

    suspend fun updateNutritionGoal(babyId: Long, nutritionGoal: NutritionGoal) {
        val baby = getBabyById(babyId)
        if (baby != null) {
            updateBaby(baby.copy(nutritionGoal = nutritionGoal))
        }
    }

    /**
     * 添加过敏食材到宝宝档案
     */
    suspend fun addAllergies(babyId: Long, newAllergies: List<com.example.babyfood.domain.model.AllergyItem>) {
        val baby = getBabyById(babyId)
        if (baby != null) {
            // 合并现有的过敏列表和新的过敏列表
            val existingIngredientNames = baby.allergies.map { it.ingredient }.toSet()
            val uniqueNewAllergies = newAllergies.filterNot { it.ingredient in existingIngredientNames }

            if (uniqueNewAllergies.isNotEmpty()) {
                val updatedAllergies = baby.allergies + uniqueNewAllergies
                updateBaby(baby.copy(allergies = updatedAllergies))
            }
        }
    }

    /**
     * 添加偏好食材到宝宝档案
     */
    suspend fun addPreferences(babyId: Long, newPreferences: List<com.example.babyfood.domain.model.PreferenceItem>) {
        val baby = getBabyById(babyId)
        if (baby != null) {
            // 合并现有的偏好列表和新的偏好列表
            val existingIngredientNames = baby.preferences.map { it.ingredient }.toSet()
            val uniqueNewPreferences = newPreferences.filterNot { it.ingredient in existingIngredientNames }

            if (uniqueNewPreferences.isNotEmpty()) {
                val updatedPreferences = baby.preferences + uniqueNewPreferences
                updateBaby(baby.copy(preferences = updatedPreferences))
            }
        }
    }

    private fun BabyEntity.toDomainModel(): Baby = Baby(
        id = id,
        name = name,
        birthDate = birthDate,
        allergies = allergies,
        weight = weight,
        height = height,
        preferences = preferences,
        nutritionGoal = nutritionGoal
    )

    private fun Baby.toEntity(): BabyEntity = BabyEntity(
        id = id,
        name = name,
        birthDate = birthDate,
        allergies = allergies,
        weight = weight,
        height = height,
        preferences = preferences,
        nutritionGoal = nutritionGoal,
        cloudId = null,
        syncStatus = "LOCAL_ONLY",
        lastSyncTime = null,
        version = 1,
        isDeleted = false
    )
}