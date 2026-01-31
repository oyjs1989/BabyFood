# Implementation Plan: Apply BabyFood UI Design System

**Branch**: `004-apply-ui-design-system` | **Date**: 2026-01-31 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/004-apply-ui-design-system/spec.md`

## Summary

Implement a comprehensive UI design system for the BabyFood Android application that applies soft, eye-friendly colors with low saturation and clear hierarchy, optimized for the baby food scenario with light/dark mode support. The design system defines primary, auxiliary, neutral, and functional color palettes, consistent component specifications (corner radii, shadows, gradients), typography system, animation definitions, responsive layout constraints, and accessibility standards meeting WCAG AA/AAA requirements. This is a visual design enhancement that affects all UI components across the application without changing core functionality or data models.

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Jetpack Compose (Material3), AndroidX Core KTX, Hilt, Room, Vico (charts), Kotlinx DateTime
**Storage**: Room SQLite database (unchanged)
**Testing**: JUnit 4.13.2, Android JUnit 1.1.5, Espresso 3.5.1
**Target Platform**: Android 7.0+ (minSdk 24), Android 14 (targetSdk 34)
**Project Type**: Mobile Android app (Jetpack Compose + MVVM architecture)
**Performance Goals**: 60fps animations with maximum 2 frame drops per second (16.6ms per frame average)
**Constraints**: Must maintain WCAG AA contrast ratio (≥4.5:1 for body text), touch targets ≥44dp x 44dp, support both light and dark modes
**Scale/Scope**: Affects all UI screens (~20+ screens) and components across the entire application

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Applicable Principles** (based on project context):
- **Simplicity**: Design system should be implemented as a centralized theme module with reusable components, avoiding duplication across screens
- **Consistency**: All UI components must follow the defined color palette, corner radius, shadow, and animation specifications without exceptions
- **Accessibility**: WCAG AA/AAA compliance is mandatory for all text contrast and touch target sizes
- **Performance**: Animations must achieve 60fps with maximum 2 frame drops per second

**Gate Status**: ✅ PASS - No violations expected. Design system enforces consistency and simplicity through centralized theming.

## Project Structure

### Documentation (this feature)

```
specs/004-apply-ui-design-system/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
│   └── ui-design-system.yaml
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```
app/src/main/java/com/example/babyfood/
├── presentation/
│   └── theme/             # Design system implementation
│       ├── Color.kt       # Color palette definitions (light/dark modes)
│       ├── Theme.kt       # Theme composition and Material3 configuration
│       ├── Type.kt        # Typography system (font sizes, weights, spacing)
│       ├── Shape.kt       # Corner radius and shape definitions
│       ├── Elevation.kt   # Shadow and elevation hierarchy
│       ├── Animation.kt   # Animation specifications (timing, duration)
│       └── components/    # Reusable UI components following design system
│       ├── Button.kt      # Primary/secondary button components
│       ├── Card.kt        # Large/small card components
│       ├── InputField.kt  # Text input and search field components
│       ├── Dialog.kt      # Modal dialog components
│       ├── Icon.kt        # Icon container components
│       ├── ProgressBar.kt # Nutrition progress bar components
│       └── Chart.kt       # Growth curve chart components
└── ui/                    # Existing UI screens (will be updated to use design system)
    ├── home/
    ├── recipes/
    ├── plans/
    ├── health/
    ├── growth/
    ├── inventory/
    ├── baby/
    ├── ai/
    └── auth/

tests/
├── unit/
│   └── theme/             # Unit tests for theme components
│       ├── ColorTest.kt
│       ├── ShapeTest.kt
│       ├── AnimationTest.kt
│       └── AccessibilityTest.kt
└── integration/
    └── ui/                # Integration tests for design system across screens
```

**Structure Decision**: Centralized design system in `presentation/theme/` directory following Android app structure. Theme components are reusable composables that existing screens will consume. No new modules or projects needed - this is a refactoring of existing UI to apply consistent styling.

## Complexity Tracking

*No violations requiring justification. Design system implementation simplifies and standardizes UI across the application.*