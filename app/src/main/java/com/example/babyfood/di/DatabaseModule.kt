package com.example.babyfood.di

import android.content.Context
import androidx.room.Room
import com.example.babyfood.data.local.database.BabyFoodDatabase
import com.example.babyfood.data.local.database.MIGRATION_1_2
import com.example.babyfood.data.local.database.MIGRATION_2_3
import com.example.babyfood.data.local.database.MIGRATION_3_4
import com.example.babyfood.data.local.database.MIGRATION_4_5
import com.example.babyfood.data.local.database.MIGRATION_5_6
import com.example.babyfood.data.local.database.MIGRATION_6_7
import com.example.babyfood.data.local.database.MIGRATION_7_8
import com.example.babyfood.data.local.database.MIGRATION_8_9
import com.example.babyfood.data.local.database.MIGRATION_9_10
import com.example.babyfood.data.local.database.MIGRATION_10_11
import com.example.babyfood.data.preferences.PreferencesManager
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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11)
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

    @Provides
    @Singleton
    fun provideUserDao(database: BabyFoodDatabase) = database.userDao()
}

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }
}