package com.example.babyfood.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.babyfood.domain.model.HealthRecord
import kotlinx.datetime.LocalDate

@Entity(tableName = "health_records")
data class HealthRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val recordDate: LocalDate,
    val weight: Float?,
    val height: Float?,
    val headCircumference: Float?,
    val ironLevel: Float?,
    val calciumLevel: Float?,
    val hemoglobin: Float?,
    val aiAnalysis: String?,
    val isConfirmed: Boolean = false,
    val expiryDate: LocalDate?,
    val notes: String?
) {
    fun toDomainModel(): HealthRecord = HealthRecord(
        id = id,
        babyId = babyId,
        recordDate = recordDate,
        weight = weight,
        height = height,
        headCircumference = headCircumference,
        ironLevel = ironLevel,
        calciumLevel = calciumLevel,
        hemoglobin = hemoglobin,
        aiAnalysis = aiAnalysis,
        isConfirmed = isConfirmed,
        expiryDate = expiryDate,
        notes = notes
    )
}

fun HealthRecord.toEntity(): HealthRecordEntity = HealthRecordEntity(
    id = id,
    babyId = babyId,
    recordDate = recordDate,
    weight = weight,
    height = height,
    headCircumference = headCircumference,
    ironLevel = ironLevel,
    calciumLevel = calciumLevel,
    hemoglobin = hemoglobin,
    aiAnalysis = aiAnalysis,
    isConfirmed = isConfirmed,
    expiryDate = expiryDate,
    notes = notes
)