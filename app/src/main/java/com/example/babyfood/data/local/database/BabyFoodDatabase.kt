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

// 数据库迁移：从版本 4 到版本 5
// 添加同步元数据字段，支持云存储功能
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 1. babies 表：添加同步元数据字段
        database.execSQL("ALTER TABLE babies ADD COLUMN cloudId TEXT")
        database.execSQL("ALTER TABLE babies ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'LOCAL_ONLY'")
        database.execSQL("ALTER TABLE babies ADD COLUMN lastSyncTime INTEGER")
        database.execSQL("ALTER TABLE babies ADD COLUMN version INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE babies ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")

        // 2. plans 表：添加同步元数据字段
        database.execSQL("ALTER TABLE plans ADD COLUMN cloudId TEXT")
        database.execSQL("ALTER TABLE plans ADD COLUMN cloudBabyId TEXT")
        database.execSQL("ALTER TABLE plans ADD COLUMN cloudRecipeId TEXT")
        database.execSQL("ALTER TABLE plans ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'PENDING_UPLOAD'")
        database.execSQL("ALTER TABLE plans ADD COLUMN lastSyncTime INTEGER")
        database.execSQL("ALTER TABLE plans ADD COLUMN version INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE plans ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")

        // 3. recipes 表：添加同步元数据字段
        database.execSQL("ALTER TABLE recipes ADD COLUMN cloudId TEXT")
        database.execSQL("ALTER TABLE recipes ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'PENDING_UPLOAD'")
        database.execSQL("ALTER TABLE recipes ADD COLUMN lastSyncTime INTEGER")
        database.execSQL("ALTER TABLE recipes ADD COLUMN version INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE recipes ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")

        // 4. 创建索引（优化查询性能）
        database.execSQL("CREATE INDEX IF NOT EXISTS index_babies_cloudId ON babies(cloudId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_plans_cloudId ON plans(cloudId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_recipes_cloudId ON recipes(cloudId)")
    }
}

// 数据库迁移：从版本 5 到版本 6
// 修复同步元数据字段的 NOT NULL 约束（修复 MIGRATION_4_5 的遗留问题）
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // SQLite 不支持直接修改列约束，需要重建表

        // 1. 重建 babies 表
        database.execSQL("""
            CREATE TABLE babies_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                birthDate TEXT NOT NULL,
                allergies TEXT NOT NULL,
                weight REAL,
                height REAL,
                preferences TEXT NOT NULL,
                nutrition_goal TEXT,
                cloudId TEXT,
                syncStatus TEXT NOT NULL DEFAULT 'LOCAL_ONLY',
                lastSyncTime INTEGER,
                version INTEGER NOT NULL DEFAULT 1,
                isDeleted INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
        database.execSQL("""
            INSERT INTO babies_new (id, name, birthDate, allergies, weight, height, preferences, nutrition_goal, cloudId, syncStatus, lastSyncTime, version, isDeleted)
            SELECT id, name, birthDate, allergies, weight, height, preferences, nutrition_goal, cloudId,
                   COALESCE(syncStatus, 'LOCAL_ONLY'),
                   lastSyncTime,
                   COALESCE(version, 1),
                   COALESCE(isDeleted, 0)
            FROM babies
        """.trimIndent())
        database.execSQL("DROP TABLE babies")
        database.execSQL("ALTER TABLE babies_new RENAME TO babies")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_babies_cloudId ON babies(cloudId)")

        // 2. 重建 plans 表
        database.execSQL("""
            CREATE TABLE plans_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                babyId INTEGER NOT NULL,
                recipeId INTEGER NOT NULL,
                plannedDate TEXT NOT NULL,
                meal_period TEXT NOT NULL DEFAULT 'BREAKFAST',
                status TEXT NOT NULL DEFAULT 'PLANNED',
                notes,
                cloudId TEXT,
                cloudBabyId TEXT,
                cloudRecipeId TEXT,
                syncStatus TEXT NOT NULL DEFAULT 'PENDING_UPLOAD',
                lastSyncTime INTEGER,
                version INTEGER NOT NULL DEFAULT 1,
                isDeleted INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
        database.execSQL("""
            INSERT INTO plans_new (id, babyId, recipeId, plannedDate, meal_period, status, notes, cloudId, cloudBabyId, cloudRecipeId, syncStatus, lastSyncTime, version, isDeleted)
            SELECT id, babyId, recipeId, plannedDate, meal_period, status, notes, cloudId, cloudBabyId, cloudRecipeId,
                   COALESCE(syncStatus, 'PENDING_UPLOAD'),
                   lastSyncTime,
                   COALESCE(version, 1),
                   COALESCE(isDeleted, 0)
            FROM plans
        """.trimIndent())
        database.execSQL("DROP TABLE plans")
        database.execSQL("ALTER TABLE plans_new RENAME TO plans")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_plans_cloudId ON plans(cloudId)")

        // 3. 重建 recipes 表
        database.execSQL("""
            CREATE TABLE recipes_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                minAgeMonths INTEGER NOT NULL,
                maxAgeMonths INTEGER NOT NULL,
                ingredients TEXT NOT NULL,
                steps TEXT NOT NULL,
                nutrition TEXT NOT NULL,
                category TEXT NOT NULL,
                isBuiltIn INTEGER NOT NULL DEFAULT 0,
                imageUrl,
                cloudId TEXT,
                syncStatus TEXT NOT NULL DEFAULT 'PENDING_UPLOAD',
                lastSyncTime INTEGER,
                version INTEGER NOT NULL DEFAULT 1,
                isDeleted INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
        database.execSQL("""
            INSERT INTO recipes_new (id, name, minAgeMonths, maxAgeMonths, ingredients, steps, nutrition, category, isBuiltIn, imageUrl, cloudId, syncStatus, lastSyncTime, version, isDeleted)
            SELECT id, name, minAgeMonths, maxAgeMonths, ingredients, steps, nutrition, category, isBuiltIn, imageUrl, cloudId,
                   COALESCE(syncStatus, 'PENDING_UPLOAD'),
                   lastSyncTime,
                   COALESCE(version, 1),
                   COALESCE(isDeleted, 0)
            FROM recipes
        """.trimIndent())
        database.execSQL("DROP TABLE recipes")
        database.execSQL("ALTER TABLE recipes_new RENAME TO recipes")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_recipes_cloudId ON recipes(cloudId)")
    }
}

// 数据库迁移：从版本 6 到版本 7
// 添加宝宝头像字段
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 为 babies 表添加 avatarUrl 字段
        database.execSQL("ALTER TABLE babies ADD COLUMN avatarUrl TEXT")
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
    version = 7,  // 升级到版本 7（添加宝宝头像）
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