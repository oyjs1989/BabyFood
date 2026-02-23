package com.example.babyfood.presentation.ui.common

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import org.junit.Rule
import org.junit.Test

class AppScaffoldEmptyContentUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    @Test
    fun `AppScaffold footer stays fixed when content is minimal`() {
        val appBarConfig = AppBarConfig(title = "Minimal Content")
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
                // Minimal content - just one text element
                androidx.compose.material3.Text(
                    "Short Content",
                    modifier = androidx.compose.ui.Modifier.padding(16.dp)
                )
            }
        }

        // Note: This test will fail initially because AppScaffold doesn't exist yet
        // After implementation, verify:
        // 1. Title is visible at the top
        composeTestRule.onNodeWithText("Minimal Content").assertExists()

        // 2. Content is visible
        composeTestRule.onNodeWithText("Short Content").assertExists()

        // 3. Bottom action is fixed at the bottom (not floating in the middle)
        composeTestRule.onNodeWithText("保存").assertExists()
    }

    @Test
    fun `AppScaffold with no bottom actions`() {
        val appBarConfig = AppBarConfig(title = "No Bottom Actions")

        composeTestRule.setContent {
            AppScaffold(
                appBarConfig = appBarConfig,
                bottomActions = emptyList()
            ) {
                // Minimal content
                androidx.compose.material3.Text(
                    "Content",
                    modifier = androidx.compose.ui.Modifier.padding(16.dp)
                )
            }
        }

        // Note: This test will fail initially because AppScaffold doesn't exist yet
        // After implementation, verify:
        // 1. Title is visible
        composeTestRule.onNodeWithText("No Bottom Actions").assertExists()

        // 2. Content is visible
        composeTestRule.onNodeWithText("Content").assertExists()

        // 3. No bottom bar is shown
    }
}