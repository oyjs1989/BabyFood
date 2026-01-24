package com.example.babyfood.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.babyfood.domain.model.AllergyItem
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.NutritionGoal
import com.example.babyfood.domain.model.PreferenceItem
import kotlinx.datetime.LocalDate

@Entity(tableName = "babies")
data class BabyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val birthDate: LocalDate,
    val allergies: List<AllergyItem>,
    val weight: Float?,
    val height: Float?,
    val preferences: List<PreferenceItem>,
    val nutritionGoal: NutritionGoal? = null,

    // 同步元数据字段（向后兼容，默认值）
    val cloudId: String? = null,                    // 云端唯一标识
    val syncStatus: String = "LOCAL_ONLY",          // 同步状态
    val lastSyncTime: Long? = null,                 // 最后同步时间戳（毫秒）
    val version: Int = 1,                           // 版本号（用于冲突检测）
    val isDeleted: Boolean = false                  // 软删除标记
) {
    fun toDomainModel(): Baby = Baby(
        id = id,
        name = name,
        birthDate = birthDate,
        allergies = allergies,
        weight = weight,
        height = height,
        preferences = preferences,
        nutritionGoal = nutritionGoal
    )
}

fun Baby.toEntity(): BabyEntity = BabyEntity(
    id = id,
    name = name,
    birthDate = birthDate,
    allergies = allergies,
    weight = weight,
    height = height,
    preferences = preferences,
    nutritionGoal = nutritionGoal
)