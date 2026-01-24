package com.example.babyfood.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.babyfood.domain.model.PlanConflict
import com.example.babyfood.domain.model.WeeklyMealPlan
import com.example.babyfood.presentation.ui.home.components.TodayMenuScreen
import com.example.babyfood.presentation.ui.plans.PlanListScreen
import com.example.babyfood.presentation.ui.plans.PlanDetailScreen
import com.example.babyfood.presentation.ui.plans.PlanFormScreen
import com.example.babyfood.presentation.ui.plans.PlansViewModel
import com.example.babyfood.presentation.ui.plans.RecommendationEditorScreen

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "首页")
    object Recipes : BottomNavItem("recipes", Icons.Default.Favorite, "食谱")
    object Plans : BottomNavItem("plans", Icons.Default.CalendarMonth, "计划")
    object Baby : BottomNavItem("baby", Icons.Default.Person, "宝宝")
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            if (currentDestination?.route in listOf(
                    "home",
                    "recipes",
                    "plans",
                    "baby"
                )
            ) {
                NavigationBar {
                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Recipes,
                        BottomNavItem.Plans,
                        BottomNavItem.Baby
                    )
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            // 首页 - 今日餐单
            composable("home") {
                TodayMenuScreen()
            }

            // 食谱库
            composable("recipes") {
                com.example.babyfood.presentation.ui.recipes.RecipesListScreen(
                    onNavigateToDetail = { recipeId ->
                        navController.navigate("recipes/detail/$recipeId")
                    },
                    onNavigateToAdd = {
                        navController.navigate("recipes/form/0")
                    }
                )
            }

            // 食谱详情
            composable("recipes/detail/{recipeId}") { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId")?.toLong() ?: 0L
                com.example.babyfood.presentation.ui.recipes.RecipeDetailScreen(
                    recipeId = recipeId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onNavigateToEdit = { id ->
                        navController.navigate("recipes/form/$id")
                    }
                )
            }

            // 添加/编辑食谱
            composable("recipes/form/{recipeId}") { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId")?.toLongOrNull()
                com.example.babyfood.presentation.ui.recipes.RecipeFormScreen(
                    recipeId = recipeId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // 辅食计划
            composable("plans") {
                PlanListScreen(
                    onNavigateToDetail = { planId ->
                        navController.navigate("plans/detail/$planId")
                    },
                    onNavigateToAdd = { babyId ->
                        navController.navigate("plans/form/$babyId/0")
                    },
                    onNavigateToRecommendationEditor = { babyId ->
                        navController.navigate("plans/recommendation/editor/$babyId")
                    }
                )
            }

            // 计划详情
            composable("plans/detail/{planId}") { backStackEntry ->
                val planId = backStackEntry.arguments?.getString("planId")?.toLong() ?: 0L
                PlanDetailScreen(
                    planId = planId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onNavigateToEdit = { planId ->
                        navController.navigate("plans/form/0/$planId")
                    }
                )
            }

            // 添加/编辑计划
            composable("plans/form/{babyId}/{planId}") { backStackEntry ->
                val babyId = backStackEntry.arguments?.getString("babyId")?.toLong() ?: 0L
                val planId = backStackEntry.arguments?.getString("planId")?.toLong() ?: 0L
                val selectedDate = if (backStackEntry.arguments?.containsKey("date") == true) {
                    // 如果有传递日期参数，使用传递的日期
                    null
                } else {
                    null
                }
                
                PlanFormScreen(
                    babyId = babyId,
                    planId = if (planId > 0) planId else null,
                    selectedDate = selectedDate,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = {
                        navController.popBackStack()
                    },
                    onNavigateToRecommendationEditor = { babyId ->
                        navController.navigate("plans/recommendation/editor/$babyId")
                    }
                )
            }

            // 宝宝页面
            composable("baby") {
                com.example.babyfood.presentation.ui.baby.BabyListScreen(
                    onNavigateToAdd = {
                        navController.navigate("baby/form/0")
                    },
                    onNavigateToDetail = { babyId ->
                        navController.navigate("baby/detail/$babyId")
                    }
                )
            }

            // 添加/编辑宝宝表单
            composable("baby/form/{babyId}") { backStackEntry ->
                val babyId = backStackEntry.arguments?.getString("babyId")?.toLong() ?: 0L
                com.example.babyfood.presentation.ui.baby.BabyFormScreen(
                    babyId = babyId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = {
                        navController.popBackStack()
                    }
                )
            }

            // 宝宝详情页
            composable("baby/detail/{babyId}") { backStackEntry ->
                val babyId = backStackEntry.arguments?.getString("babyId")?.toLong() ?: 0L
                com.example.babyfood.presentation.ui.baby.BabyDetailScreen(
                    babyId = babyId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onEdit = {
                        navController.navigate("baby/form/$babyId")
                    },
                    onManageAllergies = {
                        navController.navigate("baby/allergy/$babyId")
                    },
                    onManagePreferences = {
                        navController.navigate("baby/preference/$babyId")
                    },
                    onNavigateToHealthRecords = {
                        navController.navigate("baby/health/$babyId")
                    },
                    onNavigateToGrowth = {
                        navController.navigate("baby/growth/$babyId")
                    },
                    onNavigateToAiSettings = {
                        navController.navigate("ai/settings")
                    },
                    onNavigateToAiRecommendation = {
                        navController.navigate("ai/recommendation")
                    }
                )
            }

            // 过敏食材管理
            composable("baby/allergy/{babyId}") { backStackEntry ->
                val babyId = backStackEntry.arguments?.getString("babyId")?.toLong() ?: 0L
                com.example.babyfood.presentation.ui.baby.AllergyManagementScreen(
                    babyId = babyId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // 偏好食材管理
            composable("baby/preference/{babyId}") { backStackEntry ->
                val babyId = backStackEntry.arguments?.getString("babyId")?.toLong() ?: 0L
                com.example.babyfood.presentation.ui.baby.PreferenceManagementScreen(
                    babyId = babyId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // 体检记录列表
            composable("baby/health/{babyId}") { backStackEntry ->
                val babyId = backStackEntry.arguments?.getString("babyId")?.toLong() ?: 0L
                com.example.babyfood.presentation.ui.health.HealthRecordListScreen(
                    babyId = babyId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onAddRecord = {
                        navController.navigate("baby/health/form/$babyId/0")
                    }
                )
            }

            // 添加/编辑体检记录
            composable("baby/health/form/{babyId}/{recordId}") { backStackEntry ->
                val babyId = backStackEntry.arguments?.getString("babyId")?.toLong() ?: 0L
                val recordId = backStackEntry.arguments?.getString("recordId")?.toLong() ?: 0L
                com.example.babyfood.presentation.ui.health.HealthRecordFormScreen(
                    babyId = babyId,
                    recordId = recordId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = {
                        navController.popBackStack()
                    }
                )
            }

            // 生长曲线
            composable("baby/growth/{babyId}") { backStackEntry ->
                val babyId = backStackEntry.arguments?.getString("babyId")?.toLong() ?: 0L
                com.example.babyfood.presentation.ui.growth.GrowthCurveScreen(
                    babyId = babyId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // AI 设置
            composable("ai/settings") {
                com.example.babyfood.presentation.ui.ai.AiSettingsScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // AI 推荐
            composable("ai/recommendation") {
                com.example.babyfood.presentation.ui.ai.RecommendationScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // 推荐编辑器
            composable("plans/recommendation/editor/{babyId}") { backStackEntry ->
                val babyId = backStackEntry.arguments?.getString("babyId")?.toLong() ?: 0L
                
                // 获取计划列表页面的 ViewModel 实例（共享）
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("plans")
                }
                val viewModel: PlansViewModel = androidx.hilt.navigation.compose.hiltViewModel(parentEntry)
                
                val scope = androidx.compose.runtime.rememberCoroutineScope()
                
                android.util.Log.d("MainScreen", "========== 推荐编辑器页面加载 ==========")
                android.util.Log.d("MainScreen", "宝宝ID: $babyId")
                android.util.Log.d("MainScreen", "当前推荐数据: ${viewModel.uiState.value.recommendation != null}")
                
                // 使用 remember 保存推荐数据，确保导航过程中屏幕保持显示
                var retainedWeeklyPlan by remember { mutableStateOf<WeeklyMealPlan?>(null) }
                var retainedConflicts by remember { mutableStateOf<List<PlanConflict>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }
                
                // 当推荐数据变化时，更新保留的数据
                // 添加 babyId 到依赖项，确保进入页面时重新触发
                androidx.compose.runtime.LaunchedEffect(babyId) {
                    android.util.Log.d("MainScreen", "LaunchedEffect 触发: babyId=$babyId")
                    
                    // 先检查当前是否有推荐数据
                    val currentRecommendation = viewModel.uiState.value.recommendation
                    if (currentRecommendation != null) {
                        android.util.Log.d("MainScreen", "✓ 当前推荐数据可用，直接使用")
                        viewModel.updateConflicts(babyId, currentRecommendation)
                        retainedWeeklyPlan = currentRecommendation
                        retainedConflicts = viewModel.uiState.value.conflicts
                        isLoading = false
                        android.util.Log.d("MainScreen", "✓ 保留数据已更新: ${retainedWeeklyPlan?.dailyPlans?.size}天")
                    } else {
                        android.util.Log.d("MainScreen", "⚠️ 当前推荐数据为 null，尝试从 SavedStateHandle 恢复")
                        // 尝试从 SavedStateHandle 恢复推荐数据
                        val restoredPlan = viewModel.restoreRecommendationFromSavedState()
                        if (restoredPlan != null) {
                            android.util.Log.d("MainScreen", "✓ 从 SavedStateHandle 恢复推荐成功")
                            viewModel.updateConflicts(babyId, restoredPlan)
                            retainedWeeklyPlan = restoredPlan
                            retainedConflicts = viewModel.uiState.value.conflicts
                            isLoading = false
                            android.util.Log.d("MainScreen", "✓ 保留数据已更新: ${retainedWeeklyPlan?.dailyPlans?.size}天")
                        } else {
                            android.util.Log.e("MainScreen", "❌ 无法恢复推荐数据")
                            isLoading = false
                        }
                    }
                }
                
                android.util.Log.d("MainScreen", "渲染状态: retainedWeeklyPlan=${retainedWeeklyPlan != null}, isLoading=$isLoading")
                
                // 显示加载状态
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (retainedWeeklyPlan != null) {
                    android.util.Log.d("MainScreen", "✓ 显示推荐编辑器")
                    RecommendationEditorScreen(
                        babyId = babyId,
                        weeklyPlan = retainedWeeklyPlan!!,
                        conflicts = retainedConflicts,
                        onBack = {
                            navController.popBackStack()
                            viewModel.clearRecommendation()
                        },
                        onSave = { resolution, editedPlans ->
                            scope.launch {
                                val result = viewModel.saveRecommendation(babyId, retainedWeeklyPlan!!, resolution, editedPlans)
                                if (result.success) {
                                    navController.popBackStack()
                                    // 不需要延迟清空，因为使用了 retainedWeeklyPlan
                                }
                            }
                        }
                    )
                } else {
                    android.util.Log.e("MainScreen", "❌ 显示错误提示：推荐数据为空")
                    // 显示错误提示
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "推荐数据加载失败",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = viewModel.uiState.value.error ?: "请重试或联系客服",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.popBackStack() }
                            ) {
                                Text("返回")
                            }
                        }
                    }
                }
            }
        }
    }
}