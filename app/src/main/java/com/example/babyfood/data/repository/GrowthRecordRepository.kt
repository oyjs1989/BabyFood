package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.dao.GrowthRecordDao
import com.example.babyfood.data.local.database.entity.GrowthRecordEntity
import com.example.babyfood.domain.model.GrowthRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrowthRecordRepository @Inject constructor(
    private val growthRecordDao: GrowthRecordDao
) : BaseRepository<GrowthRecord, GrowthRecordEntity, Long>() {

    // Note: GrowthRecordDao implements BaseDao methods implicitly
    // but doesn't extend the interface due to Room limitations

    override fun GrowthRecordEntity.toDomainModel(): GrowthRecord = GrowthRecord(
        id = id,
        babyId = babyId,
        recordDate = recordDate,
        weight = weight,
        height = height,
        headCircumference = headCircumference
    )

    override fun GrowthRecord.toEntity(): GrowthRecordEntity = GrowthRecordEntity(
        id = id,
        babyId = babyId,
        recordDate = recordDate,
        weight = weight,
        height = height,
        headCircumference = headCircumference
    )

    // ============ Domain-Specific Query Methods ============

    fun getGrowthRecordsByBaby(babyId: Long): Flow<List<GrowthRecord>> =
        growthRecordDao.getGrowthRecordsByBaby(babyId).toDomainModels()

    suspend fun deleteGrowthRecordsByBaby(babyId: Long) =
        growthRecordDao.deleteGrowthRecordsByBaby(babyId)

    // ========== CRUD Operations ==========

    suspend fun insertGrowthRecord(record: GrowthRecord): Long =
        growthRecordDao.insert(record.toEntity())

    suspend fun updateGrowthRecord(record: GrowthRecord) =
        growthRecordDao.update(record.toEntity())

    suspend fun deleteGrowthRecord(record: GrowthRecord) =
        growthRecordDao.delete(record.toEntity())

    suspend fun deleteGrowthRecordById(recordId: Long) =
        growthRecordDao.deleteGrowthRecordById(recordId)
}