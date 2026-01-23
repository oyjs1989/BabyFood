package com.example.babyfood.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.TypeConverters
import com.example.babyfood.data.local.database.dao.BabyDao
import com.example.babyfood.data.local.database.dao.GrowthRecordDao
import com.example.babyfood.data.local.database.dao.HealthRecordDao
import com.example.babyfood.data.local.database.dao.PlanDao
import com.example.babyfood.data.local.database.dao.RecipeDao
import com.example.babyfood.data.local.database.entity.BabyEntity
import com.example.babyfood.data.local.database.entity.GrowthRecordEntity
import com.example.babyfood.data.local.database.entity.HealthRecordEntity
import com.example.babyfood.data.local.database.entity.PlanEntity
import com.example.babyfood.data.local.database.entity.RecipeEntity

// 数据库迁移：从版本 1 到版本 2
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 1. 为 babies 表添加 nutrition_goal 字段
        database.execSQL(
            "ALTER TABLE babies ADD COLUMN nutrition_goal TEXT"
        )

        // 2. 为 plans 表添加 meal_period 字段
        database.execSQL(
            "ALTER TABLE plans ADD COLUMN meal_period TEXT NOT NULL DEFAULT 'BREAKFAST'"
        )
    }
}

// 数据库迁移：从版本 2 到版本 3
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 1. 创建 health_records 表
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS health_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                babyId INTEGER NOT NULL,
                recordDate TEXT NOT NULL,
                weight REAL,
                height REAL,
                headCircumference REAL,
                ironLevel REAL,
                calciumLevel REAL,
                hemoglobin REAL,
                aiAnalysis TEXT,
                isConfirmed INTEGER NOT NULL DEFAULT 0,
                expiryDate TEXT,
                notes TEXT
            )
            """.trimIndent()
        )

        // 2. 创建 growth_records 表
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS growth_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                babyId INTEGER NOT NULL,
                recordDate TEXT NOT NULL,
                weight REAL NOT NULL,
                height REAL NOT NULL,
                headCircumference REAL,
                notes TEXT
            )
            """.trimIndent()
        )

        // 3. 创建索引
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_health_records_babyId ON health_records(babyId)"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_growth_records_babyId ON growth_records(babyId)"
        )
    }
}

// 数据库迁移：从版本 3 到版本 4
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // No-op migration - schema is compatible, version bump only
    }
}

@Database(
    entities = [
        BabyEntity::class,
        RecipeEntity::class,
        PlanEntity::class,
        HealthRecordEntity::class,
        GrowthRecordEntity::class
    ],
    version = 4,  // 升级到版本 4
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BabyFoodDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
    abstract fun recipeDao(): RecipeDao
    abstract fun planDao(): PlanDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun growthRecordDao(): GrowthRecordDao
}