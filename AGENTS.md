# BabyFood Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-02-01

## Active Technologies
- Kotlin 2.0.21 + Jetpack Compose + Material Design 3 (003-remove-page-back-and-titles)
- Room Database + SQLite (005-optimize-app-food-guidelines)
- Nutrition Data Integration (005-optimize-app-food-guidelines)
- Safety Risk Analysis System (005-optimize-app-food-guidelines)
- Texture Adaptation Engine (005-optimize-app-food-guidelines)
- Flavor Diversity Tracking (005-optimize-app-food-guidelines)
- Freshness & Cooking Method Advisor (005-optimize-app-food-guidelines)
- Nutrition Goal Matching (005-optimize-app-food-guidelines)

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
- Room for local database

### Database
- Room database version: 15
- Supports migrations up to version 15
- TypeConverters for complex types (LocalDate, enums)
- New tables: safety_risks, ingredient_trials, nutrition_goals, nutrition_data, user_warning_ignores

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
- Health analysis (local rules + remote LLM)
- Recipe recommendations (AI-powered)
- Strategy pattern for AI service selection
- Iron-priority recommendations (IronRichStrategy)

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
<!-- MANUAL ADDITIONS END -->