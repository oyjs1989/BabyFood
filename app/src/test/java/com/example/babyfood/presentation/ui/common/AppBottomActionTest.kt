package com.example.babyfood.presentation.ui.common

import org.junit.Test
import org.junit.Assert.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save

class AppBottomActionTest {

    @Test
    fun `AppBottomAction creation`() {
        val action = AppBottomAction(
            icon = Icons.Default.Save,
            label = "保存",
            contentDescription = "保存数据",
            onClick = {},
            enabled = true
        )

        assertEquals(Icons.Default.Save, action.icon)
        assertEquals("保存", action.label)
        assertEquals("保存数据", action.contentDescription)
        assertTrue(action.enabled)
    }

    @Test
    fun `AppBottomAction with enabled false`() {
        val action = AppBottomAction(
            icon = Icons.Default.Save,
            label = "保存",
            contentDescription = "保存数据",
            onClick = {},
            enabled = false
        )

        assertFalse(action.enabled)
    }

    @Test
    fun `AppBottomAction copy function`() {
        val original = AppBottomAction(
            icon = Icons.Default.Save,
            label = "保存",
            contentDescription = "保存数据",
            onClick = {},
            enabled = true
        )
        val copied = original.copy(enabled = false)

        assertEquals(original.icon, copied.icon)
        assertEquals(original.label, copied.label)
        assertEquals(original.contentDescription, copied.contentDescription)
        assertFalse(copied.enabled)
    }

    @Test
    fun `AppBottomAction label validation`() {
        // Test that label can be any string
        val action = AppBottomAction(
            icon = Icons.Default.Save,
            label = "ABC",
            contentDescription = "Test",
            onClick = {}
        )

        assertEquals("ABC", action.label)
    }

    @Test
    fun `AppBottomAction long label`() {
        // Test that long labels are allowed (but should be < 8 chars per spec)
        val action = AppBottomAction(
            icon = Icons.Default.Save,
            label = "123456789",
            contentDescription = "Test",
            onClick = {}
        )

        assertEquals("123456789", action.label)
    }
}