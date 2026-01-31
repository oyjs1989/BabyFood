package com.example.babyfood.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.babyfood.data.local.database.entity.PlanEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface PlanDao {
    @Query("SELECT * FROM plans WHERE babyId = :babyId ORDER BY plannedDate ASC")
    fun getPlansByBaby(babyId: Long): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans")
    suspend fun getAllPlansSync(): List<PlanEntity>

    @Query("SELECT * FROM plans WHERE id = :planId")
    suspend fun getById(planId: Long): PlanEntity?

    @Query("SELECT * FROM plans WHERE babyId = :babyId AND plannedDate = :date ORDER BY meal_period ASC")
    fun getPlansByBabyAndDate(babyId: Long, date: LocalDate): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans WHERE babyId = :babyId AND plannedDate = :date AND meal_period = :period LIMIT 1")
    suspend fun getPlansByBabyDateAndPeriod(babyId: Long, date: LocalDate, period: String): PlanEntity?

    @Query("SELECT * FROM plans WHERE babyId = :babyId AND status = :status ORDER BY plannedDate ASC")
    fun getPlansByBabyAndStatus(babyId: Long, status: com.example.babyfood.domain.model.PlanStatus): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans WHERE babyId = :babyId AND plannedDate BETWEEN :startDate AND :endDate ORDER BY plannedDate ASC, meal_period ASC")
    fun getPlansByBabyAndDateRange(babyId: Long, startDate: LocalDate, endDate: LocalDate): Flow<List<PlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: PlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<PlanEntity>): List<Long>

    @Update
    suspend fun update(plan: PlanEntity)

    @Update
    suspend fun updateAll(plans: List<PlanEntity>)

    @Delete
    suspend fun delete(plan: PlanEntity)

    @Query("DELETE FROM plans WHERE id = :planId")
    suspend fun deletePlanById(planId: Long)

    @Query("DELETE FROM plans WHERE babyId = :babyId")
    suspend fun deletePlansByBaby(babyId: Long)
}