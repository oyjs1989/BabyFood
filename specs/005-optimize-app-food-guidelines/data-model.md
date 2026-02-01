# Data Model: 优化辅食选择功能 - 基于权威营养指南

**Feature**: 005-optimize-app-food-guidelines
**Date**: 2026-01-31
**Status**: Complete
**Database Version**: 14 → 15

## Overview

本文档定义了"优化辅食选择功能"所需的所有数据模型，包括实体、关系、验证规则和状态转换。基于现有BabyFood数据模型，扩展新的实体以支持营养推荐、安全预警、质地适配等功能。

## Entity Relationships

```
Baby (现有)
  ├── NutritionGoal (新增) - 1对1
  ├── IngredientTrial (新增) - 1对多
  └── Plan (现有) - 1对多
      └── Recipe (现有) - 多对多
          ├── NutritionData (新增) - 多对1
          └── SafetyRisk (新增) - 多对多

SafetyRisk (新增，全局只读表)
  └── Ingredient (新增) - 1对多

Ingredient (新增，全局只读表)
  ├── NutritionData (新增) - 1对1
  └── SafetyRisk (新增) - 1对1
```

## Entities

### 1. SafetyRisk (安全风险)

**描述**: 定义食材的安全风险等级和处理建议，用于安全预警系统。

**表名**: `safety_risks`
**数据类型**: 只读表（初始化数据，用户不可修改）

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | PRIMARY KEY AUTOINCREMENT |
| ingredientName | String | 食材名称（标准化） | NOT NULL, UNIQUE |
| riskLevel | String | 风险等级 | NOT NULL, CHECK(riskLevel IN ('FORBIDDEN', 'NOT_RECOMMENDED', 'REQUIRES_SPECIAL_HANDLING', 'CAUTIOUS_INTRODUCTION', 'NORMAL')) |
| riskReason | String | 风险原因 | NOT NULL |
| handlingAdvice | String? | 处理建议 | NULL |
| applicableAgeRangeStart | Int? | 适用月龄范围起始 | NULL |
| applicableAgeRangeEnd | Int? | 适用月龄范围结束 | NULL |
| severity | Int | 严重程度（1-10） | NOT NULL, CHECK(severity BETWEEN 1 AND 10) |
| dataSource | String | 数据来源 | NOT NULL, CHECK(dataSource IN ('WHO', '中国营养学会', 'AAP')) |
| createdAt | Long | 创建时间戳 | NOT NULL |

**索引**:
- `idx_ingredient_name`: ingredientName

**示例数据**:
```kotlin
SafetyRisk(
    ingredientName = "蜂蜜",
    riskLevel = "FORBIDDEN",
    riskReason = "含肉毒杆菌芽孢，1岁内禁用",
    handlingAdvice = null,
    applicableAgeRangeStart = 0,
    applicableAgeRangeEnd = 12,
    severity = 10,
    dataSource = "中国营养学会"
)
```

---

### 2. IngredientTrial (食材尝试记录)

**描述**: 记录宝宝尝试过的食材及反应，用于追踪味觉发育和预防挑食。

**表名**: `ingredient_trials`

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | PRIMARY KEY AUTOINCREMENT |
| babyId | Long | 宝宝ID | NOT NULL, FOREIGN KEY REFERENCES babies(id) |
| ingredientName | String | 食材名称 | NOT NULL |
| trialDate | LocalDate | 尝试日期 | NOT NULL |
| isAllergic | Boolean | 是否过敏 | NOT NULL, DEFAULT false |
| reactionNotes | String? | 反应备注 | NULL |
| preference | String? | 偏好程度 | CHECK(preference IN ('LIKE', 'NEUTRAL', 'DISLIKE')) |
| createdAt | Long | 创建时间戳 | NOT NULL |
| updatedAt | Long | 更新时间戳 | NOT NULL |

**索引**:
- `idx_baby_ingredient`: babyId, ingredientName
- `idx_trial_date`: babyId, trialDate

**示例数据**:
```kotlin
IngredientTrial(
    babyId = 1L,
    ingredientName = "胡萝卜",
    trialDate = LocalDate.of(2026, 1, 15),
    isAllergic = false,
    reactionNotes = null,
    preference = "LIKE"
)
```

---

### 3. NutritionGoal (营养目标)

**描述**: 定义宝宝的每日营养目标，支持月龄自动生成和个体差异调整。

**表名**: `nutrition_goals`

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | PRIMARY KEY AUTOINCREMENT |
| babyId | Long | 宝宝ID | NOT NULL, UNIQUE, FOREIGN KEY REFERENCES babies(id) |
| ageInMonths | Int | 月龄 | NOT NULL |
| iron | Float | 铁 (mg/天) | NOT NULL, CHECK(iron > 0) |
| zinc | Float | 锌 (mg/天) | NOT NULL, CHECK(zinc > 0) |
| vitaminA | Float | 维生素A (μg/天) | NOT NULL, CHECK(vitaminA > 0) |
| calcium | Float | 钙 (mg/天) | NOT NULL, CHECK(calcium > 0) |
| vitaminC | Float | 维生素C (mg/天) | NOT NULL, CHECK(vitaminC > 0) |
| weightAdjustment | Float | 体重调整因子 | DEFAULT 0.0, CHECK(weightAdjustment >= -0.1 AND weightAdjustment <= 0.2) |
| healthAdjustment | Float | 健康状况调整因子 | DEFAULT 0.0, CHECK(healthAdjustment >= -0.1 AND healthAdjustment <= 0.2) |
| isCustom | Boolean | 是否用户自定义 | NOT NULL, DEFAULT false |
| lastUpdated | LocalDate | 最后更新日期 | NOT NULL |
| createdAt | Long | 创建时间戳 | NOT NULL |
| updatedAt | Long | 更新时间戳 | NOT NULL |

**索引**:
- `idx_baby_id`: babyId

**示例数据**:
```kotlin
NutritionGoal(
    babyId = 1L,
    ageInMonths = 12,
    iron = 12f,
    zinc = 4f,
    vitaminA = 400f,
    calcium = 600f,
    vitaminC = 50f,
    weightAdjustment = 0.0f,
    healthAdjustment = 0.0f,
    isCustom = false,
    lastUpdated = LocalDate.of(2026, 1, 31)
)
```

---

### 4. NutritionData (营养数据)

**描述**: 权威营养数据库的食材营养信息，用于营养分析和推荐。

**表名**: `nutrition_data`
**数据类型**: 只读表（初始化数据，用户不可修改）

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | PRIMARY KEY AUTOINCREMENT |
| name | String | 食材名称（标准化） | NOT NULL, UNIQUE |
| aliases | String | 同义词列表（JSON数组） | NOT NULL |
| iron | Float | 铁 (mg/100g) | NOT NULL, CHECK(iron >= 0) |
| zinc | Float | 锌 (mg/100g) | NOT NULL, CHECK(zinc >= 0) |
| vitaminA | Float | 维生素A (μg/100g) | NOT NULL, CHECK(vitaminA >= 0) |
| calcium | Float | 钙 (mg/100g) | NOT NULL, CHECK(calcium >= 0) |
| vitaminC | Float | 维生素C (mg/100g) | NOT NULL, CHECK(vitaminC >= 0) |
| protein | Float | 蛋白质 (g/100g) | NOT NULL, CHECK(protein >= 0) |
| category | String | 食材分类 | NOT NULL, CHECK(category IN ('GRAIN', 'MEAT', 'VEGETABLE', 'FRUIT', 'OTHER')) |
| dataSource | String | 数据来源 | NOT NULL |
| lastUpdated | LocalDate | 最后更新日期 | NOT NULL |
| createdAt | Long | 创建时间戳 | NOT NULL |

**索引**:
- `idx_name`: name
- `idx_category`: category

**示例数据**:
```kotlin
NutritionData(
    name = "牛肉",
    aliases = '["牛肉", "牛腱子", "牛里脊"]',
    iron = 2.6f,
    zinc = 4.8f,
    vitaminA = 15f,
    calcium = 23f,
    vitaminC = 0f,
    protein = 20.2f,
    category = "MEAT",
    dataSource = "中国食物成分表",
    lastUpdated = LocalDate.of(2026, 1, 31)
)
```

---

### 5. UserWarningIgnore (用户警告忽略记录)

**描述**: 记录用户忽略安全警告的行为，用于后续加强提醒。

**表名**: `user_warning_ignores`

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | PRIMARY KEY AUTOINCREMENT |
| userId | Long | 用户ID | NOT NULL, FOREIGN KEY REFERENCES users(id) |
| warningType | String | 警告类型 | NOT NULL |
| ingredientName | String | 食材名称 | NOT NULL |
| ignoreCount | Int | 忽略次数 | NOT NULL, DEFAULT 1 |
| lastIgnoredAt | LocalDate | 最后忽略日期 | NOT NULL |
| createdAt | Long | 创建时间戳 | NOT NULL |
| updatedAt | Long | 更新时间戳 | NOT NULL |

**索引**:
- `idx_user_warning`: userId, warningType, ingredientName

**示例数据**:
```kotlin
UserWarningIgnore(
    userId = 1L,
    warningType = "FORBIDDEN",
    ingredientName = "蜂蜜",
    ignoreCount = 2,
    lastIgnoredAt = LocalDate.of(2026, 1, 20)
)
```

---

## Modified Entities

### Recipe (食谱) - 扩展

**新增字段**:

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| textureType | String | 质地类型 | CHECK(textureType IN ('PUREE', 'THICK_PUREE', 'SOFT_SOLID', 'FAMILY_MEAL')) |
| isIronRich | Boolean | 是否富铁 | DEFAULT false |
| safetyRiskIds | String | 安全风险ID列表（JSON数组） | NULL |
| nutritionDataId | Long? | 营养数据ID | FOREIGN KEY REFERENCES nutrition_data(id) |

**迁移说明**: 在MIGRATION_14_15中添加这些字段。

---

### Baby (宝宝) - 扩展

**新增字段**:

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| chewingAbility | String | 咀嚼能力 | CHECK(chewingAbility IN ('NORMAL', 'STRONG', 'WEAK')) |
| nutritionGoalId | Long | 营养目标ID | FOREIGN KEY REFERENCES nutrition_goals(id) |

**迁移说明**: 在MIGRATION_14_15中添加这些字段。

---

## Validation Rules

### SafetyRisk

1. **riskLevel 必须是有效值**:
   - FORBIDDEN: 绝对禁用
   - NOT_RECOMMENDED: 不推荐
   - REQUIRES_SPECIAL_HANDLING: 需特殊处理
   - CAUTIOUS_INTRODUCTION: 需谨慎引入
   - NORMAL: 正常

2. **applicableAgeRange 必须有效**:
   - start 必须 <= end
   - start 必须 >= 0
   - end 必须 <= 24（最大月龄）

3. **severity 必须在 1-10 范围内**:
   - 1-3: 轻微风险
   - 4-6: 中等风险
   - 7-10: 严重风险

### IngredientTrial

1. **trialDate 不能是未来日期**:
   - trialDate <= 当前日期

2. **preference 不能与 isAllergic 冲突**:
   - 如果 isAllergic = true，则 preference 必须为 null

### NutritionGoal

1. **营养值必须为正数**:
   - iron, zinc, vitaminA, calcium, vitaminC > 0

2. **调整因子必须在合理范围内**:
   - weightAdjustment: [-0.1, 0.2]
   - healthAdjustment: [-0.1, 0.2]

3. **月龄必须在 6-24 范围内**:
   - ageInMonths >= 6
   - ageInMonths <= 24

### Recipe

1. **textureType 必须与适用月龄匹配**:
   - PUREE: 6-8个月
   - THICK_PUREE: 9-11个月
   - SOFT_SOLID: 12-18个月
   - FAMILY_MEAL: 18+个月

2. **isIronRich 必须基于实际铁含量**:
   - 如果 ironContent >= 6mg/100g，则 isIronRich = true
   - 否则 isIronRich = false

---

## State Transitions

### NutritionGoal 状态转换

```
[自动生成] → [用户调整] → [手动更新]
    ↓            ↓            ↓
  每月更新     用户编辑      用户编辑
```

**触发条件**:
1. **自动生成**: 宝宝创建时根据月龄自动生成
2. **每月更新**: 每月1日检查月龄变化，自动更新营养目标
3. **用户调整**: 用户在宝宝档案中手动编辑营养目标
4. **手动更新**: 用户在营养目标页面手动更新

**状态属性**:
- `isCustom`: 标记是否用户自定义
- `lastUpdated`: 记录最后更新时间

### IngredientTrial 状态转换

```
[首次尝试] → [观察反应] → [确认结果]
    ↓            ↓            ↓
  记录日期    2-3天后     标记偏好/过敏
```

**触发条件**:
1. **首次尝试**: 用户首次添加食材到食谱或记录尝试
2. **观察反应**: 系统提示用户观察2-3天
3. **确认结果**: 用户确认是否过敏和偏好程度

**状态属性**:
- `isAllergic`: 是否过敏
- `preference`: 偏好程度（LIKE/NEUTRAL/DISLIKE）

---

## Database Migration

### MIGRATION_14_15

**迁移类型**: 数据库结构扩展

**操作**:

1. **创建新表**:
```sql
CREATE TABLE safety_risks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ingredientName TEXT NOT NULL UNIQUE,
    riskLevel TEXT NOT NULL CHECK(riskLevel IN ('FORBIDDEN', 'NOT_RECOMMENDED', 'REQUIRES_SPECIAL_HANDLING', 'CAUTIOUS_INTRODUCTION', 'NORMAL')),
    riskReason TEXT NOT NULL,
    handlingAdvice TEXT,
    applicableAgeRangeStart INTEGER,
    applicableAgeRangeEnd INTEGER,
    severity INTEGER NOT NULL CHECK(severity BETWEEN 1 AND 10),
    dataSource TEXT NOT NULL CHECK(dataSource IN ('WHO', '中国营养学会', 'AAP')),
    createdAt INTEGER NOT NULL
);

CREATE INDEX idx_ingredient_name ON safety_risks(ingredientName);

CREATE TABLE ingredient_trials (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    babyId INTEGER NOT NULL,
    ingredientName TEXT NOT NULL,
    trialDate INTEGER NOT NULL,
    isAllergic INTEGER NOT NULL DEFAULT 0,
    reactionNotes TEXT,
    preference TEXT CHECK(preference IN ('LIKE', 'NEUTRAL', 'DISLIKE')),
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL,
    FOREIGN KEY (babyId) REFERENCES babies(id) ON DELETE CASCADE
);

CREATE INDEX idx_baby_ingredient ON ingredient_trials(babyId, ingredientName);
CREATE INDEX idx_trial_date ON ingredient_trials(babyId, trialDate);

CREATE TABLE nutrition_goals (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    babyId INTEGER NOT NULL UNIQUE,
    ageInMonths INTEGER NOT NULL,
    iron REAL NOT NULL CHECK(iron > 0),
    zinc REAL NOT NULL CHECK(zinc > 0),
    vitaminA REAL NOT NULL CHECK(vitaminA > 0),
    calcium REAL NOT NULL CHECK(calcium > 0),
    vitaminC REAL NOT NULL CHECK(vitaminC > 0),
    weightAdjustment REAL DEFAULT 0.0 CHECK(weightAdjustment >= -0.1 AND weightAdjustment <= 0.2),
    healthAdjustment REAL DEFAULT 0.0 CHECK(healthAdjustment >= -0.1 AND healthAdjustment <= 0.2),
    isCustom INTEGER NOT NULL DEFAULT 0,
    lastUpdated INTEGER NOT NULL,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL,
    FOREIGN KEY (babyId) REFERENCES babies(id) ON DELETE CASCADE
);

CREATE INDEX idx_baby_id ON nutrition_goals(babyId);

CREATE TABLE nutrition_data (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    aliases TEXT NOT NULL,
    iron REAL NOT NULL CHECK(iron >= 0),
    zinc REAL NOT NULL CHECK(zinc >= 0),
    vitaminA REAL NOT NULL CHECK(vitaminA >= 0),
    calcium REAL NOT NULL CHECK(calcium >= 0),
    vitaminC REAL NOT NULL CHECK(vitaminC >= 0),
    protein REAL NOT NULL CHECK(protein >= 0),
    category TEXT NOT NULL CHECK(category IN ('GRAIN', 'MEAT', 'VEGETABLE', 'FRUIT', 'OTHER')),
    dataSource TEXT NOT NULL,
    lastUpdated INTEGER NOT NULL,
    createdAt INTEGER NOT NULL
);

CREATE INDEX idx_name ON nutrition_data(name);
CREATE INDEX idx_category ON nutrition_data(category);

CREATE TABLE user_warning_ignores (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userId INTEGER NOT NULL,
    warningType TEXT NOT NULL,
    ingredientName TEXT NOT NULL,
    ignoreCount INTEGER NOT NULL DEFAULT 1,
    lastIgnoredAt INTEGER NOT NULL,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL,
    FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_warning ON user_warning_ignores(userId, warningType, ingredientName);
```

2. **扩展现有表**:
```sql
ALTER TABLE recipes ADD COLUMN textureType TEXT CHECK(textureType IN ('PUREE', 'THICK_PUREE', 'SOFT_SOLID', 'FAMILY_MEAL'));
ALTER TABLE recipes ADD COLUMN isIronRich INTEGER DEFAULT 0;
ALTER TABLE recipes ADD COLUMN safetyRiskIds TEXT;
ALTER TABLE recipes ADD COLUMN nutritionDataId INTEGER REFERENCES nutrition_data(id);

ALTER TABLE babies ADD COLUMN chewingAbility TEXT CHECK(chewingAbility IN ('NORMAL', 'STRONG', 'WEAK'));
ALTER TABLE babies ADD COLUMN nutritionGoalId INTEGER REFERENCES nutrition_goals(id);
```

3. **初始化数据**:
```sql
-- 初始化安全风险数据（200+条）
INSERT INTO safety_risks (ingredientName, riskLevel, riskReason, handlingAdvice, applicableAgeRangeStart, applicableAgeRangeEnd, severity, dataSource, createdAt) VALUES
('蜂蜜', 'FORBIDDEN', '含肉毒杆菌芽孢，1岁内禁用', NULL, 0, 12, 10, '中国营养学会', 1738368000000),
('鲨鱼', 'FORBIDDEN', '高汞鱼类，影响神经发育', NULL, 0, 24, 10, '中国营养学会', 1738368000000),
-- ... 更多数据

-- 初始化营养数据（2000+条）
INSERT INTO nutrition_data (name, aliases, iron, zinc, vitaminA, calcium, vitaminC, protein, category, dataSource, lastUpdated, createdAt) VALUES
('牛肉', '["牛肉", "牛腱子", "牛里脊"]', 2.6, 4.8, 15, 23, 0, 20.2, 'MEAT', '中国食物成分表', 1738368000000, 1738368000000),
-- ... 更多数据
```

---

## Data Access Objects (DAOs)

### SafetyRiskDao

```kotlin
@Dao
interface SafetyRiskDao {
    @Query("SELECT * FROM safety_risks WHERE ingredientName = :name")
    suspend fun getByName(name: String): SafetyRisk?

    @Query("SELECT * FROM safety_risks WHERE riskLevel = :level")
    suspend fun getByLevel(level: String): List<SafetyRisk>

    @Query("SELECT * FROM safety_risks WHERE :age >= applicableAgeRangeStart AND (:age <= applicableAgeRangeEnd OR applicableAgeRangeEnd IS NULL)")
    suspend fun getApplicableRisks(age: Int): List<SafetyRisk>

    @Query("SELECT * FROM safety_risks WHERE ingredientName IN (:names)")
    suspend fun getByNames(names: List<String>): List<SafetyRisk>
}
```

### IngredientTrialDao

```kotlin
@Dao
interface IngredientTrialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trial: IngredientTrial): Long

    @Update
    suspend fun update(trial: IngredientTrial)

    @Query("SELECT * FROM ingredient_trials WHERE babyId = :babyId ORDER BY trialDate DESC")
    fun getByBabyId(babyId: Long): Flow<List<IngredientTrial>>

    @Query("SELECT * FROM ingredient_trials WHERE babyId = :babyId AND ingredientName = :ingredientName")
    suspend fun getByBabyAndIngredient(babyId: Long, ingredientName: String): IngredientTrial?

    @Query("SELECT DISTINCT ingredientName FROM ingredient_trials WHERE babyId = :babyId")
    suspend fun getTriedIngredients(babyId: Long): List<String>
}
```

### NutritionGoalDao

```kotlin
@Dao
interface NutritionGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: NutritionGoal): Long

    @Update
    suspend fun update(goal: NutritionGoal)

    @Query("SELECT * FROM nutrition_goals WHERE babyId = :babyId")
    suspend fun getByBabyId(babyId: Long): NutritionGoal?

    @Query("SELECT * FROM nutrition_goals WHERE babyId = :babyId AND isCustom = 0")
    suspend fun getDefaultGoal(babyId: Long): NutritionGoal?
}
```

### NutritionDataDao

```kotlin
@Dao
interface NutritionDataDao {
    @Query("SELECT * FROM nutrition_data WHERE name = :name")
    suspend fun getByName(name: String): NutritionData?

    @Query("SELECT * FROM nutrition_data WHERE name LIKE '%' || :query || '%'")
    suspend fun searchByName(query: String): List<NutritionData>

    @Query("SELECT * FROM nutrition_data WHERE category = :category")
    suspend fun getByCategory(category: String): List<NutritionData>

    @Query("SELECT * FROM nutrition_data WHERE iron >= :threshold")
    suspend fun getIronRich(threshold: Float = 6f): List<NutritionData>
}
```

---

## Summary

### 新增实体数量
- 5个新实体（SafetyRisk, IngredientTrial, NutritionGoal, NutritionData, UserWarningIgnore）

### 修改实体数量
- 2个现有实体（Recipe, Baby）

### 数据库版本
- 从14升级到15

### 初始化数据量
- SafetyRisk: 200+条
- NutritionData: 2000+条

### 关键特性
- 支持离线模式（所有数据本地存储）
- 支持自动更新（营养目标每月更新）
- 支持用户自定义（营养目标、咀嚼能力）
- 支持安全预警（5级风险分类）
- 支持营养分析（5种核心营养素）