package com.example.babyfood.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.Plan
import com.example.babyfood.domain.model.PlanStatus
import kotlinx.datetime.LocalDate

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val recipeId: Long,
    val plannedDate: LocalDate,
    @ColumnInfo(name = "meal_period")
    val mealPeriod: MealPeriod,
    val status: PlanStatus = PlanStatus.PLANNED,
    val notes: String? = null,
    val mealTime: String? = null,  // 用户自定义的用餐时间（格式：HH:mm）
    val feedbackStatus: String? = null,  // 反馈状态（FINISHED、HALF、REJECTED、ALLERGY）
    val feedbackTime: String? = null,  // 反馈时间（ISO 8601 格式）

    // 同步元数据字段（向后兼容，默认值）
    override val cloudId: String? = null,           // 云端唯一标识
    val cloudBabyId: String? = null,                // 云端宝宝 ID（用于云端关联）
    val cloudRecipeId: String? = null,              // 云端食谱 ID（用于云端关联）
    override val syncStatus: String = "PENDING_UPLOAD", // 同步状态（默认待上传）
    override val lastSyncTime: Long? = null,        // 最后同步时间戳（毫秒）
    override val version: Int = 1,                  // 版本号（用于冲突检测）
    override val isDeleted: Boolean = false         // 软删除标记
) : SyncableEntity {
    fun toDomainModel(): Plan = Plan(
        id = id,
        babyId = babyId,
        recipeId = recipeId,
        plannedDate = plannedDate,
        mealPeriod = mealPeriod.name,
        status = status,
        notes = notes,
        mealTime = mealTime,
        feedbackStatus = feedbackStatus,
        feedbackTime = feedbackTime
    )
}

fun Plan.toEntity(): PlanEntity = PlanEntity(
    id = id,
    babyId = babyId,
    recipeId = recipeId,
    plannedDate = plannedDate,
    mealPeriod = try {
        MealPeriod.valueOf(mealPeriod)
    } catch (e: Exception) {
        MealPeriod.BREAKFAST  // 默认值
    },
    status = status,
    notes = notes,
    mealTime = mealTime,
    feedbackStatus = feedbackStatus,
    feedbackTime = feedbackTime
)