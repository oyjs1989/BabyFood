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
    val ironContent: Double,  // mg/100g
    val zincContent: Double,  // mg/100g
    val vitaminAContent: Double,  // mg/100g
    val calciumContent: Double,  // mg/100g
    val vitaminCContent: Double  // mg/100g
)