package com.example.babyfood.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.babyfood.domain.model.MealPeriod
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
    @ColumnInfo(name = "meal_period")
    val mealPeriod: MealPeriod,  // 新增
    val status: PlanStatus = PlanStatus.PLANNED,
    val notes: String? = null
) {
    fun toDomainModel(): Plan = Plan(
        id = id,
        babyId = babyId,
        recipeId = recipeId,
        plannedDate = plannedDate,
        mealPeriod = mealPeriod,
        status = status,
        notes = notes
    )
}

fun Plan.toEntity(): PlanEntity = PlanEntity(
    id = id,
    babyId = babyId,
    recipeId = recipeId,
    plannedDate = plannedDate,
    mealPeriod = mealPeriod,
    status = status,
    notes = notes
)