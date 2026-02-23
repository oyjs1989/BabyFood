package com.example.babyfood.theme

import com.example.babyfood.presentation.theme.*
import androidx.compose.ui.graphics.Color
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for color palette validation
 * Tests color values, contrast ratios, and dark mode color pairs
 */
class ColorTest {

    // ===== Light Mode Color Tests =====

    @Test
    fun testPageBackgroundColorValue() {
        val expected = Color(0xFFFDFBF8)
        assertEquals(
            "Page background color should match design specification",
            expected,
            PageBackground
        )
    }

    @Test
    fun testCardBackgroundColorValue() {
        val expected = Color(0xFFFFFFFF)
        assertEquals(
            "Card background color should match design specification",
            expected,
            CardBackground
        )
    }

    @Test
    fun testPrimaryTextColorValue() {
        val expected = Color(0xFF333333)
        assertEquals(
            "Primary text color should match design specification",
            expected,
            TextPrimary
        )
    }

    @Test
    fun testSecondaryTextColorValue() {
        val expected = Color(0xFF666666)
        assertEquals(
            "Secondary text color should match design specification",
            expected,
            TextSecondary
        )
    }

    @Test
    fun testAuxiliaryTextColorValue() {
        val expected = Color(0xFF505050)
        assertEquals(
            "Auxiliary text color should match design specification",
            expected,
            TextTertiary
        )
    }

    @Test
    fun testDividerColorValue() {
        val expected = Color(0xFFE5E5E5)
        assertEquals(
            "Divider color should match design specification",
            expected,
            Outline
        )
    }

    @Test
    fun testPrimaryBrandColorValue() {
        val expected = Color(0xFFFF9F69)
        assertEquals(
            "Primary brand color should match design specification",
            expected,
            Primary
        )
    }

    @Test
    fun testExtendedBrandColorValue() {
        val expected = Color(0xFFFFD4BD)
        assertEquals(
            "Extended brand color should match design specification",
            expected,
            PrimaryLight
        )
    }

    @Test
    fun testSoftGreenColorValue() {
        val expected = Color(0xFF88C999)
        assertEquals(
            "Soft green color should match design specification",
            expected,
            Green
        )
    }

    @Test
    fun testFreshBlueColorValue() {
        val expected = Color(0xFF73A6FF)
        assertEquals(
            "Fresh blue color should match design specification",
            expected,
            Blue
        )
    }

    @Test
    fun testSuccessColorValue() {
        val expected = Color(0xFF52C41A)
        assertEquals(
            "Success color should match design specification",
            expected,
            Success
        )
    }

    @Test
    fun testWarningColorValue() {
        val expected = Color(0xFFFAAD14)
        assertEquals(
            "Warning color should match design specification",
            expected,
            Warning
        )
    }

    @Test
    fun testErrorColorValue() {
        val expected = Color(0xFFF5222D)
        assertEquals(
            "Error color should match design specification",
            expected,
            Error
        )
    }

    // ===== Dark Mode Color Tests =====

    @Test
    fun testDarkPageBackgroundColorValue() {
        val expected = Color(0xFF121212)
        assertEquals(
            "Dark mode page background color should match design specification",
            expected,
            DarkPageBackground
        )
    }

    @Test
    fun testDarkCardBackgroundColorValue() {
        val expected = Color(0xFF1E1E1E)
        assertEquals(
            "Dark mode card background color should match design specification",
            expected,
            DarkCardBackground
        )
    }

    @Test
    fun testDarkPrimaryTextColorValue() {
        val expected = Color(0xFFF5F5F5)
        assertEquals(
            "Dark mode primary text color should match design specification",
            expected,
            DarkTextPrimary
        )
    }

    @Test
    fun testDarkSecondaryTextColorValue() {
        val expected = Color(0xFFC1C1C1)
        assertEquals(
            "Dark mode secondary text color should match design specification",
            expected,
            DarkTextSecondary
        )
    }

    @Test
    fun testDarkAuxiliaryTextColorValue() {
        val expected = Color(0xFF8E8E8E)
        assertEquals(
            "Dark mode auxiliary text color should match design specification",
            expected,
            DarkTextTertiary
        )
    }

    @Test
    fun testDarkDividerColorValue() {
        val expected = Color(0xFF3D3D3D)
        assertEquals(
            "Dark mode divider color should match design specification",
            expected,
            DarkOutline
        )
    }

    @Test
    fun testDarkPrimaryBrandColorValue() {
        val expected = Color(0xFFE67A4A)
        assertEquals(
            "Dark mode primary brand color should match design specification",
            expected,
            DarkPrimary
        )
    }

    @Test
    fun testDarkSuccessColorValue() {
        val expected = Color(0xFF6BDB3A)
        assertEquals(
            "Dark mode success color should match design specification",
            expected,
            DarkSuccess
        )
    }

    @Test
    fun testDarkErrorColorValue() {
        val expected = Color(0xFFFF6666)
        assertEquals(
            "Dark mode error color should match design specification",
            expected,
            DarkError
        )
    }

    // ===== Contrast Ratio Tests =====

    @Test
    fun testPrimaryTextContrastRatio() {
        val contrastRatio = calculateContrastRatio(TextPrimary, PageBackground)
        assertTrue(
            "Primary text contrast ratio ($contrastRatio) must be ≥4.5:1 (WCAG AA)",
            contrastRatio >= 4.5f
        )
    }

    @Test
    fun testSecondaryTextContrastRatio() {
        val contrastRatio = calculateContrastRatio(TextSecondary, PageBackground)
        assertTrue(
            "Secondary text contrast ratio ($contrastRatio) must be ≥4.5:1 (WCAG AA)",
            contrastRatio >= 4.5f
        )
    }

    @Test
    fun testDarkPrimaryTextContrastRatio() {
        val contrastRatio = calculateContrastRatio(DarkTextPrimary, DarkPageBackground)
        assertTrue(
            "Dark mode primary text contrast ratio ($contrastRatio) must be ≥4.5:1 (WCAG AA)",
            contrastRatio >= 4.5f
        )
    }

    @Test
    fun testDarkSecondaryTextContrastRatio() {
        val contrastRatio = calculateContrastRatio(DarkTextSecondary, DarkPageBackground)
        assertTrue(
            "Dark mode secondary text contrast ratio ($contrastRatio) must be ≥4.5:1 (WCAG AA)",
            contrastRatio >= 4.5f
        )
    }

    @Test
    fun testPrimaryBrandOnBackgroundContrastRatio() {
        val contrastRatio = calculateContrastRatio(Primary, PageBackground)
        assertTrue(
            "Primary brand on background contrast ratio ($contrastRatio) must be ≥3:1 for large text",
            contrastRatio >= 3.0f
        )
    }

    @Test
    fun testSuccessColorContrastRatio() {
        val contrastRatio = calculateContrastRatio(Success, PageBackground)
        assertTrue(
            "Success color contrast ratio ($contrastRatio) must be ≥3:1 for large text",
            contrastRatio >= 3.0f
        )
    }

    @Test
    fun testErrorColorContrastRatio() {
        val contrastRatio = calculateContrastRatio(Error, PageBackground)
        assertTrue(
            "Error color contrast ratio ($contrastRatio) must be ≥3:1 for large text",
            contrastRatio >= 3.0f
        )
    }

    // ===== Dark Mode Color Adjustment Tests =====

    @Test
    fun testDarkModeBackgroundBrightnessAdjustment() {
        // Light mode background brightness should be ~95%
        val lightBrightness = calculateRelativeLuminance(PageBackground)
        // Dark mode background brightness should be ~5%
        val darkBrightness = calculateRelativeLuminance(DarkPageBackground)
        
        assertTrue(
            "Light mode background brightness should be >90%",
            lightBrightness > 0.9f
        )
        assertTrue(
            "Dark mode background brightness should be <10%",
            darkBrightness < 0.1f
        )
    }

    @Test
    fun testDarkModeTextBrightnessAdjustment() {
        // Primary text: 90% brightness
        val primaryBrightness = calculateRelativeLuminance(DarkTextPrimary)
        // Secondary text: 80% brightness
        val secondaryBrightness = calculateRelativeLuminance(DarkTextSecondary)
        // Auxiliary text: 70% brightness
        val auxiliaryBrightness = calculateRelativeLuminance(DarkTextTertiary)
        
        assertTrue(
            "Dark mode primary text brightness should be >85%",
            primaryBrightness > 0.85f
        )
        assertTrue(
            "Dark mode secondary text brightness should be >75%",
            secondaryBrightness > 0.75f
        )
        assertTrue(
            "Dark mode auxiliary text brightness should be >65%",
            auxiliaryBrightness > 0.65f
        )
    }

    @Test
    fun testDarkModeDividerBrightnessAdjustment() {
        val brightness = calculateRelativeLuminance(DarkOutline)
        assertTrue(
            "Dark mode divider brightness should be ~25%",
            brightness > 0.2f && brightness < 0.3f
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
}