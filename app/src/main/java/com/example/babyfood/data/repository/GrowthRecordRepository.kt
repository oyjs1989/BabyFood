package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.dao.GrowthRecordDao
import com.example.babyfood.data.local.database.entity.GrowthRecordEntity
import com.example.babyfood.domain.model.GrowthRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrowthRecordRepository @Inject constructor(
    private val growthRecordDao: GrowthRecordDao
) {
    fun getGrowthRecordsByBaby(babyId: Long): Flow<List<GrowthRecord>> =
        growthRecordDao.getGrowthRecordsByBaby(babyId).map { entities -> entities.map { it.toDomainModel() } }

    suspend fun getGrowthRecordById(recordId: Long): GrowthRecord? =
        growthRecordDao.getGrowthRecordById(recordId)?.toDomainModel()

    suspend fun insertGrowthRecord(record: GrowthRecord): Long =
        growthRecordDao.insertGrowthRecord(
            com.example.babyfood.data.local.database.entity.GrowthRecordEntity(
                id = record.id,
                babyId = record.babyId,
                recordDate = record.recordDate,
                weight = record.weight,
                height = record.height,
                headCircumference = record.headCircumference,
                notes = record.notes
            )
        )

    suspend fun updateGrowthRecord(record: GrowthRecord) =
        growthRecordDao.updateGrowthRecord(
            com.example.babyfood.data.local.database.entity.GrowthRecordEntity(
                id = record.id,
                babyId = record.babyId,
                recordDate = record.recordDate,
                weight = record.weight,
                height = record.height,
                headCircumference = record.headCircumference,
                notes = record.notes
            )
        )

    suspend fun deleteGrowthRecord(record: GrowthRecord) =
        growthRecordDao.deleteGrowthRecord(
            com.example.babyfood.data.local.database.entity.GrowthRecordEntity(
                id = record.id,
                babyId = record.babyId,
                recordDate = record.recordDate,
                weight = record.weight,
                height = record.height,
                headCircumference = record.headCircumference,
                notes = record.notes
            )
        )

    suspend fun deleteGrowthRecordById(recordId: Long) =
        growthRecordDao.deleteGrowthRecordById(recordId)

    suspend fun deleteGrowthRecordsByBaby(babyId: Long) =
        growthRecordDao.deleteGrowthRecordsByBaby(babyId)
}