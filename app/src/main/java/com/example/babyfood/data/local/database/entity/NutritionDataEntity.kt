package com.example.babyfood.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "nutrition_data",
    indices = [Index(value = ["ingredientName"])]
)
data class NutritionDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ingredientName: String,
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val fiber: Double = 0.0,
    val calciumContent: Double = 0.0,
    val ironContent: Double = 0.0,
    val zincContent: Double = 0.0,
    val vitaminAContent: Double = 0.0,
    val vitaminCContent: Double = 0.0
)
