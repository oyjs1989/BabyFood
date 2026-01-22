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
│   │       │   │   └── repository/         # 数据仓库
│   │       │   │       ├── BabyRepository.kt
│   │       │   │       ├── PlanRepository.kt
│   │       │   │       ├── RecipeRepository.kt
│   │       │   │       ├── HealthRecordRepository.kt
│   │       │   │       └── GrowthRecordRepository.kt
│   │       │   ├── init/                 # 数据初始化
│   │       │   │   └── RecipeInitializer.kt
│   │       │   ├── di/                     # 依赖注入
│   │       │   │   └── DatabaseModule.kt
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
│   │       │   │       └── GrowthStandard.kt
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
│   │       │           │       └── TodayMenuScreen.kt
│   │       │           ├── plans/          # 计划管理
│   │       │           │   └── PlansViewModel.kt
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
│   │       │               └── GrowthCurveScreen.kt
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
    ↓ 调用 DAO 方法
DAO (Flow)
    ↓ 操作数据库
Database (Room SQLite)
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
- **数据库版本**：3（支持迁移 v1→v2→v3）
- **Kotlin 版本**：2.0.21
- **Compose 编译器**：使用 Kotlin 2.0.21 内置编译器
- **Kotlin 序列化**：已配置编译器插件，支持 `@Serializable` 注解
- **序列化用途**：内置食谱初始化、服务器数据同步、数据库类型转换

## 当前项目状态

项目已完成基础架构搭建和核心功能开发，采用 MVVM 架构模式。

### 构建状态
- ✅ **最新构建**：BUILD SUCCESSFUL（2026-01-22）
- ✅ **数据库版本**：3（支持迁移）
- ✅ **所有 UI 页面**：已实现并通过编译验证
- ✅ **导航路由**：完整配置并可用
- ✅ **Kotlin 序列化**：已配置并支持服务器数据同步

### 已实现功能

#### 1. 今日餐单首页
- ✅ 宝宝今日核心营养目标展示（根据月龄自动计算，用户可调整）
- ✅ 按时间轴卡片流展示早、午、晚、点心餐单
- ✅ "换一换"功能（MVP 阶段使用简单随机推荐）
- ✅ 空状态提示（未添加宝宝时引导用户）
- ✅ 基于黑白名单的食材过滤

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

#### 4. 食谱管理
- ✅ 食谱列表页面（支持搜索、按月龄筛选、按分类筛选）
- ✅ 食谱详情页面（显示食材、步骤、营养成分）
- ✅ 添加/编辑食谱功能（支持食材添加/删除、步骤编辑）
- ✅ 内置食谱初始化（15个常用辅食食谱，6-24个月）
- ✅ Kotlin 序列化配置（支持从服务器获取食谱）
- ✅ 删除食谱功能（仅限用户自定义食谱）

#### 5. 生长记录
- ✅ 生长记录数据模型
- ✅ 生长记录列表展示
- ✅ 支持体重、身高、头围数据记录
- ✅ 数据持久化存储

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

#### 7. 数据库
- ✅ Room 数据库（当前版本：3）
- ✅ 数据库迁移策略
  - MIGRATION_1_2：添加 mealPeriod 字段到 plans 表
  - MIGRATION_2_3：创建 health_records 和 growth_records 表
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
- ✅ 生长曲线页面（基础版本）
- ✅ 食谱列表页面（RecipesListScreen）
- ✅ 食谱详情页面（RecipeDetailScreen）
- ✅ 添加/编辑食谱表单（RecipeFormScreen）
- ✅ 完整的导航路由

### 待完善功能

#### 高级功能
- 生长曲线可视化图表（集成图表库）
- WHO/中国生长标准对比曲线
- 辅食计划管理（计划列表、详情、创建/编辑功能）
- AI 体检分析
- 智能食谱推荐（基于反馈和生长数据）

## 开发环境配置

### Maven 仓库镜像
项目使用阿里云 Maven 镜像加速依赖下载：
- `https://maven.aliyun.com/repository/google`
- `https://maven.aliyun.com/repository/public`
- `https://maven.aliyun.com/repository/gradle-plugin`

### Gradle 配置
- JVM 参数：`-Xmx2048m -Dfile.encoding=UTF-8`
- 并行构建：未启用（可根据需要启用）

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

## 下一步开发

### 短期目标（高优先级）
1. 完善生长曲线可视化
   - 集成图表库（Compose Charts 或 MPAndroidChart）
   - 绘制体重/身高/头围生长曲线
   - 对比 WHO/中国标准生长曲线
   - 添加数据点标注和交互

2. 完善辅食计划
   - 计划列表页面
   - 计划详情页面
   - 创建/编辑计划功能
   - 计划模板功能

### 中期目标（中优先级）
1. 智能推荐优化
   - 基于用户反馈的推荐算法
   - 基于生长数据的营养调整
   - 季节性食谱推荐
   - 过敏食材自动过滤

2. 数据同步
   - 云端数据备份
   - 多设备同步
   - 离线模式支持

3. 用户体验优化
   - 搜索功能
   - 筛选功能
   - 收藏功能
   - 分享功能
   - 数据导出功能

### 长期目标（低优先级）
1. AI 功能
   - AI 体检单识别
   - AI 营养分析
   - AI 食谱生成
   - AI 生长发育评估

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

## 许可证

请根据项目实际情况添加许可证信息。