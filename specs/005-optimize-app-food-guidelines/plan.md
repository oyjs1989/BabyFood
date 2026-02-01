# Implementation Plan: 优化辅食选择功能 - 基于权威营养指南

**Branch**: `005-optimize-app-food-guidelines` | **Date**: 2026-01-31 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/005-optimize-app-food-guidelines/spec.md`

## Summary

本功能基于WHO、中国营养学会等权威机构的六大辅食选择原则（铁优先、安全第一、质地适配、原味多样化、新鲜度、自制与市售互补）优化BabyFood应用的辅食推荐系统。核心实现包括：

1. **智能食材推荐引擎**：基于宝宝月龄、过敏食材、偏好食材，优先推荐富铁食谱
2. **安全风险预警系统**：识别绝对禁用食材（1岁内蜂蜜、高汞鱼类）、需特殊处理食材、高硝酸盐食材
3. **质地适配性引导**：根据咀嚼能力发育阶段推荐适合质地的食谱（泥糊状→稠泥/末状→软固体/块状）
4. **营养目标匹配分析**：追踪核心营养素（铁、锌、维生素A、钙、维生素C）摄入情况
5. **原味多样化引导**：记录宝宝尝试过的食材，优先推荐新食材预防挑食
6. **新鲜度与制作方式建议**：提供食材新鲜度建议和自制/市售辅食选择指导

技术方案：在现有MVVM架构基础上，扩展Recipe、Ingredient、NutritionGoal等数据模型，新增SafetyRisk、IngredientTrial等实体，利用Room数据库存储营养数据和用户偏好，结合现有的AI推荐系统增强推荐逻辑。

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Jetpack Compose, Room, Hilt, Retrofit, Kotlinx Serialization, Kotlinx DateTime
**Storage**: Room Database (SQLite) + Remote API (Python FastAPI Backend)
**Testing**: JUnit 4.13.2, Espresso 3.5.1, AndroidX Test Extensions
**Target Platform**: Android 7.0+ (minSdk 24, targetSdk 34)
**Project Type**: Mobile (Android)
**Performance Goals**:
  - 食谱推荐响应时间 < 2秒 (SC-006)
  - 支持1000并发用户 (SC-006)
  - 营养数据准确率 ≥ 95% (SC-010)
**Constraints**:
  - 离线模式支持（查看已保存食谱和营养目标）
  - 营养数据每季度更新一次（Dependencies clarified）
  - 安全警告忽略后需记录并在后续加强提醒
**Scale/Scope**:
  - 32个功能性需求
  - 6个优先级用户故事
  - 10个成功标准
  - 7个边界情况处理

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Constitution Status**: 项目尚未建立正式Constitution文件，使用AGENTS.md和IFLOW.md作为开发指南。

**Current Guidelines Compliance**:
- ✅ MVVM架构模式（IFLOW.md）
- ✅ 日志规范要求（所有组件必须打印日志）
- ✅ 依赖注入使用Hilt
- ✅ 数据持久化使用Room
- ✅ 状态管理使用StateFlow
- ✅ 遵循奥卡姆剃刀原则（AI优先，本地仅作兜底）
- ✅ 代码简化原则（使用泛型消除重复代码）

**Architecture Compliance**:
- ✅ 分层架构：UI层 → ViewModel层 → Repository层 → DAO层 → Database
- ✅ Repository封装数据访问
- ✅ ViewModel状态管理
- ✅ Hilt依赖注入
- ✅ Room数据持久化
- ✅ Flow响应式数据流

**No gate violations detected.** Proceeding to Phase 0 research.

## Project Structure

### Documentation (this feature)

```
specs/005-optimize-app-food-guidelines/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output (already exists)
├── contracts/           # Phase 1 output (Android不需要API契约，使用Kotlin数据类)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```
app/src/main/java/com/example/babyfood/
├── data/
│   ├── local/
│   │   └── database/
│   │       ├── entity/
│   │       │   ├── SafetyRiskEntity.kt           # [NEW] 安全风险实体
│   │       │   ├── IngredientTrialEntity.kt      # [NEW] 食材尝试记录实体
│   │       │   └── NutritionGoalEntity.kt        # [NEW] 营养目标实体（从BabyEntity分离）
│   │       ├── dao/
│   │       │   ├── SafetyRiskDao.kt              # [NEW] 安全风险数据访问
│   │       │   ├── IngredientTrialDao.kt         # [NEW] 食材尝试记录数据访问
│   │       │   └── NutritionGoalDao.kt           # [NEW] 营养目标数据访问
│   │       └── BabyFoodDatabase.kt               # [UPDATE] 数据库版本15，添加新表
│   ├── repository/
│   │   ├── NutritionGoalRepository.kt            # [NEW] 营养目标仓库
│   │   ├── SafetyRiskRepository.kt               # [NEW] 安全风险仓库
│   │   └── IngredientTrialRepository.kt          # [NEW] 食材尝试记录仓库
│   └── model/
│       ├── SafetyRisk.kt                         # [NEW] 安全风险领域模型
│       ├── IngredientTrial.kt                    # [NEW] 食材尝试记录领域模型
│       └── NutritionGoal.kt                      # [UPDATE] 营养目标领域模型（已存在，需扩展）
├── presentation/
│   └── ui/
│       ├── recipes/
│       │   ├── components/
│       │   │   ├── SafetyWarningBadge.kt         # [NEW] 安全警告标识组件
│       │   │   ├── NutritionInfoCard.kt          # [NEW] 营养信息卡片
│       │   │   ├── TextureIndicator.kt           # [NEW] 质地指示器
│       │   │   ├── IronRichBadge.kt              # [NEW] 富铁推荐标识
│       │   │   └── NewIngredientTag.kt           # [NEW] 新食材标签
│       │   └── RecipesListScreen.kt              # [UPDATE] 添加筛选和排序功能
│       ├── baby/
│       │   ├── components/
│       │   │   ├── NutritionGoalEditor.kt        # [NEW] 营养目标编辑器
│       │   │   └── IngredientTrialList.kt        # [NEW] 食材尝试记录列表
│       │   └── BabyDetailScreen.kt               # [UPDATE] 添加营养目标和食材尝试记录视图
│       ├── home/
│       │   └── components/
│       │       ├── NutritionSummaryCard.kt       # [NEW] 营养汇总卡片
│       │       └── SafetyAlertBanner.kt          # [NEW] 安全警告横幅
│       └── common/
│           └── NutritionEducationPage.kt         # [NEW] 营养知识科普页面
└── init/
    └── SafetyRiskInitializer.kt                  # [NEW] 初始化安全风险数据
```

**Structure Decision**: 扩展现有Android项目结构，在data、presentation、init模块中添加新组件。遵循MVVM架构，所有新功能通过Repository、ViewModel、Compose UI组件实现。数据持久化使用Room，网络请求使用Retrofit，依赖注入使用Hilt。

## Phase 0: Research Tasks

### Research Task 1: 营养数据库API集成
**Question**: 如何获取权威营养数据库（如中国食物成分表）的食材营养数据？
**Research Focus**:
- 调研可用的营养数据库API（中国食物成分表、USDA FoodData Central等）
- 评估API可靠性、数据完整性、更新频率
- 确定数据获取策略（实时查询 vs 本地缓存）
- 评估成本和许可要求

**Expected Output**: 选定的营养数据库API文档、集成方案、成本分析

### Research Task 2: 安全风险食材分类体系
**Question**: 如何建立完整的安全风险食材分类体系？
**Research Focus**:
- 调研权威机构的辅食安全指南（WHO、中国营养学会、美国儿科学会）
- 建立食材风险等级分类（绝对禁用、不推荐、需特殊处理、正常）
- 确定每种风险类型的具体食材清单和处理建议
- 建立月龄相关的风险规则（如1岁内禁用蜂蜜）

**Expected Output**: 完整的安全风险食材清单、分类标准、处理建议库

### Research Task 3: 质地分类标准实现
**Question**: 如何量化不同质地的特征和适用月龄？
**Research Focus**:
- 调研GB 10770-2025国标对颗粒大小的要求
- 建立质地量化标准（泥糊状、稠泥/末状、软固体/块状）
- 确定每种质地的食材颗粒大小范围
- 建立咀嚼能力发育阶段的质地适配规则

**Expected Output**: 质地分类标准、颗粒大小范围、月龄-质地映射表

### Research Task 4: 营养目标计算算法
**Question**: 如何根据月龄和个体差异计算个性化营养目标？
**Research Focus**:
- 研究中国营养学会《0~2岁婴幼儿喂养指南》
- 建立月龄-营养目标映射表（铁、锌、维生素A、钙、维生素C）
- 确定个体差异调整因子（体重、健康状况等）
- 建立营养目标动态更新机制

**Expected Output**: 营养目标计算算法、月龄-营养目标映射表、调整因子定义

### Research Task 5: AI推荐算法优化
**Question**: 如何将六大辅食选择原则集成到现有AI推荐系统中？
**Research Focus**:
- 分析现有RecommendationService架构
- 设计铁优先推荐策略的权重算法
- 设计安全风险过滤逻辑
- 设计质地适配性评分机制
- 评估与现有奥卡姆剃刀原则的兼容性

**Expected Output**: 优化后的AI推荐算法设计、权重配置、集成方案

## Complexity Tracking

*No constitution violations requiring justification.*

## Implementation Phases Overview

### Phase 0: Research & Data Collection (Current Phase)
- 完成上述5个研究任务
- 生成research.md文档
- 解析所有NEEDS CLARIFICATION

### Phase 1: Design & Data Modeling
- 设计数据模型（data-model.md）
- 创建Android数据类（Kotlin data classes）
- 设计Room数据库迁移（v14→v15）
- 设计UI组件结构

### Phase 2: Implementation
- 创建任务列表（tasks.md）
- 按优先级实现功能（P1→P2→P3）
- 单元测试和集成测试
- UI测试和端到端测试

### Phase 3: Validation & Launch
- 验证所有成功标准
- 性能测试和优化
- 用户接受度测试
- 文档更新

## Next Steps

1. ✅ 完成技术上下文定义
2. ✅ 完成Constitution检查
3. 🔄 执行Phase 0研究任务
4. ⏳ 生成research.md
5. ⏳ 进入Phase 1设计阶段