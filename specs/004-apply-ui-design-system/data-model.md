# Data Model: Apply BabyFood UI Design System

**Feature**: Apply BabyFood UI Design System
**Date**: 2026-01-31
**Status**: Complete

## Overview

This document defines the data model for the UI design system implementation. Since this is a visual design enhancement that applies styling to existing UI components, no new database entities or data structures are introduced. The data model focuses on the design system's configuration objects, color palettes, and theme definitions.

## Important Note

This feature does **not** introduce any new data entities, database tables, or API contracts. It is purely a UI styling enhancement that affects how existing data is presented visually. All existing data models (Baby, Plan, Recipe, etc.) remain unchanged.

---

## Design System Configuration Objects

### 1. BabyFoodColors

Defines the complete color palette for both light and dark modes.

```kotlin
data class BabyFoodColors(
    // Light mode colors
    val pageBackground: Color = Color(0xFFFDFBF8),
    val cardBackground: Color = Color(0xFFFFFFFF),
    val primaryText: Color = Color(0xFF333333),
    val secondaryText: Color = Color(0xFF666666),
    val auxiliaryText: Color = Color(0xFF999999),
    val divider: Color = Color(0xFFE5E5E5),
    
    // Brand colors
    val primaryBrand: Color = Color(0xFFFF9F69),
    val extendedBrand: Color = Color(0xFFFFD4BD),
    val softGreen: Color = Color(0xFF88C999),
    val freshBlue: Color = Color(0xFF73A6FF),
    val creamYellow: Color = Color(0xFFFFE8A3),
    
    // Functional colors
    val success: Color = Color(0xFF52C41A),
    val warning: Color = Color(0xFFFAAD14),
    val error: Color = Color(0xFFF5222D),
    
    // Dark mode colors
    val pageBackgroundDark: Color = Color(0xFF121212),
    val cardBackgroundDark: Color = Color(0xFF1E1E1E),
    val primaryTextDark: Color = Color(0xFFF5F5F5),
    val secondaryTextDark: Color = Color(0xFFC1C1C1),
    val auxiliaryTextDark: Color = Color(0xFF8E8E8E),
    val dividerDark: Color = Color(0xFF3D3D3D),
    val primaryBrandDark: Color = Color(0xFFE67A4A),
    val successDark: Color = Color(0xFF6BDB3A),
    val errorDark: Color = Color(0xFFFF6666)
)
```

**Validation Rules**:
- All color values must be valid hex color codes
- Light/dark mode color pairs must maintain WCAG AA contrast ratio (≥4.5:1)
- Brand colors must be consistent across all components

**State Transitions**: N/A (static configuration)

### 2. BabyFoodTypography

Defines the typography system with font sizes, weights, line heights, and letter spacing.

```kotlin
data class BabyFoodTypography(
    // Display styles
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,
    
    // Headline styles
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,
    
    // Title styles
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    
    // Body styles
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    
    // Label styles
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle
)
```

**Validation Rules**:
- Body text must be at least 14sp
- Button text must be at least 14sp
- Line height must be 1.4-1.6 times font size for body text
- Line height must be 1.2-1.4 times font size for headings
- Character spacing must be 0.1-0.3sp for body text
- Character spacing must be 0-0.2sp for headings

**State Transitions**: N/A (static configuration)

### 3. BabyFoodShapes

Defines corner radius values for different component types.

```kotlin
data class BabyFoodShapes(
    val largeCardRadius: Dp = 24.dp,
    val smallCardRadius: Dp = 20.dp,
    val smallButtonRadius: Dp = 16.dp,
    val inputFieldRadius: Dp = 16.dp,
    val dialogRadius: Dp = 28.dp,
    val smallIconRadius: Dp = 12.dp,
    val mediumIconRadius: Dp = 16.dp
)
```

**Validation Rules**:
- All radius values must be positive
- Radius values must match design specifications exactly

**State Transitions**: N/A (static configuration)

### 4. BabyFoodElevation

Defines shadow and elevation hierarchy levels.

```kotlin
data class BabyFoodElevation(
    val level1: Elevation = Elevation(2.dp),   // 4dp blur radius
    val level2: Elevation = Elevation(8.dp),   // 16dp blur radius
    val level3: Elevation = Elevation(24.dp)   // 32dp blur radius
)
```

**Validation Rules**:
- Elevation levels must match design specifications (2dp, 8dp, 24dp)
- Blur radius must be 2x elevation value

**State Transitions**: N/A (static configuration)

### 5. BabyFoodAnimation

Defines animation timing and duration specifications.

```kotlin
data class BabyFoodAnimation(
    val pageTransitionDuration: Int = 600,      // ms (300ms fade-in + 300ms slide)
    val pageTransitionEasing: Easing = EaseInOut,
    
    val buttonClickDuration: Int = 150,         // ms
    val buttonClickEasing: Easing = Spring(stiffness = Spring.StiffnessMedium),
    
    val cardExpandDuration: Int = 350,          // ms
    val cardExpandEasing: Easing = EaseOutBack,
    
    val loadingCycleDuration: Int = 1000,       // ms per rotation
    val loadingEasing: Easing = LinearEasing,
    
    val refreshBounceDuration: Int = 200,       // ms
    val refreshBounceEasing: Easing = EaseOut
)
```

**Validation Rules**:
- All durations must be positive integers
- Animation performance must achieve 60fps with max 2 frame drops per second

**State Transitions**: N/A (static configuration)

### 6. BabyFoodLayout

Defines responsive layout constraints and breakpoints.

```kotlin
data class BabyFoodLayout(
    val minScreenWidth: Dp = 320.dp,
    val standardScreenWidth: Dp = 360.dp,
    val maxScreenWidth: Dp = 412.dp,
    val minSpacing: Dp = 8.dp,
    val standardSpacing: Dp = 16.dp,
    val largeSpacing: Dp = 24.dp
)
```

**Validation Rules**:
- All spacing values must be multiples of 8dp
- Screen widths must match design specifications

**State Transitions**: N/A (static configuration)

### 7. BabyFoodAccessibility

Defines accessibility standards and requirements.

```kotlin
data class BabyFoodAccessibility(
    val minTouchTargetSize: Dp = 44.dp,
    val preferredTouchTargetSize: Dp = 48.dp,
    val minContrastRatio: Float = 4.5f,        // WCAG AA
    val preferredContrastRatio: Float = 7.0f   // WCAG AAA
)
```

**Validation Rules**:
- Touch target sizes must meet minimum 44dp x 44dp
- Contrast ratios must meet WCAG AA standard (≥4.5:1)
- 90% of color combinations should achieve WCAG AAA (≥7:1)

**State Transitions**: N/A (static configuration)

---

## Theme Composition

### BabyFoodTheme

The main theme composable that composes all design system configuration objects.

```kotlin
@Composable
fun BabyFoodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkColorScheme else lightColorScheme
    val typography = BabyFoodTypography()
    val shapes = BabyFoodShapes()
    val elevation = BabyFoodElevation()
    val animation = BabyFoodAnimation()
    val layout = BabyFoodLayout()
    val accessibility = BabyFoodAccessibility()
    
    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
```

**Validation Rules**:
- Theme must automatically switch between light and dark modes based on system setting
- All configuration objects must be properly composed
- Theme must be applied at the top level of the app

**State Transitions**: Theme switches between light and dark modes when system setting changes

---

## Component Style Objects

### ButtonStyles

Defines styling for different button types.

```kotlin
data class ButtonStyles(
    val primary: ButtonStyle,
    val secondary: ButtonStyle
)

data class ButtonStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val cornerRadius: Dp,
    val elevation: Elevation,
    val gradient: Brush?,
    val clickAnimation: AnimationSpec<Float>
)
```

**Validation Rules**:
- Primary buttons must use 45-degree gradient from #FF9F69 to #FFD4BD
- Secondary buttons must use 16dp corner radius
- All buttons must have minimum 44dp x 44dp touch target

**State Transitions**:
- Default → Hover (shadow intensity increases)
- Hover → Click (scale 0.95→1.0 over 150ms)
- Click → Disabled (no shadow, grayed out)

### CardStyles

Defines styling for different card types.

```kotlin
data class CardStyles(
    val large: CardStyle,
    val small: CardStyle
)

data class CardStyle(
    val backgroundColor: Color,
    val cornerRadius: Dp,
    val elevation: Elevation,
    val gradient: Brush?,
    val expandAnimation: AnimationSpec<Dp>
)
```

**Validation Rules**:
- Large cards must use 24dp corner radius
- Small cards must use 20dp corner radius
- Large cards must use vertical (90-degree) gradient from #FFFFFF to #FDFBF8

**State Transitions**:
- Default → Expanded (height animates from 0 to target over 350ms)

### InputFieldStyles

Defines styling for text input fields.

```kotlin
data class InputFieldStyle(
    val backgroundColor: Color,
    val borderColor: Color,
    val focusBorderColor: Color,
    val errorBorderColor: Color,
    val cornerRadius: Dp,
    val borderWidth: Dp
)
```

**Validation Rules**:
- Corner radius must be 16dp
- Border width must be 1dp (default) or 2dp (focus/error)
- Minimum touch target must be 44dp x 44dp

**State Transitions**:
- Default → Focus (border color changes to #FF9F69, width increases to 2dp)
- Focus → Error (border color changes to #F5222D, width stays 2dp)

---

## Data Flow

```
User System Settings (Dark Mode)
    ↓
BabyFoodTheme (Composable)
    ↓
├── BabyFoodColors (Light/Dark)
├── BabyFoodTypography
├── BabyFoodShapes
├── BabyFoodElevation
├── BabyFoodAnimation
├── BabyFoodLayout
└── BabyFoodAccessibility
    ↓
MaterialTheme
    ↓
├── Component Styles (Button, Card, InputField, etc.)
└── UI Screens (Home, Recipes, Plans, etc.)
```

---

## Summary

This feature introduces 7 new data classes for design system configuration:
1. `BabyFoodColors` - Color palette (light/dark modes)
2. `BabyFoodTypography` - Typography system
3. `BabyFoodShapes` - Corner radius specifications
4. `BabyFoodElevation` - Shadow/elevation hierarchy
5. `BabyFoodAnimation` - Animation specifications
6. `BabyFoodLayout` - Responsive layout constraints
7. `BabyFoodAccessibility` - Accessibility standards

No new database entities, API contracts, or data structures are introduced. All existing data models remain unchanged. The design system is purely a UI styling layer that affects how existing data is presented visually.