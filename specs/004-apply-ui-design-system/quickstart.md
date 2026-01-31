# Quickstart: Apply BabyFood UI Design System

**Feature**: Apply BabyFood UI Design System
**Date**: 2026-01-31
**Status**: Complete

## Overview

This guide provides step-by-step instructions for implementing the BabyFood UI design system in the application. Follow these steps to apply the new visual design with soft, eye-friendly colors, consistent component styling, and accessibility standards.

---

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Kotlin 2.0.21 or later
- Jetpack Compose 1.5.0 or later
- Material3 1.1.0 or later
- BabyFood project at commit `9e3821710e861aea9224e594d389cc666842d6ec`

---

## Phase 1: Create Design System Theme Files

### Step 1.1: Create Color.kt

**Location**: `app/src/main/java/com/example/babyfood/presentation/theme/Color.kt`

**Purpose**: Define the complete color palette for light and dark modes.

**Key Implementation**:
```kotlin
import androidx.compose.ui.graphics.Color

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

**Verification**:
- [ ] All color values match design specifications
- [ ] Color pairs meet WCAG AA contrast ratio (≥4.5:1)
- [ ] Use automated contrast checker to validate

---

### Step 1.2: Create Theme.kt

**Location**: `app/src/main/java/com/example/babyfood/presentation/theme/Theme.kt`

**Purpose**: Compose the main theme with Material3 integration.

**Key Implementation**:
```kotlin
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF9F69),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFD4BD),
    onPrimaryContainer = Color(0xFF333333),
    secondary = Color(0xFF88C999),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFDFBF8),
    onBackground = Color(0xFF333333),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF333333),
    error = Color(0xFFF5222D),
    onError = Color(0xFFFFFFFF)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE67A4A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF1E1E1E),
    onPrimaryContainer = Color(0xFFF5F5F5),
    secondary = Color(0xFF6BDB3A),
    onSecondary = Color(0xFF121212),
    background = Color(0xFF121212),
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFF5F5F5),
    error = Color(0xFFFF6666),
    onError = Color(0xFF121212)
)

@Composable
fun BabyFoodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

**Verification**:
- [ ] Theme automatically switches based on system dark mode setting
- [ ] All Material3 color slots are properly mapped
- [ ] Test theme switching on a device

---

### Step 1.3: Create Type.kt

**Location**: `app/src/main/java/com/example/babyfood/presentation/theme/Type.kt`

**Purpose**: Define typography system with font sizes, weights, and spacing.

**Key Implementation**:
```kotlin
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W400,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W400,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W400,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W400,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W400,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W400,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

**Verification**:
- [ ] Body text is at least 14sp
- [ ] Button text is at least 14sp
- [ ] Line height is 1.4-1.6x font size for body text
- [ ] Line height is 1.2-1.4x font size for headings

---

### Step 1.4: Create Shape.kt

**Location**: `app/src/main/java/com/example/babyfood/presentation/theme/Shape.kt`

**Purpose**: Define corner radius specifications for components.

**Key Implementation**:
```kotlin
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object BabyFoodShapes {
    val LargeCard = RoundedCornerShape(24.dp)
    val SmallCard = RoundedCornerShape(20.dp)
    val SmallButton = RoundedCornerShape(16.dp)
    val InputField = RoundedCornerShape(16.dp)
    val Dialog = RoundedCornerShape(28.dp)
    val SmallIcon = RoundedCornerShape(12.dp)
    val MediumIcon = RoundedCornerShape(16.dp)
}
```

**Verification**:
- [ ] All radius values match design specifications exactly
- [ ] Large cards use 24dp radius
- [ ] Small cards use 20dp radius
- [ ] Buttons use 16dp or 24dp radius
- [ ] Dialogs use 28dp radius

---

### Step 1.5: Create Elevation.kt

**Location**: `app/src/main/java/com/example/babyfood/presentation/theme/Elevation.kt`

**Purpose**: Define shadow and elevation hierarchy levels.

**Key Implementation**:
```kotlin
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object BabyFoodElevation {
    val Level1 = 2.dp   // 4dp blur radius
    val Level2 = 8.dp   // 16dp blur radius
    val Level3 = 24.dp  // 32dp blur radius
    
    // Blur radius is 2x elevation
    fun blurRadius(elevation: Dp): Dp = elevation * 2
}
```

**Verification**:
- [ ] Elevation levels match design specifications (2dp, 8dp, 24dp)
- [ ] Blur radius is 2x elevation value

---

### Step 1.6: Create Animation.kt

**Location**: `app/src/main/java/com/example/babyfood/presentation/theme/Animation.kt`

**Purpose**: Define animation timing and duration specifications.

**Key Implementation**:
```kotlin
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Spring.Companion.StiffnessMedium
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset

object BabyFoodAnimation {
    // Page transition: 300ms fade-in + 300ms slide
    val PageTransitionDuration = 300
    val PageTransitionEasing = EaseInOut
    
    // Button click: 150ms spring animation
    val ButtonClickDuration = 150
    val ButtonClickEasing = Spring(stiffness = StiffnessMedium)
    
    // Card expand: 350ms ease-out-back
    val CardExpandDuration = 350
    val CardExpandEasing = EaseOutBack
    
    // Loading: 1000ms rotation cycle
    val LoadingCycleDuration = 1000
    val LoadingEasing = LinearEasing
    
    // Refresh bounce: 200ms ease-out
    val RefreshBounceDuration = 200
    val RefreshBounceEasing = EaseOut
    
    // Animation specs
    val ButtonClickSpec: AnimationSpec<Float> = spring(
        dampingRatio = 0.8f,
        stiffness = StiffnessMedium
    )
    
    val CardExpandSpec: AnimationSpec<Float> = tween(
        durationMillis = CardExpandDuration,
        easing = CardExpandEasing
    )
    
    val PageTransitionSpec: AnimationSpec<IntOffset> = tween(
        durationMillis = PageTransitionDuration,
        easing = PageTransitionEasing
    )
}
```

**Verification**:
- [ ] All durations match design specifications
- [ ] Animations achieve 60fps with max 2 frame drops per second
- [ ] Test animations on a physical device

---

## Phase 2: Create Reusable Component Library

### Step 2.1: Create Button.kt

**Location**: `app/src/main/java/com/example/babyfood/presentation/theme/components/Button.kt`

**Purpose**: Implement primary and secondary button components with design system styling.

**Key Implementation**:
```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BabyFoodPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        interactionSource = remember { MutableInteractionSource() }
    ) {
        if (loading) {
            // Loading indicator
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
```

**Verification**:
- [ ] Button uses 45-degree gradient from #FF9F69 to #FFD4BD
- [ ] Corner radius is 24dp
- [ ] Touch target is at least 44dp x 44dp (preferably 48dp x 48dp)
- [ ] Click animation scales 0.95→1.0 over 150ms

---

### Step 2.2: Create Card.kt

**Location**: `app/src/main/java/com/example/babyfood/presentation/theme/components/Card.kt`

**Purpose**: Implement large and small card components with design system styling.

**Key Implementation**:
```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun BabyFoodLargeCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = BabyFoodElevation.Level1
        )
    ) {
        content()
    }
}
```

**Verification**:
- [ ] Large cards use 24dp corner radius
- [ ] Small cards use 20dp corner radius
- [ ] Cards apply Level 1 shadow (2dp elevation, 4dp blur)
- [ ] Large cards use vertical gradient from #FFFFFF to #FDFBF8

---

### Step 2.3: Create InputField.kt

**Location**: `app/src/main/java/com/example/babyfood/presentation/theme/components/InputField.kt`

**Purpose**: Implement text input field with design system styling.

**Key Implementation**:
```kotlin
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun BabyFoodInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 48.dp),
        placeholder = placeholder?.let { { Text(it) } },
        enabled = enabled,
        isError = isError,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color(0xFFF5F5F5),
            focusedIndicatorColor = if (isError) Color(0xFFF5222D) else Color(0xFFFF9F69),
            unfocusedIndicatorColor = Color(0xFFE5E5E5),
            errorIndicatorColor = Color(0xFFF5222D)
        ),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}
```

**Verification**:
- [ ] Corner radius is 16dp
- [ ] Border width is 1dp (default) or 2dp (focus/error)
- [ ] Focus border color is #FF9F69
- [ ] Error border color is #F5222D
- [ ] Touch target is at least 44dp x 44dp

---

## Phase 3: Apply Theme to Application

### Step 3.1: Wrap MainActivity with BabyFoodTheme

**Location**: `app/src/main/java/com/example/babyfood/MainActivity.kt`

**Action**: Wrap the existing content with `BabyFoodTheme`:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    setContent {
        BabyFoodTheme {
            BabyFoodApp()
        }
    }
}
```

**Verification**:
- [ ] App launches with correct theme
- [ ] Theme switches automatically when system dark mode changes
- [ ] All screens use consistent styling

---

### Step 3.2: Update High-Priority Screens

**Priority Order**:
1. Home screen (Today's menu)
2. Baby profile screen
3. Recipe list/detail screens
4. Health record screens
5. Growth curve screens

**Action**: Replace existing UI components with design system components:

**Example - Update Home Screen Card**:
```kotlin
// Before
Card(
    modifier = Modifier.padding(8.dp),
    elevation = CardDefaults.cardElevation(4.dp)
) {
    // Content
}

// After
BabyFoodLargeCard(
    modifier = Modifier.padding(8.dp)
) {
    // Content
}
```

**Verification**:
- [ ] All cards use correct corner radius
- [ ] All colors match design specifications
- [ ] All touch targets meet accessibility requirements
- [ ] Test on multiple screen sizes (320dp, 360dp, 412dp)

---

## Phase 4: Testing and Validation

### Step 4.1: Unit Tests

**Location**: `app/src/test/java/com/example/babyfood/theme/`

**Create tests for**:
- Color contrast ratios
- Corner radius values
- Animation durations
- Touch target sizes

**Example Test**:
```kotlin
@Test
fun testPrimaryTextContrastRatio() {
    val backgroundColor = BabyFoodColors.pageBackground
    val textColor = BabyFoodColors.primaryText
    val contrastRatio = calculateContrastRatio(textColor, backgroundColor)
    assertTrue(contrastRatio >= 4.5f, "Contrast ratio must be ≥4.5:1")
}
```

**Verification**:
- [ ] All unit tests pass
- [ ] Contrast ratios meet WCAG AA standard
- [ ] All design specifications are enforced

---

### Step 4.2: Integration Tests

**Location**: `app/src/androidTest/java/com/example/babyfood/theme/`

**Create tests for**:
- Theme switching (light/dark mode)
- Responsive layout adaptation
- Component rendering on different screen sizes

**Verification**:
- [ ] Theme switches correctly when system dark mode changes
- [ ] Layouts adapt to different screen sizes without breaking
- [ ] All components render correctly on different devices

---

### Step 4.3: UI Tests

**Location**: `app/src/androidTest/java/com/example/babyfood/ui/`

**Create tests for**:
- Visual regression (compare screenshots before/after)
- Accessibility (touch targets, contrast ratios)
- Animation smoothness (60fps with max 2 frame drops)

**Verification**:
- [ ] No visual regressions detected
- [ ] All accessibility requirements met
- [ ] Animations are smooth at 60fps

---

## Phase 5: Performance Optimization

### Step 5.1: Optimize Animation Performance

**Actions**:
- Use `remember` for expensive calculations
- Use `key` parameters for stable compositions
- Avoid expensive operations in composition
- Use `LaunchedEffect` for side effects

**Verification**:
- [ ] Animations achieve 60fps
- [ ] Maximum 2 frame drops per second
- [ ] No jank or stuttering

---

### Step 5.2: Optimize Layout Performance

**Actions**:
- Use `BoxWithConstraints` for responsive layouts
- Avoid unnecessary recompositions
- Use `Modifier.padding` instead of nested `PaddingValues`

**Verification**:
- [ ] Layouts render efficiently on different screen sizes
- [ ] No performance issues on older devices

---

## Verification Checklist

Before marking this feature complete, verify:

- [ ] All 55 functional requirements are implemented
- [ ] All 12 success criteria are met
- [ ] Design system applies to all screens (~20+ screens)
- [ ] Light/dark mode switching works correctly
- [ ] All color contrast ratios meet WCAG AA standard
- [ ] 90% of color combinations achieve WCAG AAA standard
- [ ] All touch targets are at least 44dp x 44dp
- [ ] All corner radii match design specifications
- [ ] All shadows/elevations are applied correctly
- [ ] All animations complete within specified durations
- [ ] Animations achieve 60fps with max 2 frame drops per second
- [ ] Layouts adapt correctly to different screen sizes
- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] All UI tests pass
- [ ] No visual regressions detected
- [ ] App builds successfully
- [ ] App runs on physical device without crashes

---

## Common Issues and Solutions

### Issue: Theme not applying to some screens

**Solution**: Ensure `BabyFoodTheme` wraps the entire app at the top level in `MainActivity`.

### Issue: Colors don't match design specifications

**Solution**: Verify color hex values are correct in `Color.kt`. Use automated contrast checker to validate.

### Issue: Animations are not smooth

**Solution**: Use Compose's built-in animation APIs and follow performance best practices (remember, key, LaunchedEffect).

### Issue: Layout breaks on small screens

**Solution**: Use `BoxWithConstraints` and responsive layout modifiers. Test on minimum 320dp screen width.

### Issue: Touch targets are too small

**Solution**: Use `Modifier.sizeIn(minWidth = 44.dp, minHeight = 44.dp)` on all interactive elements.

---

## Next Steps

After completing this quickstart:

1. Run `/speckit.tasks` to generate the detailed task breakdown
2. Implement the design system following the task order
3. Run automated tests to validate implementation
4. Perform manual visual testing on multiple devices
5. Gather user feedback and iterate

---

## Resources

- [Jetpack Compose Theming Documentation](https://developer.android.com/jetpack/compose/theming)
- [Material3 Design Guidelines](https://m3.material.io/)
- [WCAG 2.1 Accessibility Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [Android Accessibility Testing](https://developer.android.com/guide/topics/ui/accessibility/testing)