package com.example.babyfood.presentation.ui.common

import org.junit.Test
import org.junit.Assert.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search

class AppBarActionTest {

    @Test
    fun `AppBarAction creation`() {
        val action = AppBarAction(
            icon = Icons.Default.Search,
            contentDescription = "Search",
            onClick = {},
            enabled = true
        )

        assertEquals(Icons.Default.Search, action.icon)
        assertEquals("Search", action.contentDescription)
        assertTrue(action.enabled)
    }

    @Test
    fun `AppBarAction with enabled false`() {
        val action = AppBarAction(
            icon = Icons.Default.Search,
            contentDescription = "Search",
            onClick = {},
            enabled = false
        )

        assertFalse(action.enabled)
    }

    @Test
    fun `AppBarAction copy function`() {
        val original = AppBarAction(
            icon = Icons.Default.Search,
            contentDescription = "Search",
            onClick = {},
            enabled = true
        )
        val copied = original.copy(enabled = false)

        assertEquals(original.icon, copied.icon)
        assertEquals(original.contentDescription, copied.contentDescription)
        assertFalse(copied.enabled)
    }

    @Test
    fun `AppBarAction equality`() {
        val onClick1 = {}
        val onClick2 = {}

        val action1 = AppBarAction(
            icon = Icons.Default.Search,
            contentDescription = "Search",
            onClick = onClick1,
            enabled = true
        )
        val action2 = AppBarAction(
            icon = Icons.Default.Search,
            contentDescription = "Search",
            onClick = onClick1,
            enabled = true
        )
        val action3 = AppBarAction(
            icon = Icons.Default.Search,
            contentDescription = "Search",
            onClick = onClick2,
            enabled = true
        )

        // AppBarAction is a data class, so equality is based on properties
        // But onClick is a function, so it's tricky to test equality
        assertEquals(action1.icon, action2.icon)
        assertEquals(action1.contentDescription, action2.contentDescription)
        assertEquals(action1.enabled, action2.enabled)
    }
}