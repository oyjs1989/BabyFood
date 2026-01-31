package com.example.babyfood.theme

import com.example.babyfood.presentation.theme.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for corner radius specifications
 * Tests that all component corner radii match design specifications
 */
class ShapeTest {

    // ===== Button Corner Radius Tests =====

    @Test
    fun testButtonPrimaryRadiusValue() {
        val expectedRadius = 24.dp
        val actualRadius = ButtonPrimaryRadius
        assertEquals(
            "Primary button corner radius should be 24dp",
            expectedRadius,
            actualRadius
        )
    }

    @Test
    fun testButtonSecondaryRadiusValue() {
        val expectedRadius = 16.dp
        val actualRadius = ButtonSecondaryRadius
        assertEquals(
            "Secondary button corner radius should be 16dp",
            expectedRadius,
            actualRadius
        )
    }

    @Test
    fun testButtonPrimaryShape() {
        val expectedShape = RoundedCornerShape(24.dp)
        assertEquals(
            "ButtonPrimaryShape should be RoundedCornerShape(24.dp)",
            expectedShape.topStart,
            ButtonPrimaryShape.topStart
        )
    }

    @Test
    fun testButtonSecondaryShape() {
        val expectedShape = RoundedCornerShape(16.dp)
        assertEquals(
            "ButtonSecondaryShape should be RoundedCornerShape(16.dp)",
            expectedShape.topStart,
            ButtonSecondaryShape.topStart
        )
    }

    // ===== Card Corner Radius Tests =====

    @Test
    fun testCardLargeRadiusValue() {
        val expectedRadius = 24.dp
        val actualRadius = CardLargeRadius
        assertEquals(
            "Large card corner radius should be 24dp",
            expectedRadius,
            actualRadius
        )
    }

    @Test
    fun testCardSmallRadiusValue() {
        val expectedRadius = 20.dp
        val actualRadius = CardSmallRadius
        assertEquals(
            "Small card corner radius should be 20dp",
            expectedRadius,
            actualRadius
        )
    }

    @Test
    fun testCardLargeShape() {
        val expectedShape = RoundedCornerShape(24.dp)
        assertEquals(
            "CardLargeShape should be RoundedCornerShape(24.dp)",
            expectedShape.topStart,
            CardLargeShape.topStart
        )
    }

    @Test
    fun testCardSmallShape() {
        val expectedShape = RoundedCornerShape(20.dp)
        assertEquals(
            "CardSmallShape should be RoundedCornerShape(20.dp)",
            expectedShape.topStart,
            CardSmallShape.topStart
        )
    }

    // ===== Input Field Corner Radius Tests =====

    @Test
    fun testInputFieldRadiusValue() {
        val expectedRadius = 16.dp
        val actualRadius = InputFieldRadius
        assertEquals(
            "Input field corner radius should be 16dp",
            expectedRadius,
            actualRadius
        )
    }

    @Test
    fun testInputFieldShape() {
        val expectedShape = RoundedCornerShape(16.dp)
        assertEquals(
            "InputFieldShape should be RoundedCornerShape(16.dp)",
            expectedShape.topStart,
            InputFieldShape.topStart
        )
    }

    // ===== Dialog Corner Radius Tests =====

    @Test
    fun testDialogRadiusValue() {
        val expectedRadius = 28.dp
        val actualRadius = DialogRadius
        assertEquals(
            "Dialog corner radius should be 28dp",
            expectedRadius,
            actualRadius
        )
    }

    @Test
    fun testDialogShape() {
        val expectedShape = RoundedCornerShape(28.dp)
        assertEquals(
            "DialogShape should be RoundedCornerShape(28.dp)",
            expectedShape.topStart,
            DialogShape.topStart
        )
    }

    // ===== Icon Container Corner Radius Tests =====

    @Test
    fun testIconSmallRadiusValue() {
        val expectedRadius = 12.dp
        val actualRadius = IconSmallRadius
        assertEquals(
            "Small icon container corner radius should be 12dp",
            expectedRadius,
            actualRadius
        )
    }

    @Test
    fun testIconMediumRadiusValue() {
        val expectedRadius = 16.dp
        val actualRadius = IconMediumRadius
        assertEquals(
            "Medium icon container corner radius should be 16dp",
            expectedRadius,
            actualRadius
        )
    }

    @Test
    fun testIconSmallShape() {
        val expectedShape = RoundedCornerShape(12.dp)
        assertEquals(
            "IconSmallShape should be RoundedCornerShape(12.dp)",
            expectedShape.topStart,
            IconSmallShape.topStart
        )
    }

    @Test
    fun testIconMediumShape() {
        val expectedShape = RoundedCornerShape(16.dp)
        assertEquals(
            "IconMediumShape should be RoundedCornerShape(16.dp)",
            expectedShape.topStart,
            IconMediumShape.topStart
        )
    }

    // ===== Special Shape Tests =====

    @Test
    fun testTopRoundedCornerShape() {
        // Verify that TopRoundedCornerShape is defined
        assertNotNull(
            "TopRoundedCornerShape should be defined",
            TopRoundedCornerShape
        )
        
        // Verify it's a valid RoundedCornerShape
        assertTrue(
            "TopRoundedCornerShape should be a RoundedCornerShape",
            TopRoundedCornerShape is androidx.compose.foundation.shape.RoundedCornerShape
        )
    }

    @Test
    fun testBottomRoundedCornerShape() {
        // Verify that BottomRoundedCornerShape is defined
        assertNotNull(
            "BottomRoundedCornerShape should be defined",
            BottomRoundedCornerShape
        )
        
        // Verify it's a valid RoundedCornerShape
        assertTrue(
            "BottomRoundedCornerShape should be a RoundedCornerShape",
            BottomRoundedCornerShape is androidx.compose.foundation.shape.RoundedCornerShape
        )
    }

    @Test
    fun testCapsuleShape() {
        // Verify that CapsuleShape is defined
        assertNotNull(
            "CapsuleShape should be defined",
            CapsuleShape
        )
        
        // Verify it's a valid RoundedCornerShape
        assertTrue(
            "CapsuleShape should be a RoundedCornerShape",
            CapsuleShape is androidx.compose.foundation.shape.RoundedCornerShape
        )
    }

    // ===== Consistency Tests =====

    @Test
    fun testRadiusHierarchy() {
        // Verify that corner radii follow the expected hierarchy
        // Large cards/primary buttons > Small cards > Inputs/secondary buttons > Small icons
        
        assertTrue(
            "Primary button radius should be larger than secondary button radius",
            ButtonPrimaryRadius > ButtonSecondaryRadius
        )
        
        assertTrue(
            "Large card radius should be larger than small card radius",
            CardLargeRadius > CardSmallRadius
        )
        
        assertTrue(
            "Secondary button radius should be equal to input field radius",
            ButtonSecondaryRadius == InputFieldRadius
        )
        
        assertTrue(
            "Input field radius should be equal to medium icon radius",
            InputFieldRadius == IconMediumRadius
        )
        
        assertTrue(
            "Medium icon radius should be larger than small icon radius",
            IconMediumRadius > IconSmallRadius
        )
    }

    @Test
    fun testRadiusValuesArePositive() {
        val allRadii = listOf(
            ButtonPrimaryRadius,
            ButtonSecondaryRadius,
            CardLargeRadius,
            CardSmallRadius,
            InputFieldRadius,
            DialogRadius,
            IconSmallRadius,
            IconMediumRadius
        )

        for (radius in allRadii) {
            assertTrue(
                "All corner radii should be positive",
                radius.value > 0f
            )
        }
    }
}