package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.dao.HealthRecordDao
import com.example.babyfood.data.local.database.entity.HealthRecordEntity
import com.example.babyfood.domain.model.GrowthRecord
import com.example.babyfood.domain.model.HealthRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRecordRepository @Inject constructor(
    private val healthRecordDao: HealthRecordDao,
    private val growthRecordRepository: GrowthRecordRepository
) {
    fun getHealthRecordsByBaby(babyId: Long): Flow<List<HealthRecord>> =
        healthRecordDao.getHealthRecordsByBaby(babyId).map { entities -> entities.map { it.toDomainModel() } }

    suspend fun getHealthRecordById(recordId: Long): HealthRecord? =
        healthRecordDao.getHealthRecordById(recordId)?.toDomainModel()

    suspend fun getLatestHealthRecord(babyId: Long): HealthRecord? =
        healthRecordDao.getLatestHealthRecord(babyId)?.toDomainModel()

    suspend fun insertHealthRecord(record: HealthRecord): Long {
        val healthRecordId = healthRecordDao.insertHealthRecord(
            com.example.babyfood.data.local.database.entity.HealthRecordEntity(
                id = record.id,
                babyId = record.babyId,
                recordDate = record.recordDate,
                weight = record.weight,
                height = record.height,
                headCircumference = record.headCircumference,
                ironLevel = record.ironLevel,
                calciumLevel = record.calciumLevel,
                hemoglobin = record.hemoglobin,
                aiAnalysis = record.aiAnalysis,
                isConfirmed = record.isConfirmed,
                expiryDate = record.expiryDate,
                notes = record.notes
            )
        )
        
        // 自动同步到生长记录表（如果体重和身高不为空）
        if (record.weight != null && record.height != null) {
            val growthRecord = GrowthRecord(
                babyId = record.babyId,
                recordDate = record.recordDate,
                weight = record.weight,
                height = record.height,
                headCircumference = record.headCircumference,
                notes = record.notes ?: "从体检记录同步"
            )
            growthRecordRepository.insertGrowthRecord(growthRecord)
        }
        
        return healthRecordId
    }

    suspend fun updateHealthRecord(record: HealthRecord) =
        healthRecordDao.updateHealthRecord(
            com.example.babyfood.data.local.database.entity.HealthRecordEntity(
                id = record.id,
                babyId = record.babyId,
                recordDate = record.recordDate,
                weight = record.weight,
                height = record.height,
                headCircumference = record.headCircumference,
                ironLevel = record.ironLevel,
                calciumLevel = record.calciumLevel,
                hemoglobin = record.hemoglobin,
                aiAnalysis = record.aiAnalysis,
                isConfirmed = record.isConfirmed,
                expiryDate = record.expiryDate,
                notes = record.notes
            )
        )

    suspend fun deleteHealthRecord(record: HealthRecord) =
        healthRecordDao.deleteHealthRecord(
            com.example.babyfood.data.local.database.entity.HealthRecordEntity(
                id = record.id,
                babyId = record.babyId,
                recordDate = record.recordDate,
                weight = record.weight,
                height = record.height,
                headCircumference = record.headCircumference,
                ironLevel = record.ironLevel,
                calciumLevel = record.calciumLevel,
                hemoglobin = record.hemoglobin,
                aiAnalysis = record.aiAnalysis,
                isConfirmed = record.isConfirmed,
                expiryDate = record.expiryDate,
                notes = record.notes
            )
        )

    suspend fun deleteHealthRecordById(recordId: Long) =
        healthRecordDao.deleteHealthRecordById(recordId)

    suspend fun deleteHealthRecordsByBaby(babyId: Long) =
        healthRecordDao.deleteHealthRecordsByBaby(babyId)
}