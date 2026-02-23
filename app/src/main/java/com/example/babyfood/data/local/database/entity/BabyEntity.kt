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
    val avatarUrl: String? = null,                  // 头像 URL

    // 质地适配扩展字段
    val chewingAbility: String? = null,             // 咀嚼能力：NORMAL, STRONG, WEAK
    val preferredTextureLevel: Int? = null,         // 偏好质地级别：1-4

    // 同步元数据字段（向后兼容，默认值）
    override val cloudId: String? = null,           // 云端唯一标识
    override val syncStatus: String = "LOCAL_ONLY", // 同步状态
    override val lastSyncTime: Long? = null,        // 最后同步时间戳（毫秒）
    override val version: Int = 1,                  // 版本号（用于冲突检测）
    override val isDeleted: Boolean = false         // 软删除标记
) : SyncableEntity {
    fun toDomainModel(): Baby = Baby(
        id = id,
        name = name,
        birthDate = birthDate,
        allergies = allergies,
        weight = weight,
        height = height,
        preferences = preferences,
        nutritionGoal = nutritionGoal,
        avatarUrl = avatarUrl,
        chewingAbility = chewingAbility,
        preferredTextureLevel = preferredTextureLevel
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
    nutritionGoal = nutritionGoal,
    avatarUrl = avatarUrl,
    chewingAbility = chewingAbility,
    preferredTextureLevel = preferredTextureLevel
)