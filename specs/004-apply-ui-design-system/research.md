# Research: Apply BabyFood UI Design System

**Feature**: Apply BabyFood UI Design System
**Date**: 2026-01-31
**Status**: Complete

## Overview

This document consolidates research findings for implementing the BabyFood UI design system, covering color palette, component styling, typography, animations, accessibility, and dark mode support in Jetpack Compose for Android.

---

## 1. Color Palette Implementation

### Research Topic: How to implement light/dark mode color palettes in Jetpack Compose

**Decision**: Use Jetpack Compose Material3 `ColorScheme` with separate light and dark theme definitions, leveraging `isSystemInDarkTheme()` for automatic switching.

**Rationale**: Material3 provides built-in support for light/dark themes with proper color semantics. Compose's theming system allows defining color palettes once and automatically applying them across the app. This approach is standard, well-documented, and integrates seamlessly with system dark mode settings.

**Alternatives Considered**:
- Custom color system without Material3: More flexibility but requires significant boilerplate and manual theme switching logic
- Static color objects: Simpler but doesn't support dynamic theme switching
- Third-party theming libraries: Unnecessary complexity when Material3 provides all needed functionality

### Research Topic: How to define custom colors in Material3 theme

**Decision**: Define custom color properties in a `BabyFoodColors` data class, then map them to Material3 color slots (primary, secondary, background, surface, etc.) when creating `ColorScheme`.

**Rationale**: This approach maintains separation between the design system's color definitions and Material3's semantic color slots, making it easier to update either independently. The mapping ensures proper semantic usage of colors throughout the app.

**Alternatives Considered**:
- Direct use of hex values throughout the app: Violates DRY principle and makes theme updates difficult
- Material3 only without custom colors: Limits design system expressiveness

### Research Topic: How to ensure WCAG contrast ratios

**Decision**: Implement automated contrast ratio checks using Compose's `Color` API to calculate relative luminance and contrast ratios. Use this during development to validate all color combinations.

**Rationale**: Automated checks ensure compliance and catch violations early. Compose's `Color` class provides `luminance()` property which makes contrast calculation straightforward.

**Alternatives Considered**:
- Manual visual inspection: Prone to human error and not scalable
- External accessibility tools: Good for validation but not integrated into development workflow

---

## 2. Component Styling (Corner Radius, Shadows, Gradients)

### Research Topic: How to implement consistent corner radii in Jetpack Compose

**Decision**: Define corner radius values as `Dp` constants in a `Shape.kt` file, then use `Modifier.clip(RoundedCornerShape())` and `Modifier.border()` to apply them consistently.

**Rationale**: Centralizing corner radius values ensures consistency and makes updates easy. Compose's `RoundedCornerShape` API is straightforward and supports all required radii (12dp, 16dp, 20dp, 24dp, 28dp).

**Alternatives Considered**:
- Hardcoded values in each component: Violates DRY principle and makes updates error-prone
- Custom shape classes: Over-engineering for simple corner radius requirements

### Research Topic: How to implement shadow/elevation hierarchy in Material3

**Decision**: Use Material3's built-in elevation system with `Modifier.shadow()` and `Surface` elevation parameter. Define custom shadow colors if Material3 defaults don't match design specs.

**Rationale**: Material3's elevation system is well-designed and provides consistent shadow behavior across components. The 3-level hierarchy (Level 1: 2dp/4dp, Level 2: 8dp/16dp, Level 3: 24dp/32dp) maps directly to Material3's elevation levels.

**Alternatives Considered**:
- Custom shadow implementations with `Modifier.shadow()`: More control but requires manual tuning
- Ignoring shadows: Violates design system requirements

### Research Topic: How to implement gradients in Jetpack Compose

**Decision**: Use `Brush.linearGradient()` and `Brush.verticalGradient()` with defined color stops to create gradients for buttons, cards, and progress bars.

**Rationale**: Compose's `Brush` API provides a simple way to create gradients with full control over colors, angles, and stops. This approach is performant and integrates well with Compose's rendering system.

**Alternatives Considered**:
- Custom draw calls with `Canvas`: Overly complex for simple gradient requirements
- Image-based gradients: Not scalable and increases app size

---

## 3. Typography System

### Research Topic: How to define typography scale in Material3

**Decision**: Use Material3's `Typography` class to define font sizes, weights, line heights, and letter spacing for all text styles (display, headline, title, body, label).

**Rationale**: Material3's typography system provides a structured approach to defining text styles with proper defaults. This ensures consistency and supports dynamic font sizes based on user preferences.

**Alternatives Considered**:
- Custom typography classes: More control but breaks integration with Material3 components
- Hardcoded text styles: Violates DRY principle and makes updates difficult

### Research Topic: How to support dynamic font sizes

**Decision**: Use `FontWeight` and `TextStyle` with `sp` units (scale-independent pixels) instead of `dp` units. Compose automatically scales `sp` based on user's font size preference.

**Rationale**: Using `sp` units ensures text scales appropriately for users with accessibility needs (larger font sizes). This is Android best practice and required for accessibility compliance.

**Alternatives Considered**:
- Fixed `dp` units: Doesn't support accessibility requirements
- Custom scaling logic: Unnecessary when Compose provides built-in support

---

## 4. Animation Specifications

### Research Topic: How to implement smooth animations in Jetpack Compose

**Decision**: Use Compose's animation APIs (`animateFloatAsState`, `animateContentSize`, `AnimatedVisibility`) with defined durations and easing functions. Use `rememberInfiniteTransition` for looping animations.

**Rationale**: Compose's animation APIs are performant, easy to use, and integrate seamlessly with recomposition. They provide the required timing functions (ease-in-out, ease-out-back, linear, spring) and durations.

**Alternatives Considered**:
- Manual value interpolation: Overly complex and error-prone
- External animation libraries: Unnecessary when Compose provides all needed functionality

### Research Topic: How to achieve 60fps with minimal frame drops

**Decision**: Use Compose's built-in optimizations (skipping, stable classes, key parameter) and avoid expensive operations in composition. Use `LaunchedEffect` and `remember` for side effects and expensive computations.

**Rationale**: Compose's performance optimizations are well-tested and handle most cases automatically. Following best practices ensures smooth animations with minimal frame drops.

**Alternatives Considered**:
- Manual frame rate monitoring: Useful for debugging but not required for implementation
- Reducing animation complexity: Unnecessary if using Compose correctly

---

## 5. Accessibility Standards

### Research Topic: How to ensure touch target size requirements

**Decision**: Use `Modifier.sizeIn(minWidth = 44.dp, minHeight = 44.dp)` or `Modifier.padding()` to ensure all interactive elements meet the 44dp x 44dp minimum (preferably 48dp x 48dp).

**Rationale**: Compose's `sizeIn` modifier makes it easy to enforce minimum touch target sizes. This is a simple, declarative approach that ensures accessibility compliance.

**Alternatives Considered**:
- Manual size enforcement in each component: Error-prone and violates DRY principle
- Ignoring touch target sizes: Violates accessibility requirements

### Research Topic: How to support screen readers

**Decision**: Use Compose's `semantics` modifier to provide meaningful content descriptions for screen readers. Ensure all images, icons, and interactive elements have appropriate semantic labels.

**Rationale**: Compose's semantics API provides a standard way to make UI accessible to screen readers. This is essential for users with visual impairments.

**Alternatives Considered**:
- Ignoring screen readers: Violates accessibility requirements
- Custom accessibility implementations: Unnecessary when Compose provides built-in support

---

## 6. Dark Mode Support

### Research Topic: How to implement automatic dark mode switching

**Decision**: Use `isSystemInDarkTheme()` Compose function to detect system dark mode setting. Provide separate `ColorScheme` objects for light and dark themes, and select the appropriate one based on system setting.

**Rationale**: This approach is standard, well-tested, and provides a seamless user experience. Users don't need to manually toggle dark mode in the app.

**Alternatives Considered**:
- Manual dark mode toggle in app settings: Additional complexity and doesn't follow system preference
- Ignoring dark mode: Violates design system requirements

### Research Topic: How to adjust colors for dark mode

**Decision**: Define separate color values for light and dark modes in the `BabyFoodColors` data class. Use the documented HSL adjustment formulas to calculate dark mode colors from light mode colors.

**Rationale**: Having separate color definitions for each mode ensures precise control over appearance. The HSL formulas provide a systematic way to derive dark mode colors while maintaining visual hierarchy.

**Alternatives Considered**:
- Runtime color adjustment: More complex and harder to debug
- Ignoring dark mode color adjustments: Results in poor readability and eye strain

---

## 7. Responsive Layout Adaptation

### Research Topic: How to implement responsive layouts in Jetpack Compose

**Decision**: Use Compose's `BoxWithConstraints`, `WindowSizeClass`, and `Orientation` APIs to adapt layouts based on screen size and orientation. Use `dp` values that scale appropriately.

**Rationale**: Compose provides built-in APIs for responsive design that are easy to use and performant. The defined screen size breakpoints (320dp, 360dp, 412dp) map well to common device sizes.

**Alternatives Considered**:
- Custom layout logic: Overly complex and error-prone
- Fixed layouts: Doesn't work across different device sizes

### Research Topic: How to handle landscape orientation

**Decision**: Use `LocalConfiguration.current.orientation` to detect orientation changes. Define separate layout composable functions for portrait and landscape modes.

**Rationale**: Detecting orientation changes allows adapting layouts appropriately. Separate composable functions keep code organized and maintainable.

**Alternatives Considered**:
- Ignoring orientation: Results in poor user experience on landscape
- Complex orientation-aware logic: Over-engineering for simple use cases

---

## 8. Icon and Illustration Standards

### Research Topic: How to implement consistent icon styling

**Decision**: Use Material Icons (via `androidx.compose.material.icons`) with custom `Modifier` to apply stroke width, rounded corners, and size specifications. Define icon size constants (24dp, 32dp, 48dp).

**Rationale**: Material Icons provide a comprehensive, accessible icon library. Compose's modifier system allows applying custom styling while maintaining accessibility.

**Alternatives Considered**:
- Custom icon assets: Increases app size and maintenance burden
- Ignoring icon styling: Violates design system requirements

### Research Topic: How to implement illustrations

**Decision**: Use vector drawables (XML) for illustrations, defining them with flat minimalist style, soft rounded corners, and limited color palette (max 3 colors per illustration).

**Rationale**: Vector drawables are scalable, performant, and can be styled dynamically. This approach matches the design system's illustration requirements.

**Alternatives Considered**:
- Raster images: Not scalable and increases app size
- Ignoring illustrations: Misses opportunity for visual enhancement

---

## 9. Testing Strategy

### Research Topic: How to test design system implementation

**Decision**: Implement unit tests for color calculations, contrast ratios, and component styling. Implement integration tests for theme switching and responsive layouts. Use UI tests to verify visual appearance on different screen sizes and themes.

**Rationale**: Comprehensive testing ensures design system compliance across all scenarios. Unit tests validate individual components, integration tests verify theme behavior, and UI tests catch visual regressions.

**Alternatives Considered**:
- Manual testing only: Prone to human error and not scalable
- Ignoring testing: Increases risk of design system violations

---

## 10. Migration Strategy

### Research Topic: How to migrate existing UI to new design system

**Decision**: Implement design system components first, then migrate screens incrementally starting with high-priority screens (home, baby profile). Ensure backward compatibility during migration.

**Rationale**: Incremental migration reduces risk and allows testing each change independently. Starting with high-priority screens delivers value early.

**Alternatives Considered**:
- Big bang migration: High risk and difficult to debug
- Ignoring migration: Existing UI doesn't benefit from design system

---

## Summary

All research questions have been resolved. The design system implementation will use:
- Jetpack Compose Material3 for theming and components
- Centralized theme definitions in `presentation/theme/` directory
- Standard Compose APIs for animations, responsiveness, and accessibility
- Automated testing for contrast ratios and component styling
- Incremental migration strategy for existing UI

The approach is standard, well-documented, and aligned with Android best practices. No NEEDS CLARIFICATION items remain.