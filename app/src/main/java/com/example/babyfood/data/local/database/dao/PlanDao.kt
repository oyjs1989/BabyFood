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

    @Query("SELECT * FROM plans WHERE id = :planId")
    suspend fun getPlanById(planId: Long): PlanEntity?

    @Query("SELECT * FROM plans WHERE babyId = :babyId AND plannedDate = :date")
    fun getPlansByBabyAndDate(babyId: Long, date: LocalDate): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans WHERE babyId = :babyId AND status = :status ORDER BY plannedDate ASC")
    fun getPlansByBabyAndStatus(babyId: Long, status: com.example.babyfood.domain.model.PlanStatus): Flow<List<PlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanEntity): Long

    @Update
    suspend fun updatePlan(plan: PlanEntity)

    @Delete
    suspend fun deletePlan(plan: PlanEntity)

    @Query("DELETE FROM plans WHERE id = :planId")
    suspend fun deletePlanById(planId: Long)

    @Query("DELETE FROM plans WHERE babyId = :babyId")
    suspend fun deletePlansByBaby(babyId: Long)
}