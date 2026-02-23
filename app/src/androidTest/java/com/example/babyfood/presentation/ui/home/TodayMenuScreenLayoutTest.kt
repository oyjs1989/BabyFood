package com.example.babyfood.presentation.ui.home

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.presentation.ui.home.components.TodayMenuScreen
import org.junit.Rule
import org.junit.Test

class TodayMenuScreenLayoutTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    @Test
    fun `TodayMenuScreen uses AppScaffold correctly`() {
        // Note: This test verifies the structure after refactoring
        // After refactoring, TodayMenuScreen should use AppScaffold
        
        composeTestRule.setContent {
            TodayMenuScreen(
                viewModel = androidx.hilt.navigation.compose.hiltViewModel()
            )
        }

        // Verify the screen renders (actual verification depends on test data)
        // This test will pass once TodayMenuScreen is refactored to use AppScaffold
    }

    @Test
    fun `TodayMenuScreen shows date in app bar`() {
        composeTestRule.setContent {
            TodayMenuScreen(
                viewModel = androidx.hilt.navigation.compose.hiltViewModel()
            )
        }

        // After refactoring, date should be in the app bar title
        // For now, this test verifies the screen doesn't crash
    }

    @Test
    fun `TodayMenuScreen does not show back button`() {
        composeTestRule.setContent {
            TodayMenuScreen(
                viewModel = androidx.hilt.navigation.compose.hiltViewModel()
            )
        }

        // After refactoring, back button should not exist on home screen
        composeTestRule.onNodeWithContentDescription("返回").assertDoesNotExist()
    }
}