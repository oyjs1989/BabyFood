package com.example.babyfood.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.babyfood.domain.model.Ingredient
import com.example.babyfood.domain.model.Nutrition
import com.example.babyfood.domain.model.Recipe

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val minAgeMonths: Int,
    val maxAgeMonths: Int,
    val ingredients: List<Ingredient>,
    val steps: List<String>,
    val nutrition: Nutrition,
    val category: String,
    val isBuiltIn: Boolean = false,
    val imageUrl: String? = null
) {
    fun toDomainModel(): Recipe = Recipe(
        id = id,
        name = name,
        minAgeMonths = minAgeMonths,
        maxAgeMonths = maxAgeMonths,
        ingredients = ingredients,
        steps = steps,
        nutrition = nutrition,
        category = category,
        isBuiltIn = isBuiltIn,
        imageUrl = imageUrl
    )
}

fun Recipe.toEntity(): RecipeEntity = RecipeEntity(
    id = id,
    name = name,
    minAgeMonths = minAgeMonths,
    maxAgeMonths = maxAgeMonths,
    ingredients = ingredients,
    steps = steps,
    nutrition = nutrition,
    category = category,
    isBuiltIn = isBuiltIn,
    imageUrl = imageUrl
)