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
        val entity = baby.toEntity().copy(
            syncStatus = "PENDING_UPLOAD",
            lastSyncTime = null,
            version = 1
        )
        return babyDao.insertBaby(entity)
    }

    suspend fun updateBaby(baby: Baby) {
        val existing = babyDao.getBabyById(baby.id)
        if (existing != null) {
            val entity = baby.toEntity().copy(
                cloudId = existing.cloudId,
                syncStatus = "PENDING_UPLOAD",
                lastSyncTime = existing.lastSyncTime,
                version = existing.version + 1
            )
            babyDao.updateBaby(entity)
        }
    }

    suspend fun deleteBaby(baby: Baby) {
        val existing = babyDao.getBabyById(baby.id)
        if (existing != null) {
            // 软删除
            val entity = baby.toEntity().copy(
                cloudId = existing.cloudId,
                syncStatus = "PENDING_UPLOAD",
                lastSyncTime = existing.lastSyncTime,
                version = existing.version + 1,
                isDeleted = true
            )
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