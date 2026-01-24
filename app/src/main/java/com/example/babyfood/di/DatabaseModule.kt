package com.example.babyfood.di

import android.content.Context
import androidx.room.Room
import com.example.babyfood.data.local.database.BabyFoodDatabase
import com.example.babyfood.data.local.database.MIGRATION_1_2
import com.example.babyfood.data.local.database.MIGRATION_2_3
import com.example.babyfood.data.local.database.MIGRATION_3_4
import com.example.babyfood.data.local.database.MIGRATION_4_5
import com.example.babyfood.data.local.database.MIGRATION_5_6
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideBabyFoodDatabase(
        @ApplicationContext context: Context
    ): BabyFoodDatabase {
        return Room.databaseBuilder(
            context,
            BabyFoodDatabase::class.java,
            "baby_food_database"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideBabyDao(database: BabyFoodDatabase) = database.babyDao()

    @Provides
    @Singleton
    fun provideRecipeDao(database: BabyFoodDatabase) = database.recipeDao()

    @Provides
    @Singleton
    fun providePlanDao(database: BabyFoodDatabase) = database.planDao()

    @Provides
    @Singleton
    fun provideHealthRecordDao(database: BabyFoodDatabase) = database.healthRecordDao()

    @Provides
    @Singleton
    fun provideGrowthRecordDao(database: BabyFoodDatabase) = database.growthRecordDao()
}