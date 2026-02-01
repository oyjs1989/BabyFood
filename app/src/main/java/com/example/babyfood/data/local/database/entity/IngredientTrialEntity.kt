package com.example.babyfood.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingredient_trials",
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
data class IngredientTrialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val ingredientName: String,
    val trialDate: Long,
    val isAllergic: Boolean = false,
    val reaction: String?
)