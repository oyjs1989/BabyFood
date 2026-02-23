package com.example.babyfood.presentation.ui.common

import org.junit.Test
import org.junit.Assert.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule

class AppScaffoldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `AppScaffold renders with title`() {
        val appBarConfig = AppBarConfig(title = "Test Title")

        composeTestRule.setContent {
            AppScaffold(
                appBarConfig = appBarConfig,
                bottomActions = emptyList()
            ) {
                // Content
            }
        }

        // Note: This test will fail initially because AppScaffold doesn't exist yet
        // After implementation, verify the title is displayed
        composeTestRule.onNodeWithText("Test Title").assertExists()
    }

    @Test
    fun `AppScaffold with back button hidden`() {
        val appBarConfig = AppBarConfig(
            title = "Test",
            showBackButton = false
        )

        composeTestRule.setContent {
            AppScaffold(
                appBarConfig = appBarConfig,
                bottomActions = emptyList()
            ) {
                // Content
            }
        }

        composeTestRule.onNodeWithText("Test").assertExists()
        // Back button should not exist (after implementation)
    }

    @Test
    fun `AppScaffold with bottom actions`() {
        val appBarConfig = AppBarConfig(title = "Test")
        val bottomActions = listOf(
            AppBottomAction(
                icon = Icons.Default.Save,
                label = "保存",
                contentDescription = "保存",
                onClick = {}
            )
        )

        composeTestRule.setContent {
            AppScaffold(
                appBarConfig = appBarConfig,
                bottomActions = bottomActions
            ) {
                // Content
            }
        }

        composeTestRule.onNodeWithText("Test").assertExists()
        composeTestRule.onNodeWithText("保存").assertExists()
    }

    @Test
    fun `AppScaffold with multiple bottom actions`() {
        val appBarConfig = AppBarConfig(title = "Test")
        val bottomActions = listOf(
            AppBottomAction(
                icon = Icons.Default.Save,
                label = "保存",
                contentDescription = "保存",
                onClick = {}
            ),
            AppBottomAction(
                icon = Icons.Default.ArrowBack,
                label = "取消",
                contentDescription = "取消",
                onClick = {}
            )
        )

        composeTestRule.setContent {
            AppScaffold(
                appBarConfig = appBarConfig,
                bottomActions = bottomActions
            ) {
                // Content
            }
        }

        composeTestRule.onNodeWithText("Test").assertExists()
        composeTestRule.onNodeWithText("保存").assertExists()
        composeTestRule.onNodeWithText("取消").assertExists()
    }
}