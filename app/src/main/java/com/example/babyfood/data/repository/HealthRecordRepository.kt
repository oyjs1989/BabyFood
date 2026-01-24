package com.example.babyfood.data.repository

import com.example.babyfood.data.ai.HealthAnalysisService
import com.example.babyfood.data.ai.LocalHealthAnalysisStrategy
import com.example.babyfood.data.ai.RemoteHealthAnalysisStrategy
import com.example.babyfood.data.local.database.dao.HealthRecordDao
import com.example.babyfood.data.local.database.entity.HealthRecordEntity
import com.example.babyfood.data.strategy.StrategyManager
import com.example.babyfood.data.strategy.StrategyResult
import com.example.babyfood.data.strategy.createStrategyExecutor
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
    private val growthRecordRepository: GrowthRecordRepository,
    private val babyRepository: BabyRepository,
    private val localHealthAnalysisStrategy: LocalHealthAnalysisStrategy,
    private val remoteHealthAnalysisStrategy: RemoteHealthAnalysisStrategy,
    private val strategyManager: StrategyManager
) {
    fun getHealthRecordsByBaby(babyId: Long): Flow<List<HealthRecord>> =
        healthRecordDao.getHealthRecordsByBaby(babyId).map { entities -> entities.map { it.toDomainModel() } }

    suspend fun getHealthRecordById(recordId: Long): HealthRecord? =
        healthRecordDao.getHealthRecordById(recordId)?.toDomainModel()

    suspend fun getLatestHealthRecord(babyId: Long): HealthRecord? =
        healthRecordDao.getLatestHealthRecord(babyId)?.toDomainModel()

    /**
     * 插入体检记录（集成 AI 分析）
     * 自动调用 AI 分析服务生成分析结论
     */
    suspend fun insertHealthRecord(record: HealthRecord): Long {
        // 执行 AI 分析（使用策略管理器决定本地还是远程）
        val aiAnalysis = performHealthAnalysis(record)

        // 创建带 AI 分析结论的体检记录
        val recordWithAnalysis = record.copy(aiAnalysis = aiAnalysis)

        val healthRecordId = healthRecordDao.insertHealthRecord(
            com.example.babyfood.data.local.database.entity.HealthRecordEntity(
                id = recordWithAnalysis.id,
                babyId = recordWithAnalysis.babyId,
                recordDate = recordWithAnalysis.recordDate,
                weight = recordWithAnalysis.weight,
                height = recordWithAnalysis.height,
                headCircumference = recordWithAnalysis.headCircumference,
                ironLevel = recordWithAnalysis.ironLevel,
                calciumLevel = recordWithAnalysis.calciumLevel,
                hemoglobin = recordWithAnalysis.hemoglobin,
                aiAnalysis = recordWithAnalysis.aiAnalysis,
                isConfirmed = recordWithAnalysis.isConfirmed,
                expiryDate = recordWithAnalysis.expiryDate,
                notes = recordWithAnalysis.notes
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

    /**
     * 执行健康分析
     * 使用策略管理器决定使用本地还是远程策略
     */
    private suspend fun performHealthAnalysis(record: HealthRecord): String? {
        // 获取 Baby 数据
        val baby = babyRepository.getBabyById(record.babyId)
        if (baby == null) {
            // 如果找不到 Baby 数据，返回 null
            return null
        }

        val strategyType = strategyManager.getCurrentAiStrategy()
        
        val executor = createStrategyExecutor(
            strategyType = strategyType,
            localStrategy = { localHealthAnalysisStrategy.analyze(record, baby) },
            remoteStrategy = { remoteHealthAnalysisStrategy.analyze(record, baby) }
        )
        
        return when (val result = executor.execute()) {
            is StrategyResult.Success -> result.data
            is StrategyResult.Fallback -> {
                // 远程失败，使用本地结果
                result.data
            }
            is StrategyResult.Failure -> {
                // 本地和远程都失败，返回 null
                null
            }
        }
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