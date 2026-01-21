package com.example.babyfood.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.babyfood.presentation.ui.home.components.TodayMenuScreen

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
                Text(
                    text = "食谱库",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // 辅食计划
            composable("plans") {
                Text(
                    text = "辅食计划",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // 宝宝页面
            composable("baby") {
                com.example.babyfood.presentation.ui.baby.BabyListScreen(
                    onNavigateToAdd = {
                        navController.navigate("baby/form/0")
                    },
                    onNavigateToEdit = { babyId ->
                        navController.navigate("baby/form/$babyId")
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
        }
    }
}