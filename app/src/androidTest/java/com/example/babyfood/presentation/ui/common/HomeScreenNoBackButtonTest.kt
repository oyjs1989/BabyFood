package com.example.babyfood.presentation.ui.common

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class HomeScreenNoBackButtonTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    @Test
    fun `Home screen does not show back button when showBackButton is false`() {
        val appBarConfig = AppBarConfig(
            title = "BabyFood",
            showBackButton = false
        )

        composeTestRule.setContent {
            AppScaffold(
                appBarConfig = appBarConfig,
                bottomActions = emptyList(),
                onBackClick = {}
            ) {
                androidx.compose.material3.Text("Home Content")
            }
        }

        // Verify back button does NOT exist
        composeTestRule.onNodeWithContentDescription("返回").assertDoesNotExist()

        // Verify title is still visible
        composeTestRule.onNodeWithText("BabyFood").assertExists()
    }

    @Test
    fun `Login screen does not show back button`() {
        val appBarConfig = AppBarConfig(
            title = "登录",
            showBackButton = false
        )

        composeTestRule.setContent {
            AppScaffold(
                appBarConfig = appBarConfig,
                bottomActions = emptyList(),
                onBackClick = {}
            ) {
                androidx.compose.material3.Text("Login Form")
            }
        }

        // Verify back button does NOT exist
        composeTestRule.onNodeWithContentDescription("返回").assertDoesNotExist()

        // Verify title is visible
        composeTestRule.onNodeWithText("登录").assertExists()
    }
}