package com.example.babyfood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.babyfood.data.init.RecipeInitializer
import com.example.babyfood.presentation.theme.BabyFoodTheme
import com.example.babyfood.presentation.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var recipeInitializer: RecipeInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化内置食谱数据
        lifecycleScope.launch {
            try {
                recipeInitializer.initializeBuiltInRecipes()
            } catch (e: Exception) {
                // 初始化失败不影响应用启动，只记录日志
                e.printStackTrace()
            }
        }

        setContent {
            BabyFoodTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}