package com.example.babyfood.presentation.ui.common

import org.junit.Test
import org.junit.Assert.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search

class AppBarConfigTest {

    @Test
    fun `AppBarConfig with default values`() {
        val config = AppBarConfig(title = "Test Title")

        assertEquals("Test Title", config.title)
        assertTrue(config.showBackButton)
        assertEquals(Icons.Default.ArrowBack, config.backIcon)
        assertTrue(config.actions.isEmpty())
    }

    @Test
    fun `AppBarConfig with custom values`() {
        val action = AppBarAction(
            icon = Icons.Default.Search,
            contentDescription = "Search",
            onClick = {}
        )

        val config = AppBarConfig(
            title = "Custom Title",
            showBackButton = false,
            backIcon = Icons.Default.Search,
            actions = listOf(action)
        )

        assertEquals("Custom Title", config.title)
        assertFalse(config.showBackButton)
        assertEquals(Icons.Default.Search, config.backIcon)
        assertEquals(1, config.actions.size)
        assertEquals(action, config.actions[0])
    }

    @Test
    fun `AppBarConfig copy function`() {
        val original = AppBarConfig(title = "Original")
        val copied = original.copy(title = "Copied")

        assertEquals("Copied", copied.title)
        assertTrue(copied.showBackButton)
        assertEquals(original.backIcon, copied.backIcon)
        assertEquals(original.actions, copied.actions)
    }

    @Test
    fun `AppBarConfig with multiple actions`() {
        val actions = listOf(
            AppBarAction(
                icon = Icons.Default.Search,
                contentDescription = "Search",
                onClick = {}
            ),
            AppBarAction(
                icon = Icons.Default.ArrowBack,
                contentDescription = "Back",
                onClick = {}
            )
        )

        val config = AppBarConfig(
            title = "Test",
            actions = actions
        )

        assertEquals(2, config.actions.size)
    }

    @Test
    fun `AppBarConfig actions list immutability`() {
        val original = AppBarConfig(title = "Test")
        val actions = listOf(
            AppBarAction(
                icon = Icons.Default.Search,
                contentDescription = "Search",
                onClick = {}
            )
        )
        val modified = original.copy(actions = actions)

        // Original should remain unchanged
        assertTrue(original.actions.isEmpty())
        // Modified should have actions
        assertEquals(1, modified.actions.size)
    }
}