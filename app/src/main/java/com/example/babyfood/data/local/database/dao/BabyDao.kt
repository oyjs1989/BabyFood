package com.example.babyfood.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.babyfood.data.local.database.entity.BabyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {
    @Query("SELECT * FROM babies")
    fun getAllBabies(): Flow<List<BabyEntity>>

    @Query("SELECT * FROM babies")
    suspend fun getAllBabiesSync(): List<BabyEntity>

    @Query("SELECT * FROM babies WHERE id = :babyId")
    suspend fun getById(babyId: Long): BabyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(baby: BabyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(babies: List<BabyEntity>): List<Long>

    @Update
    suspend fun update(baby: BabyEntity)

    @Update
    suspend fun updateAll(babies: List<BabyEntity>)

    @Delete
    suspend fun delete(baby: BabyEntity)

    @Query("DELETE FROM babies WHERE id = :babyId")
    suspend fun deleteBabyById(babyId: Long)
}