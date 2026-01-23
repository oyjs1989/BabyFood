package com.example.babyfood.data.init

import com.example.babyfood.data.local.database.dao.HealthRecordDao
import com.example.babyfood.data.local.database.dao.GrowthRecordDao
import com.example.babyfood.data.local.database.entity.GrowthRecordEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 数据迁移结果
 */
sealed class MigrationResult {
    data class Success(val count: Int) : MigrationResult()
    object AlreadyMigrated : MigrationResult()
    data class Error(val message: String, val cause: Throwable? = null) : MigrationResult()
}

/**
 * 数据迁移助手 - 将体检记录中的生长数据同步到生长记录表
 */
@Singleton
class DataMigration @Inject constructor(
    private val healthRecordDao: HealthRecordDao,
    private val growthRecordDao: GrowthRecordDao
) {

    /**
     * 执行数据迁移 - 将历史体检记录中的体重、身高数据同步到生长记录表
     * 仅在生长记录表为空时执行（避免重复迁移）
     */
    fun migrateHealthRecordsToGrowthRecords(onComplete: (MigrationResult) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingGrowthRecords = growthRecordDao.getAllGrowthRecordsSync()
                if (existingGrowthRecords.isNotEmpty()) {
                    onComplete(MigrationResult.AlreadyMigrated)
                    return@launch
                }

                val healthRecords = healthRecordDao.getAllHealthRecordsSync()
                
                var migratedCount = 0
                
                healthRecords.forEach { healthRecord ->
                    if (healthRecord.weight != null && healthRecord.height != null) {
                        val existingRecord = growthRecordDao.getGrowthRecordByDate(
                            babyId = healthRecord.babyId,
                            recordDate = healthRecord.recordDate
                        )
                        
                        if (existingRecord == null) {
                            val growthRecord = GrowthRecordEntity(
                                babyId = healthRecord.babyId,
                                recordDate = healthRecord.recordDate,
                                weight = healthRecord.weight,
                                height = healthRecord.height,
                                headCircumference = healthRecord.headCircumference,
                                notes = "从体检记录迁移"
                            )
                            growthRecordDao.insertGrowthRecord(growthRecord)
                            migratedCount++
                        }
                    }
                }
                
                onComplete(MigrationResult.Success(migratedCount))
            } catch (e: Exception) {
                onComplete(MigrationResult.Error("迁移失败: ${e.message}", e))
            }
        }
    }

    /**
     * 强制重新执行迁移（用于数据修复）
     */
    fun forceMigrate(onComplete: (MigrationResult) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val healthRecords = healthRecordDao.getAllHealthRecordsSync()
                
                var migratedCount = 0
                
                healthRecords.forEach { healthRecord ->
                    if (healthRecord.weight != null && healthRecord.height != null) {
                        val growthRecord = GrowthRecordEntity(
                            babyId = healthRecord.babyId,
                            recordDate = healthRecord.recordDate,
                            weight = healthRecord.weight,
                            height = healthRecord.height,
                            headCircumference = healthRecord.headCircumference,
                            notes = "从体检记录强制迁移"
                        )
                        
                        growthRecordDao.deleteGrowthRecordByDate(
                            babyId = healthRecord.babyId,
                            recordDate = healthRecord.recordDate
                        )
                        
                        growthRecordDao.insertGrowthRecord(growthRecord)
                        migratedCount++
                    }
                }
                
                onComplete(MigrationResult.Success(migratedCount))
            } catch (e: Exception) {
                onComplete(MigrationResult.Error("强制迁移失败: ${e.message}", e))
            }
        }
    }
}
