package com.example.babyfood.presentation.ui.baby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import com.example.babyfood.presentation.ui.common.AppScaffold
import com.example.babyfood.presentation.ui.common.AppBottomAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyListScreen(
    onNavigateToAdd: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {},
    viewModel: BabyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(
        bottomActions = listOf(
            AppBottomAction(
                icon = Icons.Default.Add,
                label = "添加",
                contentDescription = "添加宝宝",
                onClick = onNavigateToAdd
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "加载中...")
                }
            } else if (uiState.babies.isEmpty()) {
                com.example.babyfood.presentation.theme.EmptyState(
                    icon = Icons.Default.ChildCare,
                    title = "还没有添加宝宝信息",
                    description = "点击下方添加按钮添加宝宝"
                )
            } else {
                val context = LocalContext.current
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.babies) { baby ->
                        val isSelected = baby.id == uiState.selectedBabyId
                        BabyCard(
                            baby = baby,
                            isSelected = isSelected,
                            onClick = { onNavigateToDetail(baby.id) },
                            onSetAsCurrent = {
                                viewModel.setAsCurrentBaby(baby)
                                // 显示 Toast 提示
                                Toast.makeText(
                                    context,
                                    "已切换到 ${baby.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BabyCard(
    baby: com.example.babyfood.domain.model.Baby,
    isSelected: Boolean,
    onClick: () -> Unit = {},
    onSetAsCurrent: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = if (isSelected) {
            null
        } else {
            androidx.compose.foundation.BorderStroke(
                0.5.dp,
                MaterialTheme.colorScheme.outline
            )
        },
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = baby.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "月龄：${baby.ageInMonths} 个月",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "出生日期：${baby.birthDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "当前选中",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                // 点击切换按钮
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(2.dp)
                ) {
                    // 空圆圈，表示可以点击切换
                    // 实际点击事件由 Card 的 onClick 处理
                }
            }
        }
    }
}