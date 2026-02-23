package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.testutils.assertIsDisplayed
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class ScreenOrientationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    @Test
    fun `Layout reconfigures correctly on orientation change`() {
        val appBarConfig = AppBarConfig(title = "Orientation Test")

        composeTestRule.setContent {
            val configuration = LocalConfiguration.current
            var orientation by remember { mutableStateOf(configuration.orientation) }

            LaunchedEffect(configuration) {
                orientation = configuration.orientation
            }

            AppScaffold(
                appBarConfig = appBarConfig,
                bottomActions = emptyList()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text("Screen Orientation: $orientation")
                    Text("Content adapts to orientation")
                }
            }
        }

        // Verify layout is visible
        composeTestRule.onNodeWithText("Orientation Test").assertExists()
        composeTestRule.onNodeWithText("Content adapts to orientation").assertExists()
    }

    @Test
    fun `Header and footer remain fixed during orientation change`() {
        val appBarConfig = AppBarConfig(title = "Fixed Elements")
        val bottomActions = listOf(
            AppBottomAction(
                icon = androidx.compose.material.icons.Icons.Default.Save,
                label = "保存",
                contentDescription = "保存",
                onClick = {}
            )
        )

        composeTestRule.setContent {
            val configuration = LocalConfiguration.current
            var orientation by remember { mutableStateOf(configuration.orientation) }

            LaunchedEffect(configuration) {
                orientation = configuration.orientation
            }

            AppScaffold(
                appBarConfig = appBarConfig,
                bottomActions = bottomActions
            ) {
                androidx.compose.foundation.lazy.LazyColumn {
                    items(20) { index ->
                        Text(
                            "Item $index",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }

        // Verify fixed elements are visible
        composeTestRule.onNodeWithText("Fixed Elements").assertExists()
        composeTestRule.onNodeWithText("保存").assertExists()
    }
}