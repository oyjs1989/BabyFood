package com.example.babyfood.presentation.ui.plans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.babyfood.domain.model.PlanConflict
import com.example.babyfood.domain.model.WeeklyMealPlan
import kotlinx.coroutines.launch

/**
 * 推荐编辑器路由
 * 封装推荐编辑器的状态管理和导航逻辑
 */
@Composable
fun RecommendationEditorRoute(
    babyId: Long,
    navController: NavController,
    viewModel: PlansViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf<EditorState>(EditorState.Loading) }

    android.util.Log.d("RecommendationEditorRoute", "========== 推荐编辑器路由加载 ==========")
    android.util.Log.d("RecommendationEditorRoute", "宝宝ID: $babyId")

    LaunchedEffect(babyId) {
        android.util.Log.d("RecommendationEditorRoute", "LaunchedEffect 触发: babyId=$babyId")

        state = when (val plan = viewModel.uiState.value.recommendation
            ?: viewModel.restoreRecommendationFromSavedState()) {
            null -> {
                android.util.Log.e("RecommendationEditorRoute", "❌ 无法恢复推荐数据")
                EditorState.Error("推荐数据加载失败", viewModel.uiState.value.error)
            }
            else -> {
                android.util.Log.d("RecommendationEditorRoute", "✓ 推荐数据可用")
                viewModel.updateConflicts(babyId, plan)
                EditorState.Success(plan, viewModel.uiState.value.conflicts)
            }
        }
    }

    when (val currentState = state) {
        is EditorState.Loading -> {
            LoadingIndicator()
        }
        is EditorState.Error -> {
            ErrorScreen(
                message = currentState.message,
                detail = currentState.detail,
                onBack = { navController.popBackStack() }
            )
        }
        is EditorState.Success -> {
            RecommendationEditorScreen(
                babyId = babyId,
                weeklyPlan = currentState.plan,
                conflicts = currentState.conflicts,
                onBack = {
                    navController.popBackStack()
                    viewModel.clearRecommendation()
                },
                onSave = { resolution, editedPlans ->
                    scope.launch {
                        val result = viewModel.saveRecommendation(
                            babyId,
                            currentState.plan,
                            resolution,
                            editedPlans
                        )
                        if (result.success) {
                            navController.popBackStack()
                            viewModel.clearRecommendation()
                        }
                    }
                }
            )
        }
    }
}

/**
 * 编辑器状态
 */
sealed class EditorState {
    object Loading : EditorState()
    data class Error(val message: String, val detail: String? = null) : EditorState()
    data class Success(val plan: WeeklyMealPlan, val conflicts: List<PlanConflict>) : EditorState()
}

/**
 * 加载指示器
 */
@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * 错误屏幕
 */
@Composable
private fun ErrorScreen(
    message: String,
    detail: String? = null,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )
            if (detail != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("返回")
            }
        }
    }
}