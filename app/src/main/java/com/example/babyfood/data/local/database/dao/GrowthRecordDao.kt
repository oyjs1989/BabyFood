package com.example.babyfood.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.babyfood.data.local.database.entity.GrowthRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface GrowthRecordDao {
    @Query("SELECT * FROM growth_records WHERE babyId = :babyId ORDER BY recordDate ASC")
    fun getGrowthRecordsByBaby(babyId: Long): Flow<List<GrowthRecordEntity>>
    @Query("SELECT * FROM growth_records WHERE id = :recordId")
    suspend fun getGrowthRecordById(recordId: Long): GrowthRecordEntity?

    @Query("SELECT * FROM growth_records ORDER BY recordDate ASC")
    suspend fun getAllGrowthRecordsSync(): List<GrowthRecordEntity>

    @Query("SELECT * FROM growth_records WHERE babyId = :babyId AND recordDate = :recordDate")
    suspend fun getGrowthRecordByDate(babyId: Long, recordDate: LocalDate): GrowthRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrowthRecord(record: GrowthRecordEntity): Long

    @Update
    suspend fun updateGrowthRecord(record: GrowthRecordEntity)

    @Delete
    suspend fun deleteGrowthRecord(record: GrowthRecordEntity)

    @Query("DELETE FROM growth_records WHERE id = :recordId")
    suspend fun deleteGrowthRecordById(recordId: Long)

    @Query("DELETE FROM growth_records WHERE babyId = :babyId")
    suspend fun deleteGrowthRecordsByBaby(babyId: Long)

    @Query("DELETE FROM growth_records WHERE babyId = :babyId AND recordDate = :recordDate")
    suspend fun deleteGrowthRecordByDate(babyId: Long, recordDate: LocalDate)
}