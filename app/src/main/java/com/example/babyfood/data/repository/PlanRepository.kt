package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.dao.PlanDao
import com.example.babyfood.data.local.database.entity.PlanEntity
import com.example.babyfood.data.local.database.entity.toEntity
import com.example.babyfood.domain.model.ConflictResolution
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.Plan
import com.example.babyfood.domain.model.PlanConflict
import com.example.babyfood.domain.model.PlanStatus
import com.example.babyfood.domain.model.SaveResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanRepository @Inject constructor(
    private val planDao: PlanDao
) {
    fun getPlansByBaby(babyId: Long): Flow<List<Plan>> =
        planDao.getPlansByBaby(babyId).map { entities -> entities.map { entity -> entity.toDomainModel() } }

    suspend fun getPlanById(planId: Long): Plan? =
        planDao.getPlanById(planId)?.toDomainModel()

    fun getPlansByBabyAndDate(babyId: Long, date: LocalDate): Flow<List<Plan>> =
        planDao.getPlansByBabyAndDate(babyId, date).map { entities -> entities.map { entity -> entity.toDomainModel() } }

    suspend fun getPlansByBabyDateAndPeriod(babyId: Long, date: LocalDate, period: MealPeriod): Plan? =
        planDao.getPlansByBabyDateAndPeriod(babyId, date, period.name)?.toDomainModel()

    fun getPlansByBabyAndStatus(babyId: Long, status: PlanStatus): Flow<List<Plan>> =
        planDao.getPlansByBabyAndStatus(babyId, status).map { entities -> entities.map { entity -> entity.toDomainModel() } }

    fun getPlansByBabyAndDateRange(babyId: Long, startDate: LocalDate, endDate: LocalDate): Flow<List<Plan>> =
        planDao.getPlansByBabyAndDateRange(babyId, startDate, endDate).map { entities -> entities.map { entity -> entity.toDomainModel() } }

    suspend fun insertPlan(plan: Plan): Long {
        val entity = plan.toEntity().copy(
            syncStatus = "PENDING_UPLOAD",
            lastSyncTime = null,
            version = 1
        )
        return planDao.insertPlan(entity)
    }

    suspend fun insertPlans(plans: List<Plan>): List<Long> {
        val entities = plans.map { plan ->
            plan.toEntity().copy(
                syncStatus = "PENDING_UPLOAD",
                lastSyncTime = null,
                version = 1
            )
        }
        return planDao.insertPlans(entities)
    }

    suspend fun updatePlan(plan: Plan) {
        val existing = planDao.getPlanById(plan.id)
        if (existing != null) {
            val entity = plan.toEntity().copy(
                cloudId = existing.cloudId,
                cloudBabyId = existing.cloudBabyId,
                cloudRecipeId = existing.cloudRecipeId,
                syncStatus = "PENDING_UPLOAD",
                lastSyncTime = existing.lastSyncTime,
                version = existing.version + 1
            )
            planDao.updatePlan(entity)
        }
    }

    suspend fun updatePlans(plans: List<Plan>) {
        val entities = plans.map { plan ->
            val existing = planDao.getPlanById(plan.id)
            if (existing != null) {
                plan.toEntity().copy(
                    cloudId = existing.cloudId,
                    cloudBabyId = existing.cloudBabyId,
                    cloudRecipeId = existing.cloudRecipeId,
                    syncStatus = "PENDING_UPLOAD",
                    lastSyncTime = existing.lastSyncTime,
                    version = existing.version + 1
                )
            } else {
                plan.toEntity().copy(
                    syncStatus = "PENDING_UPLOAD",
                    lastSyncTime = null,
                    version = 1
                )
            }
        }
        planDao.updatePlans(entities)
    }

    suspend fun deletePlan(plan: Plan) {
        val existing = planDao.getPlanById(plan.id)
        if (existing != null) {
            // 软删除
            val entity = plan.toEntity().copy(
                cloudId = existing.cloudId,
                cloudBabyId = existing.cloudBabyId,
                cloudRecipeId = existing.cloudRecipeId,
                syncStatus = "PENDING_UPLOAD",
                lastSyncTime = existing.lastSyncTime,
                version = existing.version + 1,
                isDeleted = true
            )
            planDao.updatePlan(entity)
        }
    }

    suspend fun deletePlanById(planId: Long) {
        val plan = getPlanById(planId)
        if (plan != null) {
            deletePlan(plan)
        }
    }

    suspend fun deletePlansByBaby(babyId: Long) =
        planDao.deletePlansByBaby(babyId)

    suspend fun replacePlanForPeriod(babyId: Long, date: LocalDate, period: MealPeriod, newRecipeId: Long) {
        // 先删除该餐段的原计划，再插入新计划
        val existingPlan = getPlansByBabyDateAndPeriod(babyId, date, period)
        if (existingPlan != null) {
            deletePlanById(existingPlan.id)
        }
        insertPlan(Plan(
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
        val conflicts = mutableListOf<PlanConflict>()
        
        // 获取现有计划
        val existingPlans = getPlansByBaby(babyId).first()
        
        for (newPlan in newPlans) {
            for (existingPlan in existingPlans) {
                // 检查同一天同一餐段的冲突
                if (newPlan.plannedDate == existingPlan.plannedDate && 
                    newPlan.mealPeriod == existingPlan.mealPeriod) {
                    conflicts.add(PlanConflict(
                        newPlan = newPlan,
                        existingPlan = existingPlan,
                        conflictType = com.example.babyfood.domain.model.ConflictType.SAME_DATE_AND_PERIOD
                    ))
                }
            }
        }
        
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
                deletePlanById(conflict.existingPlan.id)
            }
            
            // 插入新计划
            insertPlans(newPlans)
            
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
            insertPlans(nonConflictingPlans)
            
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