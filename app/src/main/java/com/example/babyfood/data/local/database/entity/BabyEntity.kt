package com.example.babyfood.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.babyfood.domain.model.Baby
import kotlinx.datetime.LocalDate

@Entity(tableName = "babies")
data class BabyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val birthDate: LocalDate,
    val allergies: List<String>,
    val weight: Float?,
    val height: Float?,
    val preferences: List<String>
) {
    fun toDomainModel(): Baby = Baby(
        id = id,
        name = name,
        birthDate = birthDate,
        allergies = allergies,
        weight = weight,
        height = height,
        preferences = preferences
    )
}

fun Baby.toEntity(): BabyEntity = BabyEntity(
    id = id,
    name = name,
    birthDate = birthDate,
    allergies = allergies,
    weight = weight,
    height = height,
    preferences = preferences
)