package com.example.babyfood.data.local.database

import androidx.room.TypeConverter
import com.example.babyfood.domain.model.Ingredient
import com.example.babyfood.domain.model.Nutrition
import com.example.babyfood.domain.model.PlanStatus
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    // LocalDate converters
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate = LocalDate.parse(dateString)

    // List<String> converters
    @TypeConverter
    fun fromStringList(list: List<String>): String = json.encodeToString(list)

    @TypeConverter
    fun toStringList(jsonString: String): List<String> = json.decodeFromString(jsonString)

    // Ingredient converters
    @TypeConverter
    fun fromIngredientList(list: List<Ingredient>): String = json.encodeToString(list.map { it.toEntity() })

    @TypeConverter
    fun toIngredientList(jsonString: String): List<Ingredient> =
        json.decodeFromString<List<IngredientEntity>>(jsonString).map { it.toDomainModel() }

    // Nutrition converters
    @TypeConverter
    fun fromNutrition(nutrition: Nutrition): String = json.encodeToString(nutrition.toEntity())

    @TypeConverter
    fun toNutrition(jsonString: String): Nutrition = json.decodeFromString<NutritionEntity>(jsonString).toDomainModel()

    // PlanStatus converters
    @TypeConverter
    fun fromPlanStatus(status: PlanStatus): String = status.name

    @TypeConverter
    fun toPlanStatus(statusString: String): PlanStatus = PlanStatus.valueOf(statusString)
}

// Entity versions for serialization
data class IngredientEntity(
    val name: String,
    val amount: String,
    val isAllergen: Boolean = false
) {
    fun toDomainModel(): Ingredient = Ingredient(name, amount, isAllergen)
}

fun Ingredient.toEntity(): IngredientEntity = IngredientEntity(name, amount, isAllergen)

data class NutritionEntity(
    val calories: Float?,
    val protein: Float?,
    val fat: Float?,
    val carbohydrates: Float?,
    val fiber: Float?,
    val calcium: Float?,
    val iron: Float?
) {
    fun toDomainModel(): Nutrition = Nutrition(
        calories = calories,
        protein = protein,
        fat = fat,
        carbohydrates = carbohydrates,
        fiber = fiber,
        calcium = calcium,
        iron = iron
    )
}

fun Nutrition.toEntity(): NutritionEntity = NutritionEntity(
    calories = calories,
    protein = protein,
    fat = fat,
    carbohydrates = carbohydrates,
    fiber = fiber,
    calcium = calcium,
    iron = iron
)