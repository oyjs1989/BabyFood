package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.dao.BabyDao
import com.example.babyfood.data.local.database.entity.BabyEntity
import com.example.babyfood.domain.model.Baby
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    suspend fun insertBaby(baby: Baby): Long =
        babyDao.insertBaby(baby.toEntity())

    suspend fun updateBaby(baby: Baby) =
        babyDao.updateBaby(baby.toEntity())

    suspend fun deleteBaby(baby: Baby) =
        babyDao.deleteBaby(baby.toEntity())

    suspend fun deleteBabyById(babyId: Long) =
        babyDao.deleteBabyById(babyId)

    private fun BabyEntity.toDomainModel(): Baby = Baby(
        id = id,
        name = name,
        birthDate = birthDate,
        allergies = allergies,
        weight = weight,
        height = height,
        preferences = preferences
    )

    private fun Baby.toEntity(): BabyEntity = BabyEntity(
        id = id,
        name = name,
        birthDate = birthDate,
        allergies = allergies,
        weight = weight,
        height = height,
        preferences = preferences
    )
}