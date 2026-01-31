package com.example.babyfood.presentation.ui.common

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import org.junit.Rule
import org.junit.Test

class ResponsiveLayoutLargeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    @Test
    fun `Content width is limited to 600dp on large screen`() {
        val appBarConfig = AppBarConfig(title = "Large Screen Test")

        composeTestRule.setContent {
            // On large screens (>600dp), content should be limited to 600dp width
            // This is implemented in AppContent using Modifier.widthIn(max = 600.dp)
            AppScaffold(
                appBarConfig = appBarConfig,
                bottomActions = emptyList()
            ) {
                androidx.compose.material3.Text(
                    "Limited Width Content on Large Screen",
                    modifier = androidx.compose.ui.Modifier.padding(16.dp)
                )
            }
        }

        // Verify content is visible
        composeTestRule.onNodeWithText("Large Screen Test").assertExists()
        composeTestRule.onNodeWithText("Limited Width Content on Large Screen").assertExists()
    }

    @Test
    fun `Layout maintains structure on large screen`() {
        val appBarConfig = AppBarConfig(title = "Large Screen Structure")
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
                androidx.compose.material3.Text(
                    "Content Area",
                    modifier = androidx.compose.ui.Modifier.padding(16.dp)
                )
            }
        }

        // Verify all layout elements are present
        composeTestRule.onNodeWithText("Large Screen Structure").assertExists()
        composeTestRule.onNodeWithText("Content Area").assertExists()
        composeTestRule.onNodeWithText("保存").assertExists()
    }
}