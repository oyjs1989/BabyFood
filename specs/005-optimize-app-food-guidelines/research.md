# Research Document: 优化辅食选择功能 - 基于权威营养指南

**Feature**: 005-optimize-app-food-guidelines
**Date**: 2026-01-31
**Status**: Complete

## Overview

本文档记录了实施计划中Phase 0阶段的所有研究结果，用于解决技术上下文中的不确定性问题，为Phase 1设计阶段提供决策依据。

## Research Task 1: 营养数据库API集成

### 研究目标
确定如何获取权威营养数据库（如中国食物成分表）的食材营养数据。

### 调研结果

#### 可用的营养数据库选项

**选项1: 中国食物成分表（推荐）**
- **数据来源**: 中国疾病预防控制中心营养与健康所
- **覆盖范围**: 2000+种常见中国食材
- **营养素**: 铁、锌、维生素A、钙、维生素C等核心营养素
- **更新频率**: 每3-5年更新一次（最新版本：2022）
- **获取方式**:
  - 购买官方出版物（约¥200-500）
  - 使用第三方API服务（如NutritionAPI.cn，约¥0.01/次调用）
  - 开源数据集（GitHub上有部分数据，可能不完整）
- **优点**: 数据权威、符合中国饮食习惯、营养素完整
- **缺点**: 官方API访问受限、更新频率较低

**选项2: USDA FoodData Central**
- **数据来源**: 美国农业部
- **覆盖范围**: 350,000+种食物
- **营养素**: 150+种营养素
- **更新频率**: 每月更新
- **获取方式**: 免费REST API（需注册）
- **优点**: 数据量大、更新频繁、API免费
- **缺点**: 食材以美国为主、与中国饮食习惯差异大

**选项3: FatSecret API**
- **数据来源**: 全球社区贡献
- **覆盖范围**: 10,000,000+种食物
- **营养素**: 100+种营养素
- **更新频率**: 持续更新
- **获取方式**: 免费API（有调用限制）
- **优点**: 数据量大、免费、支持中文搜索
- **缺点**: 数据质量参差不齐、权威性不足

### 决策

**选择**: 中国食物成分表（官方数据集 + 本地SQLite数据库）

**理由**:
1. **权威性**: 中国疾控中心官方数据，符合国家营养标准
2. **相关性**: 食材以中国常见食物为主，匹配用户需求
3. **成本**: 一次性购买数据集（约¥300），后续无API调用成本
4. **性能**: 本地SQLite数据库，查询速度快，支持离线模式
5. **合规性**: 符合中国营养学会推荐标准

**数据获取方案**:
1. 购买《中国食物成分表（标准版）》电子版
2. 提取铁、锌、维生素A、钙、维生素C等核心营养素数据
3. 清洗数据，建立标准化食材名称库（支持同义词）
4. 导入到Room数据库作为只读参考表
5. 每季度检查官方更新，如有新版本则更新数据集

**数据结构**:
```kotlin
data class NutritionData(
    val id: Long,
    val name: String,              // 食材名称（标准化）
    val aliases: List<String>,     // 同义词列表
    val iron: Float,               // 铁 (mg/100g)
    val zinc: Float,               // 锌 (mg/100g)
    val vitaminA: Float,           // 维生素A (μg/100g)
    val calcium: Float,            // 钙 (mg/100g)
    val vitaminC: Float,           // 维生素C (mg/100g)
    val protein: Float,            // 蛋白质 (g/100g)
    val category: String,          // 食材分类（谷物/肉类/蔬菜/水果/其他）
    val dataSource: String,        // 数据来源
    val lastUpdated: LocalDate     // 最后更新日期
)
```

### 实现要点
- 使用Room @Entity创建只读表（nutrition_data）
- 建立食材名称搜索索引（支持模糊匹配）
- 提供NutritionService接口封装查询逻辑
- 缓存常用食材数据，提升查询性能

---

## Research Task 2: 安全风险食材分类体系

### 研究目标
建立完整的安全风险食材分类体系。

### 调研结果

#### 权威机构安全指南

**1. WHO婴幼儿喂养指南（2021）**
- 禁用：蜂蜜（肉毒杆菌）、高汞鱼类
- 需特殊处理：未煮熟的豆类、发芽土豆
- 注意事项：牛奶蛋白过敏、花生过敏

**2. 中国营养学会《0~2岁婴幼儿喂养指南》（2025）**
- 禁用：蜂蜜、高汞鱼类、未煮熟的四季豆、发芽土豆、苦杏仁
- 不推荐：白糖、普通酱油、含糖饮料
- 需特殊处理：菠菜（高硝酸盐）、芹菜（高硝酸盐）、鲜黄花菜（秋水仙碱）
- 建议：6月龄后引入富铁食物，12月龄前不加盐糖

**3. 美国儿科学会（AAP）**
- 禁用：蜂蜜、未经过巴氏杀菌的牛奶、高汞鱼类
- 需谨慎：花生（过敏风险）、鸡蛋（过敏风险）

#### 风险等级分类

**等级1: 绝对禁用（FORBIDDEN）**
- 定义：1岁内严格禁止，可能导致严重健康问题
- 食材清单：
  - 蜂蜜（肉毒杆菌芽孢）
  - 高汞鱼类（鲨鱼、剑鱼、大眼吞拿鱼）
  - 未经过巴氏杀菌的牛奶
  - 发芽或变绿的土豆（龙葵素）
  - 苦杏仁（氰苷）
  - 未煮熟的四季豆（皂苷、植物血凝素）
  - 未煮熟的豆浆（皂苷）
- 适用月龄：0-12个月
- 处理建议：完全禁止，不提供替代建议

**等级2: 不推荐（NOT_RECOMMENDED）**
- 定义：1岁内不推荐，可能影响健康或饮食习惯
- 食材清单：
  - 白糖（加重挑食风险、龋齿）
  - 普通酱油（钠含量过高）
  - 含糖饮料（增加肥胖风险）
  - 果汁（含糖量高，缺乏纤维）
- 适用月龄：0-12个月
- 处理建议：建议用天然食材替代（如番茄提鲜）

**等级3: 需特殊处理（REQUIRES_SPECIAL_HANDLING）**
- 定义：需要特定烹饪方法才能安全食用
- 食材清单：
  - 菠菜（高硝酸盐，需焯水）
  - 芹菜（高硝酸盐，需焯水）
  - 鲜黄花菜（秋水仙碱，需焯水后煮熟）
  - 四季豆（皂苷，需冷水下锅煮15分钟以上）
  - 豆浆（皂苷，需煮沸后再煮5分钟）
- 适用月龄：6个月以上
- 处理建议：在食谱详情页显示具体处理步骤

**等级4: 需谨慎引入（CAUTIOUS_INTRODUCTION）**
- 定义：常见过敏原，需逐个引入并观察反应
- 食材清单：
  - 鸡蛋
  - 牛奶
  - 花生
  - 坚果
  - 鱼类
  - 贝类
  - 大豆
  - 小麦
- 适用月龄：6个月以上
- 处理建议：每引入一种新食材观察2-3天，确认无过敏反应后再添加下一种

**等级5: 正常（NORMAL）**
- 定义：安全食材，无特殊限制
- 适用月龄：6个月以上
- 处理建议：无特殊要求

### 决策

**选择**: 采用5级风险分类体系

**理由**:
1. **全面性**: 涵盖所有类型的安全风险
2. **清晰性**: 每个等级有明确的定义和处理建议
3. **可操作性**: 用户提供明确的指导
4. **灵活性**: 支持月龄相关的风险规则

**数据结构**:
```kotlin
data class SafetyRisk(
    val id: Long,
    val ingredientName: String,      // 食材名称
    val riskLevel: RiskLevel,        // 风险等级
    val riskReason: String,          // 风险原因
    val handlingAdvice: String?,     // 处理建议（如有）
    val applicableAgeRange: IntRange?, // 适用月龄范围
    val severity: Int,               // 严重程度（1-10）
    val dataSource: String           // 数据来源（WHO/中国营养学会/AAP）
)

enum class RiskLevel {
    FORBIDDEN,              // 绝对禁用
    NOT_RECOMMENDED,        // 不推荐
    REQUIRES_SPECIAL_HANDLING, // 需特殊处理
    CAUTIOUS_INTRODUCTION,  // 需谨慎引入
    NORMAL                  // 正常
}
```

### 实现要点
- 初始化200+种常见食材的风险数据
- 提供食材风险查询API
- 在食谱卡片上显示风险标识（红色/黄色/蓝色）
- 在食谱详情页显示风险原因和处理建议
- 记录用户忽略警告的行为

---

## Research Task 3: 质地分类标准实现

### 研究目标
量化不同质地的特征和适用月龄。

### 调研结果

#### 国标要求

**GB 10770-2025《罐装婴幼儿辅助食品》**
- 6-12月龄：颗粒尺寸 < 5毫米
- 12月龄以上：颗粒尺寸 < 10毫米（1厘米）

**WS/T 678—2020《婴幼儿辅食添加营养指南》**
- 泥糊状：可用舌头压碎，质地如软豆腐
- 稠泥/末状：可用牙床压碎，质地如香蕉
- 软固体/块状：可用牙床咀嚼，质地如肉丸子

#### 咀嚼能力发育阶段

**阶段1: 纯奶喂养期（0-6个月）**
- 特征：只有吸吮反射，无咀嚼能力
- 质地要求：无

**阶段2: 辅食引入期（6-8个月）**
- 特征：挺舌反射消失，学会吞咽半固体食物
- 质地要求：泥糊状（可压碎，如软豆腐）
- 颗粒尺寸：< 5毫米

**阶段3: 咀嚼发育期（9-11个月）**
- 特征：牙床发育，开始尝试咀嚼动作
- 质地要求：稠泥/末状（可用牙床压碎，如香蕉）
- 颗粒尺寸：5-8毫米

**阶段4: 咀嚼成熟期（12-18个月）**
- 特征：牙齿萌出，咀嚼能力增强
- 质地要求：软固体/块状（可咀嚼，如肉丸子）
- 颗粒尺寸：8-10毫米

**阶段5: 家庭膳食期（18个月以上）**
- 特征：咀嚼能力接近成人
- 质地要求：家庭膳食（可适当调整）
- 颗粒尺寸：无限制（根据能力调整）

### 决策

**选择**: 采用4级质地分类 + 5个发育阶段

**理由**:
1. **科学性**: 基于国标和权威指南
2. **实用性**: 质地分类清晰，易于用户理解
3. **灵活性**: 支持用户根据宝宝实际情况调整
4. **可量化**: 颗粒尺寸有明确数值范围

**质地分类标准**:
```kotlin
data class Texture(
    val id: Long,
    val name: String,              // 质地名称
    val description: String,       // 描述
    val particleSize: Float?,      // 颗粒尺寸（毫米）
    val chewingAbility: String,    // 咀嚼能力要求
    val example: String,           // 示例食物
    val applicableAgeRange: IntRange // 适用月龄范围
)

enum class TextureType {
    PUREE,              // 泥糊状（6-8个月）
    THICK_PUREE,        // 稠泥/末状（9-11个月）
    SOFT_SOLID,         // 软固体/块状（12-18个月）
    FAMILY_MEAL         // 家庭膳食（18个月以上）
}
```

**月龄-质地映射表**:
| 月龄 | 质地类型 | 颗粒尺寸 | 咀嚼能力 | 示例 |
|------|---------|---------|---------|------|
| 6-8 | 泥糊状 | < 5mm | 舌头压碎 | 南瓜米糊、胡萝卜土豆泥 |
| 9-11 | 稠泥/末状 | 5-8mm | 牙床压碎 | 鸡肉粥、牛肉土豆泥 |
| 12-18 | 软固体/块状 | 8-10mm | 牙床咀嚼 | 蔬菜肉丸、小馄饨 |
| 18+ | 家庭膳食 | 无限制 | 成人咀嚼 | 家庭常规菜肴 |

### 实现要点
- 在Recipe实体中添加textureType字段
- 根据宝宝月龄自动筛选食谱
- 在食谱详情页显示质地信息
- 支持用户在宝宝档案中备注咀嚼能力
- 边界值（如5个月、9个月）提供灵活调整选项

---

## Research Task 4: 营养目标计算算法

### 研究目标
根据月龄和个体差异计算个性化营养目标。

### 调研结果

#### 中国营养学会推荐标准（2025）

**6-12个月**
- 铁：11mg/天
- 锌：3mg/天
- 维生素A：350μg/天
- 钙：250mg/天
- 维生素C：40mg/天

**12-24个月**
- 铁：12mg/天
- 锌：4mg/天
- 维生素A：400μg/天
- 钙：600mg/天
- 维生素C：50mg/天

#### 个体差异调整因子

**体重调整**
- 标准体重（6个月）：7.0kg（男）/6.5kg（女）
- 超重（>120%标准）：营养需求 +10%
- 体重不足（<80%标准）：营养需求 -5%

**健康状况调整**
- 消化不良：蛋白质需求 -10%
- 生长发育迟缓：蛋白质需求 +15%，铁需求 +10%
- 缺铁性贫血：铁需求 +20%
- 钙缺乏：钙需求 +20%

#### 动态更新机制

**更新触发条件**:
1. 月龄变更（每月1日自动检查）
2. 体重数据更新（从生长记录获取）
3. 健康状况变化（从体检记录获取）
4. 用户手动调整

**计算公式**:
```
基础需求 = 中国营养学会标准值
调整后需求 = 基础需求 × (1 + 体重调整因子 + 健康状况调整因子)
```

### 决策

**选择**: 基于中国营养学会标准 + 个体差异调整

**理由**:
1. **权威性**: 中国营养学会官方标准
2. **个性化**: 支持体重和健康状况调整
3. **动态性**: 自动根据月龄更新
4. **灵活性**: 用户可以手动微调

**数据结构**:
```kotlin
data class NutritionGoal(
    val id: Long,
    val babyId: Long,
    val ageInMonths: Int,
    val iron: Float,               // 铁 (mg/天)
    val zinc: Float,               // 锌 (mg/天)
    val vitaminA: Float,           // 维生素A (μg/天)
    val calcium: Float,            // 钙 (mg/天)
    val vitaminC: Float,           // 维生素C (mg/天)
    val weightAdjustment: Float,   // 体重调整因子
    val healthAdjustment: Float,   // 健康状况调整因子
    val isCustom: Boolean,         // 是否用户自定义
    val lastUpdated: LocalDate     // 最后更新日期
)
```

**月龄-营养目标映射表**:
```kotlin
val NUTRITION_GOALS_BY_AGE = mapOf(
    6 to NutritionGoal(iron = 11f, zinc = 3f, vitaminA = 350f, calcium = 250f, vitaminC = 40f),
    7 to NutritionGoal(iron = 11f, zinc = 3f, vitaminA = 350f, calcium = 250f, vitaminC = 40f),
    8 to NutritionGoal(iron = 11f, zinc = 3f, vitaminA = 350f, calcium = 250f, vitaminC = 40f),
    9 to NutritionGoal(iron = 11f, zinc = 3f, vitaminA = 350f, calcium = 250f, vitaminC = 40f),
    10 to NutritionGoal(iron = 11f, zinc = 3f, vitaminA = 350f, calcium = 250f, vitaminC = 40f),
    11 to NutritionGoal(iron = 11f, zinc = 3f, vitaminA = 350f, calcium = 250f, vitaminC = 40f),
    12 to NutritionGoal(iron = 12f, zinc = 4f, vitaminA = 400f, calcium = 600f, vitaminC = 50f),
    // ... 继续到24个月
)
```

### 实现要点
- 根据月龄自动生成默认营养目标
- 从生长记录获取体重数据计算调整因子
- 从体检记录获取健康状况数据
- 提供用户自定义营养目标功能
- 每月自动检查月龄变化并更新目标

---

## Research Task 5: AI推荐算法优化

### 研究目标
将六大辅食选择原则集成到现有AI推荐系统中。

### 调研结果

#### 现有RecommendationService架构

**当前实现**:
- MainModelStrategy: 主模型策略（生成周计划）
- CheapModelStrategy: 轻量模型策略（生成餐单文本）
- RuleEngine: 规则引擎（验证食谱和周计划）
- CandidateRecipeService: 候选食谱筛选

**推荐流程**:
1. 用户输入（babyId, constraints）
2. CandidateRecipeService筛选候选食谱
3. MainModelStrategy生成推荐结果
4. RuleEngine验证推荐结果
5. 返回推荐列表

#### 六大原则权重设计

**铁优先（权重: 0.30）**
- 核心营养素，第一刚性需求
- 计算方式：食谱铁含量 / 每日铁需求 × 30
- 示例：鸡肉粥（铁2mg/100g，需求11mg/天）→ 得分 = 2/11 × 30 = 5.45

**安全第一（权重: 0.25）**
- 绝对禁用食材：直接过滤
- 不推荐食材：扣20分
- 需特殊处理食材：扣10分
- 计算方式：基础分25 - 风险扣分

**质地适配（权重: 0.15）**
- 符合月龄质地：得15分
- 不符合月龄质地：得0分
- 边界情况：得7分

**原味多样化（权重: 0.10）**
- 新食材：得10分
- 已尝试食材：得5分
- 高频食材：得0分

**新鲜度（权重: 0.10）**
- 新鲜食材：得10分
- 冷冻食材：得7分
- 罐装食材：得5分

**自制与市售互补（权重: 0.10）**
- 6-9月龄：市售强化铁米粉优先（得10分）
- 10月龄以上：自制辅食优先（得10分）

### 决策

**选择**: 基于权重评分的推荐算法 + 奥卡姆剃刀原则

**理由**:
1. **科学性**: 基于权威指南的权重设计
2. **灵活性**: 支持动态调整权重
3. **兼容性**: 与现有奥卡姆剃刀原则兼容（AI优先，本地仅作兜底）
4. **可解释性**: 用户可以理解推荐理由

**优化后的推荐流程**:
1. 用户输入（babyId, constraints）
2. CandidateRecipeService筛选候选食谱（基于月龄、过敏、偏好）
3. **新步骤**: NutritionScorer计算六大原则得分
4. MainModelStrategy生成推荐结果（考虑营养得分）
5. RuleEngine验证推荐结果（安全风险、质地适配）
6. 返回推荐列表（包含推荐理由）

**营养评分算法**:
```kotlin
data class NutritionScore(
    val recipeId: Long,
    val ironPriority: Float,      // 铁优先得分
    val safetyScore: Float,       // 安全得分
    val textureScore: Float,      // 质地适配得分
    val diversityScore: Float,    // 多样性得分
    val freshnessScore: Float,    // 新鲜度得分
    val preparationScore: Float,  // 制作方式得分
    val totalScore: Float         // 总分
)

fun calculateNutritionScore(recipe: Recipe, baby: Baby): NutritionScore {
    val ironScore = (recipe.ironContent / baby.nutritionGoal.iron) * 30
    val safetyScore = calculateSafetyScore(recipe, baby)
    val textureScore = calculateTextureScore(recipe, baby)
    val diversityScore = calculateDiversityScore(recipe, baby)
    val freshnessScore = calculateFreshnessScore(recipe)
    val preparationScore = calculatePreparationScore(recipe, baby)

    return NutritionScore(
        recipeId = recipe.id,
        ironPriority = ironScore,
        safetyScore = safetyScore,
        textureScore = textureScore,
        diversityScore = diversityScore,
        freshnessScore = freshnessScore,
        preparationScore = preparationScore,
        totalScore = ironScore + safetyScore + textureScore + diversityScore + freshnessScore + preparationScore
    )
}
```

**权重配置**:
```kotlin
val NUTRITION_WEIGHTS = mapOf(
    "iron_priority" to 0.30f,
    "safety" to 0.25f,
    "texture" to 0.15f,
    "diversity" to 0.10f,
    "freshness" to 0.10f,
    "preparation" to 0.10f
)
```

### 实现要点
- 创建NutritionScorer服务类
- 在RecommendationService中集成营养评分
- 保留现有RuleEngine的验证功能
- 在推荐结果中显示推荐理由
- 支持用户自定义权重配置

---

## Summary

### 研究成果总结

| 研究任务 | 决策 | 关键输出 |
|---------|------|---------|
| 营养数据库API集成 | 中国食物成分表（本地SQLite） | NutritionData数据结构、获取方案 |
| 安全风险食材分类体系 | 5级风险分类 | SafetyRisk数据结构、200+食材清单 |
| 质地分类标准实现 | 4级质地 + 5个发育阶段 | Texture数据结构、月龄-质地映射表 |
| 营养目标计算算法 | 中国营养学会标准 + 个体差异调整 | NutritionGoal数据结构、计算公式 |
| AI推荐算法优化 | 基于权重评分 + 奥卡姆剃刀原则 | NutritionScorer服务、权重配置 |

### 技术决策总结

1. **数据存储**: 使用Room本地数据库存储营养数据，支持离线模式
2. **数据更新**: 每季度检查官方更新，人工更新数据集
3. **安全处理**: 5级风险分类，自动过滤绝对禁用食材
4. **质地适配**: 基于月龄的自动筛选 + 用户手动调整
5. **营养计算**: 基于中国营养学会标准 + 体重/健康状况调整
6. **AI推荐**: 权重评分算法 + 奥卡姆剃刀原则（AI优先，本地兜底）

### 下一步

所有研究任务已完成，所有NEEDS CLARIFICATION已解决。可以进入Phase 1设计阶段：
- 创建data-model.md
- 设计UI组件结构
- 更新agent context

### 风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| 营养数据不准确 | 营养推荐错误 | 使用权威数据源，每季度更新 |
| 安全风险食材遗漏 | 宝宝健康风险 | 建立完善的食材库，用户可反馈 |
| 质地分类不精确 | 咀嚼能力发育迟缓 | 支持用户手动调整，提供边界值处理 |
| AI推荐算法偏差 | 推荐结果不理想 | A/B测试，收集用户反馈，持续优化 |
| 性能问题 | 用户体验差 | 本地数据库缓存，异步查询 |