package com.example.babyfood.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.TypeConverters
import com.example.babyfood.data.local.database.dao.BabyDao
import com.example.babyfood.data.local.database.dao.GrowthRecordDao
import com.example.babyfood.data.local.database.dao.HealthRecordDao
import com.example.babyfood.data.local.database.dao.IngredientTrialDao
import com.example.babyfood.data.local.database.dao.InventoryItemDao
import com.example.babyfood.data.local.database.dao.NutritionDataDao
import com.example.babyfood.data.local.database.dao.NutritionGoalDao
import com.example.babyfood.data.local.database.dao.PlanDao
import com.example.babyfood.data.local.database.dao.RecipeDao
import com.example.babyfood.data.local.database.dao.SafetyRiskDao
import com.example.babyfood.data.local.database.dao.UserDao
import com.example.babyfood.data.local.database.dao.UserWarningIgnoreDao
import com.example.babyfood.data.local.database.entity.BabyEntity
import com.example.babyfood.data.local.database.entity.GrowthRecordEntity
import com.example.babyfood.data.local.database.entity.HealthRecordEntity
import com.example.babyfood.data.local.database.entity.IngredientTrialEntity
import com.example.babyfood.data.local.database.entity.InventoryItemEntity
import com.example.babyfood.data.local.database.entity.NutritionDataEntity
import com.example.babyfood.data.local.database.entity.NutritionGoalEntity
import com.example.babyfood.data.local.database.entity.PlanEntity
import com.example.babyfood.data.local.database.entity.RecipeEntity
import com.example.babyfood.data.local.database.entity.SafetyRiskEntity
import com.example.babyfood.data.local.database.entity.UserEntity
import com.example.babyfood.data.local.database.entity.UserWarningIgnoreEntity

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

// 数据库迁移：从版本 7 到版本 8
// 添加用户表，支持登录功能
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 创建 users 表（不使用索引，因为 Room 实体类中没有定义索引）
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                phone TEXT,
                email TEXT,
                nickname TEXT NOT NULL,
                avatar TEXT,
                createdAt TEXT NOT NULL,
                updatedAt TEXT NOT NULL,
                isEmailVerified INTEGER NOT NULL,
                isPhoneVerified INTEGER NOT NULL,
                isLoggedIn INTEGER NOT NULL,
                lastLoginTime TEXT
            )
        """.trimIndent())
    }
}

// 数据库迁移：从版本 8 到版本 9
// 修复 users 表的结构问题（删除索引，重建表以匹配 UserEntity 定义）
val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 1. 创建新的 users 表（不带索引）
        database.execSQL("""
            CREATE TABLE users_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                phone TEXT,
                email TEXT,
                nickname TEXT NOT NULL,
                avatar TEXT,
                createdAt TEXT NOT NULL,
                updatedAt TEXT NOT NULL,
                isEmailVerified INTEGER NOT NULL,
                isPhoneVerified INTEGER NOT NULL,
                isLoggedIn INTEGER NOT NULL,
                lastLoginTime TEXT
            )
        """.trimIndent())

        // 2. 迁移数据
        database.execSQL("""
            INSERT INTO users_new (id, phone, email, nickname, avatar, createdAt, updatedAt, isEmailVerified, isPhoneVerified, isLoggedIn, lastLoginTime)
            SELECT id, phone, email, nickname, avatar, createdAt, updatedAt, isEmailVerified, isPhoneVerified, isLoggedIn, lastLoginTime
            FROM users
        """.trimIndent())

        // 3. 删除旧表
        database.execSQL("DROP TABLE users")

        // 4. 重命名新表
        database.execSQL("ALTER TABLE users_new RENAME TO users")
    }
}

// 数据库迁移：从版本 9 到版本 10
// 为 recipes 表添加 cookingTime 字段
val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 为 recipes 表添加 cookingTime 字段
        database.execSQL("ALTER TABLE recipes ADD COLUMN cookingTime INTEGER")
    }
}

// 数据库迁移：从版本 10 到版本 11
// 为 plans 表添加 mealTime 字段
val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 为 plans 表添加 mealTime 字段
        database.execSQL("ALTER TABLE plans ADD COLUMN mealTime TEXT")
    }
}

// 数据库迁移：从版本 11 到版本 12
// 为 plans 表添加反馈相关字段（feedbackStatus 和 feedbackTime）
val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 为 plans 表添加 feedbackStatus 字段
        database.execSQL("ALTER TABLE plans ADD COLUMN feedbackStatus TEXT")

        // 为 plans 表添加 feedbackTime 字段
        database.execSQL("ALTER TABLE plans ADD COLUMN feedbackTime TEXT")
    }
}

// 数据库迁移：从版本 12 到版本 13
// 更新 babies 表的 allergies 和 preferences 字段结构（由于使用了 JSON 序列化，这个迁移是版本升级标记）
// 实际数据结构变化通过 TypeConverters 处理，addedDate 字段在新的 AllergyItem 和 PreferenceItem 中
val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 由于 allergies 和 preferences 字段使用 JSON 序列化存储，
        // 数据结构的变化由 TypeConverters 在读写时自动处理
        // 新的 AllergyItem 和 PreferenceItem 添加了 addedDate 字段，
        // 旧数据反序列化时 addedDate 会为 null，新数据会有值
        // 这里不需要修改数据库结构，只是版本升级
    }
}

// 数据库迁移：从版本 13 到版本 14
// 添加 inventory_items 表，支持仓库功能
val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 创建 inventory_items 表
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS inventory_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                foodId INTEGER NOT NULL,
                foodName TEXT NOT NULL,
                foodImageUrl TEXT,
                productionDate TEXT NOT NULL,
                expiryDate TEXT NOT NULL,
                storageMethod TEXT NOT NULL,
                quantity REAL NOT NULL,
                unit TEXT NOT NULL,
                addedAt TEXT NOT NULL,
                notes TEXT,
                cloudId TEXT,
                syncStatus TEXT NOT NULL DEFAULT 'LOCAL_ONLY',
                lastSyncTime INTEGER,
                version INTEGER NOT NULL DEFAULT 1,
                isDeleted INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        // 创建索引
        database.execSQL("CREATE INDEX IF NOT EXISTS index_inventory_items_foodId ON inventory_items(foodId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_inventory_items_expiryDate ON inventory_items(expiryDate)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_inventory_items_cloudId ON inventory_items(cloudId)")
    }
}

// 数据库迁移：从版本 14 到版本 15
val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 1. 创建 safety_risks 表
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS safety_risks (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ingredientName TEXT NOT NULL,
                riskLevel TEXT NOT NULL,
                riskReason TEXT NOT NULL,
                handlingAdvice TEXT,
                applicableAgeRangeStart INTEGER,
                applicableAgeRangeEnd INTEGER,
                severity INTEGER NOT NULL,
                dataSource TEXT NOT NULL,
                createdAt INTEGER NOT NULL
            )
        """.trimIndent()
        )

        // 2. 创建 ingredient_trials 表
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS ingredient_trials (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                babyId INTEGER NOT NULL,
                ingredientName TEXT NOT NULL,
                trialDate INTEGER NOT NULL,
                isAllergic INTEGER NOT NULL DEFAULT 0,
                reaction TEXT,
                FOREIGN KEY(babyId) REFERENCES babies(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        // 3. 创建 nutrition_goals 表
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS nutrition_goals (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                babyId INTEGER NOT NULL,
                calories REAL NOT NULL,
                protein REAL NOT NULL,
                calcium REAL NOT NULL,
                iron REAL NOT NULL,
                vitaminA REAL NOT NULL,
                vitaminC REAL NOT NULL,
                FOREIGN KEY(babyId) REFERENCES babies(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        // 4. 创建 nutrition_data 表
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS nutrition_data (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ingredientName TEXT NOT NULL,
                ironContent REAL NOT NULL,
                zincContent REAL NOT NULL,
                vitaminAContent REAL NOT NULL,
                calciumContent REAL NOT NULL,
                vitaminCContent REAL NOT NULL
            )
            """.trimIndent()
        )

        // 5. 创建 user_warning_ignores 表
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS user_warning_ignores (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                userId INTEGER NOT NULL,
                ingredientName TEXT NOT NULL,
                warningType TEXT NOT NULL,
                ignoreDate INTEGER NOT NULL,
                ignoreCount INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        // 6. 扩展 recipes 表
        database.execSQL("ALTER TABLE recipes ADD COLUMN textureType TEXT")
        database.execSQL("ALTER TABLE recipes ADD COLUMN isIronRich INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE recipes ADD COLUMN ironContent REAL")
        database.execSQL("ALTER TABLE recipes ADD COLUMN riskLevelList TEXT")
        database.execSQL("ALTER TABLE recipes ADD COLUMN safetyAdvice TEXT")

        // 7. 扩展 babies 表
        database.execSQL("ALTER TABLE babies ADD COLUMN chewingAbility TEXT")
        database.execSQL("ALTER TABLE babies ADD COLUMN preferredTextureLevel INTEGER")

        // 创建索引
        database.execSQL("CREATE INDEX IF NOT EXISTS index_safety_risks_ingredientName ON safety_risks(ingredientName)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_ingredient_trials_babyId ON ingredient_trials(babyId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_nutrition_goals_babyId ON nutrition_goals(babyId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_nutrition_data_ingredientName ON nutrition_data(ingredientName)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_user_warning_ignores_userId ON user_warning_ignores(userId)")
    }
}

@Database(
    entities = [
        BabyEntity::class,
        RecipeEntity::class,
        PlanEntity::class,
        HealthRecordEntity::class,
        GrowthRecordEntity::class,
        UserEntity::class,
        InventoryItemEntity::class,
        SafetyRiskEntity::class,
        IngredientTrialEntity::class,
        NutritionGoalEntity::class,
        NutritionDataEntity::class,
        UserWarningIgnoreEntity::class
    ],
    version = 15,  // 升级到版本 15（优化辅食选择功能 - 营养指南）
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BabyFoodDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
    abstract fun recipeDao(): RecipeDao
    abstract fun planDao(): PlanDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun growthRecordDao(): GrowthRecordDao
    abstract fun userDao(): UserDao
    abstract fun inventoryItemDao(): InventoryItemDao
    abstract fun safetyRiskDao(): SafetyRiskDao
    abstract fun ingredientTrialDao(): IngredientTrialDao
    abstract fun nutritionGoalDao(): NutritionGoalDao
    abstract fun nutritionDataDao(): NutritionDataDao
    abstract fun userWarningIgnoreDao(): UserWarningIgnoreDao
}