package com.example.babyfood.data.local.database

import androidx.room.TypeConverter
import com.example.babyfood.domain.model.Ingredient
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.Nutrition
import com.example.babyfood.domain.model.NutritionGoal
import com.example.babyfood.domain.model.PlanStatus
import com.example.babyfood.domain.model.StorageMethod
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    // LocalDate converters
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate = LocalDate.parse(dateString)

    // String list converters
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

    // Enum converters
    @TypeConverter
    fun fromPlanStatus(status: PlanStatus): String = status.name

    @TypeConverter
    fun toPlanStatus(statusString: String): PlanStatus = PlanStatus.valueOf(statusString)

    @TypeConverter
    fun fromMealPeriod(period: MealPeriod): String = period.name

    @TypeConverter
    fun toMealPeriod(periodString: String): MealPeriod = MealPeriod.valueOf(periodString)

    @TypeConverter
    fun fromStorageMethod(method: StorageMethod): String = method.name

    @TypeConverter
    fun toStorageMethod(methodString: String): StorageMethod = StorageMethod.valueOf(methodString)

    // Complex type converters with null handling
    @TypeConverter
    fun fromNutritionGoal(goal: NutritionGoal?): String =
        if (goal == null) "" else json.encodeToString(goal.toEntity())

    @TypeConverter
    fun toNutritionGoal(jsonString: String): NutritionGoal? =
        jsonString.takeIf { it.isNotEmpty() }?.let { json.decodeFromString<NutritionGoalEntity>(it).toDomainModel() }

    @TypeConverter
    fun fromAllergyItemList(items: List<com.example.babyfood.domain.model.AllergyItem>): String = json.encodeToString(items)

    @TypeConverter
    fun toAllergyItemList(jsonString: String): List<com.example.babyfood.domain.model.AllergyItem> = json.decodeFromString(jsonString)

    @TypeConverter
    fun fromPreferenceItemList(items: List<com.example.babyfood.domain.model.PreferenceItem>): String = json.encodeToString(items)

    @TypeConverter
    fun toPreferenceItemList(jsonString: String): List<com.example.babyfood.domain.model.PreferenceItem> = json.decodeFromString(jsonString)
}

// Entity versions for serialization
@Serializable
data class IngredientEntity(
    val name: String,
    val amount: String,
    val isAllergen: Boolean = false
) {
    fun toDomainModel(): Ingredient = Ingredient(name, amount, isAllergen)
}

fun Ingredient.toEntity(): IngredientEntity = IngredientEntity(name, amount, isAllergen)

@Serializable
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
@Serializable
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