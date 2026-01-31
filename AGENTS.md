# BabyFood Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-01-31

## Active Technologies
- Kotlin 2.0.21 + Jetpack Compose + Material Design 3 (003-remove-page-back-and-titles)

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
- Room database version: 14
- Supports migrations up to version 14
- TypeConverters for complex types (LocalDate, enums)

### AI Features
- Health analysis (local rules + remote LLM)
- Recipe recommendations (AI-powered)
- Strategy pattern for AI service selection

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