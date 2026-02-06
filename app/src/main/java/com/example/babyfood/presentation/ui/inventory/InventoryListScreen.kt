package com.example.babyfood.presentation.ui.inventory

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.babyfood.domain.model.ExpiryStatus
import com.example.babyfood.domain.model.StorageMethod
import com.example.babyfood.presentation.theme.Warning
import com.example.babyfood.presentation.theme.OnWarningContainer
import com.example.babyfood.presentation.ui.common.AppScaffold
import com.example.babyfood.presentation.ui.common.AppBottomAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    onNavigateToAdd: () -> Unit = {},
    onNavigateToEdit: (Long) -> Unit = {},
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<com.example.babyfood.domain.model.InventoryItem?>(null) }

    // 搜索防抖
    LaunchedEffect(searchQuery) {
        kotlinx.coroutines.delay(300)
        viewModel.searchInventoryItems(searchQuery)
    }

    // 监听保存成功
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.clearSavedFlag()
        }
    }

    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { if (!uiState.isDeleting) showDeleteDialog = false },
            title = { Text("删除物品") },
            text = {
                if (uiState.isDeleting) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Text("正在删除...")
                    }
                } else {
                    Text("确定要删除 ${itemToDelete!!.foodName} 吗？")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteInventoryItem(itemToDelete!!)
                        showDeleteDialog = false
                        itemToDelete = null
                    },
                    enabled = !uiState.isDeleting
                ) {
                    if (uiState.isDeleting) {
                        Text("删除中...")
                    } else {
                        Text("删除", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    enabled = !uiState.isDeleting
                ) {
                    Text("取消")
                }
            }
        )
    }

    AppScaffold(
        bottomActions = listOf(
            AppBottomAction(
                icon = Icons.Default.Add,
                label = "添加",
                contentDescription = "添加库存",
                onClick = onNavigateToAdd
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 搜索框
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("搜索食材名称...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "搜索")
            },
            trailingIcon = {
                if (uiState.isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "清除")
                    }
                }
            },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search)
        )

        // 筛选器
        var showExpiryStatusFilter by remember { mutableStateOf(false) }
        var showStorageMethodFilter by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                // 过期状态筛选
                Box {
                    FilterChip(
                        selected = uiState.selectedExpiryStatus != null,
                        onClick = { showExpiryStatusFilter = true },
                        label = {
                            Text(
                                text = uiState.selectedExpiryStatus?.getDisplayText() ?: "过期状态",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        trailingIcon = if (uiState.selectedExpiryStatus != null) {
                            {
                                IconButton(onClick = {
                                    viewModel.filterByExpiryStatus(null)
                                }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "清除筛选",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        } else null
                    )

                    DropdownMenu(
                        expanded = showExpiryStatusFilter,
                        onDismissRequest = { showExpiryStatusFilter = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("全部") },
                            onClick = {
                                viewModel.filterByExpiryStatus(null)
                                showExpiryStatusFilter = false
                            }
                        )
                        ExpiryStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.getDisplayText()) },
                                onClick = {
                                    viewModel.filterByExpiryStatus(status)
                                    showExpiryStatusFilter = false
                                }
                            )
                        }
                    }
                }

                // 保存方式筛选
                Box {
                    FilterChip(
                        selected = uiState.selectedStorageMethod != null,
                        onClick = { showStorageMethodFilter = true },
                        label = {
                            Text(
                                text = uiState.selectedStorageMethod?.displayName ?: "保存方式",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        trailingIcon = if (uiState.selectedStorageMethod != null) {
                            {
                                IconButton(onClick = {
                                    viewModel.filterByStorageMethod(null)
                                }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "清除筛选",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        } else null
                    )

                    DropdownMenu(
                        expanded = showStorageMethodFilter,
                        onDismissRequest = { showStorageMethodFilter = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("全部") },
                            onClick = {
                                viewModel.filterByStorageMethod(null)
                                showStorageMethodFilter = false
                            }
                        )
                        StorageMethod.values().forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method.displayName) },
                                onClick = {
                                    viewModel.filterByStorageMethod(method)
                                    showStorageMethodFilter = false
                                }
                            )
                        }
                    }
                }
            }

            // 列表
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("加载中...")
                }
            } else if (uiState.filteredItems.isEmpty()) {
                val isFiltering = searchQuery.isNotEmpty() || uiState.selectedExpiryStatus != null || uiState.selectedStorageMethod != null
                com.example.babyfood.presentation.theme.EmptyState(
                    icon = if (isFiltering) Icons.Default.Search else Icons.Default.Kitchen,
                    title = if (isFiltering) "没有找到符合条件的食材" else "还没有添加库存食材",
                    description = if (isFiltering) "尝试调整搜索条件或筛选器" else "点击右下角 + 按钮添加库存食材"
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.filteredItems) { item ->
                        InventoryItemCard(
                            item = item,
                            onEdit = { onNavigateToEdit(item.id) },
                            onDelete = {
                                itemToDelete = item
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryItemCard(
    item: com.example.babyfood.domain.model.InventoryItem,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val expiryStatus = item.getExpiryStatus()
    val statusColor = when (expiryStatus) {
        ExpiryStatus.EXPIRED -> MaterialTheme.colorScheme.error
        ExpiryStatus.URGENT -> MaterialTheme.colorScheme.error
        ExpiryStatus.WARNING -> Warning
        ExpiryStatus.NORMAL -> MaterialTheme.colorScheme.surfaceVariant
    }

    val statusTextColor = when (expiryStatus) {
        ExpiryStatus.EXPIRED -> MaterialTheme.colorScheme.onError
        ExpiryStatus.URGENT -> MaterialTheme.colorScheme.onError
        ExpiryStatus.WARNING -> OnWarningContainer
        ExpiryStatus.NORMAL -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val statusText = when (expiryStatus) {
        ExpiryStatus.EXPIRED -> "已过期"
        ExpiryStatus.URGENT -> "已过期"
        ExpiryStatus.WARNING -> "3天内食用"
        ExpiryStatus.NORMAL -> "${item.getRemainingDays()}天后到期"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 食材图片
            Box(
                modifier = Modifier.size(88.dp)
            ) {
                if (item.foodImageUrl != null) {
                    AsyncImage(
                        model = item.foodImageUrl,
                        contentDescription = "食材图片",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.foodName.firstOrNull()?.toString() ?: "?",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 食材信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.foodName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.quantity}${item.unit}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 保质期标签
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusTextColor,
                    modifier = Modifier
                        .background(statusColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )

                // 提示文字
                if (expiryStatus == ExpiryStatus.EXPIRED) {
                    Text(
                        text = "今日到期，请尽快食用！",
                        style = MaterialTheme.typography.bodySmall,
                        color = Warning,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                } else if (expiryStatus == ExpiryStatus.URGENT) {
                    Text(
                        text = "3天内食用，建议尽快安排",
                        style = MaterialTheme.typography.bodySmall,
                        color = Warning,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // 操作按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}