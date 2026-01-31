package com.example.babyfood.presentation.ui.common

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class SubpageBackButtonTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    @Test
    fun `Recipe detail page shows back button`() {
        val appBarConfig = AppBarConfig(
            title = "食谱详情",
            showBackButton = true
        )

        composeTestRule.setContent {
            AppScaffold(
                appBarConfig = appBarConfig,
                bottomActions = emptyList(),
                onBackClick = {}
            ) {
                androidx.compose.material3.Text("Recipe Details")
            }
        }

        // Verify back button exists
        composeTestRule.onNodeWithContentDescription("返回").assertExists()
        composeTestRule.onNodeWithText("食谱详情").assertExists()
    }

    @Test
    fun `Baby detail page shows back button`() {
        val appBarConfig = AppBarConfig(
            title = "宝宝详情",
            showBackButton = true
        )

        composeTestRule.setContent {
            AppScaffold(
                appBarConfig = appBarConfig,
                bottomActions = emptyList(),
                onBackClick = {}
            ) {
                androidx.compose.material3.Text("Baby Details")
            }
        }

        // Verify back button exists
        composeTestRule.onNodeWithContentDescription("返回").assertExists()
        composeTestRule.onNodeWithText("宝宝详情").assertExists()
    }

    @Test
    fun `Plan detail page shows back button`() {
        val appBarConfig = AppBarConfig(
            title = "计划详情",
            showBackButton = true
        )

        composeTestRule.setContent {
            AppScaffold(
                appBarConfig = appBarConfig,
                bottomActions = emptyList(),
                onBackClick = {}
            ) {
                androidx.compose.material3.Text("Plan Details")
            }
        }

        // Verify back button exists
        composeTestRule.onNodeWithContentDescription("返回").assertExists()
        composeTestRule.onNodeWithText("计划详情").assertExists()
    }
}