package com.example.babyfood.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import com.example.babyfood.presentation.ui.icons.AppIcons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.example.babyfood.presentation.theme.ANIMATION_DURATION_PAGE_FADE_IN
import com.example.babyfood.presentation.theme.ANIMATION_DURATION_PAGE_SLIDE
import com.example.babyfood.presentation.theme.EasingEaseInOut
import kotlinx.coroutines.launch
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.domain.model.PlanConflict
import com.example.babyfood.domain.model.WeeklyMealPlan
import com.example.babyfood.presentation.ui.home.components.TodayMenuScreen
import com.example.babyfood.presentation.ui.plans.PlanListScreen
import com.example.babyfood.presentation.ui.plans.PlanDetailScreen
import com.example.babyfood.presentation.ui.plans.PlanFormScreen
import com.example.babyfood.presentation.ui.plans.PlansViewModel
import com.example.babyfood.presentation.ui.plans.RecommendationEditorScreen
import com.example.babyfood.presentation.ui.plans.RecommendationEditorRoute

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", AppIcons.Home, "首页")
    object Recipes : BottomNavItem("recipes", AppIcons.Recipes, "食谱")
    object Plans : BottomNavItem("plans", AppIcons.Plans, "计划")
    object Inventory : BottomNavItem("inventory", AppIcons.Inventory, "仓库")
    object Baby : BottomNavItem("baby", AppIcons.Baby, "宝宝")
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            // 在登录、注册、服务条款和隐私政策页面隐藏 AppHeader
            if (currentDestination?.route !in listOf("login", "register", "legal/terms", "legal/privacy")) {
                com.example.babyfood.presentation.ui.common.AppHeader(
                    config = com.example.babyfood.presentation.ui.common.AppHeaderConfig(
                        currentRoute = currentDestination?.route,
                        onAppLogoClick = {
                            // 点击应用名称跳转到首页
                            if (currentDestination?.route != "home") {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                    ),
                    authRepository = mainViewModel.getAuthRepository(),
                    onLoginClick = {
                        navController.navigate("login")
                    },
                    onRegisterClick = {
                        navController.navigate("register")
                    },
                    onSettingsClick = {
                        navController.navigate("user/settings")
                    },
                    onLogoutClick = {
                        // 调用 MainViewModel 的 logout 方法
                        mainViewModel.logout(
                            onSuccess = {
                                // 登出成功，导航到登录页面并清除导航栈
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onFailure = {
                                // 登出失败，可以显示错误提示
                            }
                        )
                    }
                )
            }
        },
        bottomBar = {
            if (currentDestination?.route in listOf("home", "recipes", "plans", "inventory", "baby")) {
                AppBottomBar(
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                fadeIn(
                    animationSpec = androidx.compose.animation.core.tween(
                        durationMillis = ANIMATION_DURATION_PAGE_FADE_IN,
                        easing = EasingEaseInOut
                    )
                ) + slideInHorizontally(
                    animationSpec = androidx.compose.animation.core.tween(
                        durationMillis = ANIMATION_DURATION_PAGE_SLIDE,
                        easing = EasingEaseInOut
                    ),
                    initialOffsetX = { it }
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = androidx.compose.animation.core.tween(
                        durationMillis = ANIMATION_DURATION_PAGE_FADE_IN,
                        easing = EasingEaseInOut
                    )
                ) + slideOutHorizontally(
                    animationSpec = androidx.compose.animation.core.tween(
                        durationMillis = ANIMATION_DURATION_PAGE_SLIDE,
                        easing = EasingEaseInOut
                    ),
                    targetOffsetX = { -it }
                )
            }
        ) {
            // 登录页面
            composable("login") {
                com.example.babyfood.presentation.ui.auth.LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate("register")
                    },
                    onForgotPasswordClick = {
                        // TODO: 导航到忘记密码页面
                    }
                )
            }

            // 注册页面
            composable("register") {
                com.example.babyfood.presentation.ui.auth.RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onBackToLogin = {
                        navController.popBackStack()
                    },
                    onViewTermsOfService = {
                        navController.navigate("legal/terms")
                    },
                    onViewPrivacyPolicy = {
                        navController.navigate("legal/privacy")
                    }
                )
            }

            // 服务条款
            composable("legal/terms") {
                com.example.babyfood.presentation.ui.common.legal.TermsOfServiceScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onAgree = {
                        navController.popBackStack()
                    }
                )
            }

            // 隐私政策
            composable("legal/privacy") {
                com.example.babyfood.presentation.ui.common.legal.PrivacyPolicyScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onAgree = {
                        navController.popBackStack()
                    }
                )
            }

            // 首页 - 今日餐单
            composable("home") {
                TodayMenuScreen(
                    onViewRecipeDetail = { recipeId ->
                        navController.navigate("recipes/detail/$recipeId")
                    }
                )
            }

            // 食谱库
            composable("recipes") {
                com.example.babyfood.presentation.ui.recipes.RecipesListScreen(
                    onNavigateToDetail = { recipeId ->
                        navController.navigate("recipes/detail/$recipeId")
                    }
                )
            }

            // 食谱表单（添加/编辑）
            composable("recipes/form/{recipeId}") { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId")?.toLongOrNull()
                com.example.babyfood.presentation.ui.recipes.RecipeFormScreen(
                    recipeId = recipeId,
                    onBack = {
                        navController.navigate("recipes") {
                            popUpTo("recipes") { inclusive = true }
                        }
                    },
                    onSave = {
                        navController.navigate("recipes") {
                            popUpTo("recipes") { inclusive = true }
                        }
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
                val selectedDate = backStackEntry.arguments?.getString("date")?.let {
                    kotlinx.datetime.LocalDate.parse(it)
                }

                PlanFormScreen(
                    babyId = babyId,
                    planId = if (planId > 0) planId else null,
                    selectedDate = selectedDate,
                    onBack = {
                        navController.navigate("plans") {
                            popUpTo("plans") { inclusive = true }
                        }
                    },
                    onSave = {
                        navController.navigate("plans") {
                            popUpTo("plans") { inclusive = true }
                        }
                    },
                    onNavigateToRecommendationEditor = { babyId ->
                        navController.navigate("plans/recommendation/editor/$babyId")
                    }
                )
            }

            // 仓库页面
            composable("inventory") {
                com.example.babyfood.presentation.ui.inventory.InventoryListScreen(
                    onNavigateToAdd = {
                        navController.navigate("inventory/form/0")
                    },
                    onNavigateToEdit = { itemId ->
                        navController.navigate("inventory/form/$itemId")
                    }
                )
            }

            // 添加/编辑仓库物品
            composable("inventory/form/{itemId}") { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")?.toLong() ?: 0L
                com.example.babyfood.presentation.ui.inventory.InventoryFormScreen(
                    itemId = itemId,
                    onBack = {
                        navController.navigate("inventory") {
                            popUpTo("inventory") { inclusive = true }
                        }
                    },
                    onSave = {
                        navController.navigate("inventory") {
                            popUpTo("inventory") { inclusive = true }
                        }
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
                        navController.navigate("baby") {
                            popUpTo("baby") { inclusive = true }
                        }
                    },
                    onSave = {
                        navController.navigate("baby") {
                            popUpTo("baby") { inclusive = true }
                        }
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
                    onEditNutritionGoal = { goal ->
                        mainViewModel.updateBabyNutritionGoal(babyId, goal)
                    },
                    onGenerateNutritionRecommendation = {
                        // 返回挂起函数，由调用方在协程中执行
                        mainViewModel.generateNutritionRecommendation(babyId)
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
                        navController.navigate("baby/health/$babyId") {
                            popUpTo("baby/health/$babyId") { inclusive = true }
                        }
                    },
                    onSave = {
                        navController.navigate("baby/health/$babyId") {
                            popUpTo("baby/health/$babyId") { inclusive = true }
                        }
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

            // 个人设置主页面
            composable("user/settings") {
                com.example.babyfood.presentation.ui.user.SettingsScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onEditNickname = {
                        navController.navigate("user/settings/nickname")
                    },
                    onChangePassword = {
                        navController.navigate("user/settings/password")
                    },
                    onSelectAvatar = {
                        navController.navigate("user/settings/avatar")
                    },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // 修改昵称页面
            composable("user/settings/nickname") {
                com.example.babyfood.presentation.ui.user.EditNicknameScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onSaveSuccess = {
                        navController.popBackStack()
                    }
                )
            }

            // 修改密码页面
            composable("user/settings/password") {
                com.example.babyfood.presentation.ui.user.ChangePasswordScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onChangeSuccess = {
                        navController.popBackStack()
                    }
                )
            }

            // 头像选择页面
            composable("user/settings/avatar") {
                com.example.babyfood.presentation.ui.user.AvatarPickerScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onAvatarSelected = {
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

                RecommendationEditorRoute(babyId, navController, viewModel)
            }
        }
    }
}

@Composable
private fun AppBottomBar(
    currentDestination: androidx.navigation.NavDestination?,
    navController: androidx.navigation.NavController
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Recipes,
        BottomNavItem.Plans,
        BottomNavItem.Inventory,
        BottomNavItem.Baby
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    ) {
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