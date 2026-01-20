package com.example.babyfood.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.babyfood.domain.model.Plan
import com.example.babyfood.domain.model.PlanStatus
import kotlinx.datetime.LocalDate

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val recipeId: Long,
    val plannedDate: LocalDate,
    val status: PlanStatus = PlanStatus.PLANNED,
    val notes: String? = null
) {
    fun toDomainModel(): Plan = Plan(
        id = id,
        babyId = babyId,
        recipeId = recipeId,
        plannedDate = plannedDate,
        status = status,
        notes = notes
    )
}

fun Plan.toEntity(): PlanEntity = PlanEntity(
    id = id,
    babyId = babyId,
    recipeId = recipeId,
    plannedDate = plannedDate,
    status = status,
    notes = notes
)