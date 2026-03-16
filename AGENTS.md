# BabyFood Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-03-04

## Active Technologies
- Kotlin 2.0.21 + Jetpack Compose + Material Design 3 (003-remove-page-back-and-titles)
- Room Database + SQLite (005-optimize-app-food-guidelines)
- Nutrition Data Integration (005-optimize-app-food-guidelines)
- Safety Risk Analysis System (005-optimize-app-food-guidelines)
- Texture Adaptation Engine (005-optimize-app-food-guidelines)
- Flavor Diversity Tracking (005-optimize-app-food-guidelines)
- Freshness & Cooking Method Advisor (005-optimize-app-food-guidelines)
- Nutrition Goal Matching (005-optimize-app-food-guidelines)
- Backend AI Proxy (001-architecture-optimization)
- Real-time Sync with ID Mapping (001-architecture-optimization)

## Project Structure
```
app/src/main/java/com/example/babyfood/
├── data/                  # Data layer (repositories, DAOs, entities)
├── domain/                # Domain layer (models)
├── presentation/          # Presentation layer (UI components, ViewModels)
│   └── ui/
│       ├── common/        # Shared UI components
│       ├── baby/          # Baby management screens
│       ├── recipes/       # Recipe management screens
│       ├── plans/         # Plan management screens
│       ├── health/        # Health record screens
│       ├── growth/        # Growth curve screens
│       ├── inventory/     # Inventory management screens
│       ├── ai/            # AI-related screens
│       └── auth/          # Authentication screens
└── di/                    # Dependency injection

tests/
├── unit/                  # Unit tests
└── integration/           # Integration tests
```

## Commands
```bash
# Build
./gradlew assembleDebug

# Run tests
./gradlew test

# Install to device
./gradlew installDebug

# Clean build
./gradlew clean
```

## Code Style
- Kotlin: Follow official Kotlin code style (`kotlin.code.style=official`)
- Compose: Use Material Design 3 guidelines
- MVVM Architecture: Separate UI, ViewModel, Repository layers
- Dependency Injection: Use Hilt

## Recent Changes
- 004-android-business-logic-migration: Business logic migration to backend
  - Milestone A: Protocol freeze - unified API contracts with backend
  - Milestone B.1: Recommendation pipeline backend migration - RemoteRecommendationStrategy
  - Milestone B.2: Candidate recipe filtering migration with cloudId/localId mapping
  - Milestone B.3: Health analysis convergence - StandardHealthAnalysisStrategy
  - Milestone B.4: Nutrition guidance services decision - all retain local fallback
  - Milestone C: Code cleanup - removed deprecated strategies (MainModelStrategy, CheapModelStrategy, RemoteHealthAnalysisStrategy)
- 001-architecture-optimization: Architecture optimization and security enhancement
  - User Story 1: Remove hardcoded API Key via backend proxy
  - User Story 2: Complete SyncManager with ID mapping and conflict resolution
  - User Story 3: Unify Entity mapping code location
  - User Story 4: Simplify BaseUiViewModel abstract design
- 005-optimize-app-food-guidelines: Complete nutrition guidance optimization system
  - User Story 1: Iron-priority recipe recommendations with IronRichStrategy
  - User Story 2: Texture adaptation guidance (4 texture types, 5 developmental stages)
  - User Story 3: Safety risk warning system (5-level risk classification)
  - User Story 4: Flavor diversity tracking and new ingredient recommendations
  - User Story 5: Freshness advice and cooking method recommendations (homemade vs store-bought)
  - User Story 6: Nutrition goal matching and analysis with progress tracking
- 003-remove-page-back-and-titles: Removing page title bars and back buttons for more content space
- 002-unified-header-body-footer-layout: Implemented unified header-body-footer layout
- 001-fix-login-loading-animation: Fixed login button loading animation

<!-- MANUAL ADDITIONS START -->
## Important Notes

### Build System
- Gradle 9.3.0 with Kotlin DSL
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Compile SDK: 34

### Key Architecture Patterns
- MVVM with Jetpack Compose
- Repository pattern for data access
- Flow for reactive data streams
- Hilt for dependency injection
- Room for local database (cache layer)
- **Cloud-First Architecture**: All data is stored in cloud as primary source, local database serves as cache

### Database
- Room database version: 17
- Supports migrations up to version 17
- TypeConverters for complex types (LocalDate, enums)
- New tables: safety_risks, ingredient_trials, nutrition_goals, nutrition_data, user_warning_ignores, id_mappings
- **Note**: Local database serves as cache layer; cloud database is the primary data source

### Nutrition Guidance System (005-optimize-app-food-guidelines)

#### Safety Risk Analysis
- 5-level risk classification: FORBIDDEN, NOT_RECOMMENDED, REQUIRES_SPECIAL_HANDLING, CAUTIOUS_INTRODUCTION, NORMAL
- 2200+ safety risk data entries
- Automatic risk detection and warning display
- User warning ignore tracking and reinforcement

#### Texture Adaptation
- 4 texture types: PUREE (6-8 months), MASH (9-10 months), CHUNK (11-14 months), SOLID (15+ months)
- 5 developmental stages with chewing ability assessment
- Automatic texture-based recipe filtering

#### Flavor Diversity
- Ingredient trial tracking system
- Flavor diversity scoring (0-100 scale)
- New ingredient recommendation and tagging
- Natural flavor priority ("原味优先" principle)

#### Freshness & Cooking Method Advice
- Freshness recommendations for different ingredient types
- Homemade vs store-bought cooking method guidance
- Storage safety warnings
- Age-based recommendations (6-9 months: store-bought优先, 10+ months: homemade推荐)

#### Nutrition Goal Matching
- Age-based nutrition goal calculation (Chinese Nutrition Society standards)
- Real-time nutrition intake tracking
- Progress bars for calories, protein, calcium, iron
- Nutrition summary and deficiency/excess analysis

### AI Features
- Health analysis (backend-first with local fallback)
  - StandardHealthAnalysisStrategy: Backend API `/api/v1/health/analyze`
  - LocalHealthAnalysisStrategy: Local rule-based fallback
- Recipe recommendations (backend-first)
  - RemoteRecommendationStrategy: Backend API `/api/v1/recommendations/generate`
  - RuleEngine: Local validation and filtering
- Candidate recipe filtering (backend-first with local fallback)
  - Backend API `/api/v1/recipes/candidates`
  - Local fallback via RuleEngine
- Strategy pattern for AI service selection
- Iron-priority recommendations (IronRichStrategy)
- **Backend-First Architecture**: All AI operations prioritize backend API, local processing as fallback

### Navigation
- Navigation Compose for routing
- Bottom navigation for main sections
- Header navigation for home
- Deep linking support

### Logging
- All components must log using android.util.Log
- Use class name as log tag
- Format: "========== Method Start ==========" and "========== Method End =========="
- Log levels: d (debug), i (info), w (warning), e (error)

### Color System (颜色管理规范)

**强制规则：所有颜色必须统一在 `Color.kt` 中定义，禁止在 UI 代码中硬编码颜色值。**

#### 颜色定义位置
- 文件路径：`presentation/theme/Color.kt`
- 所有颜色常量必须在此文件中定义

#### 颜色命名规范
1. **功能命名**：`Success`, `Error`, `Warning` 等功能状态色
2. **语义命名**：`RiskForbidden`, `ScoreExcellent` 等业务语义色
3. **容器命名**：`XxxContainer` 表示背景色，`OnXxxContainer` 表示前景色
4. **组件命名**：`ButtonPrimary`, `InputDisabledContainer` 等组件色

#### 颜色分类
- **品牌色**：`Primary`, `Peach`, `Coral` 等品牌核心色
- **文字色**：`TextPrimary`, `TextSecondary`, `Charcoal` 等
- **背景色**：`BackgroundLight`, `CardBackground`, `Surface` 等
- **功能色**：`Success`, `Warning`, `Error` 等
- **风险等级色**：`RiskForbidden`, `RiskNotRecommended` 等
- **营养评分色**：`ScoreExcellent`, `ScoreGood` 等
- **餐次标签色**：`MealBreakfast`, `MealLunch`, `MealDinner`, `MealSnack`
- **状态色**：`StatusFinished`, `StatusHalf`, `StatusAllergy` 等
- **灰度色**：`Gray900`, `Gray700`, `Gray500` 等

#### 使用示例
```kotlin
// ✅ 正确：使用 Color.kt 中定义的颜色
Text(text = "Hello", color = TextPrimary)
Box(modifier = Modifier.background(SuccessContainer))

// ❌ 错误：硬编码颜色值
Text(text = "Hello", color = Color(0xFF333333))
Box(modifier = Modifier.background(Color(0xFFE8F5E9)))
```

#### 添加新颜色的流程
1. 在 `Color.kt` 中添加颜色定义
2. 使用有意义的名称
3. 添加注释说明用途
4. 在 UI 组件中引用该颜色常量
<!-- MANUAL ADDITIONS END -->