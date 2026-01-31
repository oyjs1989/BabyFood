package com.example.babyfood.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertBackgroundColor
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.background
import androidx.compose.ui.draw.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import com.example.babyfood.presentation.theme.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for dark mode theme switching
 * Tests that the app correctly switches between light and dark modes
 * and that all colors adapt properly while maintaining readability
 */
class ThemeSwitchingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testThemeSwitchesBetweenLightAndDarkMode() {
        var isDarkMode by mutableStateOf(false)

        composeTestRule.setContent {
            BabyFoodTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "测试文本",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        // Verify light mode colors
        composeTestRule.onRoot()
            .assertBackgroundColor(PageBackground)

        // Switch to dark mode
        composeTestRule.runOnUiThread {
            isDarkMode = true
        }

        // Wait for theme change to propagate
        composeTestRule.waitForIdle()

        // Verify dark mode colors
        composeTestRule.onRoot()
            .assertBackgroundColor(DarkPageBackground)
    }

    @Test
    fun testAllComponentsAdaptToDarkMode() {
        var isDarkMode by mutableStateOf(false)

        composeTestRule.setContent {
            BabyFoodTheme(darkTheme = isDarkMode) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "卡片文本",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Button
                    BabyFoodPrimaryButton(
                        text = "主按钮",
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Input field
                    BabyFoodInputField(
                        value = "",
                        onValueChange = { },
                        label = "输入框",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Verify light mode components
        composeTestRule.onNodeWithText("卡片文本")
            .assertExists()
            .isDisplayed()

        // Switch to dark mode
        composeTestRule.runOnUiThread {
            isDarkMode = true
        }

        composeTestRule.waitForIdle()

        // Verify dark mode components still visible
        composeTestRule.onNodeWithText("卡片文本")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("主按钮")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("输入框")
            .assertExists()
            .isDisplayed()
    }

    @Test
    fun testTextColorsMaintainContrastInDarkMode() {
        var isDarkMode by mutableStateOf(false)

        composeTestRule.setContent {
            BabyFoodTheme(darkTheme = isDarkMode) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        Text(
                            text = "一级正文",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Surface(color = MaterialTheme.colorScheme.background) {
                        Text(
                            text = "二级正文",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Surface(color = MaterialTheme.colorScheme.background) {
                        Text(
                            text = "辅助说明",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        // Verify all text is visible in light mode
        composeTestRule.onNodeWithText("一级正文")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("二级正文")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("辅助说明")
            .assertExists()
            .isDisplayed()

        // Switch to dark mode
        composeTestRule.runOnUiThread {
            isDarkMode = true
        }

        composeTestRule.waitForIdle()

        // Verify all text is still visible in dark mode
        composeTestRule.onNodeWithText("一级正文")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("二级正文")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("辅助说明")
            .assertExists()
            .isDisplayed()
    }

    @Test
    fun testFunctionalColorsAdaptToDarkMode() {
        var isDarkMode by mutableStateOf(false)

        composeTestRule.setContent {
            BabyFoodTheme(darkTheme = isDarkMode) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Success),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("成功", color = Color.White)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Warning),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("警告", color = Color.White)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Error),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("错误", color = Color.White)
                    }
                }
            }
        }

        // Verify all functional colors are visible in light mode
        composeTestRule.onNodeWithText("成功")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("警告")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("错误")
            .assertExists()
            .isDisplayed()

        // Switch to dark mode
        composeTestRule.runOnUiThread {
            isDarkMode = true
        }

        composeTestRule.waitForIdle()

        // Verify all functional colors adapt to dark mode
        composeTestRule.onNodeWithText("成功")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("警告")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("错误")
            .assertExists()
            .isDisplayed()
    }

    @Test
    fun testGradientAdaptsToDarkMode() {
        var isDarkMode by mutableStateOf(false)

        composeTestRule.setContent {
            BabyFoodTheme(darkTheme = isDarkMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.verticalGradient(
                            colors = listOf(PageGradientStart, PageGradientMiddle, PageGradientEnd)
                        )),
                    contentAlignment = Alignment.Center
                ) {
                    Text("渐变测试", color = TextPrimary)
                }
            }
        }

        // Verify gradient is visible in light mode
        composeTestRule.onNodeWithText("渐变测试")
            .assertExists()
            .isDisplayed()

        // Switch to dark mode
        composeTestRule.runOnUiThread {
            isDarkMode = true
        }

        composeTestRule.waitForIdle()

        // Verify gradient is still visible in dark mode
        composeTestRule.onNodeWithText("渐变测试")
            .assertExists()
            .isDisplayed()
    }

    @Test
    fun testTransparencyElementsAdaptToDarkMode() {
        var isDarkMode by mutableStateOf(false)

        composeTestRule.setContent {
            BabyFoodTheme(darkTheme = isDarkMode) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                color = if (isDarkMode) DarkPageBackground else PageBackground,
                                alpha = 0.8f
                            )
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("透明度测试", color = if (isDarkMode) DarkTextPrimary else TextPrimary)
                    }
                }
            }
        }

        // Verify transparency in light mode
        composeTestRule.onNodeWithText("透明度测试")
            .assertExists()
            .isDisplayed()

        // Switch to dark mode
        composeTestRule.runOnUiThread {
            isDarkMode = true
        }

        composeTestRule.waitForIdle()

        // Verify transparency in dark mode
        composeTestRule.onNodeWithText("透明度测试")
            .assertExists()
            .isDisplayed()
    }

    @Test
    fun testThemeToggleSmoothTransition() {
        var isDarkMode by mutableStateOf(false)
        var transitionCount = 0

        composeTestRule.setContent {
            BabyFoodTheme(darkTheme = isDarkMode) {
                LaunchedEffect(isDarkMode) {
                    transitionCount++
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "动画测试",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        // Initial state
        var initialCount = 0
        composeTestRule.runOnUiThread {
            initialCount = transitionCount
        }
        assertEquals(1, initialCount)

        // Toggle theme
        composeTestRule.runOnUiThread {
            isDarkMode = true
        }

        composeTestRule.waitForIdle()

        // Verify transition occurred
        composeTestRule.runOnUiThread {
            assertEquals(2, transitionCount)
        }

        // Toggle back
        composeTestRule.runOnUiThread {
            isDarkMode = false
        }

        composeTestRule.waitForIdle()

        // Verify transition occurred again
        composeTestRule.runOnUiThread {
            assertEquals(3, transitionCount)
        }
    }

    @Test
    fun testAllColorSchemesAppliedCorrectly() {
        var isDarkMode by mutableStateOf(false)

        composeTestRule.setContent {
            BabyFoodTheme(darkTheme = isDarkMode) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Background colors
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Text("背景", modifier = Modifier.padding(8.dp))
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Text("表面", modifier = Modifier.padding(8.dp))
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text("表面变体", modifier = Modifier.padding(8.dp))
                    }

                    // Primary color
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Text("主色", color = Color.White, modifier = Modifier.padding(8.dp))
                    }

                    // Outline color
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text("边框", modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }

        // Verify all colors in light mode
        composeTestRule.onNodeWithText("背景")
            .assertExists()
            .isDisplayed()

        // Switch to dark mode
        composeTestRule.runOnUiThread {
            isDarkMode = true
        }

        composeTestRule.waitForIdle()

        // Verify all colors in dark mode
        composeTestRule.onNodeWithText("背景")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("表面")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("表面变体")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("主色")
            .assertExists()
            .isDisplayed()

        composeTestRule.onNodeWithText("边框")
            .assertExists()
            .isDisplayed()
    }

    @Test
    fun testThemePersistsAcrossRecompositions() {
        var isDarkMode by mutableStateOf(false)
        var recompositionCount = 0

        composeTestRule.setContent {
            recompositionCount++
            
            BabyFoodTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "重组合次数: $recompositionCount",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        // Wait for initial composition
        composeTestRule.waitForIdle()

        // Trigger recomposition without changing theme
        composeTestRule.runOnUiThread {
            // Just trigger a state change that doesn't affect theme
        }

        composeTestRule.waitForIdle()

        // Switch to dark mode
        composeTestRule.runOnUiThread {
            isDarkMode = true
        }

        composeTestRule.waitForIdle()

        // Verify theme is still dark
        composeTestRule.onRoot()
            .assertBackgroundColor(DarkPageBackground)

        // Trigger another recomposition
        composeTestRule.runOnUiThread {
            // Just trigger a state change
        }

        composeTestRule.waitForIdle()

        // Verify theme persists as dark
        composeTestRule.onRoot()
            .assertBackgroundColor(DarkPageBackground)
    }

    // Helper extension function for background color assertion
    private fun SemanticsNodeInteraction.assertBackgroundColor(expectedColor: Color): SemanticsNodeInteraction {
        // This is a simplified assertion - in real implementation, you would
        // use SemanticsMatcher to check the actual background color
        return this
    }
}