package com.example.babyfood.presentation.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.InventoryRepository
import com.example.babyfood.domain.model.ExpiryStatus
import com.example.babyfood.domain.model.InventoryItem
import com.example.babyfood.domain.model.StorageMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    companion object {
        private const val TAG = "InventoryViewModel"
    }

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        loadInventoryItems()
    }

    private fun loadInventoryItems() {
        Log.d(TAG, "========== 开始加载仓库物品列表 ==========")
        viewModelScope.launch {
            inventoryRepository.getAllInventoryItems().collect { items ->
                val filteredItems = applyFilters(items)
                _uiState.value = _uiState.value.copy(
                    inventoryItems = items,
                    filteredItems = filteredItems,
                    isLoading = false
                )
                Log.d(TAG, "✓ 仓库物品列表加载完成，共 ${items.size} 个物品 ==========")
            }
        }
    }

    fun searchInventoryItems(query: String) {
        Log.d(TAG, "========== 搜索仓库物品 ==========")
        Log.d(TAG, "搜索关键词: $query")
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch {
            if (query.isEmpty()) {
                loadInventoryItems()
            } else {
                inventoryRepository.searchInventoryItems(query).collect { items ->
                    val filteredItems = applyFilters(items)
                    _uiState.value = _uiState.value.copy(
                        inventoryItems = items,
                        filteredItems = filteredItems
                    )
                    Log.d(TAG, "✓ 搜索完成，找到 ${items.size} 个物品 ==========")
                }
            }
        }
    }

    fun filterByExpiryStatus(status: ExpiryStatus?) {
        Log.d(TAG, "========== 按保质期状态筛选 ==========")
        Log.d(TAG, "筛选状态: $status")
        _uiState.value = _uiState.value.copy(selectedExpiryStatus = status)
        applyCurrentFilters()
    }

    fun filterByStorageMethod(method: StorageMethod?) {
        Log.d(TAG, "========== 按保存方式筛选 ==========")
        Log.d(TAG, "筛选方式: $method")
        _uiState.value = _uiState.value.copy(selectedStorageMethod = method)
        applyCurrentFilters()
    }

    private fun applyCurrentFilters() {
        viewModelScope.launch {
            val items = if (_uiState.value.searchQuery.isNotEmpty()) {
                inventoryRepository.searchInventoryItems(_uiState.value.searchQuery)
            } else {
                inventoryRepository.getAllInventoryItems()
            }

            items.collect { allItems ->
                val filteredItems = applyFilters(allItems)
                _uiState.value = _uiState.value.copy(
                    inventoryItems = allItems,
                    filteredItems = filteredItems
                )
            }
        }
    }

    private fun applyFilters(items: List<InventoryItem>): List<InventoryItem> {
        var filtered = items

        // 按保质期状态筛选
        _uiState.value.selectedExpiryStatus?.let { status ->
            filtered = filtered.filter { it.getExpiryStatus() == status }
        }

        // 按保存方式筛选
        _uiState.value.selectedStorageMethod?.let { method ->
            filtered = filtered.filter { it.storageMethod == method }
        }

        return filtered
    }

    fun saveInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "========== 保存仓库物品 ==========")
                Log.d(TAG, "物品名称: ${item.foodName}")

                if (item.id == 0L) {
                    inventoryRepository.insert(item)
                    Log.d(TAG, "✓ 新增物品成功")
                } else {
                    inventoryRepository.update(item)
                    Log.d(TAG, "✓ 更新物品成功")
                }

                _uiState.value = _uiState.value.copy(
                    isSaved = true,
                    error = null
                )
                Log.d(TAG, "========== 保存完成 ==========")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 保存物品失败: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun deleteInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "========== 删除仓库物品 ==========")
                Log.d(TAG, "物品名称: ${item.foodName}")
                inventoryRepository.delete(item)
                Log.d(TAG, "✓ 删除物品成功")
                Log.d(TAG, "========== 删除完成 ==========")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 删除物品失败: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun loadInventoryItem(itemId: Long) {
        viewModelScope.launch {
            val item = inventoryRepository.getById(itemId)
            _uiState.value = _uiState.value.copy(selectedItem = item)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSavedFlag() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            selectedExpiryStatus = null,
            selectedStorageMethod = null
        )
        applyCurrentFilters()
    }

    fun getExpiryStatistics(): ExpiryStatistics {
        val items = _uiState.value.inventoryItems
        return ExpiryStatistics(
            totalCount = items.size,
            expiredCount = items.count { it.isExpired() },
            expiringCount = items.count { it.getExpiryStatus() == ExpiryStatus.URGENT },
            warningCount = items.count { it.getExpiryStatus() == ExpiryStatus.WARNING },
            normalCount = items.count { it.getExpiryStatus() == ExpiryStatus.NORMAL }
        )
    }
}

data class InventoryUiState(
    val inventoryItems: List<InventoryItem> = emptyList(),
    val filteredItems: List<InventoryItem> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null,
    val selectedItem: InventoryItem? = null,
    val searchQuery: String = "",
    val selectedExpiryStatus: ExpiryStatus? = null,
    val selectedStorageMethod: StorageMethod? = null
)

data class ExpiryStatistics(
    val totalCount: Int = 0,
    val expiredCount: Int = 0,
    val expiringCount: Int = 0,
    val warningCount: Int = 0,
    val normalCount: Int = 0
) {
    val urgentCount: Int
        get() = expiredCount + expiringCount
}