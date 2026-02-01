package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.dao.PlanDao
import com.example.babyfood.data.local.database.dao.RecipeDao
import com.example.babyfood.data.local.database.entity.PlanEntity
import com.example.babyfood.domain.model.ConflictResolution
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.Plan
import com.example.babyfood.domain.model.PlanConflict
import com.example.babyfood.domain.model.PlanStatus
import com.example.babyfood.domain.model.SaveResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanRepository @Inject constructor(
    private val planDao: PlanDao,
    private val recipeDao: RecipeDao
) : SyncableRepository<Plan, PlanEntity, Long>() {

    // Note: PlanDao implements SyncableDao methods implicitly
    // but doesn't extend the interface due to Room limitations

    override fun PlanEntity.toDomainModel(): Plan = Plan(
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

    override fun Plan.toEntity(): PlanEntity = PlanEntity(
        id = id,
        babyId = babyId,
        recipeId = recipeId,
        plannedDate = plannedDate,
        mealPeriod = try {
            MealPeriod.valueOf(mealPeriod)
        } catch (e: Exception) {
            MealPeriod.BREAKFAST
        },
        status = status,
        notes = notes,
        mealTime = mealTime,
        feedbackStatus = feedbackStatus,
        feedbackTime = feedbackTime
    )

    override fun getItemId(item: Plan): Long = item.id

    // ============ CRUD Operations ============

    suspend fun getById(id: Long): Plan? =
        planDao.getById(id)?.toDomainModel()

    suspend fun insert(item: Plan): Long =
        planDao.insert(item.toEntity().prepareForInsert())

    suspend fun update(item: Plan) {
        val existing = planDao.getById(item.id)
        if (existing != null) {
            planDao.update(item.toEntity().prepareForUpdate(existing))
        }
    }

    suspend fun delete(item: Plan) {
        planDao.delete(item.toEntity())
    }

    suspend fun insertAll(items: List<Plan>) {
        planDao.insertAll(items.map { it.toEntity().prepareForInsert() })
    }

    suspend fun deleteById(id: Long) {
        planDao.deletePlanById(id)
    }

    // ============ Domain-Specific Query Methods ============

    fun getPlansByBaby(babyId: Long): Flow<List<Plan>> =
        planDao.getPlansByBaby(babyId).toDomainModels()

    fun getPlansByBabyAndDate(babyId: Long, date: LocalDate): Flow<List<Plan>> =
        planDao.getPlansByBabyAndDate(babyId, date).toDomainModels()

    suspend fun getPlansByBabyDateAndPeriod(babyId: Long, date: LocalDate, period: MealPeriod): Plan? =
        planDao.getPlansByBabyDateAndPeriod(babyId, date, period.name)?.toDomainModel()

    fun getPlansByBabyAndStatus(babyId: Long, status: PlanStatus): Flow<List<Plan>> =
        planDao.getPlansByBabyAndStatus(babyId, status).toDomainModels()

    fun getPlansByBabyAndDateRange(babyId: Long, startDate: LocalDate, endDate: LocalDate): Flow<List<Plan>> =
        planDao.getPlansByBabyAndDateRange(babyId, startDate, endDate).toDomainModels()

    suspend fun deletePlansByBaby(babyId: Long) =
        planDao.deletePlansByBaby(babyId)

    // ============ Custom Business Logic Methods ============

    suspend fun replacePlanForPeriod(babyId: Long, date: LocalDate, period: MealPeriod, newRecipeId: Long) {
        // 先删除该餐段的原计划，再插入新计划
        val existingPlan = getPlansByBabyDateAndPeriod(babyId, date, period)
        if (existingPlan != null) {
            deleteById(existingPlan.id)
        }
        insert(Plan(
            babyId = babyId,
            recipeId = newRecipeId,
            plannedDate = date,
            mealPeriod = period.name,
            status = PlanStatus.PLANNED
        ))
    }

    /**
     * 检测冲突：返回冲突的计划列表
     */
    suspend fun detectConflicts(babyId: Long, newPlans: List<Plan>): List<PlanConflict> {
        android.util.Log.d("PlanRepository", "========== 开始检测冲突 ==========")
        android.util.Log.d("PlanRepository", "宝宝ID: $babyId, 新计划数: ${newPlans.size}")
        
        val conflicts = mutableListOf<PlanConflict>()

        // 获取现有计划
        val existingPlans = getPlansByBaby(babyId).first()
        android.util.Log.d("PlanRepository", "现有计划数: ${existingPlans.size}")

        // 获取所有食谱名称
        val allRecipeIds = (existingPlans.map { it.recipeId } + newPlans.map { it.recipeId }).distinct()
        val recipes = recipeDao.getByIds(allRecipeIds)
        val recipeNameMap = recipes.associateBy({ it.id }, { it.name })
        android.util.Log.d("PlanRepository", "加载了 ${recipes.size} 个食谱")

        for (newPlan in newPlans) {
            for (existingPlan in existingPlans) {
                // 检查同一天同一餐段的冲突
                if (newPlan.plannedDate == existingPlan.plannedDate &&
                    newPlan.mealPeriod == existingPlan.mealPeriod) {
                    val existingRecipeName = recipeNameMap[existingPlan.recipeId] ?: "未知食谱"
                    val newRecipeName = recipeNameMap[newPlan.recipeId] ?: "未知食谱"
                    
                    conflicts.add(PlanConflict(
                        newPlan = newPlan,
                        existingPlan = existingPlan,
                        conflictType = com.example.babyfood.domain.model.ConflictType.SAME_DATE_AND_PERIOD,
                        existingRecipeName = existingRecipeName,
                        newRecipeName = newRecipeName
                    ))
                    
                    android.util.Log.d("PlanRepository", "检测到冲突: ${existingPlan.plannedDate} ${existingPlan.mealPeriod}, 现有: $existingRecipeName, 推荐: $newRecipeName")
                }
            }
        }

        android.util.Log.d("PlanRepository", "✓ 检测到 ${conflicts.size} 个冲突")
        android.util.Log.d("PlanRepository", "========== 冲突检测完成 ==========")
        return conflicts
    }

    /**
     * 冲突解决：覆盖现有计划
     */
    suspend fun overwritePlans(babyId: Long, newPlans: List<Plan>): SaveResult {
        return try {
            // 先删除冲突的计划
            val conflicts = detectConflicts(babyId, newPlans)
            for (conflict in conflicts) {
                deleteById(conflict.existingPlan.id)
            }

            // 插入新计划
            insertAll(newPlans)

            SaveResult(
                success = true,
                savedCount = newPlans.size,
                skippedCount = 0
            )
        } catch (e: Exception) {
            SaveResult(
                success = false,
                error = e.message
            )
        }
    }

    /**
     * 冲突解决：跳过冲突计划，只插入新计划
     */
    suspend fun insertNonConflictingPlans(babyId: Long, newPlans: List<Plan>): SaveResult {
        return try {
            // 获取冲突的计划
            val conflicts = detectConflicts(babyId, newPlans)
            val conflictingPlanIds = conflicts.map { it.newPlan.plannedDate to it.newPlan.mealPeriod }.toSet()

            // 过滤掉冲突的计划
            val nonConflictingPlans = newPlans.filter { plan ->
                (plan.plannedDate to plan.mealPeriod) !in conflictingPlanIds
            }

            // 插入非冲突的计划
            insertAll(nonConflictingPlans)

            SaveResult(
                success = true,
                savedCount = nonConflictingPlans.size,
                skippedCount = conflicts.size
            )
        } catch (e: Exception) {
            SaveResult(
                success = false,
                error = e.message
            )
        }
    }

    /**
     * 保存推荐结果（带冲突处理）
     */
    suspend fun saveRecommendation(
        babyId: Long,
        newPlans: List<Plan>,
        conflictResolution: ConflictResolution
    ): SaveResult {
        return when (conflictResolution) {
            ConflictResolution.OVERWRITE_ALL -> overwritePlans(babyId, newPlans)
            ConflictResolution.SKIP_CONFLICTS -> insertNonConflictingPlans(babyId, newPlans)
            ConflictResolution.CANCEL -> SaveResult(
                success = true,
                savedCount = 0,
                skippedCount = 0
            )
        }
    }
}