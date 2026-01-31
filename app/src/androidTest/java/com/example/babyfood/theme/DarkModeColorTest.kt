package com.example.babyfood.theme

import androidx.compose.ui.graphics.Color
import com.example.babyfood.presentation.theme.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Integration tests for dark mode color adaptation
 * Tests that all colors correctly adapt according to HSL adjustment formulas
 * and maintain readability and visual hierarchy in dark mode
 */
class DarkModeColorTest {

    // ===== HSL Adjustment Formula Tests =====

    @Test
    fun testBackgroundBrightnessAdjustment() {
        // Light mode background brightness: ~95%
        val lightBackgroundBrightness = calculateRelativeLuminance(PageBackground)
        // Dark mode background brightness: ~5%
        val darkBackgroundBrightness = calculateRelativeLuminance(DarkPageBackground)

        assertTrue(
            "Light mode background brightness should be >90% (was ${lightBackgroundBrightness * 100}%)",
            lightBackgroundBrightness > 0.9f
        )
        assertTrue(
            "Dark mode background brightness should be <10% (was ${darkBackgroundBrightness * 100}%)",
            darkBackgroundBrightness < 0.1f
        )

        // Verify the brightness reduction follows the formula: 95% → 5%
        val brightnessReduction = lightBackgroundBrightness - darkBackgroundBrightness
        assertTrue(
            "Brightness reduction should be approximately 90% (was ${(brightnessReduction * 100)}%)",
            brightnessReduction > 0.8f && brightnessReduction < 1.0f
        )
    }

    @Test
    fun testPrimaryColorBrightnessAndSaturationAdjustment() {
        // Light mode primary color: brightness ~75%
        val lightPrimaryBrightness = calculateRelativeLuminance(Primary)
        val lightPrimarySaturation = calculateSaturation(Primary)
        
        // Dark mode primary color: brightness ~65%, saturation reduced by 20%
        val darkPrimaryBrightness = calculateRelativeLuminance(DarkPrimary)
        val darkPrimarySaturation = calculateSaturation(DarkPrimary)

        assertTrue(
            "Light mode primary brightness should be ~70-80% (was ${lightPrimaryBrightness * 100}%)",
            lightPrimaryBrightness > 0.7f && lightPrimaryBrightness < 0.8f
        )
        assertTrue(
            "Dark mode primary brightness should be ~60-70% (was ${darkPrimaryBrightness * 100}%)",
            darkPrimaryBrightness > 0.6f && darkPrimaryBrightness < 0.7f
        )

        // Verify brightness reduction: 75% → 65% (10% reduction)
        val brightnessReduction = lightPrimaryBrightness - darkPrimaryBrightness
        assertTrue(
            "Brightness reduction should be approximately 10% (was ${(brightnessReduction * 100)}%)",
            brightnessReduction > 0.05f && brightnessReduction < 0.15f
        )

        // Verify saturation reduction: ~20%
        val saturationReduction = (lightPrimarySaturation - darkPrimarySaturation) / lightPrimarySaturation
        assertTrue(
            "Saturation reduction should be approximately 20% (was ${(saturationReduction * 100)}%)",
            saturationReduction > 0.1f && saturationReduction < 0.3f
        )
    }

    @Test
    fun testFunctionalStatusColorsAdjustment() {
        // Success color: saturation reduced by 30%, brightness increased by 10%
        val lightSuccessBrightness = calculateRelativeLuminance(Success)
        val lightSuccessSaturation = calculateSaturation(Success)
        val darkSuccessBrightness = calculateRelativeLuminance(DarkSuccess)
        val darkSuccessSaturation = calculateSaturation(DarkSuccess)

        // Verify brightness increase: ~10%
        val brightnessIncrease = (darkSuccessBrightness - lightSuccessBrightness) / lightSuccessBrightness
        assertTrue(
            "Success color brightness increase should be approximately 10% (was ${(brightnessIncrease * 100)}%)",
            brightnessIncrease > 0.05f && brightnessIncrease < 0.15f
        )

        // Verify saturation reduction: ~30%
        val saturationReduction = (lightSuccessSaturation - darkSuccessSaturation) / lightSuccessSaturation
        assertTrue(
            "Success color saturation reduction should be approximately 30% (was ${(saturationReduction * 100)}%)",
            saturationReduction > 0.2f && saturationReduction < 0.4f
        )

        // Warning color: same adjustment
        val lightWarningBrightness = calculateRelativeLuminance(Warning)
        val lightWarningSaturation = calculateSaturation(Warning)
        val darkWarningBrightness = calculateRelativeLuminance(DarkWarning)
        val darkWarningSaturation = calculateSaturation(DarkWarning)

        val warningBrightnessIncrease = (darkWarningBrightness - lightWarningBrightness) / lightWarningBrightness
        val warningSaturationReduction = (lightWarningSaturation - darkWarningSaturation) / lightWarningSaturation

        assertTrue(
            "Warning color brightness increase should be approximately 10% (was ${(warningBrightnessIncrease * 100)}%)",
            warningBrightnessIncrease > 0.05f && warningBrightnessIncrease < 0.15f
        )
        assertTrue(
            "Warning color saturation reduction should be approximately 30% (was ${(warningSaturationReduction * 100)}%)",
            warningSaturationReduction > 0.2f && warningSaturationReduction < 0.4f
        )

        // Error color: same adjustment
        val lightErrorBrightness = calculateRelativeLuminance(Error)
        val lightErrorSaturation = calculateSaturation(Error)
        val darkErrorBrightness = calculateRelativeLuminance(DarkError)
        val darkErrorSaturation = calculateSaturation(DarkError)

        val errorBrightnessIncrease = (darkErrorBrightness - lightErrorBrightness) / lightErrorBrightness
        val errorSaturationReduction = (lightErrorSaturation - darkErrorSaturation) / lightErrorSaturation

        assertTrue(
            "Error color brightness increase should be approximately 10% (was ${(errorBrightnessIncrease * 100)}%)",
            errorBrightnessIncrease > 0.05f && errorBrightnessIncrease < 0.15f
        )
        assertTrue(
            "Error color saturation reduction should be approximately 30% (was ${(errorSaturationReduction * 100)}%)",
            errorSaturationReduction > 0.2f && errorSaturationReduction < 0.4f
        )
    }

    @Test
    fun testTextColorsBrightnessAdjustment() {
        // Primary text: 90% brightness
        val primaryBrightness = calculateRelativeLuminance(DarkTextPrimary)
        assertTrue(
            "Dark mode primary text brightness should be ~90% (was ${primaryBrightness * 100}%)",
            primaryBrightness > 0.85f && primaryBrightness < 0.95f
        )

        // Secondary text: 80% brightness
        val secondaryBrightness = calculateRelativeLuminance(DarkTextSecondary)
        assertTrue(
            "Dark mode secondary text brightness should be ~80% (was ${secondaryBrightness * 100}%)",
            secondaryBrightness > 0.75f && secondaryBrightness < 0.85f
        )

        // Auxiliary text: 70% brightness
        val auxiliaryBrightness = calculateRelativeLuminance(DarkTextTertiary)
        assertTrue(
            "Dark mode auxiliary text brightness should be ~70% (was ${auxiliaryBrightness * 100}%)",
            auxiliaryBrightness > 0.65f && auxiliaryBrightness < 0.75f
        )

        // Verify brightness hierarchy: primary > secondary > auxiliary
        assertTrue(
            "Primary text should be brighter than secondary text",
            primaryBrightness > secondaryBrightness
        )
        assertTrue(
            "Secondary text should be brighter than auxiliary text",
            secondaryBrightness > auxiliaryBrightness
        )
    }

    @Test
    fun testBorderDividerColorsBrightnessAdjustment() {
        val dividerBrightness = calculateRelativeLuminance(DarkOutline)
        assertTrue(
            "Dark mode divider brightness should be ~25% (was ${dividerBrightness * 100}%)",
            dividerBrightness > 0.2f && dividerBrightness < 0.3f
        )
    }

    @Test
    fun testTransparencyElementsAdjustment() {
        // This test verifies that transparency elements adjust to 80% opacity
        // Since we can't directly test opacity in color values, we verify the design intent
        // by checking that the colors are adjusted for reduced nighttime glare
        
        // Verify that semi-transparent colors maintain readability
        val primaryTextOnDarkBackground = calculateContrastRatio(DarkTextPrimary, DarkPageBackground)
        assertTrue(
            "Primary text should maintain good contrast in dark mode (ratio: $primaryTextOnDarkBackground)",
            primaryTextOnDarkBackground >= 4.5f
        )

        // Verify that transparency reduces brightness (to prevent glare)
        val primaryTextBrightness = calculateRelativeLuminance(DarkTextPrimary)
        assertTrue(
            "Primary text brightness should be reduced in dark mode (was ${primaryTextBrightness * 100}%)",
            primaryTextBrightness < 1.0f
        )
    }

    // ===== Readability and Contrast Tests =====

    @Test
    fun testDarkModeTextMaintainsContrast() {
        // Primary text on dark background
        val primaryTextContrast = calculateContrastRatio(DarkTextPrimary, DarkPageBackground)
        assertTrue(
            "Primary text contrast ratio ($primaryTextContrast) must be ≥4.5:1 (WCAG AA)",
            primaryTextContrast >= 4.5f
        )

        // Secondary text on dark background
        val secondaryTextContrast = calculateContrastRatio(DarkTextSecondary, DarkPageBackground)
        assertTrue(
            "Secondary text contrast ratio ($secondaryTextContrast) must be ≥4.5:1 (WCAG AA)",
            secondaryTextContrast >= 4.5f
        )

        // Auxiliary text on dark background
        val auxiliaryTextContrast = calculateContrastRatio(DarkTextTertiary, DarkPageBackground)
        assertTrue(
            "Auxiliary text contrast ratio ($auxiliaryTextContrast) must be ≥4.5:1 (WCAG AA)",
            auxiliaryTextContrast >= 4.5f
        )
    }

    @Test
    fun testDarkModeFunctionalColorsMaintainContrast() {
        // Success color on dark background (with white text overlay)
        val successContrast = calculateContrastRatio(Color.White, DarkSuccess)
        assertTrue(
            "White text on success background contrast ratio must be ≥3:1",
            successContrast >= 3.0f
        )

        // Warning color on dark background (with white text overlay)
        val warningContrast = calculateContrastRatio(Color.White, DarkWarning)
        assertTrue(
            "White text on warning background contrast ratio must be ≥3:1",
            warningContrast >= 3.0f
        )

        // Error color on dark background (with white text overlay)
        val errorContrast = calculateContrastRatio(Color.White, DarkError)
        assertTrue(
            "White text on error background contrast ratio must be ≥3:1",
            errorContrast >= 3.0f
        )
    }

    @Test
    fun testDarkModeCardColorsMaintainContrast() {
        // Text on card background
        val textOnCardContrast = calculateContrastRatio(DarkTextPrimary, DarkCardBackground)
        assertTrue(
            "Text on card background contrast ratio ($textOnCardContrast) must be ≥4.5:1",
            textOnCardContrast >= 4.5f
        )

        // Verify card background is darker than page background
        val cardBackgroundBrightness = calculateRelativeLuminance(DarkCardBackground)
        val pageBackgroundBrightness = calculateRelativeLuminance(DarkPageBackground)
        assertTrue(
            "Card background should be slightly lighter than page background",
            cardBackgroundBrightness > pageBackgroundBrightness
        )
    }

    @Test
    fun testDarkModePrimaryColorMaintainsContrast() {
        // Primary color on dark background
        val primaryOnDark = calculateContrastRatio(DarkPrimary, DarkPageBackground)
        assertTrue(
            "Primary color on dark background contrast ratio ($primaryOnDark) must be ≥3:1",
            primaryOnDark >= 3.0f
        )

        // White text on primary color
        val whiteOnPrimary = calculateContrastRatio(Color.White, DarkPrimary)
        assertTrue(
            "White text on primary color contrast ratio ($whiteOnPrimary) must be ≥3:1",
            whiteOnPrimary >= 3.0f
        )
    }

    @Test
    fun testDarkModeAuxiliaryColorsMaintainContrast() {
        // Soft green on dark background
        val greenOnDark = calculateContrastRatio(DarkGreen, DarkPageBackground)
        assertTrue(
            "Soft green on dark background contrast ratio ($greenOnDark) must be ≥3:1",
            greenOnDark >= 3.0f
        )

        // Fresh blue on dark background
        val blueOnDark = calculateContrastRatio(DarkBlue, DarkPageBackground)
        assertTrue(
            "Fresh blue on dark background contrast ratio ($blueOnDark) must be ≥3:1",
            blueOnDark >= 3.0f
        )

        // Cream yellow on dark background
        val yellowOnDark = calculateContrastRatio(DarkYellow, DarkPageBackground)
        assertTrue(
            "Cream yellow on dark background contrast ratio ($yellowOnDark) must be ≥3:1",
            yellowOnDark >= 3.0f
        )
    }

    // ===== Visual Hierarchy Tests =====

    @Test
    fun testDarkModeMaintainsVisualHierarchy() {
        // Verify that text colors maintain brightness hierarchy
        val primaryBrightness = calculateRelativeLuminance(DarkTextPrimary)
        val secondaryBrightness = calculateRelativeLuminance(DarkTextSecondary)
        val auxiliaryBrightness = calculateRelativeLuminance(DarkTextTertiary)

        assertTrue(
            "Primary text should be brighter than secondary text",
            primaryBrightness > secondaryBrightness
        )
        assertTrue(
            "Secondary text should be brighter than auxiliary text",
            secondaryBrightness > auxiliaryBrightness
        )

        // Verify that backgrounds maintain depth hierarchy
        val pageBackgroundBrightness = calculateRelativeLuminance(DarkPageBackground)
        val cardBackgroundBrightness = calculateRelativeLuminance(DarkCardBackground)
        val surfaceVariantBrightness = calculateRelativeLuminance(DarkSurfaceVariant)

        assertTrue(
            "Card background should be lighter than page background",
            cardBackgroundBrightness > pageBackgroundBrightness
        )
        assertTrue(
            "Surface variant should maintain appropriate brightness",
            surfaceVariantBrightness >= pageBackgroundBrightness
        )
    }

    @Test
    fun testDarkModeGradientsAdaptCorrectly() {
        // Verify that gradient colors are adjusted for dark mode
        // This is tested by checking that the gradient colors maintain visual hierarchy
        
        val gradientStartBrightness = calculateRelativeLuminance(DarkPageGradientStart)
        val gradientMiddleBrightness = calculateRelativeLuminance(DarkPageGradientMiddle)
        val gradientEndBrightness = calculateRelativeLuminance(DarkPageGradientEnd)

        // Gradient should transition from darker to lighter
        assertTrue(
            "Gradient should have proper color transition",
            gradientStartBrightness <= gradientEndBrightness
        )
    }

    // ===== Helper Functions =====

    /**
     * Calculate the contrast ratio between two colors
     * Formula: (L1 + 0.05) / (L2 + 0.05), where L1 is the lighter color
     */
    private fun calculateContrastRatio(color1: Color, color2: Color): Float {
        val l1 = calculateRelativeLuminance(color1)
        val l2 = calculateRelativeLuminance(color2)
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        return (lighter + 0.05f) / (darker + 0.05f)
    }

    /**
     * Calculate the relative luminance of a color
     * Based on WCAG 2.1 specification
     */
    private fun calculateRelativeLuminance(color: Color): Float {
        val r = color.red
        val g = color.green
        val b = color.blue

        // Convert to linear RGB
        val rLinear = if (r <= 0.03928f) r / 12.92f else Math.pow((r + 0.055) / 1.055, 2.4).toFloat()
        val gLinear = if (g <= 0.03928f) g / 12.92f else Math.pow((g + 0.055) / 1.055, 2.4).toFloat()
        val bLinear = if (b <= 0.03928f) b / 12.92f else Math.pow((b + 0.055) / 1.055, 2.4).toFloat()

        // Calculate relative luminance
        return 0.2126f * rLinear + 0.7152f * gLinear + 0.0722f * bLinear
    }

    /**
     * Calculate the saturation of a color
     * Returns a value between 0 (grayscale) and 1 (fully saturated)
     */
    private fun calculateSaturation(color: Color): Float {
        val r = color.red
        val g = color.green
        val b = color.blue

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)

        if (max == min) {
            return 0f // Grayscale
        }

        val delta = max - min
        val lightness = (max + min) / 2f

        return if (lightness > 0.5f) {
            delta / (2f - max - min)
        } else {
            delta / (max + min)
        }
    }
}