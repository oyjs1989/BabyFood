# BabyFood 项目文档

## 项目概述

BabyFood 是一个使用 Kotlin 开发的 Android 应用程序，专注于婴幼儿辅食管理。项目采用现代化的 Android 开发技术栈，使用 MVVM 架构和 Jetpack Compose 构建 UI。

### 核心功能
- **今日餐单**：展示宝宝每日餐单，支持营养目标自定义和餐单推荐
- **宝宝档案**：管理多个宝宝信息，包括基本信息、过敏食材、偏好食材
- **食谱管理**：管理食谱库，支持浏览、搜索、添加、编辑、删除食谱
- **体检记录**：记录宝宝的体检数据，包括体重、身高、头围、血液指标等
- **生长曲线**：追踪宝宝生长发育情况，对比标准生长曲线
- **黑白名单**：管理过敏食材和偏好食材，支持有效期设置
- **营养目标**：根据月龄自动计算营养目标，支持手动调整
- **AI 智能推荐**：基于 AI 的智能食谱推荐和周计划生成，支持本地规则引擎和远程 LLM API

- **项目名称**：BabyFood
- **项目类型**：Android 应用
- **包名**：com.example.babyfood
- **当前版本**：1.0 (versionCode: 1)
- **开发语言**：Kotlin 2.0.21
- **UI 框架**：Jetpack Compose
- **架构模式**：MVVM

## 技术栈

### 核心技术
- **语言**：Kotlin 2.0.21
- **构建工具**：Gradle 9.3.0 + Kotlin DSL
- **Android Gradle Plugin**：8.9.1
- **UI 框架**：Jetpack Compose
- **依赖注入**：Hilt
- **数据库**：Room

### Android SDK 版本
- **minSdk**：24 (Android 7.0)
- **targetSdk**：34 (Android 14)
- **compileSdk**：34

### 主要依赖库
- `androidx.core:core-ktx:1.12.0` - AndroidX Core KTX 扩展
- `androidx.appcompat:appcompat:1.6.1` - AppCompat 兼容库
- `com.google.android.material:material:1.11.0` - Material Design 组件
- `androidx.constraintlayout:constraintlayout:2.1.4` - ConstraintLayout 布局
- `androidx.compose.ui:ui:*` - Jetpack Compose UI 基础组件
- `androidx.compose.material3:material3:*` - Material Design 3 组件
- `androidx.compose.foundation:foundation:*` - Jetpack Compose 基础库
- `androidx.lifecycle:lifecycle-*` - Lifecycle 组件（ViewModel、LiveData）
- `androidx.navigation:navigation-compose` - Navigation Compose 导航
- `com.google.dagger:hilt-android:*` - Hilt 依赖注入
- `androidx.room:room-*` - Room 数据库
- `org.jetbrains.kotlinx:kotlinx-datetime:*` - Kotlinx DateTime 日期时间处理
- `org.jetbrains.kotlinx:kotlinx-serialization:*` - Kotlinx 序列化支持
- `com.patrykandpatrick.vico:compose:*` - Vico 图表库（生长曲线可视化）
- `com.squareup.retrofit2:retrofit:2.9.0` - Retrofit 网络请求
- `com.squareup.retrofit2:converter-kotlinx-serialization:0.8.0` - Kotlinx 序列化转换器
- `com.squareup.okhttp3:okhttp:4.12.0` - OkHttp HTTP 客户端
- `com.squareup.okhttp3:logging-interceptor:4.12.0` - OkHttp 日志拦截器
- `androidx.work:work-runtime-ktx:2.9.0` - WorkManager 后台任务

### 测试框架
- `junit:junit:4.13.2` - 单元测试
- `androidx.test.ext:junit:1.1.5` - Android JUnit 扩展
- `androidx.test.espresso:espresso-core:3.5.1` - UI 测试

## 项目结构

```
BabyFood/
├── app/                           # 应用模块
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/babyfood/
│   │       │   ├── BabyFoodApplication.kt  # 应用入口
│   │       │   ├── MainActivity.kt         # 主 Activity
│   │       │   ├── data/                  # 数据层
│   │       │   │   ├── local/             # 本地数据
│   │       │   │   │   └── database/      # Room 数据库
│   │       │   │   │       ├── BabyFoodDatabase.kt
│   │       │   │   │       ├── Converters.kt
│   │       │   │   │       ├── dao/       # 数据访问对象
│   │       │   │   │       │   ├── BabyDao.kt
│   │       │   │   │       │   ├── PlanDao.kt
│   │       │   │   │       │   ├── RecipeDao.kt
│   │       │   │   │       │   ├── HealthRecordDao.kt
│   │       │   │   │       │   └── GrowthRecordDao.kt
│   │       │   │   │       └── entity/    # 数据库实体
│   │       │   │   │           ├── BabyEntity.kt
│   │       │   │   │           ├── PlanEntity.kt
│   │       │   │   │           ├── RecipeEntity.kt
│   │       │   │   │           ├── HealthRecordEntity.kt
│   │       │   │   │           └── GrowthRecordEntity.kt
│   │       │   │   ├── remote/            # 远程数据
│   │       │   │   │   ├── api/           # REST API 接口
│   │       │   │   │   │   ├── RecipeApiService.kt
│   │       │   │   │   │   ├── PlanApiService.kt
│   │       │   │   │   │   ├── BabyApiService.kt
│   │       │   │   │   │   ├── SyncApiService.kt
│   │       │   │   │   │   └── HealthAnalysisApiService.kt
│   │       │   │   │   ├── dto/           # 数据传输对象
│   │       │   │   │   │   ├── CloudBaby.kt
│   │       │   │   │   │   ├── CloudPlan.kt
│   │       │   │   │   │   ├── CloudRecipe.kt
│   │       │   │   │   │   ├── HealthAnalysisRequest.kt
│   │       │   │   │   │   ├── SyncPullResponse.kt
│   │       │   │   │   │   ├── SyncPushRequest.kt
│   │       │   │   │   │   └── SyncPushResponse.kt
│   │       │   │   │   ├── mapper/       # 数据映射器
│   │       │   │   │   │   ├── BabyMapper.kt
│   │       │   │   │   │   ├── PlanMapper.kt
│   │       │   │   │   │   └── RecipeMapper.kt
│   │       │   │   │   └── RemoteDataSource.kt
│   │       │   │   ├── sync/              # 同步管理
│   │       │   │   │   └── SyncManager.kt
│   │       │   │   ├── ai/                # AI 服务
│   │       │   │   │   ├── HealthAnalysisService.kt
│   │       │   │   │   ├── LocalHealthAnalysisStrategy.kt
│   │       │   │   │   ├── RemoteHealthAnalysisStrategy.kt
│   │       │   │   │   └── recommendation/     # AI 推荐系统
│   │       │   │   │       ├── RecommendationService.kt
│   │       │   │   │       ├── CandidateRecipeService.kt
│   │       │   │   │       ├── MainModelStrategy.kt
│   │       │   │   │       ├── CheapModelStrategy.kt
│   │       │   │   │       └── RuleEngine.kt
│   │       │   │   ├── strategy/          # 策略管理
│   │       │   │   │   └── StrategyManager.kt
│   │       │   │   └── repository/         # 数据仓库
│   │       │   │       ├── BabyRepository.kt
│   │       │   │       ├── PlanRepository.kt
│   │       │   │       ├── RecipeRepository.kt
│   │       │   │       ├── HealthRecordRepository.kt
│   │       │   │       └── GrowthRecordRepository.kt
│   │       │   ├── init/                 # 数据初始化
│   │       │   │   └── RecipeInitializer.kt
│   │       │   ├── di/                     # 依赖注入
│   │       │   │   ├── DatabaseModule.kt
│   │       │   │   └── NetworkModule.kt
│   │       │   ├── domain/                 # 领域层
│   │       │   │   └── model/              # 数据模型
│   │       │   │       ├── Baby.kt
│   │       │   │       ├── Plan.kt
│   │       │   │       ├── Recipe.kt
│   │       │   │       ├── MealPeriod.kt
│   │       │   │       ├── NutritionGoal.kt
│   │       │   │       ├── AllergyItem.kt
│   │       │   │       ├── PreferenceItem.kt
│   │       │   │       ├── HealthRecord.kt
│   │       │   │       ├── GrowthRecord.kt
│   │       │   │       ├── GrowthStandard.kt
│   │       │   │       ├── SyncStatus.kt
│   │       │   │       ├── RecommendationRequest.kt
│   │       │   │       ├── RecommendationResponse.kt
│   │       │   │       ├── PlanConflict.kt
│   │       │   │       └── ConflictResolution.kt
│   │       │   └── presentation/          # 表现层
│   │       │       ├── theme/             # 主题配置
│   │       │       │   ├── Color.kt
│   │       │       │   ├── Theme.kt
│   │       │       │   └── Type.kt
│   │       │           └── ui/                # UI 层
│   │       │           ├── MainScreen.kt   # 主屏幕
│   │       │           ├── baby/           # 宝宝相关
│   │       │           │   ├── BabyViewModel.kt
│   │       │           │   ├── BabyListScreen.kt
│   │       │           │   ├── BabyFormScreen.kt
│   │       │           │   ├── BabyDetailScreen.kt
│   │       │           │   ├── AllergyManagementScreen.kt
│   │       │           │   └── PreferenceManagementScreen.kt
│   │       │           ├── home/           # 首页
│   │       │           │   ├── HomeViewModel.kt
│   │       │           │   └── components/
│   │       │           │       ├── NutritionGoalCard.kt
│   │       │           │       ├── MealTimeline.kt
│   │       │           │       ├── TodayMenuScreen.kt
│   │       │           │       ├── RecipeSelectorDialog.kt
│   │       │           │       └── WeeklyPlansSection.kt
│   │       │           ├── plans/          # 计划管理
│   │       │           │   ├── PlansViewModel.kt
│   │       │           │   ├── PlanListScreen.kt
│   │       │           │   ├── PlanDetailScreen.kt
│   │       │           │   ├── PlanFormScreen.kt
│   │       │           │   ├── RecommendationEditorScreen.kt
│   │       │           │   ├── ConflictResolutionDialog.kt
│   │       │           │   └── DateRangePickerDialog.kt
│   │       │           ├── recipes/        # 食谱管理
│   │       │           │   ├── RecipesViewModel.kt
│   │       │           │   ├── RecipesListScreen.kt
│   │       │           │   ├── RecipeDetailScreen.kt
│   │       │           │   └── RecipeFormScreen.kt
│   │       │           ├── health/         # 体检记录
│   │       │           │   ├── HealthRecordViewModel.kt
│   │       │           │   ├── HealthRecordListScreen.kt
│   │       │           │   └── HealthRecordFormScreen.kt
│   │       │           └── growth/         # 生长记录
│   │       │               ├── GrowthRecordViewModel.kt
│   │       │               ├── GrowthCurveScreen.kt
│   │       │               └── GrowthChart.kt
│   │       │           ├── ai/             # AI 功能
│   │       │               ├── AiSettingsViewModel.kt
│   │       │               ├── RecommendationViewModel.kt
│   │       │               ├── AiSettingsScreen.kt
│   │       │               └── RecommendationScreen.kt
│   │       ├── res/                          # 资源文件
│   │       │   ├── drawable/                 # 图片资源
│   │       │   ├── layout/                   # 布局文件
│   │       │   │   └── activity_main.xml
│   │       │   ├── mipmap-*/                  # 应用图标
│   │       │   ├── values/                    # 值资源
│   │       │   │   ├── colors.xml
│   │       │   │   ├── strings.xml
│   │       │   │   └── themes.xml
│   │       │   └── xml/                       # XML 配置
│   │       │       ├── backup_rules.xml
│   │       │       └── data_extraction_rules.xml
│   │       └── AndroidManifest.xml           # 应用清单
│   ├── build.gradle.kts                        # 应用模块构建配置
│   └── build/                                  # 构建输出目录
├── gradle/                                     # Gradle 包装器
│   └── wrapper/
├── build.gradle.kts                            # 项目级构建配置
├── settings.gradle.kts                         # Gradle 设置
├── gradle.properties                           # Gradle 属性配置
├── gradlew                                     # Unix/Linux/Mac 构建脚本
├── gradlew.bat                                 # Windows 构建脚本
├── IFLOW.md                                    # 项目文档
└── PRD.md                                      # 产品需求文档

## 构建和运行

### 前置要求
- JDK 17 或更高版本
- Android SDK (API 34)
- Android Studio (推荐) 或命令行工具

### 构建命令

#### Windows 系统
```bash
# 清理构建产物
gradlew.bat clean

# 构建项目（Debug 版本）
gradlew.bat assembleDebug

# 构建项目（Release 版本）
gradlew.bat assembleRelease

# 运行单元测试
gradlew.bat test

# 运行 Android 设备测试
gradlew.bat connectedAndroidTest

# 安装 Debug 版本到连接的设备
gradlew.bat installDebug

# 卸载应用
gradlew.bat uninstallDebug
```

#### Unix/Linux/macOS 系统
```bash
# 清理构建产物
./gradlew clean

# 构建项目（Debug 版本）
./gradlew assembleDebug

# 构建项目（Release 版本）
./gradlew assembleRelease

# 运行单元测试
./gradlew test

# 运行 Android 设备测试
./gradlew connectedAndroidTest

# 安装 Debug 版本到连接的设备
./gradlew installDebug

# 卸载应用
./gradlew uninstallDebug
```

### 构建输出
- Debug APK：`app/build/outputs/apk/debug/app-debug.apk`
- Release APK：`app/build/outputs/apk/release/app-release.apk`

## 开发约定

### 代码风格
- 使用 Kotlin 官方代码风格（`kotlin.code.style=official`）
- 遵循 Android 官方代码规范
- 使用有意义的变量和方法命名

### 日志规范（强制要求）
**所有开发组件都必须打印对应的日志，便于问题排查和调试。**

#### 日志标签规范
- 使用类名作为日志标签，格式：`android.util.Log.d("ClassName", "message")`
- 例如：`PlansViewModel`、`RecommendationService`、`MainScreen`

#### 日志级别规范
- `Log.d()` - 调试信息：方法进入、关键步骤、状态变化
- `Log.i()` - 一般信息：正常流程、成功操作
- `Log.w()` - 警告信息：异常情况但可继续执行
- `Log.e()` - 错误信息：操作失败、异常捕获

#### 日志格式规范
```
========== 方法名开始 ==========
✓ 操作成功
❌ 操作失败
⚠️ 警告信息
========== 方法名结束 ==========
```

#### 必须添加日志的场景
1. **ViewModel 方法**：所有公开方法必须记录进入和退出
2. **Service 方法**：所有服务方法必须记录请求参数和响应结果
3. **Repository 方法**：所有数据操作必须记录操作类型和结果
4. **UI 组件**：关键状态变化必须记录
5. **异常捕获**：所有 catch 块必须记录异常信息

#### 日志示例
```kotlin
fun generateRecommendation() {
    android.util.Log.d("PlansViewModel", "========== 开始生成推荐 ==========")
    android.util.Log.d("PlansViewModel", "宝宝ID: $babyId")
    
    try {
        val result = recommendationService.generateRecommendation(request)
        android.util.Log.d("PlansViewModel", "✓ 推荐生成成功")
        android.util.Log.d("PlansViewModel", "========== 推荐生成完成 ==========")
    } catch (e: Exception) {
        android.util.Log.e("PlansViewModel", "❌ 推荐生成失败: ${e.message}")
        android.util.Log.e("PlansViewModel", "异常堆栈: ", e)
    }
}
```

### 架构约定
- 项目采用 MVVM 架构模式
- 分层架构：UI 层 → ViewModel 层 → Repository 层 → DAO 层 → Database
- 使用 Jetpack Compose 构建 UI
- 使用 Hilt 进行依赖注入
- 使用 Room 进行数据持久化
- 使用 Flow 进行响应式数据流

### 数据流
```
UI (Jetpack Compose)
    ↓ 观察 StateFlow
ViewModel (StateFlow/LiveData)
    ↓ 调用 Repository 方法
Repository (Flow)
    ↓ 调用 DAO/Strategy 方法
┌─────────────────────────────────┐
│  Service Layer (策略管理)       │
│  - StrategyManager              │
│  - HealthAnalysisService        │
│  - RecommendationService        │
│  - LocalStrategy/RemoteStrategy │
└─────────────────────────────────┘
    ↓
┌─────────────────────────────────┐
│  Data Source Layer              │
│  - LocalSource (Room DAO)       │
│  - RemoteSource (API Service)   │
│  - SyncManager                  │
└─────────────────────────────────┘
    ↓
Database (Room SQLite) / Cloud DB
```

### 状态管理模式
- **ViewModel**：使用 `StateFlow` 管理页面状态
- **Repository**：使用 `Flow` 提供响应式数据流
- **Database**：Room 提供 `Flow` 查询支持自动更新
- **UI**：通过 `collectAsState()` 观察状态变化

### 依赖管理
- 使用阿里云 Maven 镜像加速依赖下载
- 优先使用稳定版本的依赖库
- 定期更新依赖版本

### 配置说明
- **AndroidX**：已启用（`android.useAndroidX=true`）
- **非传递性 R 类**：已启用（`android.nonTransitiveRClass=true`）
- **JVM 目标**：Java 17
- **Gradle 配置缓存**：已禁用（`org.gradle.configuration-cache=false`）
- **数据库版本**：6（支持迁移 v1→v2→v3→v4→v5→v6）
- **Kotlin 版本**：2.0.21
- **Compose 编译器**：使用 Kotlin 2.0.21 内置编译器
- **Kotlin 序列化**：已配置编译器插件，支持 `@Serializable` 注解
- **序列化用途**：内置食谱初始化、服务器数据同步、数据库类型转换

## 当前项目状态

项目已完成基础架构搭建和核心功能开发，采用 MVVM 架构模式。

### 构建状态
- ✅ **最新构建**：BUILD SUCCESSFUL（2026-01-24）
- ✅ **数据库版本**：6（支持迁移 v1→v2→v3→v4→v5→v6）
- ✅ **所有 UI 页面**：已实现并通过编译验证
- ✅ **导航路由**：完整配置并可用
- ✅ **Kotlin 序列化**：已配置并支持服务器数据同步
- ✅ **网络层**：Retrofit + OkHttp 已配置
- ✅ **云同步架构**：完整实现（REST API、SyncManager、RemoteDataSource）
- ✅ **AI 分析架构**：完整实现（本地规则引擎 + 远程 LLM API）
- ✅ **AI 推荐架构**：完整实现（多策略推荐系统、规则引擎、候选筛选）
- ✅ **内置食谱库**：30 个辅食食谱（6-24个月），覆盖早餐、午餐、晚餐、点心
- ✅ **AI 推荐日志**：详细日志输出，便于调试和问题定位
- ✅ **AI 推荐原则**：遵循奥卡姆剃刀原则，AI 优先，本地仅作兜底

### 已实现功能

#### 1. 今日餐单首页
- ✅ 宝宝今日核心营养目标展示（根据月龄自动计算，用户可调整）
- ✅ 按时间轴卡片流展示早、午、晚、点心餐单
- ✅ "换一换"功能（基于 AI 智能推荐，支持多策略切换）
- ✅ 空状态提示（未添加宝宝时引导用户）
- ✅ 基于黑白名单的食材过滤
- ✅ 周计划展示（WeeklyPlansSection，显示本周计划概览）
- ✅ 食谱选择对话框（RecipeSelectorDialog，为餐段时间段选择食谱）

#### 2. 宝宝档案管理
- ✅ 支持多个宝宝管理
- ✅ 宝宝列表展示（含添加和编辑功能）
- ✅ 宝宝详情页面（基本信息、营养目标、黑白名单）
- ✅ 黑白名单管理（过敏食材和偏好食材，支持有效期）
- ✅ 营养目标自定义（根据中国营养学会标准自动计算）
- ✅ 过期食材自动标记和过滤

#### 3. 体检记录管理
- ✅ 体检记录列表展示
- ✅ 添加/编辑体检记录表单
- ✅ 支持记录体重、身高、头围、铁、钙、血红蛋白等指标
- ✅ 支持备注信息
- ✅ 数据持久化存储
- ✅ AI 健康分析（本地规则引擎 + 远程 LLM API）
- ✅ 自动同步到生长记录表

#### 4. 食谱管理
- ✅ 食谱列表页面（支持搜索、按月龄筛选、按分类筛选）
- ✅ 食谱详情页面（显示食材、步骤、营养成分）
- ✅ 添加/编辑食谱功能（支持食材添加/删除、步骤编辑）
- ✅ 内置食谱初始化（30个常用辅食食谱，6-24个月）
  - 早餐类：南瓜米糊、胡萝卜土豆泥、苹果泥、香蕉泥、肉末粥、蔬菜肉末粥、南瓜鸡肉粥
  - 午餐类：鸡肉粥、牛肉土豆泥、蔬菜肉丸汤、番茄牛肉面、土豆烧鸡块、三鲜小馄饨、红烧牛肉面
  - 晚餐类：清蒸鱼块、蔬菜肉末蒸蛋、虾仁蒸蛋
  - 点心类：蛋黄泥、鱼肉泥、豆腐泥、西红柿炒鸡蛋、蒸蛋羹、水果沙拉、蔬菜小饼、蔬菜鸡蛋饼、虾仁炒蛋、蔬菜鸡肉丸、鱼丸汤
- ✅ Kotlin 序列化配置（支持从服务器获取食谱）
- ✅ 删除食谱功能（仅限用户自定义食谱）

#### 5. 生长记录
- ✅ 生长记录数据模型
- ✅ 生长记录列表展示
- ✅ 支持体重、身高、头围数据记录
- ✅ 数据持久化存储
- ✅ GrowthChart 组件（基于 Vico 图表库）
- ✅ 支持体重、身高、头围曲线绘制
- ✅ 对比 WHO/中国标准生长曲线
- ✅ 数据点标注和交互

#### 6. 数据模型
- ✅ Baby - 宝宝信息（含过敏、偏好、营养目标）
- ✅ Plan - 餐单计划（含餐段时间段）
- ✅ Recipe - 食谱信息（含营养成分）
- ✅ MealPeriod - 餐段时间段枚举
- ✅ NutritionGoal - 营养目标（含月龄自动计算）
- ✅ AllergyItem - 过敏食材项（带有效期）
- ✅ PreferenceItem - 偏好食材项（带有效期）
- ✅ HealthRecord - 体检记录
- ✅ GrowthRecord - 生长记录
- ✅ GrowthStandard - WHO/中国生长标准
- ✅ SyncStatus - 同步状态枚举（SYNCED、PENDING_UPLOAD、PENDING_DOWNLOAD、ERROR、LOCAL_ONLY）
- ✅ CloudBaby - 云端宝宝数据模型（脱敏版本）
- ✅ CloudPlan - 云端计划数据模型
- ✅ CloudRecipe - 云端食谱数据模型
- ✅ RecommendationRequest - AI 推荐请求模型
- ✅ RecommendationResponse - AI 推荐响应模型
- ✅ PlanConflict - 计划冲突模型
- ✅ ConflictResolution - 冲突解决模型

#### 7. 数据库
- ✅ Room 数据库（当前版本：6）
- ✅ 数据库迁移策略
  - MIGRATION_1_2：添加 mealPeriod 字段到 plans 表
  - MIGRATION_2_3：创建 health_records 和 growth_records 表
  - MIGRATION_3_4：无操作迁移（版本号递增）
  - MIGRATION_4_5：添加同步元数据字段（cloudId、syncStatus、lastSyncTime、version、isDeleted）
  - MIGRATION_5_6：修复同步元数据字段约束（重建表）
- ✅ 完整的 DAO 接口（BabyDao、PlanDao、RecipeDao、HealthRecordDao、GrowthRecordDao）
- ✅ TypeConverters 支持复杂类型（AllergyItem、PreferenceItem、LocalDate 等）
- ✅ 数据库索引优化

#### 8. 业务逻辑层
- ✅ Repository 封装数据访问
- ✅ ViewModel 状态管理
- ✅ Hilt 依赖注入

#### 9. UI 层
- ✅ Jetpack Compose UI
- ✅ Material Design 3 主题
- ✅ 底部导航栏
- ✅ 今日餐单页面
- ✅ 宝宝列表页面
- ✅ 宝宝详情页面
- ✅ 添加/编辑宝宝表单
- ✅ 过敏食材管理页面
- ✅ 偏好食材管理页面
- ✅ 体检记录列表页面
- ✅ 添加/编辑体检记录表单
- ✅ 生长曲线页面（集成 Vico 图表库，支持体重/身高/头围曲线可视化，对比 WHO/中国标准）
- ✅ 食谱列表页面（RecipesListScreen）
- ✅ 食谱详情页面（RecipeDetailScreen）
- ✅ 添加/编辑食谱表单（RecipeFormScreen）
- ✅ 辅食计划列表/详情/表单（PlanListScreen/PlanDetailScreen/PlanFormScreen）
- ✅ 完整的导航路由

#### 10. 云同步架构
- ✅ REST API 接口（Recipe、Plan、Baby、Sync、HealthAnalysis）
- ✅ RemoteDataSource 接口和实现
- ✅ SyncManager 同步管理器（拉取、推送、冲突解决）
- ✅ 数据映射器（Entity ↔ Cloud 转换）
- ✅ Repository 自动同步标记
- ✅ 软删除支持（isDeleted 字段）
- ✅ 版本控制（version 字段）
- ✅ DTOs（SyncPullResponse、SyncPushRequest、SyncPushResponse）

#### 11. AI 分析架构
- ✅ HealthAnalysisService 接口
- ✅ LocalHealthAnalysisStrategy（基于规则的本地健康分析）
- ✅ RemoteHealthAnalysisStrategy（LLM API 远程健康分析）
- ✅ StrategyManager 策略管理器（自动降级）
- ✅ 健康指标检测（血红蛋白、铁、钙、体重、身高、头围）

#### 12. AI 智能推荐系统
- ✅ RecommendationService（推荐服务接口）
- ✅ CandidateRecipeService（候选食谱筛选，基于月龄、过敏、偏好）
- ✅ MainModelStrategy（主模型策略，生成周计划）
- ✅ CheapModelStrategy（轻量模型策略，生成餐单文本描述）
- ✅ RuleEngine（规则引擎，验证食谱和周计划）
- ✅ 策略类型支持：LOCAL、REMOTE、HYBRID、DISABLED
- ✅ 运行时策略切换和自动降级
- ✅ **奥卡姆剃刀原则**：
  - AI 推荐完全由模型策略生成，本地规则仅作记录日志
  - 本地判断仅在 AI 无法处理时介入（如过敏食材硬性过滤）
  - 简化逻辑，优先信任 AI 智能推荐

#### 13. 辅食计划管理
- ✅ 计划列表页面（PlanListScreen）
- ✅ 计划详情页面（PlanDetailScreen，显示营养汇总）
- ✅ 创建/编辑计划表单（PlanFormScreen）
- ✅ AI 推荐编辑器（RecommendationEditorScreen，支持冲突解决）
- ✅ 冲突解决对话框（ConflictResolutionDialog）
- ✅ 日期范围选择对话框（DateRangePickerDialog）
- ✅ 完整的状态管理和导航路由

#### 17. 导航路由配置
完整的导航路由配置，支持以下路由：

- `home` - 今日餐单首页
- `recipes` - 食谱列表
- `recipes/detail/{recipeId}` - 食谱详情
- `recipes/form/{recipeId}` - 添加/编辑食谱
- `plans` - 计划列表
- `plans/detail/{planId}` - 计划详情
- `plans/form/{babyId}/{planId}` - 添加/编辑计划
- `plans/recommendation/editor/{babyId}` - AI 推荐编辑器
- `baby` - 宝宝列表
- `baby/form/{babyId}` - 添加/编辑宝宝
- `baby/detail/{babyId}` - 宝宝详情
- `baby/allergy/{babyId}` - 过敏食材管理
- `baby/preference/{babyId}` - 偏好食材管理
- `baby/health/{babyId}` - 体检记录列表
- `baby/health/form/{babyId}/{recordId}` - 添加/编辑体检记录
- `baby/growth/{babyId}` - 生长曲线
- `ai/settings` - AI 设置
- `ai/recommendation` - AI 推荐

#### 14. AI 设置与推荐界面
- ✅ AI 设置页面（AiSettingsScreen，策略切换：本地/远程/混合）
- ✅ AI 推荐界面（RecommendationScreen）
- ✅ AiSettingsViewModel 和 RecommendationViewModel 完整实现

#### 15. 首页增强功能
- ✅ 食谱选择对话框（RecipeSelectorDialog，为餐段时间段选择食谱）
- ✅ 周计划展示（WeeklyPlansSection，在首页显示本周计划）
- ✅ 完整的换一换功能（基于 AI 推荐）

#### 16. 生长曲线可视化
- ✅ GrowthChart 组件（基于 Vico 图表库）
- ✅ 支持体重、身高、头围曲线绘制
- ✅ 对比 WHO/中国标准生长曲线
- ✅ 数据点标注和交互

### 待完善功能

#### 基础设施
- 实现 SharedPreferences 存储 lastSyncTime
- 实现本地 ID 映射（Plans 和 Babies 的 cloudId ↔ localId 映射）
- 实现冲突解决用户选择 UI（允许用户选择保留云端或本地版本）
- 安全的 API Key 管理（使用环境变量或 Android Keystore 存储 DashScope API Key）
- 实现 JWT 认证（在 NetworkModule 中添加实际的认证逻辑）
- 更新 API URL 为实际的后端服务器地址

#### 高级功能
- 图片支持（食谱图片上传/展示）
- 数据导出与分享（PDF/Excel 导出、Android Sharesheet）
- 搜索优化（搜索历史、热门推荐、收藏功能）
- 后端服务器实现（REST API 接口实际部署）
- 单元测试和集成测试

## 开发环境配置

### Maven 仓库镜像
项目使用阿里云 Maven 镜像加速依赖下载：
- `https://maven.aliyun.com/repository/google`
- `https://maven.aliyun.com/repository/public`
- `https://maven.aliyun.com/repository/gradle-plugin`

### Gradle 配置
- JVM 参数：`-Xmx2048m -Dfile.encoding=UTF-8`
- 并行构建：未启用（可根据需要启用）

### 设计原则

#### 奥卡姆剃刀原则（Occam's Razor）
在 AI 功能设计和实现中，遵循奥卡姆剃刀原则："如无必要，勿增实体"。

**应用场景：**

1. **AI 推荐系统**
   - **原则**：AI 优先，本地仅作兜底
   - **实现**：
     - AI 推荐完全由模型策略生成周计划
     - 本地规则引擎仅用于记录日志，不干预结果
     - 本地判断仅在 AI 无法处理的场景介入（如过敏食材硬性过滤、年龄范围验证）
   - **避免**：过度依赖本地规则限制 AI 推荐的灵活性

2. **AI 健康分析**
   - **原则**：远程 LLM 优先，本地规则降级
   - **实现**：
     - 优先使用远程 LLM API 进行智能健康分析
     - 网络失败或 API 不可用时，降级到本地规则引擎
     - 本地规则仅提供基础的健康指标检测
   - **避免**：本地规则过于复杂，影响用户体验

3. **数据同步**
   - **原则**：云端优先，本地为辅
   - **实现**：
     - 数据以云端为准，本地作为缓存
     - 冲突时优先保留云端版本（或让用户选择）
     - 本地数据仅在离线时使用
   - **避免**：复杂的冲突解决逻辑导致数据不一致

**核心思想：**
- 简化系统复杂度
- 优先信任 AI 智能能力
- 本地规则仅作为安全网和兜底方案
- 避免过度工程化

#### AI Function Calling（工具调用）原则
在 AI 功能扩展中，遵循工具调用原则，使 AI 能够通过结构化接口调用应用功能。

**核心原理：**

AI 调用工具的本质是一个"回合制游戏"，AI 不直接执行代码，而是输出结构化文本（通常是 JSON），由程序解析参数并执行真正的工具调用，最后将结果传回 AI 生成最终回复。

**调用流程：**

1. **定义阶段**：程序在发给 AI 的请求中附带工具列表，使用 JSON Schema 格式描述工具名称、参数类型、必填项、枚举值等
2. **决策阶段**：AI 接收用户问题和工具列表，通过内部推理判断是否需要调用工具
3. **输出阶段**：AI 输出函数名和参数（JSON 格式），例如：`{"name": "get_recipe", "arguments": "{\"recipeId\": 1}"}`
4. **执行阶段**：程序监听 AI 返回，解析参数，执行真实的函数（如查询数据库、调用 API）
5. **总结阶段**：程序将工具执行结果再次发送给 AI，AI 结合结果用自然语言回答用户

**参数控制策略：**

为了保证 AI 输出正确的参数，需要遵循以下策略：

- **强约束的 JSON Schema**：明确参数类型（string、number、boolean）、必填字段（required）、枚举值（enum）
- **详细的参数描述**：用大白话描述参数含义和格式，避免歧义
- **模型微调支持**：主流模型（GPT-4o、Claude 3.5 Sonnet）都经过函数调用微调，会自动进入"结构化输出模式"

**在 BabyFood 中的应用：**

以下业务功能适合工具化，可被 AI 调用：

1. **食谱管理工具**
   - `search_recipes`：搜索食谱（支持关键词、月龄、分类筛选）
   - `get_recipe_detail`：获取食谱详情（食材、步骤、营养成分）
   - `create_recipe`：创建新食谱
   - `update_recipe`：更新食谱信息
   - `delete_recipe`：删除食谱

2. **宝宝档案管理工具**
   - `get_baby_list`：获取宝宝列表
   - `get_baby_detail`：获取宝宝详情（基本信息、营养目标、黑白名单）
   - `create_baby`：创建宝宝档案
   - `update_baby`：更新宝宝信息
   - `add_allergy`：添加过敏食材
   - `add_preference`：添加偏好食材

3. **体检记录管理工具**
   - `get_health_records`：获取体检记录列表
   - `get_health_record_detail`：获取体检记录详情
   - `create_health_record`：创建体检记录
   - `analyze_health`：分析体检数据（调用 AI 健康分析服务）

4. **生长记录管理工具**
   - `get_growth_records`：获取生长记录列表
   - `create_growth_record`：创建生长记录
   - `get_growth_curve`：获取生长曲线数据

5. **餐单计划管理工具**
   - `get_plans`：获取餐单计划列表
   - `get_plan_detail`：获取计划详情
   - `create_plan`：创建餐单计划
   - `generate_recommendation`：生成 AI 推荐计划（调用 RecommendationService）

6. **营养目标工具**
   - `calculate_nutrition_goal`：根据月龄计算营养目标
   - `get_nutrition_goal`：获取宝宝当前营养目标
   - `update_nutrition_goal`：更新营养目标

7. **黑白名单管理工具**
   - `get_allergies`：获取过敏食材列表
   - `get_preferences`：获取偏好食材列表
   - `add_allergy_item`：添加过敏食材（支持有效期）
   - `add_preference_item`：添加偏好食材（支持有效期）

**工具定义示例：**

```json
{
  "type": "function",
  "function": {
    "name": "get_recipe_detail",
    "description": "获取指定食谱的详细信息，包括食材清单、制作步骤和营养成分",
    "parameters": {
      "type": "object",
      "properties": {
        "recipeId": {
          "type": "integer",
          "description": "食谱的唯一标识符（ID），例如：1、15、30"
        }
      },
      "required": ["recipeId"]
    }
  }
}
```

**设计要求：**

- **工具定义必须使用严格的 JSON Schema**：明确类型、必填项、枚举值
- **参数描述必须清晰明确**：避免歧义，提供示例
- **必填字段必须明确列出**：AI 会检查必填项是否齐全
- **枚举值必须限制在固定范围内**：避免 AI 编造无效值

**核心思想：**

- AI 是决策者，程序是执行者
- 通过结构化接口实现 AI 与应用的交互
- 参数准确性依赖于 JSON Schema 的严格定义
- 工具调用扩展了 AI 的能力边界，使其能够操作真实数据

## 常见问题

### 构建失败
- 确保 JDK 17 已安装并配置正确
- 检查网络连接，确保能访问 Maven 仓库
- 尝试清理构建：`gradlew.bat clean`
- 检查 Kotlin 版本是否为 2.0.21
- 清理 Gradle 缓存：`gradlew.bat clean --no-daemon`

### 依赖下载缓慢
- 项目已配置阿里云镜像，如果仍然缓慢，可检查网络代理设置
- 尝试使用 VPN 或切换网络环境
- 检查 `gradle.properties` 中的代理配置

### 设备连接问题
- 确保 USB 调试已开启
- 检查 Android 设备是否正确连接
- 使用 `adb devices` 命令验证设备连接
- 尝试重启 ADB 服务：`adb kill-server && adb start-server`

### 数据库迁移问题
- 如果遇到数据库迁移错误，请卸载应用重新安装
- 检查 `BabyFoodDatabase.kt` 中的迁移逻辑
- 确保数据库版本号正确递增

### Kotlin Compose 编译错误
- 确保 Kotlin 插件版本正确
- 检查 Compose 编译器配置
- 尝试清理构建缓存：`gradlew.bat clean`

### Kotlin 序列化错误
- 确保 `@Serializable` 注解已添加到需要序列化的类
- 检查 `build.gradle.kts` 中序列化插件配置正确
- 清理构建缓存：`gradlew.bat clean`
- 确保 Kotlin 序列化库版本与 Kotlin 版本匹配

### AI 推荐相关问题

#### 云端 AI 推荐接口规范

**接口地址：** `POST /api/recommendation/generate`

**请求格式（JSON）：**
```json
{
  "babyId": 1,
  "ageInMonths": 14,
  "allergies": ["鸡蛋", "牛奶"],
  "preferences": ["南瓜", "胡萝卜"],
  "availableIngredients": ["鸡肉", "菠菜"],
  "useAvailableIngredientsOnly": false,
  "constraints": {
    "maxFishPerWeek": 2,
    "maxEggPerWeek": 3,
    "breakfastComplexity": "MODERATE",
    "maxDailyMeals": 4
  },
  "startDate": "2026-01-24",
  "days": 7
}
```

**响应格式（JSON）：**
```json
{
  "success": true,
  "errorMessage": null,
  "weeklyPlan": {
    "startDate": "2026-01-24",
    "endDate": "2026-01-30",
    "dailyPlans": [
      {
        "date": "2026-01-24",
        "meals": [
          {
            "mealPeriod": "BREAKFAST",
            "recipeId": 1,
            "recipeName": "南瓜米糊",
            "nutritionNotes": "提供优质蛋白质和碳水化合物",
            "childFriendlyText": "早餐有南瓜米糊，香香的很好吃哦～"
          },
          {
            "mealPeriod": "LUNCH",
            "recipeId": 15,
            "recipeName": "鸡肉粥",
            "nutritionNotes": "提供丰富的蛋白质、维生素和矿物质",
            "childFriendlyText": "午餐有鸡肉粥，营养满满！"
          }
        ]
      }
    ],
    "nutritionSummary": {
      "weeklyCalories": 3500.0,
      "weeklyProtein": 175.0,
      "weeklyCalcium": 2800.0,
      "weeklyIron": 70.0,
      "dailyAverage": {
        "calories": 500.0,
        "protein": 25.0,
        "calcium": 400.0,
        "iron": 10.0
      },
      "highlights": [
        "蛋白质摄入充足，有助于肌肉发育",
        "钙摄入充足，有助于骨骼发育"
      ]
    }
  },
  "warnings": [
    "平均热量偏低，建议增加主食摄入"
  ]
}
```

**重要说明：**
- **recipeId 字段**：响应中的 `recipeId` 是本地食谱的 ID，用于从本地数据库查找完整的食谱信息
- **mealPeriod 枚举值**：必须是 `"BREAKFAST"`、`"LUNCH"`、`"DINNER"`、`"SNACK"` 之一
- **日期格式**：使用 ISO 8601 格式，如 `"2026-01-24"`
- **success 字段**：`true` 表示成功，`false` 表示失败
- **errorMessage 字段**：失败时包含错误信息，成功时为 `null`

#### AI 推荐显示"推荐数据加载失败"
**可能原因：**
1. 候选食谱为空（当前年龄段没有适合的食谱）
2. 过敏食材过滤后没有合适的食谱
3. 宝宝年龄超出支持范围（6-36个月）

**排查步骤：**
1. 查看日志中的"AI推荐服务"标签，查看详细错误信息
2. 检查宝宝年龄是否在 6-36 个月范围内
3. 检查是否设置了过敏食材，导致食谱被过滤
4. 检查内置食谱库是否正常初始化（应有 30 个食谱）

**解决方案：**
- 如果是年龄问题，调整宝宝信息或添加更多年龄段食谱
- 如果是过敏食材问题，暂时移除过敏食材或添加更多兼容食谱
- 如果是食谱库问题，卸载应用重新安装

#### AI 推荐结果蛋类/鱼类超限
**说明：**
- 根据**奥卡姆剃刀原则**，AI 推荐完全由模型策略生成
- 本地规则仅记录日志，不干预结果
- 超限警告仅作为参考信息，不影响推荐结果

**原因：**
- AI 模型可能根据营养均衡考虑，适当调整食材比例
- 本地约束（如每周最多 3 次蛋类）仅供参考

**处理方式：**
- 用户可以根据实际需求手动调整周计划
- 也可以在 AI 设置中调整约束条件

## 下一步开发

### 短期目标（高优先级）
1. 基础设施完善
   - 实现 SharedPreferences 存储 lastSyncTime
   - 实现本地 ID 映射（Plans 和 Babies）
   - 实现冲突解决用户选择 UI
   - 安全的 API Key 管理（环境变量或 Android Keystore）
   - 实现 JWT 认证逻辑
   - 更新 API URL 为实际后端地址

2. 后端服务器实现
   - 实现 Recipe、Plan、Baby、Sync REST API 接口
   - 实现用户认证和授权
   - 实现数据同步服务
   - 部署服务器并配置域名

### 中期目标（中优先级）
1. 同步功能完善
   - 实现完整的冲突解决策略
   - 添加手动同步按钮
   - 完善同步状态显示
   - 添加同步日志和错误处理

2. 用户体验优化
   - 搜索历史记录
   - 热门推荐功能
   - 收藏功能
   - 分享功能
   - 数据导出功能（PDF/Excel）

3. 图片支持
   - 食谱图片上传
   - 食谱图片展示
   - 图片压缩和优化
   - 云存储集成

### 长期目标（低优先级）
1. AI 功能增强
   - AI 体检单识别（OCR）
   - AI 营养分析增强
   - AI 食谱生成
   - AI 生长发育评估
   - 协同过滤推荐算法优化

2. 社区功能
   - 用户分享
   - 食谱交流
   - 专家问答
   - 经验分享

3. 商业化功能
   - 付费食谱
   - 专家咨询
   - 商品推荐
   - 会员系统

4. 测试完善
   - 单元测试
   - 集成测试
   - UI 测试
   - 性能测试

## 许可证

请根据项目实际情况添加许可证信息。