package com.example.babyfood.presentation.ui.common

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import org.junit.Rule
import org.junit.Test

class AppScaffoldUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    @Test
    fun `AppScaffold header and footer stay fixed while content scrolls`() {
        val appBarConfig = AppBarConfig(title = "Scroll Test")
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
                // Scrollable content with many items
                androidx.compose.foundation.lazy.LazyColumn {
                    items(100) { index ->
                        androidx.compose.material3.Text(
                            "Item $index",
                            modifier = androidx.compose.ui.Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }

        // Note: This test will fail initially because AppScaffold doesn't exist yet
        // After implementation, verify:
        // 1. Title is visible at the top
        composeTestRule.onNodeWithText("Scroll Test").assertExists()

        // 2. Bottom action is visible at the bottom
        composeTestRule.onNodeWithText("保存").assertExists()

        // 3. Scroll to bottom and verify header and footer are still visible
        composeTestRule.onNodeWithText("Item 99").performScrollTo()
        composeTestRule.onNodeWithText("Scroll Test").assertExists()
        composeTestRule.onNodeWithText("保存").assertExists()
    }
}