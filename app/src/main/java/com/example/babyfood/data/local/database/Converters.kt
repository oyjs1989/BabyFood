package com.example.babyfood.data.local.database

import androidx.room.TypeConverter
import com.example.babyfood.domain.model.Ingredient
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.Nutrition
import com.example.babyfood.domain.model.NutritionGoal
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

    // NutritionGoal converters - 新增
    @TypeConverter
    fun fromNutritionGoal(goal: NutritionGoal?): String =
        if (goal == null) "" else json.encodeToString(goal.toEntity())

    @TypeConverter
    fun toNutritionGoal(jsonString: String): NutritionGoal? =
        if (jsonString.isEmpty()) null else json.decodeFromString<NutritionGoalEntity>(jsonString).toDomainModel()

    // MealPeriod converters - 新增
    @TypeConverter
    fun fromMealPeriod(period: MealPeriod): String = period.name

    @TypeConverter
    fun toMealPeriod(periodString: String): MealPeriod = MealPeriod.valueOf(periodString)

    // AllergyItem converters - 新增
    @TypeConverter
    fun fromAllergyItemList(items: List<com.example.babyfood.domain.model.AllergyItem>): String = 
        json.encodeToString(items)

    @TypeConverter
    fun toAllergyItemList(jsonString: String): List<com.example.babyfood.domain.model.AllergyItem> = 
        json.decodeFromString(jsonString)

    // PreferenceItem converters - 新增
    @TypeConverter
    fun fromPreferenceItemList(items: List<com.example.babyfood.domain.model.PreferenceItem>): String = 
        json.encodeToString(items)

    @TypeConverter
    fun toPreferenceItemList(jsonString: String): List<com.example.babyfood.domain.model.PreferenceItem> = 
        json.decodeFromString(jsonString)
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

// NutritionGoalEntity - 新增
data class NutritionGoalEntity(
    val calories: Float,
    val protein: Float,
    val calcium: Float,
    val iron: Float
) {
    fun toDomainModel(): NutritionGoal = NutritionGoal(
        calories = calories,
        protein = protein,
        calcium = calcium,
        iron = iron
    )
}

fun NutritionGoal.toEntity(): NutritionGoalEntity = NutritionGoalEntity(
    calories = calories,
    protein = protein,
    calcium = calcium,
    iron = iron
)