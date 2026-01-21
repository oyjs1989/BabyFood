package com.example.babyfood.data.repository

import com.example.babyfood.data.local.database.dao.PlanDao
import com.example.babyfood.data.local.database.entity.PlanEntity
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.Plan
import com.example.babyfood.domain.model.PlanStatus
import kotlinx.coroutines.flow.Flow
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

    suspend fun insertPlan(plan: Plan): Long =
        planDao.insertPlan(plan.toEntity())

    suspend fun updatePlan(plan: Plan) =
        planDao.updatePlan(plan.toEntity())

    suspend fun deletePlan(plan: Plan) =
        planDao.deletePlan(plan.toEntity())

    suspend fun deletePlanById(planId: Long) =
        planDao.deletePlanById(planId)

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
            mealPeriod = period,
            status = PlanStatus.PLANNED
        ))
    }

    private fun PlanEntity.toDomainModel(): Plan = Plan(
        id = id,
        babyId = babyId,
        recipeId = recipeId,
        plannedDate = plannedDate,
        mealPeriod = mealPeriod,
        status = status,
        notes = notes
    )

    private fun Plan.toEntity(): PlanEntity = PlanEntity(
        id = id,
        babyId = babyId,
        recipeId = recipeId,
        plannedDate = plannedDate,
        mealPeriod = mealPeriod,
        status = status,
        notes = notes
    )
}