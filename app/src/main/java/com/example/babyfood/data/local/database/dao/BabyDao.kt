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
    suspend fun getBabyById(babyId: Long): BabyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaby(baby: BabyEntity): Long

    @Update
    suspend fun updateBaby(baby: BabyEntity)

    @Delete
    suspend fun deleteBaby(baby: BabyEntity)

    @Query("DELETE FROM babies WHERE id = :babyId")
    suspend fun deleteBabyById(babyId: Long)
}