# Tasks: 优化辅食选择功能 - 基于权威营养指南

**Input**: Design documents from `/specs/005-optimize-app-food-guidelines/`
**Prerequisites**: plan.md, spec.md, data-model.md, research.md, quickstart.md

**Tests**: 本功能未明确要求测试任务，因此不包含测试相关的任务。如需添加测试，请在实施前明确要求。

**Organization**: 任务按用户故事组织，以支持每个故事的独立实现和测试。

## Format: `[ID] [P?] [Story] Description`
- **[P]**: 可并行执行（不同文件，无依赖）
- **[Story]**: 任务所属的用户故事（US1, US2, US3, US4, US5, US6）
- 描述中包含确切的文件路径

## Path Conventions
- **Android项目**: `app/src/main/java/com/example/babyfood/`
- **数据层**: `data/local/database/entity/`, `data/local/database/dao/`, `data/repository/`
- **表现层**: `presentation/ui/`, `presentation/theme/`
- **初始化**: `init/`

---

## Phase 1: Setup (共享基础设施) ✅ MVP已完成

**目的**: 项目初始化和基本结构设置

- [X] T001 创建营养功能包结构
  - 在 `app/src/main/java/com/example/babyfood/` 下创建 `nutrition/` 包
  - 子包结构: `model/`, `repository/`, `service/`, `ui/`
- [X] T002 [P] 更新数据库版本配置
  - 修改 `app/src/main/java/com/example/babyfood/data/local/database/BabyFoodDatabase.kt`
  - 将数据库版本从14升级到15
- [X] T003 [P] 准备营养数据资源文件
  - 创建 `app/src/main/assets/nutrition_data/` 目录
  - 准备安全风险数据文件 `safety_risks.json`
  - 准备营养数据文件 `nutrition_data.json`

---

## Phase 2: Foundational (阻塞性先决条件) ✅ MVP已完成

**目的**: 所有用户故事实现前必须完成的核心基础设施

**⚠️ 关键**: 此阶段完成前，任何用户故事工作都无法开始

- [X] T004 实现数据库迁移 MIGRATION_14_15
  - 在 `BabyFoodDatabase.kt` 中添加迁移逻辑
  - 创建5个新表: safety_risks, ingredient_trials, nutrition_goals, nutrition_data, user_warning_ignores
  - 扩展Recipe和Baby表
- [X] T005 [P] 创建SafetyRisk实体类
  - 文件: `app/src/main/java/com/example/babyfood/data/local/database/entity/SafetyRiskEntity.kt`
  - 定义8个字段和索引
- [X] T006 [P] 创建IngredientTrial实体类
  - 文件: `app/src/main/java/com/example/babyfood/data/local/database/entity/IngredientTrialEntity.kt`
  - 定义5个字段
- [X] T007 [P] 创建NutritionGoal实体类
  - 文件: `app/src/main/java/com/example/babyfood/data/local/database/entity/NutritionGoalEntity.kt`
  - 定义6个字段
- [X] T008 [P] 创建NutritionData实体类
  - 文件: `app/src/main/java/com/example/babyfood/data/local/database/entity/NutritionDataEntity.kt`
  - 定义6个字段
- [X] T009 [P] 创建UserWarningIgnore实体类
  - 文件: `app/src/main/java/com/example/babyfood/data/local/database/entity/UserWarningIgnoreEntity.kt`
  - 定义5个字段
- [X] T010 [P] 创建SafetyRiskDao接口
  - 文件: `app/src/main/java/com/example/babyfood/data/local/database/dao/SafetyRiskDao.kt`
  - 实现查询方法: getByIngredientName, getAllForbiddenIngredients, getRisksByAge
- [X] T011 [P] 创建IngredientTrialDao接口
  - 文件: `app/src/main/java/com/example/babyfood/data/local/database/dao/IngredientTrialDao.kt`
  - 实现CRUD方法和统计方法
- [X] T012 [P] 创建NutritionGoalDao接口
  - 文件: `app/src/main/java/com/example/babyfood/data/local/database/dao/NutritionGoalDao.kt`
  - 实现按宝宝ID查询方法
- [X] T013 [P] 创建NutritionDataDao接口
  - 文件: `app/src/main/java/com/example/babyfood/data/local/database/dao/NutritionDataDao.kt`
  - 实现按食材名称和营养素查询方法
- [X] T014 [P] 创建UserWarningIgnoreDao接口
  - 文件: `app/src/main/java/com/example/babyfood/data/local/database/dao/UserWarningIgnoreDao.kt`
  - 实现CRUD和统计方法
- [X] T015 创建SafetyRiskInitializer初始化器
  - 文件: `app/src/main/java/com/example/babyfood/init/SafetyRiskInitializer.kt`
  - 从资源文件加载2200+条初始化数据
  - 实现数据验证和插入逻辑
- [X] T016 扩展Recipe实体
  - 修改 `RecipeEntity.kt`
  - 添加字段: textureType, isIronRich, ironContent, riskLevelList, safetyAdvice
- [X] T017 扩展Baby实体
  - 修改 `BabyEntity.kt`
  - 添加字段: chewingAbility, preferredTextureLevel
- [X] T018 扩展BabyEntity转换器
  - 修改 `Converters.kt`
  - 添加新字段的TypeConverter支持
- [X] T019 在BabyFoodDatabase中注册新DAO
  - 修改 `BabyFoodDatabase.kt`
  - 添加4个新DAO: SafetyRiskDao, IngredientTrialDao, NutritionGoalDao, NutritionDataDao
  - 注册SafetyRiskInitializer

**检查点**: 基础设施就绪 - 用户故事实现现在可以并行开始 ✅

---

## Phase 3: User Story 1 - 智能食材推荐与铁优先提醒 (Priority: P1) 🎯 MVP ✅已完成

**目的**: 实现基于权威营养指南的智能食材推荐系统，优先推荐富铁食谱

**独立测试**: 查看宝宝的今日餐单或创建新计划，系统应自动推荐富含铁的食谱并显示"富铁推荐"标识

### User Story 1 Implementation

- [X] T020 [P] [US1] 创建IronRichStrategy策略类
  - 文件: `app/src/main/java/com/example/babyfood/data/ai/recommendation/IronRichStrategy.kt`
  - 实现铁优先推荐逻辑（权重评分算法）
- [X] T021 [P] [US1] 扩展CandidateRecipeService
  - 修改 `CandidateRecipeService.kt`
  - 添加根据铁含量筛选食谱的方法
- [X] T022 [US1] 更新RecommendationService
  - 修改 `RecommendationService.kt`
  - 集成IronRichStrategy到推荐流程
- [X] T023 [P] [US1] 创建IronRichBadge组件
  - 文件: `app/src/main/java/com/example/babyfood/presentation/ui/common/IronRichBadge.kt`
  - 实现"富铁推荐"标识UI组件
- [X] T024 [US1] 更新食谱卡片组件
  - 修改 `RecipeCard.kt` (或相应组件)
  - 集成IronRichBadge显示逻辑
- [X] T025 [US1] 更新HomeViewModel
  - 修改 `HomeViewModel.kt`
  - 添加铁优先推荐的触发逻辑
- [X] T026 [US1] 添加营养目标计算服务
  - 文件: `app/src/main/java/com/example/babyfood/data/service/NutritionGoalCalculator.kt`
  - 实现基于月龄的营养目标计算（中国营养学会标准）
- [X] T027 [US1] 更新营养汇总显示
  - 修改营养汇总UI组件
  - 显示铁含量和达标状态

**检查点**: 此时User Story 1应该完全功能化且可独立测试 ✅

---

## Phase 4: User Story 2 - 质地适配性引导 (Priority: P2)

**目的**: 根据宝宝的咀嚼能力发育阶段推荐适合质地的食谱

**独立测试**: 查看宝宝的月龄和食谱推荐，系统应根据月龄自动筛选适合质地的食谱

### User Story 2 Implementation

- [X] T028 [P] [US2] 创建TextureType枚举
  - 文件: `app/src/main/java/com/example/babyfood/domain/model/TextureType.kt`
  - 定义4个质地类型: PUREE, MASH, CHUNK, SOLID
- [X] T029 [P] [US2] 创建TextureMatchingService
  - 文件: `app/src/main/java/com/example/babyfood/data/service/TextureMatchingService.kt`
  - 实现质地适配算法（5个发育阶段）
- [X] T030 [US2] 扩展RecipeRepository
  - 修改 `RecipeRepository.kt`
  - 添加按质地筛选食谱的方法
- [X] T031 [US2] 更新RecipeFormScreen
  - 修改 `RecipeFormScreen.kt`
  - 添加质地类型选择UI
- [X] T032 [US2] 创建TextureWarning组件
  - 文件: `app/src/main/java/com/example/babyfood/presentation/ui/common/TextureWarning.kt`
  - 实现质地不适配警告UI
- [X] T033 [US2] 更新食谱详情页
  - 修改 `RecipeDetailScreen.kt`
  - 显示质地信息和适用月龄提示
- [X] T034 [US2] 更新BabyFormScreen
  - 修改 `BabyFormScreen.kt`
  - 添加咀嚼能力备注输入框

**检查点**: 此时User Stories 1和2都应该独立工作

---

## Phase 5: User Story 3 - 安全风险预警与禁忌食材管理 (Priority: P2) ✅ 已完成

**目的**: 自动识别并预警辅食中的安全风险，包括绝对禁用食材、需特殊处理食材

**独立测试**: 添加包含风险食材的食谱或查看食谱详情，系统应显示安全警告

### User Story 3 Implementation

- [X] T035 [P] [US3] 创建SafetyRiskRepository
  - 文件: `app/src/main/java/com/example/babyfood/data/repository/SafetyRiskRepository.kt`
  - 实现安全风险查询逻辑
- [X] T036 [P] [US3] 创建SafetyRiskAnalyzer服务
  - 文件: `app/src/main/java/com/example/babyfood/data/service/SafetyRiskAnalyzer.kt`
  - 实现风险评估算法（5级风险分类）
- [X] T037 [P] [US3] 创建SafetyWarningBadge组件
  - 文件: `app/src/main/java/com/example/babyfood/presentation/ui/common/SafetyWarningBadge.kt`
  - 实现红色警告标识（禁用）和黄色警告标识（不推荐）
- [X] T038 [P] [US3] 创建HandlingAdviceDialog对话框
  - 文件: `app/src/main/java/com/example/babyfood/presentation/ui/common/HandlingAdviceDialog.kt`
  - 实现处理建议显示对话框
- [X] T039 [US3] 更新食谱卡片组件
  - 修改 `RecipeCard.kt`
  - 集成SafetyWarningBadge显示逻辑
- [X] T040 [US3] 更新食谱详情页
  - 修改 `RecipeDetailScreen.kt`
  - 显示风险原因和处理建议
- [X] T041 [US3] 创建UserWarningIgnoreRepository
  - 文件: `app/src/main/java/com/example/babyfood/data/repository/UserWarningIgnoreRepository.kt`
  - 实现用户忽略警告的记录逻辑
- [X] T042 [US3] 实现忽略警告加强提醒逻辑
  - 修改 `SafetyRiskAnalyzer.kt`
  - 检测用户历史忽略行为，加强提醒

**检查点**: 所有用户故事现在应该独立功能化 ✅

---

## Phase 6: User Story 4 - 原味多样化引导与挑食预防 (Priority: P3)

**目的**: 引导用户遵循"原味优先"原则，记录宝宝尝试过的食材，优先推荐新食材

**独立测试**: 查看宝宝的食材记录和食谱推荐，系统应统计已尝试食材并优先推荐新食材

### User Story 4 Implementation

- [ ] T043 [P] [US4] 创建IngredientTrialRepository
  - 文件: `app/src/main/java/com/example/babyfood/data/repository/IngredientTrialRepository.kt`
  - 实现食材尝试记录的CRUD操作
- [ ] T044 [P] [US4] 创建FlavorDiversityService
  - 文件: `app/src/main/java/com/example/babyfood/data/service/FlavorDiversityService.kt`
  - 实现口味多样性推荐算法
- [ ] T045 [P] [US4] 创建FlavorNaturalBadge组件
  - 文件: `app/src/main/java/com/example/babyfood/presentation/ui/common/FlavorNaturalBadge.kt`
  - 实现"原味推荐"标识UI
- [ ] T046 [US4] 创建NewIngredientTag组件
  - 文件: `app/src/main/java/com/example/babyfood/presentation/ui/common/NewIngredientTag.kt`
  - 实现"新食材"标签UI
- [ ] T047 [US4] 更新食谱详情页
  - 修改 `RecipeDetailScreen.kt`
  - 显示"原味推荐"标识和调味品警告
- [ ] T048 [US4] 更新推荐结果显示
  - 修改推荐UI组件
  - 标注新食材并统计食材种类
- [ ] T049 [US4] 创建食材尝试记录表单
  - 文件: `app/src/main/java/com/example/babyfood/presentation/ui/recipes/IngredientTrialForm.kt`
  - 实现记录宝宝尝试食材的UI

**检查点**: User Stories 1-4都应该独立工作

---

## Phase 7: User Story 5 - 新鲜度与制作方式建议 (Priority: P3)

**目的**: 提供食材新鲜度建议和自制/市售辅食选择指导

**独立测试**: 查看食谱详情或食材建议，系统应提供新鲜度提示和制作方式建议

### User Story 5 Implementation

- [ ] T050 [P] [US5] 创建FreshnessAdvisor服务
  - 文件: `app/src/main/java/com/example/babyfood/data/service/FreshnessAdvisor.kt`
  - 实现新鲜度建议算法（绿叶蔬菜、根茎类、冷冻/罐装）
- [ ] T051 [P] [US5] 创建CookingMethodRecommender服务
  - 文件: `app/src/main/java/com/example/babyfood/data/service/CookingMethodRecommender.kt`
  - 实现自制/市售推荐逻辑（6-9月龄优先市售，10月龄+推荐自制）
- [ ] T052 [US5] 创建FreshnessTipCard组件
  - 文件: `app/src/main/java/com/example/babyfood/presentation/ui/common/FreshnessTipCard.kt`
  - 实现新鲜度建议卡片UI
- [ ] T053 [US5] 创建CookingMethodBadge组件
  - 文件: `app/src/main/java/com/example/babyfood/presentation/ui/common/CookingMethodBadge.kt`
  - 实现"自制"/"市售"推荐标识UI
- [ ] T054 [US5] 更新食谱详情页
  - 修改 `RecipeDetailScreen.kt`
  - 显示新鲜度建议和制作方式推荐
- [ ] T055 [US5] 创建StorageSafetyDialog对话框
  - 文件: `app/src/main/java/com/example/babyfood/presentation/ui/common/StorageSafetyDialog.kt`
  - 实现存储安全警告对话框

**检查点**: User Stories 1-5都应该独立工作

---

## Phase 8: User Story 6 - 营养目标与食谱匹配度分析 (Priority: P2)

**目的**: 分析宝宝当前饮食的营养摄入情况，与推荐食谱的营养成分进行匹配

**独立测试**: 查看宝宝的营养目标和今日餐单的营养汇总，系统应显示各营养素的摄入情况和达标率

### User Story 6 Implementation

- [ ] T056 [P] [US6] 创建NutritionGoalRepository
  - 文件: `app/src/main/java/com/example/babyfood/data/repository/NutritionGoalRepository.kt`
  - 实现营养目标的CRUD操作
- [ ] T057 [P] [US6] 创建NutritionMatcher服务
  - 文件: `app/src/main/java/com/example/babyfood/data/service/NutritionMatcher.kt`
  - 实现营养摄入分析和匹配算法
- [ ] T058 [P] [US6] 创建NutritionSummaryCard组件
  - 文件: `app/src/main/java/com/example/babyfood/presentation/ui/common/NutritionSummaryCard.kt`
  - 实现营养汇总卡片UI
- [ ] T059 [US6] 创建NutritionProgressBar组件
  - 文件: `app/src/main/java/com/example/babyfood/presentation/ui/common/NutritionProgressBar.kt`
  - 实现营养素达标率进度条UI
- [ ] T060 [US6] 创建NutritionHighlightBadge组件
  - 文件: `app/src/main/java/com/example/babyfood/presentation/ui/common/NutritionHighlightBadge.kt`
  - 实现营养亮点标识UI
- [ ] T061 [US6] 更新营养目标配置页面
  - 修改 `BabyDetailScreen.kt`
  - 允许用户查看和编辑营养目标
- [ ] T062 [US6] 更新今日餐单页面
  - 修改 `TodayMenuScreen.kt`
  - 显示营养汇总和各营养素摄入情况
- [ ] T063 [US6] 更新食谱详情页
  - 修改 `RecipeDetailScreen.kt`
  - 显示营养亮点和匹配度提示

**检查点**: 所有用户故事（1-6）都应该独立功能化

---

## Phase 9: Polish & Cross-Cutting Concerns

**目的**: 影响多个用户故事的改进

- [X] T064 [P] 添加所有营养功能的日志记录
  - 在所有新建的服务和ViewModel中添加Log.d调用
  - 遵循日志规范: 方法开始/结束、操作成功/失败
- [ ] T065 [P] 更新AGENTS.md文档
  - 添加新技术栈说明
  - 更新Recent Changes章节
- [X] T066 [P] 运行代码质量检查
  - 执行: `./gradlew ktlintCheck`
  - 执行: `./gradlew detekt`
- [X] T067 构建验证
  - 执行: `./gradlew clean assembleDebug`
  - 确保编译无错误
- [ ] T068 性能验证
  - 测试食谱推荐响应时间 < 2秒
  - 测试并发用户支持（模拟）
- [ ] T069 营养数据准确率验证
  - 对比权威营养数据库验证数据准确性
  - 目标准确率 ≥ 95%
- [X] T070 [P] 更新quickstart.md测试场景
  - 验证所有用户故事的独立测试场景
  - 确保每个用户故事可以独立验证
- [ ] T071 离线模式验证
  - 测试网络不可用时的离线功能
  - 确保基础功能（查看已保存食谱、营养目标）正常工作

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: 无依赖 - 可以立即开始
- **Foundational (Phase 2)**: 依赖Setup完成 - 阻塞所有用户故事
- **User Stories (Phase 3-8)**: 全部依赖Foundational阶段完成
  - 用户故事可以按优先级顺序实现（P1 → P2 → P3）
  - 或如果有多个开发者，可以并行实现
- **Polish (Phase 9)**: 依赖所有期望的用户故事完成

### User Story Dependencies

- **User Story 1 (P1)**: Foundational完成后可开始 - 不依赖其他故事
- **User Story 2 (P2)**: Foundational完成后可开始 - 可与US1集成但应独立可测试
- **User Story 3 (P2)**: Foundational完成后可开始 - 可与US1/US2集成但应独立可测试
- **User Story 4 (P3)**: Foundational完成后可开始 - 可与之前故事集成但应独立可测试
- **User Story 5 (P3)**: Foundational完成后可开始 - 可与之前故事集成但应独立可测试
- **User Story 6 (P2)**: Foundational完成后可开始 - 可与之前故事集成但应独立可测试

### Within Each User Story

- Repository和Service层可以并行开发（不同文件）
- UI组件可以并行开发（不同文件）
- 核心实现后进行集成
- 故事完成后可以独立测试

### Parallel Opportunities

- 所有Setup任务标记[P]可以并行运行
- 所有Foundational任务标记[P]可以并行运行（在Phase 2内）
- Foundational阶段完成后，所有用户故事可以并行开始（如果团队资源允许）
- 每个用户故事内标记[P]的UI组件可以并行开发
- 不同用户故事可以由不同团队成员并行开发

---

## Parallel Example: User Story 1

```bash
# 启动User Story 1的所有策略和服务类:
Task: "创建IronRichStrategy策略类 in data/ai/recommendation/IronRichStrategy.kt"
Task: "扩展CandidateRecipeService in CandidateRecipeService.kt"

# 启动User Story 1的所有UI组件:
Task: "创建IronRichBadge组件 in presentation/ui/common/IronRichBadge.kt"
Task: "添加营养目标计算服务 in data/service/NutritionGoalCalculator.kt"
```

---

## Implementation Strategy

### MVP First (仅User Story 1)

1. 完成Phase 1: Setup
2. 完成Phase 2: Foundational（关键 - 阻塞所有故事）
3. 完成Phase 3: User Story 1
4. **停止并验证**: 独立测试User Story 1
5. 如果准备好则部署/演示

### Incremental Delivery

1. 完成Setup + Foundational → 基础设施就绪
2. 添加User Story 1 → 独立测试 → 部署/演示（MVP！）
3. 添加User Story 2 → 独立测试 → 部署/演示
4. 添加User Story 3 → 独立测试 → 部署/演示
5. 添加User Story 4 → 独立测试 → 部署/演示
6. 添加User Story 5 → 独立测试 → 部署/演示
7. 添加User Story 6 → 独立测试 → 部署/演示
8. 每个故事增加价值而不破坏之前的故事

### Parallel Team Strategy

多个开发者时:

1. 团队一起完成Setup + Foundational
2. Foundational完成后:
   - 开发者A: User Story 1 + User Story 6
   - 开发者B: User Story 2 + User Story 3
   - 开发者C: User Story 4 + User Story 5
3. 故事独立完成和集成

---

## Task Summary

- **总任务数**: 71个任务
- **Setup阶段**: 3个任务
- **Foundational阶段**: 16个任务
- **User Story 1**: 8个任务
- **User Story 2**: 7个任务
- **User Story 3**: 8个任务
- **User Story 4**: 7个任务
- **User Story 5**: 6个任务
- **User Story 6**: 8个任务
- **Polish阶段**: 8个任务

### Parallel Opportunities

- **Setup**: 2个并行任务
- **Foundational**: 10个并行任务
- **User Story 1**: 2个并行任务
- **User Story 2**: 2个并行任务
- **User Story 3**: 2个并行任务
- **User Story 4**: 2个并行任务
- **User Story 5**: 2个并行任务
- **User Story 6**: 2个并行任务
- **Polish**: 4个并行任务

### Independent Test Criteria

- **User Story 1**: 查看今日餐单，系统推荐富铁食谱并显示"富铁推荐"标识
- **User Story 2**: 查看食谱推荐，系统根据月龄筛选适合质地的食谱
- **User Story 3**: 查看含风险食材的食谱，系统显示安全警告
- **User Story 4**: 查看食材记录和推荐，系统标注新食材
- **User Story 5**: 查看食谱详情，系统显示新鲜度建议
- **User Story 6**: 查看营养汇总，系统显示各营养素摄入情况

### Suggested MVP Scope

**MVP = User Story 1 only**

MVP包含:
- Phase 1: Setup (3 tasks)
- Phase 2: Foundational (16 tasks)
- Phase 3: User Story 1 (8 tasks)
- 基础Polish任务（日志、构建验证）

MVP任务总数: ~30个任务

**MVP价值**: 用户可以获得基于权威营养指南的铁优先推荐功能，这是辅食选择的最核心需求。

---

## Notes

- [P]任务 = 不同文件，无依赖
- [Story]标签将任务映射到特定用户故事以实现可追溯性
- 每个用户故事应该可以独立完成和测试
- 在每个任务或逻辑组后提交
- 在任何检查点停止以独立验证故事
- 避免: 模糊任务、同一文件冲突、破坏独立性的跨故事依赖
- 本功能未包含测试任务，如需添加测试请在实施前明确要求