package com.example.babyfood.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.babyfood.data.local.database.entity.HealthRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface HealthRecordDao {
    @Query("SELECT * FROM health_records WHERE babyId = :babyId ORDER BY recordDate DESC")
    fun getHealthRecordsByBaby(babyId: Long): Flow<List<HealthRecordEntity>>

    @Query("SELECT * FROM health_records WHERE id = :recordId")
    suspend fun getHealthRecordById(recordId: Long): HealthRecordEntity?

    @Query("SELECT * FROM health_records WHERE babyId = :babyId ORDER BY recordDate DESC LIMIT 1")
    suspend fun getLatestHealthRecord(babyId: Long): HealthRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(record: HealthRecordEntity): Long

    @Update
    suspend fun updateHealthRecord(record: HealthRecordEntity)

    @Delete
    suspend fun deleteHealthRecord(record: HealthRecordEntity)

    @Query("DELETE FROM health_records WHERE id = :recordId")
    suspend fun deleteHealthRecordById(recordId: Long)

    @Query("DELETE FROM health_records WHERE babyId = :babyId")
    suspend fun deleteHealthRecordsByBaby(babyId: Long)
}