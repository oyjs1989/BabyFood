package com.example.babyfood.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "nutrition_goals",
    foreignKeys = [
        ForeignKey(
            entity = BabyEntity::class,
            parentColumns = ["id"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["babyId"])]
)
data class NutritionGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val calories: Double,
    val protein: Double,
    val calcium: Double,
    val iron: Double,
    val vitaminA: Double,
    val vitaminC: Double
)