package com.example.babyfood.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.babyfood.domain.model.GrowthRecord
import kotlinx.datetime.LocalDate

@Entity(tableName = "growth_records")
data class GrowthRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val recordDate: LocalDate,
    val weight: Float,
    val height: Float,
    val headCircumference: Float? = null,
    val notes: String? = null
) {
    fun toDomainModel(): GrowthRecord = GrowthRecord(
        id = id,
        babyId = babyId,
        recordDate = recordDate,
        weight = weight,
        height = height,
        headCircumference = headCircumference,
        notes = notes
    )
}

fun GrowthRecord.toEntity(): GrowthRecordEntity = GrowthRecordEntity(
    id = id,
    babyId = babyId,
    recordDate = recordDate,
    weight = weight,
    height = height,
    headCircumference = headCircumference,
    notes = notes
)