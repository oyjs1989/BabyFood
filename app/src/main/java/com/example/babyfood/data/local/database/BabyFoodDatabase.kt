package com.example.babyfood.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.babyfood.data.local.database.dao.BabyDao
import com.example.babyfood.data.local.database.dao.PlanDao
import com.example.babyfood.data.local.database.dao.RecipeDao
import com.example.babyfood.data.local.database.entity.BabyEntity
import com.example.babyfood.data.local.database.entity.PlanEntity
import com.example.babyfood.data.local.database.entity.RecipeEntity

@Database(
    entities = [BabyEntity::class, RecipeEntity::class, PlanEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BabyFoodDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
    abstract fun recipeDao(): RecipeDao
    abstract fun planDao(): PlanDao
}