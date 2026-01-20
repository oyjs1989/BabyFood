package com.example.babyfood.di

import android.content.Context
import androidx.room.Room
import com.example.babyfood.data.local.database.BabyFoodDatabase
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
}